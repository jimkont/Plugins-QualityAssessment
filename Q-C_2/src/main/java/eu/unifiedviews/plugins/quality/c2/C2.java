package eu.unifiedviews.plugins.quality.c2;

import au.com.bytecode.opencsv.CSVWriter;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import org.openrdf.model.URI;
import org.openrdf.query.*;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriterFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;
import au.com.bytecode.opencsv.CSVReader;

@DPU.AsQuality
public class C2 extends ConfigurableBase<C2Config_V1> implements ConfigDialogProvider<C2Config_V1> {

    private final Logger LOG = LoggerFactory.getLogger(C2.class);

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;
    
    @SimpleRdfConfigurator.Configure(dataUnitFieldName = "outRdfData")
    public SimpleRdfWrite rdfQualityGraph = null;

    public C2() {
        super(C2Config_V1.class);
    }

    @Override
    public AbstractConfigDialog<C2Config_V1> getConfigurationDialog() {
        return new C2VaadinDialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {

        // Get configuration parameters        
        ArrayList<String> subject_ = this.config.getSubject();
        ArrayList<String> property_ = this.config.getProperty();

        if ((subject_ == null) && (property_ == null)) {
            LOG.warn("No subject or property has been specified.");
        } else {

            Double [] results = new Double[subject_.size()];
            
            //  It evaluates the completeness, for every subject specified in the DPU Configuration
            for (int i = 0; i < subject_.size(); i++) {

                String key = subject_.get(i);
                String value = property_.get(i);

                String query1 = "";
                String query2 = "";
                
                if (key.trim().length() > 0 && value.trim().length() > 0) {
                    
                    query1 = "SELECT (COUNT(?s) AS ?counter) WHERE { ?s a <" + key + "> . }";
                    query2 = "SELECT (COUNT(?o) AS ?counter) WHERE { ?s a <" + key + "> . " +
                            "OPTIONAl { ?s <" + value + "> ?o } . " +
                            "OPTIONAL { ?s ?p ?blank . ?blank <" + value + "> ?o } }";

                    final File outFile_1;
                    final File outFile_2;

                    try {

                        // Create two temp files, used to evaluate the completeness, in the output directory
                        outFile_1 = new File(java.net.URI.create(outFilesData.getBaseFileURIString()+"counter_1.csv"));
                        outFile_2 = new File(java.net.URI.create(outFilesData.getBaseFileURIString()+"counter_2.csv"));

                        final Map<String, URI> graphs = getGraphs();

                        final DatasetImpl dataset = new DatasetImpl();

                        for (URI graph_1 : graphs.values()) {
                            dataset.addDefaultGraph(graph_1);
                        }

                        // Execute the above two Queries specified above
                        this.executeQuery(context, outFile_1, query1, dataset);
                        this.executeQuery(context, outFile_2, query2, dataset);

                        // Get the result
                        results[i] = this.calculateMean(context, outFile_1.getAbsolutePath(), outFile_2.getAbsolutePath());

                    } catch (DataUnitException ex) {
                        context.sendMessage(DPUContext.MessageType.ERROR, "Problem with DataUnit.", "", ex);
                        return;
                    }
                    
                    // Delete the two temp files
                    outFile_1.delete();
                    outFile_2.delete();
                }
            }

            // Create the RDF output with the result
            this.createOutputGraph(context, "qualityGraph1", subject_, property_, results);
        }
    }

    private void executeQuery (DPUContext context, File outFile, String query, DatasetImpl dataset) {

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
   
    private void createOutputGraph(DPUContext context, String namegraph,  ArrayList<String> subject, ArrayList<String> property, Double[] results) {

        try {

            // Set the Date of the DPU execution
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            Date date = dateFormat.parse(dateFormat.format(new Date()));

            // Set the Main & Quality Graph
             rdfQualityGraph = SimpleRdfFactory.create(outRdfData, context);
             rdfQualityGraph.setPolicy(AddPolicy.BUFFERED);

            // Initialization of the Quality Ontology
            QualityOntology.init(rdfQualityGraph.getValueFactory(), this.toString());
            
            // Initialize the observation array
            URI[] EX_OBSERVATIONS = new URI[results.length];
            

            // Set the name of the Quality Graph
            URI graphName = rdfQualityGraph.getValueFactory().createURI(QualityOntology.EX + namegraph);

            // Set the name of the two Output Graphs

            rdfQualityGraph.setOutputGraph(graphName.toString());

            // Add Subject, Property and Object to the Quality Graph
            rdfQualityGraph.add(QualityOntology.EX_COMPLETENESS_DIMENSION, QualityOntology.RDF_A_PREDICATE, QualityOntology.DAQ_DIMENSION);
            rdfQualityGraph.add(QualityOntology.EX_COMPLETENESS_DIMENSION, QualityOntology.DAQ_HAS_METRIC, QualityOntology.EX_DPU_NAME);
            rdfQualityGraph.add(QualityOntology.EX_DPU_NAME, QualityOntology.RDF_A_PREDICATE, QualityOntology.DAQ_METRIC);
            for (int z = 0; z < results.length; z++) {
            	EX_OBSERVATIONS[z] = rdfQualityGraph.getValueFactory().createURI(QualityOntology.EX +"obs"+ z+1);
            	rdfQualityGraph.add(QualityOntology.EX_DPU_NAME, QualityOntology.DAQ_HAS_OBSERVATION, EX_OBSERVATIONS[z]);
            	rdfQualityGraph.add(EX_OBSERVATIONS[z], QualityOntology.RDF_A_PREDICATE, QualityOntology.QB_OBSERVATION);
            	rdfQualityGraph.add(EX_OBSERVATIONS[z], QualityOntology.DAQ_COMPUTED_ON, rdfQualityGraph.getValueFactory().createURI(blank_node));
            	rdfQualityGraph.getValueFactory().createURI(blank_node), QualityOntology.RDF_A_PREDICATE, QualityOntology.RDF_STATEMENT);
            	rdfQualityGraph.getValueFactory().createURI(blank_node), QualityOntology.RDF_SUBJECT_PREDICATE, rdfQualityGraph.getValueFactory().createURI(subject.get(z)));
            	rdfQualityGraph.getValueFactory().createURI(blank_node), QualityOntology.RDF_PREDICATE_PREDICATE, rdfQualityGraph.getValueFactory().createURI(property.get(z)));
            	rdfQualityGraph.add(EX_OBSERVATIONS[z], QualityOntology.DC_DATE, rdfQualityGraph.getValueFactory().createLiteral(date));
            	rdfQualityGraph.add(EX_OBSERVATIONS[z], QualityOntology.DAQ_VALUE, rdfQualityGraph.getValueFactory().createLiteral(results[z]));

            }

           
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

    public String toString() {
        String name = this.getClass().getName();
        int index = name.lastIndexOf(".");
        return name.substring(index + 1);
    }
    
   /* private void createCSV (DPUContext context, ArrayList<String> subject, ArrayList<String> property, Double[] results) {

        CSVWriter writer = null;

        try {

            // Add new file to the output variable
            final String outFileUri = outFilesData.addNewFile(config.getFileName());
            
            // Set a Virtual Path to the file specified in the configuration
            VirtualPathHelpers.setVirtualPath(outFilesData, config.getFileName(), config.getFileName());

            // Create the output file in the working directory (or test directory specified in the test file)
            final File outFile = new File(java.net.URI.create((this.config.getPath() == null) ? outFileUri : this.config.getPath() + this.config.getFileName()));
            writer = new CSVWriter(new FileWriter(outFile),';', '"', '\n');

            // Write the CSV Header
            String [] header = {"subject","property","quality"};
            writer.writeNext(header);

            // Write the CSV Content, every iteration is a property evaluated
            for (int z = 0; z < results.length; z++) {
                String [] record = {subject.get(z), property.get(z), ""+ results[z]};
                writer.writeNext(record);
            }

            writer.close();

        } catch (DataUnitException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "", e);
        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "I/0 Failed", "", e);
        }
    }*/

    private Double calculateMean (DPUContext context, String path_1, String path_2) {

        CSVReader csvReader_1 = null;
        CSVReader csvReader_2 = null;

        Double mean = 0.0;

        try {

            // Get the two temp files created above
            csvReader_1 = new CSVReader(new FileReader(path_1));
            csvReader_2 = new CSVReader(new FileReader(path_2));

            double value_1 = 0;
            double value_2 = 0;

            // Get the values from the CSVs
            value_1 = Integer.parseInt(csvReader_1.readAll().get(1)[0]);
            value_2 = Integer.parseInt(csvReader_2.readAll().get(1)[0]);

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
}
