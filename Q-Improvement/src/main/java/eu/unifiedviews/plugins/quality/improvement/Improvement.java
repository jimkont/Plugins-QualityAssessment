package eu.unifiedviews.plugins.quality.improvement;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
//import org.openrdf.model.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@DPU.AsQuality
public class Improvement extends AbstractDpu<ImprovementConfig_V1> {

    //private static final Logger LOG = LoggerFactory.getLogger(Improvement.class);

    //public static final String VALIDITY_GRAPH_SYMBOLIC_NAME = "validityQualityGraph";

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFileData;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    //private static ValueFactory valueFactory;

    public Improvement() {
        super(ImprovementVaadinDialog.class, ConfigHistory.noHistory(ImprovementConfig_V1.class));
    }

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
        put("^\\d{8}$", "yyyyMMdd");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
        put("^(((((0[1-9])|(1\\d)|(2[0-8]))\\/((0[1-9])|(1[0-2])))|((31\\/((0[13578])|(1[02])))|((29|30)\\/((0[1,3-9])|(1[0-2])))))\\/((20[0-9][0-9])|(19[0-9][0-9])))|((29\\/02\\/(19|20)(([02468][048])|([13579][26]))))$", "dd/MM/yyyy");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
        put("^\\d{12}$", "yyyyMMddHHmm");
        put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        put("^\\d{14}$", "yyyyMMddHHmmss");
        put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
    }};

    private static final String DATATYPE_DATE_URI = "<http://www.w3.org/2001/XMLSchema#date>";

    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "Improvement.message");

        String dpuDir =  ctx.getExecMasterContext().getDpuContext().getDpuInstanceDirectory();

        FilesDataUnit.Iteration filesInput;
        FilesDataUnit.Entry file;
        String dataset;

        try {

            // Getting input file
            filesInput = inFilesData.getIteration();
            file = filesInput.next();

            if (file.getFileURIString().substring(0,5).equals("file:")) {
                dataset = file.getFileURIString().substring(5);
            } else {
                dataset = file.getFileURIString();
            }

        } catch (DataUnitException e) {
            throw new DPUException(ctx.tr("Improvement.error.dataunit"), e);
        }

        // Get content file from source
        File source = new File(dataset);
        String outputFile = dpuDir + source.getName();
        final File destination = new File(java.net.URI.create(outputFile));
        if (destination.exists()) {
            if (!destination.delete()) {
                throw new DPUException(ctx.tr("Improvement.error.delete"));
            }
        }
        Path fileToValidate = Paths.get(source.getPath());
        String contentFile;
        try {
            contentFile = new String(Files.readAllBytes(fileToValidate));
        } catch (IOException e) {
            throw new DPUException(ctx.tr("Improvement.error.read"), e);
        }

        // Check datatype Date
        contentFile = checkDate(contentFile);

        // Save result to output
        saveResult(destination, contentFile);
    }

    private static String determineDateFormat(String dateString) {
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return DATE_FORMAT_REGEXPS.get(regexp);
            }
        }
        return null;
    }

    private String checkDate(String contentFile) throws DPUException {

        int index = 0;
        while (index != -1) {
            index = contentFile.indexOf(DATATYPE_DATE_URI);
            if (index != -1) {
                int tmp_index = index - 3; // Skip "^^
                String tmp_content = contentFile.substring(0, tmp_index);
                int index_quote = tmp_content.lastIndexOf("\"");
                String tmp_date = contentFile.substring(index_quote + 1, tmp_index);
                String date_format = determineDateFormat(tmp_date);

                if (date_format != null) {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat(date_format);
                        Date date = formatter.parse(tmp_date);
                        formatter.applyPattern("yyyy-MM-dd");
                        String new_date = formatter.format(date);

                        contentFile = contentFile.substring(0,index_quote+1)+new_date+contentFile.substring(tmp_index);
                        contentFile = contentFile.replaceFirst(DATATYPE_DATE_URI, "<DATATYPE_DATE_URI>");

                    } catch (ParseException e) {
                        throw new DPUException(ctx.tr("Improvement.error.parse"), e);
                    }
                } else {
                    throw new DPUException(ctx.tr("Improvement.error.format"));
                }
            }
        }

        return contentFile.replaceAll("<DATATYPE_DATE_URI>", DATATYPE_DATE_URI);
    }

    private void saveResult(final File destination, String content) throws DPUException {

        try {
            if (destination.createNewFile()) {
                PrintWriter out = new PrintWriter(destination);
                out.write(content);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            throw new DPUException(ctx.tr("Improvement.error.write" + e));
        }

        // Add file.
        faultTolerance.execute(new FaultTolerance.Action() {

            @Override
            public void action() throws Exception {
                FilesDataUnitUtils.addFile(outFileData, destination, destination.getAbsolutePath());
            }
        }, "Improvement.error.file.add");
    }
}