package eu.unifiedviews.plugins.quality.numericalrangechecker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
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
public class NumericalRangeChecker extends AbstractDpu<NumericalRangeCheckerConfig_V1> {

    public static final String ACCURACY_GRAPH_SYMBOLIC_NAME = "accuracyQualityGraph";

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @ExtensionInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf report;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    private static ValueFactory valueFactory;

    public NumericalRangeChecker() {
        super(NumericalRangeCheckerVaadinDialog.class, ConfigHistory.noHistory(NumericalRangeCheckerConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "ACC4.message");

        valueFactory = report.getValueFactory();

        // Get configuration parameters
        final String classUri = config.getClassUri();
        String propertyUri = config.getProperty();
        double lowerBound = config.getLowerBound();
        double upperBound = config.getUpperBound();

        final String query1 =
                "SELECT (COUNT(?s) AS ?counter) " +
                "WHERE { " +
                    "?s rdf:type <"+ classUri +"> . " +
                "}";

        final String query2 =
                "SELECT (COUNT(?s) AS ?counter) " +
                "WHERE { " +
                    "?s rdf:type <"+ classUri +"> . " +
                    "?s <"+ propertyUri +"> ?o . " +
                    "FILTER (?o > "+ lowerBound +" && ?o < "+ upperBound +") . " +
                "}";

        // Prepare SPARQL query 1.
        final SparqlUtils.SparqlSelectObject query_1 = faultTolerance.execute(
                new FaultTolerance.ActionReturn<SparqlUtils.SparqlSelectObject>() {

                    @Override
                    public SparqlUtils.SparqlSelectObject action() throws Exception {
                        return SparqlUtils.createSelect(query1,
                                DataUnitUtils.getEntries(inRdfData, RDFDataUnit.Entry.class));
                    }

                });

        // Execute query 1 and get result.
        final SparqlUtils.QueryResultCollector result_1 = new SparqlUtils.QueryResultCollector();
        faultTolerance.execute(inRdfData, new FaultTolerance.ConnectionAction() {

            @Override
            public void action(RepositoryConnection connection) throws Exception {
                result_1.prepare();
                SparqlUtils.execute(connection, ctx, query_1, result_1);
            }
        });

        // Prepare SPARQL query 2.
        final SparqlUtils.SparqlSelectObject query_2 = faultTolerance.execute(
                new FaultTolerance.ActionReturn<SparqlUtils.SparqlSelectObject>() {

                    @Override
                    public SparqlUtils.SparqlSelectObject action() throws Exception {
                        return SparqlUtils.createSelect(query2,
                                DataUnitUtils.getEntries(inRdfData, RDFDataUnit.Entry.class));
                    }

                });

        // Execute query 2 and get result.
        final SparqlUtils.QueryResultCollector result_2 = new SparqlUtils.QueryResultCollector();
        faultTolerance.execute(inRdfData, new FaultTolerance.ConnectionAction() {

            @Override
            public void action(RepositoryConnection connection) throws Exception {
                result_2.prepare();
                SparqlUtils.execute(connection, ctx, query_2, result_2);
            }
        });

        // Check result size.
        if (result_1.getResults().isEmpty() || result_2.getResults().isEmpty()) {
            throw new DPUException(ctx.tr("ACC4.error.empty.result"));
        }

        // Prepare variables.
        Value x = result_1.getResults().get(0).get("counter");
        Value y = result_2.getResults().get(0).get("counter");

        final double accuracy = Double.parseDouble(y.stringValue()) / Double.parseDouble(x.stringValue());

        // Set output.
        final RDFDataUnit.Entry output = faultTolerance.execute(new FaultTolerance.ActionReturn<RDFDataUnit.Entry>() {

            @Override
            public RDFDataUnit.Entry action() throws Exception {
                return RdfDataUnitUtils.addGraph(outRdfData, ACCURACY_GRAPH_SYMBOLIC_NAME);
            }
        });
        report.setOutput(output);

        // EX_ACCURACY_DIMENSION entity.
        final EntityBuilder dpuEntity = new EntityBuilder(QualityOntology.EX_ACCURACY_DIMENSION, valueFactory);
        dpuEntity
                .property(RDF.TYPE, QualityOntology.DAQ_METRIC);

        // EX_DPU_NAME entity.
        final EntityBuilder reportEntity = new EntityBuilder(NumericalRangeCheckerVocabulary.EX_DPU_NAME, valueFactory);
        reportEntity
                .property(RDF.TYPE, QualityOntology.DAQ_DIMENSION)
                .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);

        // EX_OBSERVATIONS entity.
        final EntityBuilder observationEntity = createObservation(accuracy, 1);
        final EntityBuilder observationEntityBNode = createObservationBNode(classUri, propertyUri, 1);

        dpuEntity
                .property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity);

        // Add entities to output graph.
        report.add(reportEntity.asStatements());
        report.add(dpuEntity.asStatements());
        report.add(observationEntity.asStatements());
        report.add(observationEntityBNode.asStatements());
    }

    /**
     * Creates observation for entity.
     *
     * @param value
     * @param observationIndex
     * @return EntityBuilder
     * @throws DPUException
     */
    private EntityBuilder createObservation(double value, int observationIndex) throws DPUException {

        String obs = String.format(NumericalRangeCheckerVocabulary.EX_OBSERVATIONS, observationIndex);
        String obs_bnode = obs +"/bnode_"+ observationIndex;

        final EntityBuilder observationEntity = new EntityBuilder(valueFactory.createURI(obs), valueFactory);

        // Prepare variables.
        final SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date reportDate;
        try {
            reportDate = reportDateFormat.parse(reportDateFormat.format(new Date()));
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("ACC4.error.date.parse.failed"), ex);
        }

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, QualityOntology.QB_OBSERVATION)
                .property(QualityOntology.DAQ_COMPUTED_ON, valueFactory.createURI(obs_bnode))
                .property(DCTERMS.DATE, valueFactory.createLiteral(reportDate))
                .property(QualityOntology.DAQ_VALUE, valueFactory.createLiteral(value));

        return observationEntity;
    }

    /**
     * Creates observation for entity.
     *
     * @param subject
     * @param property
     * @param observationIndex
     * @return EntityBuilder
     * @throws DPUException
     */
    private EntityBuilder createObservationBNode(String subject, String property, int observationIndex) throws DPUException {

        String obs = String.format(NumericalRangeCheckerVocabulary.EX_OBSERVATIONS, observationIndex) +"/bnode_"+ observationIndex;
        final EntityBuilder observationEntity = new EntityBuilder(valueFactory.createURI(obs), valueFactory);

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, RDF.STATEMENT)
                .property(RDF.SUBJECT, valueFactory.createLiteral(subject))
                .property(RDF.PROPERTY, valueFactory.createLiteral(property));

        return observationEntity;
    }
}
