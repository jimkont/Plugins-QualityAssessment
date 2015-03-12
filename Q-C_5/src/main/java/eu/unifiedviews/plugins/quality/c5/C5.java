package eu.unifiedviews.plugins.quality.c5;

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
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DC;
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

@DPU.AsQuality
public class C5 extends AbstractDpu<C5Config_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(C5.class);

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

	public C5() {
		super(C5VaadinDialog.class, ConfigHistory.noHistory(C5Config_V1.class));
	}
		
    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "C5.message");

        valueFactory = report.getValueFactory();

        // Get configuration parameters
        ArrayList<String> _subject = this.config.getSubject();
        ArrayList<String> _property = this.config.getProperty();
        ArrayList<String> _properties = this.config.getProperties();

        if ((_subject == null) || (_property == null)) {
            LOG.warn("No subject or property or regular expression has been specified.");
        } else {
            Double[] results = new Double[_subject.size()];
            // It evaluates the completeness, for every subject specified in the DPU Configuration
            for (int i = 0; i < _subject.size(); ++i) {
                String subject = _subject.get(i);
                String property = _property.get(i);

                if (!subject.trim().isEmpty() && !property.trim().isEmpty()) {

                    final String q1 =
                            "SELECT (COUNT(?s) AS ?conceptCount) " +
                            "WHERE { " +
                                "?s rdf:type <" + subject + "> . " +
                            "}";

                    final String q2 =
                            "SELECT (COUNT(?s) AS ?conceptCount) " +
                            "WHERE { " +
                                "?s rdf:type <" + subject + ">. " +
                                "?s <" + property + "> ?label. " +
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
                    final SparqlUtils.SparqlSelectObject query2 = faultTolerance.execute(
                            new FaultTolerance.ActionReturn<SparqlUtils.SparqlSelectObject>() {
                                @Override
                                public SparqlUtils.SparqlSelectObject action() throws Exception {
                                    return SparqlUtils.createSelect(q2,
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
                    final SparqlUtils.QueryResultCollector result2 = new SparqlUtils.QueryResultCollector();
                    faultTolerance.execute(inRdfData, new FaultTolerance.ConnectionAction() {
                        @Override
                        public void action(RepositoryConnection connection) throws Exception {
                            result2.prepare();
                            SparqlUtils.execute(connection, ctx, query2, result2);
                        }
                    });

                    // Check result size.
                    if (result1.getResults().isEmpty() || result2.getResults().isEmpty()) {
                        throw new DPUException(ctx.tr("C5.error.empty.result"));
                    }

                    // Prepare variables.
                    Value denom = result1.getResults().get(0).get("conceptCount");
                    Value num = result2.getResults().get(0).get("conceptCount");
                    results[i] = 0.0;
                    if (Double.parseDouble(denom.stringValue()) != 0)
                        results[i] = Double.parseDouble(num.stringValue()) / Double.parseDouble(denom.stringValue());
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
                    C5Vocabulary.EX_DPU_NAME, valueFactory);
            reportEntity.property(RDF.TYPE, QualityOntology.DAQ_DIMENSION)
                    .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);

            // EX_OBSERVATIONS entity.
            EntityBuilder[] ent = new EntityBuilder[results.length];
            EntityBuilder[] bNode = new EntityBuilder[results.length];
            for (int i = 0; i < results.length; ++i) {
                final EntityBuilder observationEntity = createObservation(results[i], (i+1));
                final EntityBuilder observationEntityBNode = createObservationBNode(_subject.get(i), _property.get(i), (i+1));

                // Add binding from EX_COMPLETENESS_DIMENSION
                dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity);
                ent[i] = observationEntity;
                bNode[i] = observationEntityBNode;
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
        String obs = String.format(C5Vocabulary.EX_OBSERVATIONS, observationIndex);
        String obsBNode = obs + "/bnode_" + observationIndex;
        final EntityBuilder observationEntity = new EntityBuilder(
                valueFactory.createURI(obs), valueFactory);

        // Prepare variables.
        final SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date reportDate;
        try {
            reportDate = reportDateFormat.parse(reportDateFormat.format(new Date()));
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("C5.error.date.parse.failed"), ex);
        }

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, QualityOntology.QB_OBSERVATION)
                .property(QualityOntology.DAQ_COMPUTED_ON, valueFactory.createURI(obsBNode))
                .property(DC.DATE, valueFactory.createLiteral(reportDate))
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
        String obs = String.format(C5Vocabulary.EX_OBSERVATIONS, observationIndex) + "/bnode_" + observationIndex;
        final EntityBuilder observationEntity = new EntityBuilder(valueFactory.createURI(obs), valueFactory);

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, RDF.STATEMENT)
                .property(RDF.SUBJECT, valueFactory.createLiteral(subject))
                .property(RDF.PROPERTY, valueFactory.createLiteral(property));
        
        return observationEntity;
    }
	
}
