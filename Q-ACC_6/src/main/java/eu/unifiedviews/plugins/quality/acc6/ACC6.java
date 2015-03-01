package eu.unifiedviews.plugins.quality.acc6;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Map;

import cz.cuni.mff.xrg.uv.boost.dpu.advanced.AbstractDpu;
import cz.cuni.mff.xrg.uv.boost.extensions.FaultTolerance;
import cz.cuni.mff.xrg.uv.boost.dpu.initialization.AutoInitializer;
import cz.cuni.mff.xrg.uv.boost.dpu.config.ConfigHistory;
import cz.cuni.mff.xrg.uv.boost.rdf.simple.WritableSimpleRdf;
import cz.cuni.mff.xrg.uv.boost.rdf.sparql.SparqlUtils;
import cz.cuni.mff.xrg.uv.boost.rdf.EntityBuilder;
import cz.cuni.mff.xrg.uv.utils.dataunit.DataUnitUtils;
import cz.cuni.mff.xrg.uv.utils.dataunit.rdf.RdfDataUnitUtils;

@DPU.AsQuality
public class ACC6 extends AbstractDpu<ACC6Config_V1> {

    private final Logger LOG = LoggerFactory.getLogger(ACC6.class);

    public static final String ACCURACY_GRAPH_SYMBOLIC_NAME = "accuracyQualityGraph";

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @AutoInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf report;

    @AutoInitializer.Init
    public FaultTolerance faultTolerance;
    
    public ACC6() {
        super(ACC6VaadinDialog.class, ConfigHistory.noHistory(ACC6Config_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException, DataUnitException {

        // Get configuration parameters        
        ArrayList<String> _subject = this.config.getSubject();
        ArrayList<String> _property = this.config.getProperty();
        ArrayList<String> _regExp = this.config.getRegularExpression();

        if ((_subject == null) || (_property == null) || _regExp == null) {
            LOG.warn("No subject or property or regular expression has been specified.");
        } else {
            //  It evaluates the completeness, for every subject specified in the DPU Configuration
            for (int i = 0; i < _subject.size(); ++i) {

                String key = _subject.get(i);
                String value = _property.get(i);
                String regExp = _regExp.get(i);
                
                regExp.replace("[", "#x5B");
                regExp.replace("]", "#x5D");

                if (!key.trim().isEmpty() && !value.trim().isEmpty() && !regExp.trim().isEmpty()) {

                    final String q1 = "SELECT (COUNT(?s) AS ?counter) WHERE { ?s rdf:type <" + key + "> . }";
                    final String q2 = "SELECT (COUNT(?s) AS ?counter) WHERE { "
                            + "?s rdf:type <" + key + "> . "
                            + "?s <" + value + "> ?o ."
                            + "FILTER regex(?o, \"" + regExp + "\") }";
                    
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
                        throw new DPUException(ctx.tr("dpu.error.empty.result"));
                    }

                    // Prepare variables.
                    Value denom = result1.getResults().get(0).get("counter");
                    Value num = result2.getResults().get(0).get("counter");

                    final ValueFactory valueFactory = report.getValueFactory();

                    // Set output.
                    report.setOutput(RdfDataUnitUtils.addGraph(outRdfData, ACCURACY_GRAPH_SYMBOLIC_NAME));

                    // EX_TIMELINESS_DIMENSION entity.
                    final EntityBuilder dpuEntity = new EntityBuilder(
                            QualityOntology.EX_ACCURACY_DIMENSION, valueFactory);
                    dpuEntity.property(RDF.PREDICATE, QualityOntology.DAQ_METRIC);
                    
                    // EX_DPU_NAME entity.
                    final EntityBuilder reportEntity = new EntityBuilder(
                            ACC6Vocabulary.EX_DPU_NAME, valueFactory);
                    reportEntity.property(RDF.PREDICATE, QualityOntology.DAQ_DIMENSION)
                            .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);
                    
                    // EX_OBSERVATIONS entity.
                    //for (int index = 0; index < result1.getResults().size(); ++index) {
                        //  final Map<String, Value> observation = result1.getResults().get(index);
                        final EntityBuilder observationEntity = createObservation(valueFactory,
                                result1.getResults().get(0), result2.getResults().get(0), key, i);
                        // Add binding from EX_TIMELINESS_DIMENSION
                        dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity);
                        // Add observation entity to output
                        report.add(observationEntity.asStatements());
                   // }

                    // Add entities to output.
                    report.add(dpuEntity.asStatements());
                    report.add(reportEntity.asStatements());
                }
            }
        }
    }
    
    /**
     * Creates observation for entity.
     *
     * @param valueFactory
     * @param result1
     * @param result2
     * @param subject
     * @param observationIndex
     * @return
     * @throws DPUException
     */
    private EntityBuilder createObservation(ValueFactory valueFactory, Map<String, Value> result1,
                                            Map<String, Value> result2, final String subject, 
                                            int observationIndex) throws DPUException {
        final EntityBuilder observationEntity = new EntityBuilder(
                valueFactory.createURI(String.format(ACC6Vocabulary.EX_OBSERVATIONS, observationIndex)),
                valueFactory);
        // Prepare variables.
        Value num = result2.get("counter");
        Value denom = result1.get("counter");

        double accuracy = 0;
        if (Double.parseDouble(denom.stringValue()) != 0)
            accuracy = Double.parseDouble(num.stringValue()) / Double.parseDouble(denom.stringValue());

        Resource sub = new Resource() {
            @Override
            public String stringValue() {
                return subject;
            }
        };

        // Add triple about report.
        fillReport(valueFactory, observationEntity, sub, accuracy);
        // And return entity.
        return observationEntity;
    }
    /**
     * Create a report about accuracy.
     *
     * @param resource
     * @param value
     */
    private void fillReport(ValueFactory valueFactory, EntityBuilder observationEntity, Resource resource,
                            double value) throws DPUException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date date;
        try {
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
