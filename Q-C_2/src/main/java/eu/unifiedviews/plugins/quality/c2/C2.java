package eu.unifiedviews.plugins.quality.c2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DC;
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
public class C2 extends AbstractDpu<C2Config_V1> {

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
    
    public C2() {
        super(C2VaadinDialog.class, ConfigHistory.noHistory(C2Config_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "C2.message");

        valueFactory = report.getValueFactory();

        // Get configuration parameters
        ArrayList<String> subject_ = this.config.getSubject();
        ArrayList<String> property_ = this.config.getProperty();

        if ((subject_ == null) && (property_ == null)) {
            throw new DPUException(ctx.tr("C2.error.nothing.specified"));
        } else {

            Double [] results = new Double[subject_.size()];
            
            //  It evaluates the completeness, for every subject specified in the DPU Configuration
            for (int i = 0; i < subject_.size(); i++) {

                String key = subject_.get(i);
                String value = property_.get(i);
                
                if (key.trim().length() > 0 && value.trim().length() > 0) {

                    final String query1 =
                            "SELECT (COUNT(?s) AS ?counter) " +
                                    "WHERE { " +
                                    "?s a <" + key + "> . " +
                                    "}";

                    final String query2 =
                            "SELECT (COUNT(?o) AS ?counter) " +
                                    "WHERE { " +
                                    "?s a <" + key + "> . " +
                                    "?s <" + value + "> ?o . " +
                                    " }";

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
                        throw new DPUException(ctx.tr("C2.error.empty.result"));
                    }

                    // Prepare variables.
                    Value x = result_1.getResults().get(0).get("counter");
                    Value y = result_2.getResults().get(0).get("counter");

                    results[i] = 0.0;
                    if (Double.parseDouble(x.stringValue()) != 0)
                        results[i] = Double.parseDouble(y.stringValue()) / Double.parseDouble(x.stringValue());
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
            final EntityBuilder dpuEntity = new EntityBuilder(QualityOntology.EX_COMPLETENESS_DIMENSION, valueFactory);
            dpuEntity
                    .property(RDF.TYPE, QualityOntology.DAQ_METRIC);

            // EX_DPU_NAME entity.
            final EntityBuilder reportEntity = new EntityBuilder(C2Vocabulary.EX_DPU_NAME, valueFactory);
            reportEntity
                    .property(RDF.TYPE, QualityOntology.DAQ_DIMENSION)
                    .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);

            // EX_OBSERVATIONS entity.
            EntityBuilder[] eb = new EntityBuilder[results.length];
            EntityBuilder[] bn = new EntityBuilder[results.length];

            for (int index = 0; index < results.length; index++) {

                final EntityBuilder observationEntity = createObservation(results[index], index+1);
                final EntityBuilder observationEntityBNode = createObservationBNode(subject_.get(index), property_.get(index), index+1);

                // Add binding from EX_COMPLETENESS_DIMENSION
                dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity);

                eb[index] = observationEntity;
                bn[index] = observationEntityBNode;
            }

            // Add entities to output graph.
            report.add(reportEntity.asStatements());
            report.add(dpuEntity.asStatements());
            for (int i = 0; i < eb.length; i++) {
                report.add(eb[i].asStatements());
            }
            for (int j = 0; j < bn.length; j++) {
                report.add(bn[j].asStatements());
            }
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

        String obs = String.format(C2Vocabulary.EX_OBSERVATIONS, observationIndex);
        String obs_bnode = obs +"/bnode_"+ observationIndex;

        final EntityBuilder observationEntity = new EntityBuilder(valueFactory.createURI(obs), valueFactory);

        // Prepare variables.
        final SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date reportDate;
        try {
            reportDate = reportDateFormat.parse(reportDateFormat.format(new Date()));
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("C2.error.date.parse.failed"), ex);
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

        String obs = String.format(C2Vocabulary.EX_OBSERVATIONS, observationIndex) +"/bnode_"+ observationIndex;
        final EntityBuilder observationEntity = new EntityBuilder(valueFactory.createURI(obs), valueFactory);

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, RDF.STATEMENT)
                .property(RDF.SUBJECT, valueFactory.createLiteral(subject))
                .property(RDF.PROPERTY, valueFactory.createLiteral(property));

        return observationEntity;
    }
}
