package eu.unifiedviews.plugins.quality.completenessimprovement;

import java.util.*;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
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

@DPU.AsQuality
public class CompletenessImprovement extends AbstractDpu<CompletenessImprovementConfig_V1> {

    public static final String COMPLETENESS_GRAPH_SYMBOLIC_NAME = "silkCompletenessQualityGraph";

    @DataUnit.AsInput(name = "input_source")
    public RDFDataUnit inRdfData_source;

    @DataUnit.AsInput(name = "input_target")
    public RDFDataUnit inRdfData_target;

    @DataUnit.AsInput(name = "input_silk")
    public RDFDataUnit inRdfData_silk;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @ExtensionInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf rdfGraph;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    private static ValueFactory valueFactory;

    public CompletenessImprovement() {
        super(CompletenessImprovementVaadinDialog.class, ConfigHistory.noHistory(CompletenessImprovementConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "C3.message");

        valueFactory = rdfGraph.getValueFactory();

        ArrayList<String> sourcePropertyMapped = config.getProperty_source();
        ArrayList<String> targetPropertyMapped = config.getProperty_target();

        // Set output.
        final RDFDataUnit.Entry output = faultTolerance.execute(new FaultTolerance.ActionReturn<RDFDataUnit.Entry>() {

            @Override
            public RDFDataUnit.Entry action() throws Exception {
                return RdfDataUnitUtils.addGraph(outRdfData, COMPLETENESS_GRAPH_SYMBOLIC_NAME);
            }
        });

        rdfGraph.setOutput(output);

        final String queryGetSameAs =
                "SELECT ?s ?o " +
                "WHERE { " +
                    "?s <http://www.w3.org/2002/07/owl#sameAs> ?o . " +
                "}";

        List<Map<String, Value>> sameAs = executeQuery(inRdfData_silk, queryGetSameAs);

        for (int i = 0; i < sameAs.size(); i++) {

            Map<String, Value> mapped = sameAs.get(i);

            Value sourceValue = mapped.get("s");
            Value targetValue = mapped.get("o");

            final String queryGetSource =
                    "SELECT ?p ?o " +
                    "WHERE { " +
                        "<"+ sourceValue.stringValue() +"> ?p ?o . " +
                    "}";

            final String queryGetTarget =
                    "SELECT ?p ?o " +
                    "WHERE { " +
                        "<"+ targetValue.stringValue() +"> ?p ?o . " +
                    "}";

            List<Map<String, Value>> source = executeQuery(inRdfData_source, queryGetSource);
            List<Map<String, Value>> target = executeQuery(inRdfData_target, queryGetTarget);

            // Check result size.
            if (!source.isEmpty() && !target.isEmpty()) {

                for (int j = 0; j < target.size(); j++) {

                    Value targetProperty = target.get(j).get("p");
                    Value targetObject = target.get(j).get("o");

                    if (targetPropertyMapped.contains(targetProperty.stringValue())) {

                        int index = targetPropertyMapped.indexOf(targetProperty.stringValue());

                        final String queryGetObject =
                                "SELECT ?o " +
                                "WHERE { " +
                                    "<"+ sourceValue.stringValue() +"> <"+ sourcePropertyMapped.get(index) +"> ?o . " +
                                "}";

                        List<Map<String, Value>> sourceObjects = executeQuery(inRdfData_source, queryGetObject);

                        // With targetObject and the list of sourceObject, the Accuracy step can be done!
                        for (int z = 0; z < sourceObjects.size(); z++) {

                            Value sourceObject = sourceObjects.get(z).get("o");

                            /////
                            // Check the Accuracy and choose which value, between source and target, put in the final graph
                            /////

                            // Insert correct value
                            insertTriple(sourceValue, sourcePropertyMapped.get(index), sourceObject);
                        }

                    } else {
                        // Insert new missing property (and its value) from target dataset
                        insertTriple(sourceValue, targetProperty.stringValue(), targetObject);
                    }
                }
            } else {
                throw new DPUException(ctx.tr("C3.error.empty.result"));
            }
        }

        // Copy original source graph to the output graph (without mapped property)
        copyGraph(sourcePropertyMapped);
    }

    private List<Map<String, Value>> executeQuery(final RDFDataUnit dataset, final String query) throws DPUException {

        // Prepare SPARQL query.
        final SparqlUtils.SparqlSelectObject q = faultTolerance.execute(
                new FaultTolerance.ActionReturn<SparqlUtils.SparqlSelectObject>() {

                    @Override
                    public SparqlUtils.SparqlSelectObject action() throws Exception {
                        return SparqlUtils.createSelect(query,
                                DataUnitUtils.getEntries(dataset, RDFDataUnit.Entry.class));
                    }

                });

        // Execute query and get result.
        final SparqlUtils.QueryResultCollector r = new SparqlUtils.QueryResultCollector();
        faultTolerance.execute(dataset, new FaultTolerance.ConnectionAction() {

            @Override
            public void action(RepositoryConnection connection) throws Exception {
                r.prepare();
                SparqlUtils.execute(connection, ctx, q, r);
            }
        });

        return r.getResults();
    }

    private void copyGraph(ArrayList<String> sourcePropertyMapped) throws DPUException {

        final String query = "SELECT ?s ?p ?o  WHERE { ?s ?p ?o } ";

        List<Map<String, Value>> graph = executeQuery(inRdfData_source, query);

        for (int i = 0; i < graph.size(); i++) {

            Value subject = graph.get(i).get("s");
            Value property = graph.get(i).get("p");
            Value object = graph.get(i).get("o");

            if (!sourcePropertyMapped.contains(property.stringValue())) {
                insertTriple(subject, property.stringValue(), object);
            }
        }
    }

    private void insertTriple(Value subject, String property, Value object) throws DPUException {

        final EntityBuilder update =
                new EntityBuilder(valueFactory.createURI(subject.stringValue()), valueFactory);
        update.property(valueFactory.createURI(property), object);

        rdfGraph.add(update.asStatements());

        // System.out.println("<"+ subject +"> <"+ property +">  "+ object);
    }
}
