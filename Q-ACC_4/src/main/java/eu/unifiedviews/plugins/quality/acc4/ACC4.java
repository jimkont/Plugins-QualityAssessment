package eu.unifiedviews.plugins.quality.acc4;

import cz.cuni.mff.xrg.uv.boost.dpu.advanced.AbstractDpu;
import cz.cuni.mff.xrg.uv.boost.dpu.config.ConfigHistory;
import cz.cuni.mff.xrg.uv.boost.dpu.initialization.AutoInitializer;
import cz.cuni.mff.xrg.uv.boost.extensions.FaultTolerance;
import cz.cuni.mff.xrg.uv.boost.rdf.EntityBuilder;
import cz.cuni.mff.xrg.uv.boost.rdf.simple.WritableSimpleRdf;
import cz.cuni.mff.xrg.uv.boost.rdf.sparql.SparqlUtils;
import cz.cuni.mff.xrg.uv.utils.dataunit.DataUnitUtils;
import cz.cuni.mff.xrg.uv.utils.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.*;
import org.openrdf.query.impl.DatasetImpl;
//import org.openrdf.query.resultio.TupleQueryResultWriter;
//import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriterFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
//import au.com.bytecode.opencsv.CSVReader;
//import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.AddPolicy;
//import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.OperationFailedException;
//import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.SimpleRdfFactory;
//import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.SimpleRdfWrite;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;

@DPU.AsQuality
public class ACC4 extends AbstractDpu<ACC4Config_V1> {

    //private final Logger LOG = LoggerFactory.getLogger(ACC4.class);

    public static final String ACCURACY_GRAPH_SYMBOLIC_NAME = "accuracyQualityGraph";

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @AutoInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf report;

    @AutoInitializer.Init
    public FaultTolerance faultTolerance;

    public ACC4() {
        super(ACC4VaadinDialog.class, ConfigHistory.noHistory(ACC4Config_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException, DataUnitException {

        // Get configuration parameters
        String classUri = "http://unifiedviews.eu/ontology/Meteo";
        String propertyUri = "http://comsode.disco.unimib.it/resource/dataset/meteo/minTemperature";
        int lowerBound = 0;
        int upperBound = 2;

        final String query1 = "SELECT (COUNT(?s) as ?counter ) WHERE { ?s ?p ?o . }";

        final String query2 = "SELECT (COUNT(?s) as ?counter ) " +
                "WHERE { " +
                "?s rdf:type <"+ classUri +"> . " +
                "?s <"+ propertyUri +"> ?o . " +
                "FILTER (?o > "+ lowerBound +" && ?o < "+ upperBound +") " +
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
            throw new DPUException(ctx.tr("dpu.error.emmpty.result"));
        } else {
            Value x = result_2.getResults().get(0).get("counter");
            Value y = result_1.getResults().get(0).get("counter");

            //double accuracy = Double.parseDouble(x.stringValue()) / Double.parseDouble(y.stringValue());
            System.out.println(x.stringValue() + " "+y.stringValue());
        }

        final ValueFactory valueFactory = report.getValueFactory();
        // Set output.
        report.setOutput(RdfDataUnitUtils.addGraph(outRdfData, ACCURACY_GRAPH_SYMBOLIC_NAME));

        // EX_TIMELINESS_DIMENSION entity.
        final EntityBuilder dpuEntity = new EntityBuilder(QualityOntology.EX_ACCURACY_DIMENSION, valueFactory);
        //dpuEntity.property(RDF.PREDICATE, QualityOntology.DAQ_METRIC);

        // EX_DPU_NAME entity.
        final EntityBuilder reportEntity = new EntityBuilder(ACC4Vocabulary.EX_DPU_NAME, valueFactory);
//        reportEntity.property(RDF.PREDICATE, QualityOntology.DAQ_DIMENSION)
  //              .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);

        // EX_OBSERVATIONS entity.
        //for (int index = 0; index < result.getResults().size(); ++index) {
            //final Map<String, Value> observation = result.getResults().get(index);
            final EntityBuilder observationEntity = createObservation(valueFactory, result_1.getResults().get(0), result_2.getResults().get(0), classUri, 0);
            //currentTime, startTime,
            //        observation.get("o"), observation.get("s"), index);
            // Add binding from EX_TIMELINESS_DIMENSION
            dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity);
            // Add observation entity to outpu.
            report.add(observationEntity.asStatements());
        //}

        // Add entities to output.
        report.add(dpuEntity.asStatements());
        report.add(reportEntity.asStatements());

        //if ((classUri == null) || (propertyUri == null) || (lowerBound == 0)) {
        //   LOG.warn("No subject or property has been specified.");
        //} else {

            /*Double result;
            
            //  It evaluates the completeness, for every subject specified in the DPU Configuration
            //for (int i = 0; i < subject_.size(); i++) {

                //String key = subject_.get(i);
                //String value = property_.get(i);


                
                //if (key.trim().length() > 0 && value.trim().length() > 0) {
                    
                    String query1 = "SELECT (COUNT(?s) as ?counter ) " +
                             "WHERE { " +
                                "?s rdf:type <"+ classUri +"> . " +
                             "}";

                    String query2 = "SELECT (COUNT(?s) as ?counter ) " +
                             "WHERE { " +
                                "?s rdf:type <"+ classUri +"> . " +
                                "?s <"+ propertyUri +"> ?o . " +
                                "FILTER (?o > "+ lowerBound +" && ?o < "+ upperBound +") " +
                             "}";


                    final File outFile_1;
                    final File outFile_2;

                    try {

                        // Create two temp files, used to evaluate the completeness, in the output directory
                        outFile_1 = new File(java.net.URI.create(context.getDpuInstanceDirectory()+"result_1.csv"));
                        outFile_2 = new File(java.net.URI.create(context.getDpuInstanceDirectory()+"result_2.csv"));

                        final Map<String, URI> graphs = getGraphs();

                        final DatasetImpl dataset = new DatasetImpl();

                        for (URI graph_1 : graphs.values()) {
                            dataset.addDefaultGraph(graph_1);
                        }

                        // Execute the above two Queries specified above
                        this.executeQuery(outFile_1, query1, dataset);
                        this.executeQuery(outFile_2, query2, dataset);

                        // Get the result
                        result = this.calculateMean(context, outFile_1.getAbsolutePath(), outFile_2.getAbsolutePath());

                    } catch (DataUnitException ex) {
                        context.sendMessage(DPUContext.MessageType.ERROR, "Problem with DataUnit.", "", ex);
                        return;
                    }
                    
                    // Delete the two temp files
                    outFile_1.delete();
                    outFile_2.delete();
                //}
            //}

            // Create the RDF output with the result
            this.createOutputGraph("qualityGraph1", classUri, propertyUri, result);
        //}*/
    }

    /**
     * Creates observation for entity.
     *
     * @param valueFactory
     * @param result_1
     * @param result_2
     * @param subject
     * @param observationIndex
     * @return
     * @throws DPUException
     */
    private EntityBuilder createObservation(ValueFactory valueFactory, Map<String, Value> result_1, Map<String, Value> result_2, final String subject, int observationIndex) throws DPUException {
        final EntityBuilder observationEntity = new EntityBuilder(
                valueFactory.createURI(String.format(ACC4Vocabulary.EX_OBSERVATIONS, observationIndex)),
                valueFactory);
        // Prepare variables.
        Value x = result_2.get("counter");
        Value y = result_1.get("counter");

        double accuracy = Double.parseDouble(x.stringValue()) / Double.parseDouble(y.stringValue());

        Resource sub = new Resource() {
            @Override
            public String stringValue() {
                return subject;
            }
        };
        //final Date lastEditDate;
        //try {
        //    lastEditDate = new SimpleDateFormat("yyyy-MM-dd").parse(lastEdit.stringValue());
        //} catch (ParseException ex) {
        //    throw new DPUException(ctx.tr("dpu.error.date.parse.failed"), ex);
        //}
        //double lastModificationTime = lastEditDate.getTime();
        //double currency = 1 - ((currentTime - lastModificationTime) / (currentTime - startTime));
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
    private void fillReport(ValueFactory valueFactory, EntityBuilder observationEntity, Resource resource, double value) throws DPUException {
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

    /*private void executeQuery (File outFile, String query, DatasetImpl dataset) {

        RepositoryConnection connection = null;

        try (OutputStream outputStream = new FileOutputStream(outFile)) {

            connection = inRdfData.getConnection();

            // Prepare the execution of the query
            final SPARQLResultsCSVWriterFactory writerFactory = new SPARQLResultsCSVWriterFactory();
            final TupleQueryResultWriter resultWriter = writerFactory.getWriter(outputStream);
            TupleQuery querySparql = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

            // Execute the Query
            querySparql.setDataset(dataset);
            querySparql.evaluate(resultWriter);

        } catch (IOException | RepositoryException | QueryEvaluationException | TupleQueryResultHandlerException ex) {
            LOG.warn("IOException", ex);
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "", ex);
        } catch (MalformedQueryException ex) {
            LOG.warn("MalformedQueryException", ex);
            context.sendMessage(DPUContext.MessageType.ERROR, "Invalid query.", "", ex);
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed.", "Problem with DataUnit.", ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (RepositoryException ex) {
                LOG.warn("Close on connection has failed.", ex);
            }
        }
    }
   
    private void createOutputGraph(String namegraph,  String classUri, String propertyUri, Double result) {

        try {

            // Set the Date of the DPU execution
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            Date date = dateFormat.parse(dateFormat.format(new Date()));

            // Set the Main & Quality Graph
             SimpleRdfWrite rdfQualityGraph = SimpleRdfFactory.create(outRdfData, context);
             rdfQualityGraph.setPolicy(AddPolicy.IMMEDIATE);

            // Initialization of the Quality Ontology
            //QualityOntology.init(rdfQualityGraph.getValueFactory(), this.toString());
            
            // Initialize the observation array
            //URI EX_OBSERVATIONS;
            

            // Set the name of the Quality Graph
            URI graphName = rdfQualityGraph.getValueFactory().createURI(QualityOntology.EX + namegraph);

            // Set the name of the two Output Graphs

            rdfQualityGraph.setOutputGraph(graphName.toString());

            // Add Subject, Property and Object to the Quality Graph
            rdfQualityGraph.add(QualityOntology.EX_COMPLETENESS_DIMENSION, QualityOntology.RDF_A_PREDICATE, QualityOntology.DAQ_DIMENSION);
            rdfQualityGraph.add(QualityOntology.EX_COMPLETENESS_DIMENSION, QualityOntology.DAQ_HAS_METRIC, QualityOntology.EX_DPU_NAME);
            rdfQualityGraph.add(QualityOntology.EX_DPU_NAME, QualityOntology.RDF_A_PREDICATE, QualityOntology.DAQ_METRIC);

            //for (int z = 0; z < results.length; z++) {
            	URI EX_OBSERVATIONS = rdfQualityGraph.getValueFactory().createURI(QualityOntology.EX + "obs" + 1);
            	rdfQualityGraph.add(QualityOntology.EX_DPU_NAME, QualityOntology.DAQ_HAS_OBSERVATION, EX_OBSERVATIONS);
            	rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.RDF_A_PREDICATE, QualityOntology.QB_OBSERVATION);

                // Some temporary string
                String blank_node = EX_OBSERVATIONS.stringValue() + "/blank_node";

            	rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DAQ_COMPUTED_ON,
                        rdfQualityGraph.getValueFactory().createURI(blank_node));

            	rdfQualityGraph.add(rdfQualityGraph.getValueFactory().createURI(blank_node),
                        QualityOntology.RDF_A_PREDICATE, QualityOntology.RDF_STATEMENT);

            	rdfQualityGraph.add(rdfQualityGraph.getValueFactory().createURI(blank_node),
                        QualityOntology.RDF_SUBJECT_PREDICATE, rdfQualityGraph.getValueFactory().createURI(classUri));

            	rdfQualityGraph.add(rdfQualityGraph.getValueFactory().createURI(blank_node),
                        QualityOntology.RDF_PREDICATE_PREDICATE, rdfQualityGraph.getValueFactory().createURI(propertyUri));

            	rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DC_DATE,
                        rdfQualityGraph.getValueFactory().createLiteral(date));

            	rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DAQ_VALUE,
                        rdfQualityGraph.getValueFactory().createLiteral(result));

            //}

           
            // Create the Quality Graph
            if (rdfQualityGraph != null) {
                rdfQualityGraph.flushBuffer();
            }

        } catch (OperationFailedException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Operation Failed Exception.", "", e);
        } catch (ParseException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Error during parsing Date.", "", e);
        }
    }

    private Double calculateMean (DPUContext context, String path_1, String path_2) {

        CSVReader csvReader_1 = null;
        CSVReader csvReader_2 = null;

        Double mean = 0.0;

        try {

            // Get the two temp files created above
            csvReader_1 = new CSVReader(new FileReader(path_1));
            csvReader_2 = new CSVReader(new FileReader(path_2));

            double value_1;
            double value_2;

            // Get the values from the CSVs
            value_1 = Integer.parseInt(csvReader_1.readAll().get(1)[0]);
            value_2 = Integer.parseInt(csvReader_2.readAll().get(1)[0]);

            System.out.println(value_1 +" "+ value_2);

            // Calculate the Mean
            mean = value_2/value_1;

            csvReader_1.close();
            csvReader_2.close();

        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "I/0 Failed", "", e);
        }

        return mean;
    }

    private Map<String, URI> getGraphs() throws DataUnitException {

        final Map<String, URI> graphUris = new HashMap<>();

        // Get the input stream
        try (RDFDataUnit.Iteration iter = inRdfData.getIteration()) {
            while (iter.hasNext()) {
                final RDFDataUnit.Entry entry = iter.next();
                // Put in the Graph URI the Entry Name and DataGraph URI
                graphUris.put(entry.getSymbolicName(), entry.getDataGraphURI());
            }
        }

        return graphUris;
    }

    public String toString() {
        String name = this.getClass().getName();
        int index = name.lastIndexOf(".");
        return name.substring(index + 1);
    }*/
}
