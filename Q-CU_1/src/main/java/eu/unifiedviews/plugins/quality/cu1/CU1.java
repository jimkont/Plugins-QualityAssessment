package eu.unifiedviews.plugins.quality.cu1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.rdf.simple.WritableSimpleRdf;
import eu.unifiedviews.helpers.dpu.rdf.EntityBuilder;
import eu.unifiedviews.helpers.dpu.rdf.sparql.SparqlUtils;
import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;

@DPU.AsQuality
public class CU1 extends AbstractDpu<CU1Config_V1> {

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @ExtensionInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf report;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    private static ValueFactory valueFactory;

    public static final String CURRENCY_GRAPH_SYMBOLIC_NAME = "currencyQualityGraph";

    private static final String QUERY = "SELECT ?s ?o WHERE { ?s <http://purl.org/dc/terms/modified> ?o }";

    public CU1() {
        super(CU1VaadinDialog.class, ConfigHistory.noHistory(CU1Config_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        valueFactory = report.getValueFactory();

        // Prepare SPARQL query.
        final SparqlUtils.SparqlSelectObject query = faultTolerance.execute(
                new FaultTolerance.ActionReturn<SparqlUtils.SparqlSelectObject>() {

                    @Override
                    public SparqlUtils.SparqlSelectObject action() throws Exception {
                        return SparqlUtils.createSelect(QUERY,
                                DataUnitUtils.getEntries(inRdfData, RDFDataUnit.Entry.class)
                        );
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
            throw new DPUException(ctx.tr("dpu.error.empty.result"));
        }

        final Map<String, Value> observation = result.getResults().get(0);

        // Prepare variables.
        final SimpleDateFormat dpuDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date startDate;
        final Date lastEditDate;
        try {
            startDate = dpuDateFormat.parse("2007-01-01");
            lastEditDate = dpuDateFormat.parse(observation.get("o").stringValue());
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("dpu.error.date.parse.failed"), ex);
        }
        final double currentTime = new Date().getTime();

        // Compute the final value.
        double currency = 1 - ((currentTime - lastEditDate.getTime()) / (currentTime - startDate.getTime()));

        // Set output.
        final RDFDataUnit.Entry output = faultTolerance.execute(new FaultTolerance.ActionReturn<RDFDataUnit.Entry>() {

            @Override
            public RDFDataUnit.Entry action() throws Exception {
                return RdfDataUnitUtils.addGraph(outRdfData, CURRENCY_GRAPH_SYMBOLIC_NAME);
            }
        });
        report.setOutput(output);

        // EX_TIMELINESS_DIMENSION entity.
        final EntityBuilder dpuEntity = new EntityBuilder(QualityOntology.EX_TIMELINESS_DIMENSION, valueFactory);
        dpuEntity
                .property(RDF.TYPE, QualityOntology.DAQ_METRIC);

        // EX_DPU_NAME entity.
        final EntityBuilder reportEntity = new EntityBuilder(CU1Vocabulary.EX_DPU_NAME, valueFactory);
        reportEntity
                .property(RDF.TYPE, QualityOntology.DAQ_DIMENSION)
                .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);

        // EX_OBSERVATIONS entity.
        final EntityBuilder observationEntity = createObservation(observation.get("s"), currency, 0);
        dpuEntity
                .property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity);

        // Add entities to output graph.
        report.add(reportEntity.asStatements());
        report.add(dpuEntity.asStatements());
        report.add(observationEntity.asStatements());
    }

    /**
     * Creates observation for entity.
     *
     * @param value
     * @param subject
     * @param observationIndex
     * @return EntityBuilder
     * @throws DPUException
     */
    private EntityBuilder createObservation(Value subject, double value, int observationIndex) throws DPUException {

        final EntityBuilder observationEntity = new EntityBuilder(
                valueFactory.createURI(String.format(CU1Vocabulary.EX_OBSERVATIONS, observationIndex)),
                valueFactory);

        // Prepare variables.
        final SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date reportDate;
        try {
            reportDate = reportDateFormat.parse(reportDateFormat.format(new Date()));
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("dpu.error.date.parse.failed"), ex);
        }

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, QualityOntology.QB_OBSERVATION)
                .property(QualityOntology.DAQ_COMPUTED_ON, subject)
                .property(DC.DATE, valueFactory.createLiteral(reportDate))
                .property(QualityOntology.DAQ_VALUE, valueFactory.createLiteral(value));

        return observationEntity;
    }
}
