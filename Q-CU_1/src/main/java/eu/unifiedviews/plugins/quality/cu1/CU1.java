package eu.unifiedviews.plugins.quality.cu1;

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
    public WritableFilesDataUnit outFilesData;

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

            // Query to extract the last modified date.
            String query = "SELECT ?s ?o WHERE { ?s <http://purl.org/dc/terms/modified> ?o }";

            // Create a temp file, used to evaluate the currency, in the output directory
            final File outFile = new File(java.net.URI.create(outFilesData.getBaseFileURIString()+"temp.csv"));

            // Execute the Query specified above
            this.executeQuery(context, outFile, query);

            String[] resultQuery = this.getLastEditDate(context, outFile.getAbsolutePath());
            
            // Get the required Date
            Date today = new Date();
            Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2007-01-01");
            Date lastEdit = new SimpleDateFormat("yyyy-MM-dd").parse(resultQuery[1]);

            double currentTime = today.getTime();
            double lastModificationTime = lastEdit.getTime();
            double startTime = startDate.getTime();

            // Final Currency
            double currency = 1 - ((currentTime - lastModificationTime) / (currentTime - startTime));
            
            resultQuery[1] = ""+ currency;

            // Create the output CSV file with the result
            this.createCSV(context, resultQuery);

        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Problem with DataUnit.", "", ex);
        }
        catch (ParseException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Problem during parsing Date.", "", ex);
        }
    }

    private void executeQuery (DPUContext context, File outFile, String query) {

        RepositoryConnection connection = null;

        try (OutputStream outputStream = new FileOutputStream(outFile)) {

            connection = inRdfData.getConnection();

            // Prepare the execution of the query
            final SPARQLResultsCSVWriterFactory writerFactory = new SPARQLResultsCSVWriterFactory();
            final TupleQueryResultWriter resultWriter = writerFactory.getWriter(outputStream);
            TupleQuery querySparql = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

            // Execute the Query
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

    private void createCSV (DPUContext context, String[] result) {

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
    }

    private String[] getLastEditDate (DPUContext context, String path) {

        CSVReader csvReader;
        
        String[] result = new String[2];

        try {

            // Get the two temp files created above
            csvReader = new CSVReader(new FileReader(path));

            // Get the values from the CSV
            result = csvReader.readAll().get(1);

            csvReader.close();

        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "I/0 Failed", "", e);
        }

        return result;
    }
}
