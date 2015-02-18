package eu.unifiedviews.plugins.quality.acc1;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dpu.DPU.AsQuality;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.openrdf.model.URI;
import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.AddPolicy;
import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.OperationFailedException;
import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.SimpleRdfFactory;
import cz.cuni.mff.xrg.uv.rdf.utils.dataunit.rdf.simple.SimpleRdfWrite;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;

@AsQuality
public class ACC1 extends ConfigurableBase<ACC1Config_V1> implements ConfigDialogProvider<ACC1Config_V1> {

    private final Logger LOG = LoggerFactory.getLogger(ACC1.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit filesInput;

    //@DataUnit.AsOutput(name = "output")
    //public WritableFilesDataUnit outFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    public ACC1() {
        super(ACC1Config_V1.class);
    }

    @Override
    public AbstractConfigDialog<ACC1Config_V1> getConfigurationDialog() {
        return new ACC1VaadinDialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {

        // Set the virtual path to the input file
        VirtualPathHelpers.create(filesInput);
        
        final Iterator<FilesDataUnit.Entry> filesIteration;

        try {
            filesIteration = FilesHelper.getFiles(filesInput).iterator();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "Can't get file iterator.", ex);
            return;
        }

        if (filesIteration.hasNext()) {

            // Get the input file given to the DPU
            FilesDataUnit.Entry file = filesIteration.next();

            try {

                // Set the name (with extention) of the destination file for the conversion
                String outputUri = (new File(context.getWorkingDir().toString() +"input.nt")).toURI().toString();

                // Convert the RDF file to the N-X format
                this.rdfToNx(context, file.getFileURIString(), outputUri);
                
                // Get the JSON of the POST Request
                String json = this.executeRequest(context, config.getV_host(), config.getV_port(), config.getV_path(), outputUri.substring(5));

                JSONParser jsonPrs = new JSONParser();
                JSONObject jsonObj = (JSONObject) jsonPrs.parse(json);

                // Get the Final Status of the Request
                String status = (String) jsonObj.get("status");

                if (status.equals("okay")) {
                    // Get the Results from the JSON object
                    JSONArray v_results = (JSONArray) jsonObj.get("results");

                    // Create the output File
                    //this.createCSV(context, v_results); 
                    this.createOutputGraph(context, v_results);
                }

            } catch (Exception ex) {
                context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "", ex);
            }
        }
    }

    private void rdfToNx (DPUContext context, String sourceUri, String destinationUri) {

        final File inFile;
        final File outFile;

        // Get paths as non URI. This can be also done by conversion into File and then back to string.
        final String source = sourceUri.substring(5);
        final String destination = destinationUri.substring(5);

        try {

            // Get the 
            inFile = new File(java.net.URI.create(sourceUri));
            outFile = new File(java.net.URI.create(destinationUri));
            InputStream in = new FileInputStream(inFile);
            OutputStream out = new FileOutputStream(outFile);

            // Define the Source Format and the Destination Format in relation to the file extension
            RDFFormat sourceFormat = RDFParserRegistry.getInstance().getFileFormatForFileName(source, RDFFormat.RDFXML);
            RDFFormat destinationFormat = RDFParserRegistry.getInstance().getFileFormatForFileName(destination, RDFFormat.RDFXML);

            // Define Parser and Writer for the conversion
            RDFParser parser = RDFParserRegistry.getInstance().get(sourceFormat).getParser();
            RDFWriter writer = RDFWriterRegistry.getInstance().get(destinationFormat).getWriter(out);

            // Configure the Parser to avoid some type of errors (Every BasicParserSettings is exclused)
            parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_LANGUAGE_TAGS);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_RELATIVE_URIS);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.NORMALIZE_DATATYPE_VALUES);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS);
            
            parser.setRDFHandler(writer);
            
            // Parse (and Convert) the RDF File
            parser.parse(in, "unknown:namespace");
            
            out.flush();

            in.close();
            out.close();

        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "IO Exeption 1", "Error on File", e);
        } catch (RDFParseException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "RDFParser Exeption", "", e);
        } catch (RDFHandlerException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "RDFHandler Exeption", "", e);
        }
    }

    private String executeRequest(DPUContext context, String v_host, int v_port, String v_path, String f_path) throws Exception {

        // Content to Validate (N-X Input)
        String content = "";

        // Get the file with the N-X Input
        File file = new File(f_path);
        FileInputStream contentFile = null;

        try {

            // Put the file contenct in to the variable
            contentFile = new FileInputStream(file);

            StringBuilder builder = new StringBuilder();
            int line;

            while ((line = contentFile.read()) != -1){
                builder.append((char) line);
            }

            content = builder.toString();

        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "IO Exeption 2", "Error on File", e);
        } finally {
            try {
                if (contentFile != null) {
                    contentFile.close();
                }
            } catch (IOException e) {
                context.sendMessage(DPUContext.MessageType.ERROR, "IO Exeption 3", "Error on File", e);
            }
        }

        // Create Url with Encoded Content
        String encodedContent = java.net.URLEncoder.encode(content,"UTF-8");
        
        // Create Url 
        String url = v_host + ":"+ v_port +"/"+ v_path +"/alerts";
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        // Create the Request
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setDoOutput(true);
        String parameters = "fulldata="+ encodedContent +"&format=json";

        // Execute the Query
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();
        
        // Get the Response
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        // Write the Response in to the file
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        // Return the Decoded requested Content
        return java.net.URLDecoder.decode(response.toString(),"UTF-8");
    }

    private void createOutputGraph(DPUContext context, JSONArray results) {

        try {

            // Set the Date of the DPU execution
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            Date date = dateFormat.parse(dateFormat.format(new Date()));

            // Set the Main & Quality Graph
             SimpleRdfWrite rdfQualityGraph = SimpleRdfFactory.create(outRdfData, context);
             rdfQualityGraph.setPolicy(AddPolicy.BUFFERED);

            // Initialization of the Quality Ontology
            QualityOntology.init(rdfQualityGraph.getValueFactory(), this.toString());
            
            // Set the name of the Quality Graph
            URI graphName = rdfQualityGraph.getValueFactory().createURI(QualityOntology.EX + "QualityGraph");

            // Set the name of the two Output Graphs

            rdfQualityGraph.setOutputGraph(graphName.toString());

            // Add Subject, Property and Object to the Quality Graph
            rdfQualityGraph.add(QualityOntology.EX_ACCURACY_DIMENSION, QualityOntology.RDF_A_PREDICATE, QualityOntology.DAQ_DIMENSION);
            rdfQualityGraph.add(QualityOntology.EX_ACCURACY_DIMENSION, QualityOntology.DAQ_HAS_METRIC, QualityOntology.EX_DPU_NAME);
            rdfQualityGraph.add(QualityOntology.EX_DPU_NAME, QualityOntology.RDF_A_PREDICATE, QualityOntology.DAQ_METRIC);

            Iterator i = results.iterator();
            int z=0;
            // Write the CSV Content, every iteration is a error/warning found
            while (i.hasNext()) {
            	
            	URI EX_OBSERVATIONS = rdfQualityGraph.getValueFactory().createURI(QualityOntology.EX +"obs"+ z+1);
            	rdfQualityGraph.add(QualityOntology.EX_DPU_NAME, QualityOntology.DAQ_HAS_OBSERVATION, EX_OBSERVATIONS);
            	rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.RDF_A_PREDICATE, QualityOntology.QB_OBSERVATION);
            	rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DC_DATE, rdfQualityGraph.getValueFactory().createLiteral(date));
            	
                JSONObject innerObj = (JSONObject) i.next();

                // Get Type, Code and Message of the error
                String type = innerObj.get("type").toString();
                int code = Integer.parseInt(innerObj.get("code").toString());
                String msg = innerObj.get("msg").toString();
                msg = msg.replaceAll("&lt;", "<");
                msg = msg.replaceAll("&gt;", ">");
                msg = msg.replaceAll("&quot;", "'");
                rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DAQ_HAS_SEVERITY, rdfQualityGraph.getValueFactory().createLiteral(type));
                rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DCTERMS_PROBLEMDESCRIPTION, rdfQualityGraph.getValueFactory().createURI(msg));
                
            	/*rdfQualityGraph.add(EX_OBSERVATIONS, QualityOntology.DAQ_COMPUTED_ON, rdfQualityGraph.getValueFactory().createURI(blank_node));
            	rdfQualityGraph.getValueFactory().createURI(blank_node), QualityOntology.RDF_A_PREDICATE, QualityOntology.RDF_STATEMENT);
            	rdfQualityGraph.getValueFactory().createURI(blank_node), QualityOntology.RDF_SUBJECT_PREDICATE, rdfQualityGraph.getValueFactory().createURI(subject.get(z)));
            	rdfQualityGraph.getValueFactory().createURI(blank_node), QualityOntology.RDF_PREDICATE_PREDICATE, rdfQualityGraph.getValueFactory().createURI(property.get(z)));*/
                
                z=+1;
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
    
    /*private void createCSV (DPUContext context, JSONArray results) {

        CSVWriter writer = null;

        try {

            // Add new file to the output variable
            final String outFileUri = outFilesData.addNewFile(config.getFileName());

            // Set a Virtual Path to the file specified in the configuration
            VirtualPathHelpers.setVirtualPath(outFilesData, config.getFileName(), config.getFileName());

            // Create the output file in the working directory (or test directory specified in the test file)
            final File outFile = new File(java.net.URI.create((this.config.getPath() == null) ? outFileUri : this.config.getPath() + this.config.getFileName()));
            writer = new CSVWriter(new FileWriter(outFile, false));

            // Write the CSV Header
            String [] header = {"type","code","message"};
            writer.writeNext(header);

            Iterator i = results.iterator();

            // Write the CSV Content, every iteration is a error/warning found
            while (i.hasNext()) {

                JSONObject innerObj = (JSONObject) i.next();

                // Get Type, Code and Message of the error
                String type = innerObj.get("type").toString();
                int code = Integer.parseInt(innerObj.get("code").toString());
                String msg = innerObj.get("msg").toString();
                msg = msg.replaceAll("&lt;", "<");
                msg = msg.replaceAll("&gt;", ">");
                msg = msg.replaceAll("&quot;", "'");

                String [] record = {type, ""+ code, msg};
                writer.writeNext(record);
            }

            writer.close();

        } catch (DataUnitException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "", e);
        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "I/0 Failed", "", e);
        }
    }*/
}
