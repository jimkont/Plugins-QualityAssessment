package eu.unifiedviews.plugins.quality.acc1;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryConnection;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.rdf.simple.WritableSimpleRdf;
import eu.unifiedviews.helpers.dpu.rdf.EntityBuilder;
import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;

@DPU.AsQuality
public class ACC1 extends AbstractDpu<ACC1Config_V1> {

    public static final String ACCURACY_GRAPH_SYMBOLIC_NAME = "accuracyQualityGraph";

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @ExtensionInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf report;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    private static ValueFactory valueFactory;

    private RDFFormat inputRdfFormat = RDFFormat.TURTLE;
    private RDFFormat outputRdfFormat = RDFFormat.NTRIPLES;

    public ACC1() {
        super(ACC1VaadinDialog.class, ConfigHistory.noHistory(ACC1Config_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        valueFactory = report.getValueFactory();

        final String inputFileName = "inputGraph." + inputRdfFormat.getDefaultFileExtension();
        final String outputFileName = "outputGraph." + outputRdfFormat.getDefaultFileExtension();
        final File inFile = new File(java.net.URI.create("file:"+ ctx.getExecMasterContext().getDpuContext().getWorkingDir() + inputFileName));
        final File outFile = new File(java.net.URI.create("file:"+ ctx.getExecMasterContext().getDpuContext().getWorkingDir() + outputFileName));

        final List<RDFDataUnit.Entry> graphs = FaultToleranceUtils.getEntries(faultTolerance, inRdfData, RDFDataUnit.Entry.class);

        // Save the RDF graph in the input temp file
        exportGraph(graphs, inFile);

        // Convert the RDF file to the NTriples format
        rdfToNt(inFile, outFile);

        JSONArray results;

        try {

            // Get the JSON of the POST Request
            String json = executeRequest(config.getV_host(), config.getV_port(), config.getV_path(), outFile.toURI().toString().substring(5));

            JSONParser jsonPrs = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonPrs.parse(json);

            // Get the Final Status of the Request
            String status = (String) jsonObj.get("status");

            if (status.equals("okay")) {

                // Get the Results from the JSON object
                results = (JSONArray) jsonObj.get("results");

            } else {
                throw ContextUtils.dpuException(ctx, "ACC1.error.json");
            }

        } catch (Exception ex) {
            throw ContextUtils.dpuException(ctx, ex, "ACC1.error.request");
        }

        ArrayList<String> note = new ArrayList<>();
        ArrayList<String> warning = new ArrayList<>();
        ArrayList<String> error = new ArrayList<>();

        Iterator i = results.iterator();
        while (i.hasNext()) {

            JSONObject innerObj = (JSONObject) i.next();

            // Get Type, Code and Message of the error
            String type = innerObj.get("type").toString();
            //int code = Integer.parseInt(innerObj.get("code").toString());
            String msg = innerObj.get("msg").toString();
            msg = msg.replaceAll("&lt;", "<");
            msg = msg.replaceAll("&gt;", ">");
            msg = msg.replaceAll("&quot;", "'");

            if (type.equals("note")) note.add(msg);
            if (type.equals("warning")) warning.add(msg);
            if (type.equals("error")) error.add(msg);
        }

        // Set output.
        final RDFDataUnit.Entry output = faultTolerance.execute(new FaultTolerance.ActionReturn<RDFDataUnit.Entry>() {

            @Override
            public RDFDataUnit.Entry action() throws Exception {
                return RdfDataUnitUtils.addGraph(outRdfData, ACCURACY_GRAPH_SYMBOLIC_NAME);
            }
        });
        report.setOutput(output);

        // EX_ACCURACY_DIMENSION entity.
        final EntityBuilder dpuEntity = new EntityBuilder(QualityOntology.EX_ACCURACY_DIMENSION, valueFactory);
        dpuEntity
                .property(RDF.TYPE, QualityOntology.DAQ_METRIC);

        // EX_DPU_NAME entity.
        final EntityBuilder reportEntity = new EntityBuilder(ACC1Vocabulary.EX_DPU_NAME, valueFactory);
        reportEntity
                .property(RDF.TYPE, QualityOntology.DAQ_DIMENSION)
                .property(QualityOntology.DAQ_HAS_METRIC, dpuEntity);

        // EX_OBSERVATIONS entity.
        final EntityBuilder observationEntity_note = createObservation("Note", note, 1);
        final EntityBuilder observationEntity_warning = createObservation("Warning", warning, 2);
        final EntityBuilder observationEntity_error = createObservation("Error", error, 3);

        // Add binding from EX_ACCURACY_DIMENSION
        dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity_note);
        dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity_warning);
        dpuEntity.property(QualityOntology.DAQ_HAS_OBSERVATION, observationEntity_error);

        // Add entities to output graph.
        report.add(reportEntity.asStatements());
        report.add(dpuEntity.asStatements());
        report.add(observationEntity_note.asStatements());
        report.add(observationEntity_warning.asStatements());
        report.add(observationEntity_error.asStatements());

        if (note.size() != 0) {
            for (int x = 0; x < note.size(); x++) {
                final EntityBuilder observationEntity_note_bnode = createObservationBNode(note.get(x), 1, x+1);
                report.add(observationEntity_note_bnode.asStatements());
            }

        }

        if (warning.size() != 0) {
            for (int y = 0; y < warning.size(); y++) {
                final EntityBuilder observationEntity_warning_bnode = createObservationBNode(warning.get(y), 2, y+1);
                report.add(observationEntity_warning_bnode.asStatements());
            }
        }

        if (error.size() != 0) {
            for (int z = 0; z < note.size(); z++) {
                final EntityBuilder observationEntity_error_bnode = createObservationBNode(error.get(z), 3, z+1);
                report.add(observationEntity_error_bnode.asStatements());
            }
        }
    }

    private void exportGraph(final List<RDFDataUnit.Entry> sources, File exportFile) throws DPUException {

        // Prepare inputs.
        final URI[] sourceUris = faultTolerance.execute(new FaultTolerance.ActionReturn<URI[]>() {

            @Override
            public URI[] action() throws Exception {
                return RdfDataUnitUtils.asGraphs(sources);
            }

        });

        try (FileOutputStream outStream = new FileOutputStream(exportFile);
             OutputStreamWriter outWriter = new OutputStreamWriter(outStream, Charset.forName("UTF-8"))
        ) {

            faultTolerance.execute(inRdfData, new FaultTolerance.ConnectionAction() {

                @Override
                public void action(RepositoryConnection connection) throws Exception {
                    RDFWriter writer = Rio.createWriter(outputRdfFormat, outWriter);
                    connection.export(writer, sourceUris);
                }
            });

        } catch (IOException ex) {
        throw ContextUtils.dpuException(ctx, ex, "ACC1.error.output");
        }
    }

    private void rdfToNt (File inFile, File outFile) throws DPUException {

        // Get paths as non URI. This can be also done by conversion into File and then back to string.
        final String source = inFile.getAbsolutePath();
        final String destination = outFile.getAbsolutePath();

        try {

            // Get the files
            InputStream in = new FileInputStream(inFile);
            OutputStream out = new FileOutputStream(outFile);

            // Define the Source Format and the Destination Format in relation to the file extension
            RDFFormat sourceFormat = RDFParserRegistry.getInstance().getFileFormatForFileName(source, RDFFormat.RDFXML);
            RDFFormat destinationFormat = RDFParserRegistry.getInstance().getFileFormatForFileName(destination, RDFFormat.RDFXML);

            // Define Parser and Writer for the conversion
            RDFParser parser = RDFParserRegistry.getInstance().get(sourceFormat).getParser();
            RDFWriter writer = RDFWriterRegistry.getInstance().get(destinationFormat).getWriter(out);

            // Configure the Parser to avoid some type of errors (Every BasicParserSettings is excluded)
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

        } catch (IOException ex) {
            throw ContextUtils.dpuException(ctx, ex, "ACC1.error.file");
        } catch (RDFParseException ex) {
            throw ContextUtils.dpuException(ctx, ex, "ACC1.error.parsing");
        } catch (RDFHandlerException ex) {
            throw ContextUtils.dpuException(ctx, ex, "ACC1.error.parsing");
        }
    }

    private String executeRequest(String v_host, int v_port, String v_path, String f_path) throws Exception {

        // Get the file with the NTriples Input
        File file = new File(f_path);
        FileInputStream contentFile = null;

        // Put the file content in to the variable
        contentFile = new FileInputStream(file);

        StringBuilder builder = new StringBuilder();
        int line;

        while ((line = contentFile.read()) != -1){
            builder.append((char) line);
        }

        String content = builder.toString();

        if (contentFile != null) contentFile.close();

        // Create encoded Content
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

        // Execute the Request
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

    /**
     * Creates observation for entity.
     *
     * @param values
     * @param observationIndex
     * @return EntityBuilder
     * @throws DPUException
     */
    private EntityBuilder createObservation(String type, ArrayList<String> values, int observationIndex) throws DPUException {

        String obs = String.format(ACC1Vocabulary.EX_OBSERVATIONS, observationIndex);

        final URI ERROR;

        if (type.equals("Note")) {
            ERROR = QualityOntology.EX_ACCURACY_NOTE;
        } else if (type.equals("Warning")) {
            ERROR = QualityOntology.EX_ACCURACY_WARNING;
        } else {
            ERROR = QualityOntology.EX_ACCURACY_ERROR;
        }

        final EntityBuilder observationEntity = new EntityBuilder(valueFactory.createURI(obs), valueFactory);

        // Prepare variables.
        final SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date reportDate;
        try {
            reportDate = reportDateFormat.parse(reportDateFormat.format(new Date()));
        } catch (ParseException ex) {
        throw new DPUException(ctx.tr("ACC1.error.date.parse.failed"), ex);
        }

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, QualityOntology.QB_OBSERVATION)
                .property(RDF.TYPE, ERROR);

        if (values.size() != 0) {
            for (int i = 0; i < values.size(); i++) {
                String obs_bnode = obs +"/bnode_"+ (i+1);
                observationEntity
                        .property(QualityOntology.DAQ_COMPUTED_ON, valueFactory.createURI(obs_bnode));
            }
        } else {
            observationEntity
                    .property(RDFS.COMMENT, valueFactory.createLiteral("No " + type + " found."));
        }

        observationEntity
                .property(DC.DATE, valueFactory.createLiteral(reportDate));

        return observationEntity;
    }

    /**
     * Creates observation for entity.
     *
     * @param message
     * @param observationIndex
     * @param bnodeIndex
     * @return EntityBuilder
     * @throws DPUException
     */
    private EntityBuilder createObservationBNode(String message, int observationIndex, int bnodeIndex) throws DPUException {

        String obs = String.format(ACC1Vocabulary.EX_OBSERVATIONS, observationIndex) +"/bnode_"+ bnodeIndex;
        final EntityBuilder observationEntity = new EntityBuilder(valueFactory.createURI(obs), valueFactory);

        // Set the observation.
        observationEntity
                .property(DCTERMS.DESCRIPTION, valueFactory.createLiteral(message));

        return observationEntity;
    }
}
