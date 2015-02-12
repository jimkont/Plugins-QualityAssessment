package eu.unifiedviews.plugins.quality.cu1;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import org.openrdf.repository.RepositoryConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.RDF;
import cz.cuni.mff.xrg.uv.boost.dpu.advanced.AbstractDpu;
import cz.cuni.mff.xrg.uv.boost.dpu.config.ConfigHistory;
import cz.cuni.mff.xrg.uv.boost.dpu.initialization.AutoInitializer;
import cz.cuni.mff.xrg.uv.boost.extensions.FaultTolerance;
import cz.cuni.mff.xrg.uv.boost.rdf.EntityBuilder;
import cz.cuni.mff.xrg.uv.boost.rdf.simple.WritableSimpleRdf;
import cz.cuni.mff.xrg.uv.boost.rdf.sparql.SparqlUtils;
import cz.cuni.mff.xrg.uv.utils.dataunit.DataUnitUtils;
import cz.cuni.mff.xrg.uv.utils.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;

@DPU.AsQuality
public class CU1 extends AbstractDpu<CU1Config_V1> {

    private static final String QUERY = "SELECT ?s ?o WHERE { ?s <http://purl.org/dc/terms/modified> ?o }";

    public static final String CURRENCY_GRAPH_SYMBOLIC_NAME = "currencyQualityGraph";

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @AutoInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf report;

    @AutoInitializer.Init
    public FaultTolerance faultTolerance;

    public CU1() {
        super(CU1VaadinDialog.class, ConfigHistory.noHistory(CU1Config_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException, DataUnitException {
        // Prepare SPARQL query.
        final SparqlUtils.SparqlSelectObject query = faultTolerance.execute(
                new FaultTolerance.ActionReturn<SparqlUtils.SparqlSelectObject>() {

                    @Override
                    public SparqlUtils.SparqlSelectObject action() throws Exception {
                        return SparqlUtils.createSelect(QUERY,
                                DataUnitUtils.getEntries(inRdfData, RDFDataUnit.Entry.class));
                    }

                });
        // Execute query and get result.
        final SparqlUtils.QueryResultCollector result = new SparqlUtils.QueryResultCollector();
        faultTolerance.execute(inRdfData, new FaultTolerance.ConnectionAction() {

            @Override
            public void action(RepositoryConnection connection) throws Exception {
                result.prepare();
                SparqlUtils.execute(connection, ctx, query, result);
            }
        });
        // Check result size.
        if (result.getResults().isEmpty()) {
            throw new DPUException(ctx.tr("dpu.error.emmpty.result"));
        }

        // Prepare variables.
        final Date now = new Date();
        final Date startDate;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2007-01-01");
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("dpu.error.date.parse.failed"), ex);
        }
        final double currentTime = now.getTime();
        final double startTime = startDate.getTime();
        final ValueFactory valueFactory = report.getValueFactory();
        // Set output.
        report.setOutput(RdfDataUnitUtils.addGraph(outRdfData, CURRENCY_GRAPH_SYMBOLIC_NAME));

        // EX_TIMELINESS_DIMENSION entity.
        final EntityBuilder dpuEntity = new EntityBuilder(
                QualityOntology.EX_TIMELINESS_DIMENSION, valueFactory);
        dpuEntity.property(RDF.PREDICATE, QualityOntology.DAQ_METRIC);

        // EX_DPU_NAME entity.
        final EntityBuilder reportEntity = new EntityBuilder(
                CU1Vocabulary.EX_DPU_NAME, valueFactory);
        reportEntity.property(RDF.PREDICATE, QualityOntology.DAQ_DIMENSION)
                .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);

        // EX_OBSERVATIONS entity.
        for (int index = 0; index < result.getResults().size(); ++index) {
            final Map<String, Value> observation = result.getResults().get(index);
            final EntityBuilder observationEntity = createObservation(valueFactory, currentTime, startTime,
                    observation.get("o"), observation.get("s"), index);
            // Add binding from EX_TIMELINESS_DIMENSION
            dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity);
            // Add observation entity to outpu.
            report.add(observationEntity.asStatements());
        }

        // Add entities to output.
        report.add(dpuEntity.asStatements());
        report.add(reportEntity.asStatements());
    }

    /**
     * Creates observation for entity.
     *
     * @param valueFactory
     * @param currentTime
     * @param startTime
     * @param lastEdit
     * @param subject
     * @param observationIndex
     * @return
     * @throws DPUException
     */
    private EntityBuilder createObservation(ValueFactory valueFactory, double currentTime, double startTime,
            Value lastEdit, Value subject, int observationIndex) throws DPUException {
        final EntityBuilder observationEntity = new EntityBuilder(
                valueFactory.createURI(String.format(CU1Vocabulary.EX_OBSERVATIONS, observationIndex)),
                valueFactory);
        // Prepare variables.
        final Date lastEditDate;
        try {
            lastEditDate = new SimpleDateFormat("yyyy-MM-dd").parse(lastEdit.stringValue());
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("dpu.error.date.parse.failed"), ex);
        }
        double lastModificationTime = lastEditDate.getTime();
        double currency = 1 - ((currentTime - lastModificationTime) / (currentTime - startTime));
        // Add triple about report.
        fillReport(valueFactory, observationEntity, (Resource) subject, currency);
        // And return entity.
        return observationEntity;
    }

    /**
     * Create a report about currency.
     *
     * @param resource
     * @param value
     */
    private void fillReport(ValueFactory valueFactory, EntityBuilder observationEntity, Resource resource,
            double value) throws DPUException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date date;
        try {
            // TODO Can we use date = new Date() instead?
            date = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("dpu.error.date.parse.failed"), ex);
        }
        observationEntity
                .property(RDF.PREDICATE, QualityOntology.QB_OBSERVATION)
                .property(QualityOntology.DAQ_COMPUTED_ON, resource)
                .property(DC.DATE, valueFactory.createLiteral(date))
                .property(QualityOntology.DAQ_VALUE, valueFactory.createLiteral(value));
    }

}
