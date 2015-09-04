package eu.unifiedviews.plugins.quality.metadatacompletenesschecker;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.helpers.dpu.extension.rdf.simple.WritableSimpleRdf;
import eu.unifiedviews.helpers.dpu.rdf.EntityBuilder;
import eu.unifiedviews.helpers.dpu.rdf.sparql.SparqlUtils;
import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Main data processing unit class.
 *
 * @author Vincenzo Cutrona
 */
@DPU.AsQuality
public class MetadataCompletenessChecker extends AbstractDpu<MetadataCompletenessCheckerConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataCompletenessChecker.class);

    public static final String COMPLETENESS_GRAPH_SYMBOLIC_NAME = "completenessQualityGraph";

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @ExtensionInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf report;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    private static ValueFactory valueFactory;

    public MetadataCompletenessChecker() {
        super(MetadataCompletenessCheckerVaadinDialog.class, ConfigHistory.noHistory(MetadataCompletenessCheckerConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "MetadataCompletenessChecker.message");

        valueFactory = report.getValueFactory();

        // Get configuration parameters
        ArrayList<String> _property = this.config.getProperty();

        if (_property == null) {
            LOG.warn(ctx.tr("MetadataCompletenessChecker.error.nothing.specified"));
        } else {
            double result = 0;
            // It evaluates the completeness, for every subject specified in the DPU Configuration
            for (int i = 0; i < _property.size(); ++i) {
                String property = _property.get(i);

                if (!property.trim().isEmpty()) {
                    final String q1 =
                            "SELECT (COUNT(?s) AS ?propertyCount) " +
                                    "WHERE { " +
                                    "?s <" + property + "> ?label . " +
                                    "}";

                    // Prepare SPARQL queries.
                    final SparqlUtils.SparqlSelectObject query1 = faultTolerance.execute(
                            new FaultTolerance.ActionReturn<SparqlUtils.SparqlSelectObject>() {
                                @Override
                                public SparqlUtils.SparqlSelectObject action() throws Exception {
                                    return SparqlUtils.createSelect(q1,
                                            DataUnitUtils.getEntries(inRdfData, RDFDataUnit.Entry.class));
                                }
                            });

                    // Execute queries and get results.
                    final SparqlUtils.QueryResultCollector result1 = new SparqlUtils.QueryResultCollector();
                    faultTolerance.execute(inRdfData, new FaultTolerance.ConnectionAction() {
                        @Override
                        public void action(RepositoryConnection connection) throws Exception {
                            result1.prepare();
                            SparqlUtils.execute(connection, ctx, query1, result1);
                        }
                    });

                    // Check result size.
                    if (result1.getResults().isEmpty()) {
                        throw new DPUException(ctx.tr("MetadataCompletenessChecker.error.empty.result"));
                    }

                    // Prepare variables.
                    int count = Integer.parseInt(result1.getResults().get(0).get("propertyCount").stringValue());

                    result += (count > 0) ? 1 : 0;
                }
            }

            result = result / _property.size();

            // Set output.
            final RDFDataUnit.Entry output = faultTolerance.execute(new FaultTolerance.ActionReturn<RDFDataUnit.Entry>() {
                @Override
                public RDFDataUnit.Entry action() throws Exception {
                    return RdfDataUnitUtils.addGraph(outRdfData, COMPLETENESS_GRAPH_SYMBOLIC_NAME);
                }
            });
            report.setOutput(output);

            // EX_COMPLETENESS_DIMENSION entity.
            final EntityBuilder dpuEntity = new EntityBuilder(
                    QualityOntology.EX_COMPLETENESS_DIMENSION, valueFactory);
            dpuEntity.property(RDF.TYPE, QualityOntology.DAQ_METRIC);

            // EX_DPU_NAME entity.
            final EntityBuilder reportEntity = new EntityBuilder(
                    MetadataCompletenessCheckerVocabulary.EX_DPU_NAME, valueFactory);
            reportEntity.property(RDF.TYPE, QualityOntology.DAQ_DIMENSION)
                    .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);

            // EX_OBSERVATIONS entity.
            final EntityBuilder observationEntity = createObservation(result, 1);

            // Add binding from EX_COMPLETENESS_DIMENSION
            dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity);

            // Add entities to output graph.
            report.add(reportEntity.asStatements());
            report.add(dpuEntity.asStatements());
            report.add(observationEntity.asStatements());
        }
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
        String obs = String.format(MetadataCompletenessCheckerVocabulary.EX_OBSERVATIONS, observationIndex);
        final EntityBuilder observationEntity = new EntityBuilder(
                valueFactory.createURI(obs), valueFactory);

        // Prepare variables.
        final SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date reportDate;
        try {
            reportDate = reportDateFormat.parse(reportDateFormat.format(new Date()));
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("MetadataCompletenessChecker.error.date.parse.failed"), ex);
        }

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, QualityOntology.QB_OBSERVATION)
                .property(DCTERMS.DATE, valueFactory.createLiteral(reportDate))
                .property(QualityOntology.DAQ_VALUE, valueFactory.createLiteral(value));

        return observationEntity;
    }

}
