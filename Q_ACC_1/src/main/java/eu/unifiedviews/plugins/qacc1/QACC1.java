package eu.unifiedviews.plugins.qacc1;

import au.com.bytecode.opencsv.CSVWriter;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU.AsTransformer;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
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
import java.util.*;

@AsTransformer
public class QACC1 extends ConfigurableBase<QACC1Config_V1> implements ConfigDialogProvider<QACC1Config_V1> {

    private final Logger LOG = LoggerFactory.getLogger(QACC1.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    public QACC1() {
        super(QACC1Config_V1.class);
    }

    @Override
    public AbstractConfigDialog<QACC1Config_V1> getConfigurationDialog() {
        return new QACC1VaadinDialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {

        VirtualPathHelpers.create(filesInput);
        final Iterator<FilesDataUnit.Entry> filesIteration;

        try {
            filesIteration = FilesHelper.getFiles(filesInput).iterator();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "Can't get file iterator.", ex);
            return;
        }

        if (filesIteration.hasNext()) {

            FilesDataUnit.Entry file = filesIteration.next();

            try {

                String outputUri = outFilesData.getBaseFileURIString()+"input.nt";

                this.executeConv(context, file.getFileURIString().substring(5), outputUri.substring(5));
                String json = this.executeRequest(context, config.getV_host(), config.getV_port(), config.getV_path(), outputUri.substring(5));

                JSONParser jsonPrs = new JSONParser();
                JSONObject jsonObj = (JSONObject) jsonPrs.parse(json);

                // Get the Final Status of the Request
                String status = (String) jsonObj.get("status");

                if (status.equals("okay")) {
                    // Get the Results from the JSON object
                    JSONArray v_results = (JSONArray) jsonObj.get("results");

                    this.executeCSV(context, v_results);
                }

            } catch (Exception ex) {
                context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "", ex);
            }
        }
    }

    private void executeConv(DPUContext context, String source, String destination) {

        final File inFile;
        final File outFile;

        try {

            inFile = new File(source);
            outFile = new File(java.net.URI.create("file:"+ destination));

            InputStream in = new FileInputStream(inFile);
            OutputStream out = new FileOutputStream(outFile);

            RDFFormat sourceFormat = RDFParserRegistry.getInstance().getFileFormatForFileName(source, RDFFormat.RDFXML);
            RDFFormat destinationFormat = RDFParserRegistry.getInstance().getFileFormatForFileName(destination, RDFFormat.RDFXML);

            RDFParser parser = RDFParserRegistry.getInstance().get(sourceFormat).getParser();
            RDFWriter writer = RDFWriterRegistry.getInstance().get(destinationFormat).getWriter(out);

            parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_LANGUAGE_TAGS);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_RELATIVE_URIS);

            parser.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES);

            parser.getParserConfig().addNonFatalError(BasicParserSettings.NORMALIZE_DATATYPE_VALUES);
            parser.getParserConfig().addNonFatalError(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS);

            parser.setRDFHandler(writer);
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

        File file = new File(f_path);
        FileInputStream contentFile = null;

        try {

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
        String url = v_host + ":"+ v_port +"/"+ v_path +"/alerts";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        String parameters = "fulldata="+ encodedContent +"&format=json";

        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return java.net.URLDecoder.decode(response.toString(),"UTF-8");
    }

    private void executeCSV (DPUContext context, JSONArray results) {

        CSVWriter writer = null;

        try {

            final String outFileUri = outFilesData.addNewFile(config.getFileName());
            VirtualPathHelpers.setVirtualPath(outFilesData, config.getFileName(), config.getFileName());

            final File outFile = new File(java.net.URI.create((this.config.getPath() == null) ? outFileUri : this.config.getPath() + this.config.getFileName()));
            writer = new CSVWriter(new FileWriter(outFile, false));

            String [] header = {"type","code","message"};
            writer.writeNext(header);

            Iterator i = results.iterator();

            while (i.hasNext()) {

                JSONObject innerObj = (JSONObject) i.next();

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
    }
}
