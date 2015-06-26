package eu.unifiedviews.plugins.quality.lc;

import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.helpers.dpu.extension.rdf.simple.WritableSimpleRdf;
import eu.unifiedviews.helpers.dpu.rdf.EntityBuilder;
import eu.unifiedviews.helpers.dpu.rdf.sparql.SparqlUtils;
import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;

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
public class LC extends AbstractDpu<LCConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(LC.class);

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

    public LC() {
        super(LCVaadinDialog.class, ConfigHistory.noHistory(LCConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "LC.message");

        valueFactory = report.getValueFactory();

        // Get configuration parameters
        ArrayList<String> _properties = this.config.getProperties();

        int[] results = new int[_properties.size()];
        // It evaluates the completeness, for every properties specified in the DPU Configuration
        for (int i = 0; i < _properties.size(); ++i) {
            String property = _properties.get(i);

            if (!property.trim().isEmpty()) {

                final String q1 =
                        "SELECT (COUNT(?label) AS ?labelCount) " +
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
                    throw new DPUException(ctx.tr("LC.error.empty.result"));
                } else {
                    results[i] = Integer.parseInt(result1.getResults().get(0).get("labelCount").stringValue());
                }
            }
        }

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
                LCVocabulary.EX_DPU_NAME, valueFactory);
        reportEntity.property(RDF.TYPE, QualityOntology.DAQ_DIMENSION)
                .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);

        // EX_OBSERVATIONS entity.
        EntityBuilder[] ent = new EntityBuilder[results.length];
        EntityBuilder[] bNode = new EntityBuilder[results.length];
        for (int i = 0; i < results.length; ++i) {
            //if (results[i] != 0) {
                final EntityBuilder observationEntity = createObservation(results[i], (i + 1));
                final EntityBuilder observationEntityBNode = createObservationBNode(_properties.get(i), (i + 1));

                // Add binding from EX_COMPLETENESS_DIMENSION
                dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity);
                ent[i] = observationEntity;
                bNode[i] = observationEntityBNode;
            //}
        }

        // Add entities to output graph.
        report.add(reportEntity.asStatements());
        report.add(dpuEntity.asStatements());
        for (int i = 0; i < ent.length; ++i) {
            report.add(ent[i].asStatements());
        }
        for (int i = 0; i < bNode.length; ++i) {
            report.add(bNode[i].asStatements());
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
    private EntityBuilder createObservation(int value, int observationIndex) throws DPUException {
        String obs = String.format(LCVocabulary.EX_OBSERVATIONS, observationIndex);
        String obsBNode = obs + "/bnode_" + observationIndex;
        final EntityBuilder observationEntity = new EntityBuilder(
                valueFactory.createURI(obs), valueFactory);

        // Prepare variables.
        final SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date reportDate;
        try {
            reportDate = reportDateFormat.parse(reportDateFormat.format(new Date()));
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("LC.error.date.parse.failed"), ex);
        }

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, QualityOntology.QB_OBSERVATION)
                .property(QualityOntology.DAQ_COMPUTED_ON, valueFactory.createURI(obsBNode))
                .property(DCTERMS.DATE, valueFactory.createLiteral(reportDate))
                .property(QualityOntology.DAQ_VALUE, valueFactory.createLiteral(value));

        return observationEntity;
    }

    /**
     * Creates observation for entity.
     *
     * @param property
     * @param observationIndex
     * @return EntityBuilder
     * @throws DPUException
     */
    private EntityBuilder createObservationBNode(String property, int observationIndex) throws DPUException {
        String obs = String.format(LCVocabulary.EX_OBSERVATIONS, observationIndex) + "/bnode_" + observationIndex;
        final EntityBuilder observationEntity = new EntityBuilder(valueFactory.createURI(obs), valueFactory);

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, RDF.STATEMENT)
                .property(RDF.PROPERTY, valueFactory.createLiteral(property));
                //.property(RDF.VALUE, valueFactory.createLiteral(lang));

        return observationEntity;
    }

}
