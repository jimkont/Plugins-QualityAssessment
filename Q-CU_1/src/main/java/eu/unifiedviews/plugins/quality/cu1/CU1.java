package eu.unifiedviews.plugins.quality.cu1;

import au.com.bytecode.opencsv.CSVWriter;
import cz.cuni.mff.xrg.uv.boost.dpu.addon.impl.SimpleRdfConfigurator;
import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.AddPolicy;
import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.OperationFailedException;
import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.SimpleRdfFactory;
import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.SimpleRdfWrite;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import org.openrdf.model.URI;
import org.openrdf.query.*;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriterFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import au.com.bytecode.opencsv.CSVReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@DPU.AsQuality
public class CU1 extends ConfigurableBase<CU1Config_V1> implements ConfigDialogProvider<CU1Config_V1> {

    private final Logger LOG = LoggerFactory.getLogger(CU1.class);

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @SimpleRdfConfigurator.Configure(dataUnitFieldName = "outRdfData")
    public SimpleRdfWrite rdfMainGraph = null;

    @SimpleRdfConfigurator.Configure(dataUnitFieldName = "outRdfData")
    public SimpleRdfWrite rdfQualityGraph = null;

    public static URI[] EX_OBSERVATIONS;
    
    public CU1() {
        super(CU1Config_V1.class);
    }

    @Override
    public AbstractConfigDialog<CU1Config_V1> getConfigurationDialog() {
        return new CU1VaadinDialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {

        try {

            // Query to extract the subject and the last modified date.
            String query = "SELECT ?s ?o WHERE { ?s <http://purl.org/dc/terms/modified> ?o }";

            // Execute the Query specified above
            String[] resultQuery = this.executeQuery(context, query);
            //String[] resultQuery = this.getResultQuery(context, fileAbsolutePath);
            
            // Get the required Date
            Date now = new Date();
            Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2007-01-01");
            Date lastEdit = new SimpleDateFormat("yyyy-MM-dd").parse(resultQuery[1]);

            double currentTime = now.getTime();
            double lastModificationTime = lastEdit.getTime();
            double startTime = startDate.getTime();

            // Final Currency
            double currency = 1 - ((currentTime - lastModificationTime) / (currentTime - startTime));

            // Create the output CSV file with the result
            //this.createCSV(context, resultQuery);

            this.createOutputGraph(context, "qualityGraph1", resultQuery[0], currency);

        }
        catch (ParseException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Problem during parsing Date.", "", e);
        }
    }

    private String[] executeQuery (DPUContext context, String query) {

        // Set an Array to put the query result
        String[] result = new String[2];

        // Create a temp file, used to evaluate the currency, in the output directory
        final File outFile = new File(java.net.URI.create(context.getDpuInstanceDirectory()+"temp.csv"));

        RepositoryConnection connection = null;

        try (OutputStream outputStream = new FileOutputStream(outFile)) {

            connection = inRdfData.getConnection();

            // Prepare the execution of the query
            final SPARQLResultsCSVWriterFactory writerFactory = new SPARQLResultsCSVWriterFactory();
            final TupleQueryResultWriter resultWriter = writerFactory.getWriter(outputStream);
            TupleQuery querySparql = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

            // Execute the Query
            querySparql.evaluate(resultWriter);

            // Get the temp file created above
            CSVReader csvReader = new CSVReader(new FileReader(outFile.getAbsolutePath()));

            // Get the values from the CSV
            result = csvReader.readAll().get(1);

            csvReader.close();

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

        return result;
    }

    private void createOutputGraph(DPUContext context, String namegraph, String resource, double value) {

        try {

            // Set the Date of the DPU execution
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            Date date = dateFormat.parse(dateFormat.format(new Date()));

            // Set the Main & Quality Graph
             rdfQualityGraph = SimpleRdfFactory.create(outRdfData, context);
             rdfQualityGraph.setPolicy(AddPolicy.BUFFERED);

            // Initialization of the Quality Ontology
            QualityOntology.init(rdfQualityGraph.getValueFactory(), this.toString());
            
            EX_OBSERVATIONS = new URI[1];
            EX_OBSERVATIONS[0] = rdfQualityGraph.getValueFactory().createURI(QualityOntology.EX +"obs"+ 1);
            
            // Set the name of the Quality Graph
            URI graphName = rdfQualityGraph.getValueFactory().createURI(QualityOntology.EX + namegraph);

            // Set the name of the two Output Graphs

            rdfQualityGraph.setOutputGraph(graphName.toString());

            // Add Subject, Property and Object to the Quality Graph
            rdfQualityGraph.add(QualityOntology.EX_TIMELINESS_DIMENSION, QualityOntology.RDF_A_PREDICATE, QualityOntology.DAQ_DIMENSION);
            rdfQualityGraph.add(QualityOntology.EX_TIMELINESS_DIMENSION, QualityOntology.DAQ_HAS_METRIC, QualityOntology.EX_DPU_NAME);
            rdfQualityGraph.add(QualityOntology.EX_DPU_NAME, QualityOntology.RDF_A_PREDICATE, QualityOntology.DAQ_METRIC);
            
            rdfQualityGraph.add(QualityOntology.EX_DPU_NAME, QualityOntology.DAQ_HAS_OBSERVATION, EX_OBSERVATIONS);
            rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.RDF_A_PREDICATE, QualityOntology.QB_OBSERVATION);
            rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DAQ_COMPUTED_ON, rdfQualityGraph.getValueFactory().createURI(resource));
            rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DC_DATE, rdfQualityGraph.getValueFactory().createLiteral(date));
            rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DAQ_VALUE, rdfQualityGraph.getValueFactory().createLiteral(value));

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

    /*private void createCSV (DPUContext context, String[] result) {

        CSVWriter writer;

        try {

            // Add new file to the output variable
            final String outFileUri = outFilesData.addNewFile(config.getFileName());

            // Set a Virtual Path to the file specified in the configuration
            VirtualPathHelpers.setVirtualPath(outFilesData, config.getFileName(), config.getFileName());

            // Create the output file in the working directory (or test directory specified in the test file)
            final File outFile = new File(java.net.URI.create((this.config.getPath() == null) ? outFileUri : this.config.getPath() + this.config.getFileName()));
            writer = new CSVWriter(new FileWriter(outFile),';', '"', '\n');

            // Write the CSV Header
            String [] header = {"subject","metric","value"};
            writer.writeNext(header);

            // Write the CSV Content, every iteration is a property evaluated
            String [] record = {result[0], "currency", ""+ result[1]};
            writer.writeNext(record);

            writer.close();

        } catch (DataUnitException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "", e);
        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "I/0 Failed", "", e);
        }
    }*/
}
