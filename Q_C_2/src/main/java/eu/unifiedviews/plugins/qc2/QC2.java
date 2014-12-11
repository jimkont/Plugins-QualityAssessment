package eu.unifiedviews.plugins.qc2;

import au.com.bytecode.opencsv.CSVWriter;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU.AsTransformer;
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

@AsTransformer
public class QC2 extends ConfigurableBase<QC2Config_V1> implements ConfigDialogProvider<QC2Config_V1> {

    private final Logger LOG = LoggerFactory.getLogger(QC2.class);

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    public QC2() {
        super(QC2Config_V1.class);
    }

    @Override
    public AbstractConfigDialog<QC2Config_V1> getConfigurationDialog() {
        return new QC2VaadinDialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {

        ArrayList<String> subject_ = this.config.getSubject();
        ArrayList<String> property_ = this.config.getProperty();

        if ((subject_ == null) && (property_ == null)) {
            LOG.warn("No subject or property has been specified.");
        } else {

            Double [] results = new Double[subject_.size()];

            for (int i = 0; i < subject_.size(); i++) {

                String key = subject_.get(i);
                String value = property_.get(i);

                String query1 = "";
                String query2 = "";

                if (key.trim().length() > 0 && value.trim().length() > 0) {

                    query1 = "SELECT (COUNT(?s) AS ?counter) WHERE { ?s a <" + key + "> . }";
                    query2 = "SELECT (COUNT(?s) AS ?counter) WHERE { ?s a <" + key + "> . ?s <" + value + "> ?o . }";

                    final File outFile_1;
                    final File outFile_2;

                    try {

                        outFile_1 = new File(java.net.URI.create(outFilesData.getBaseFileURIString()+"out_1.csv"));
                        outFile_2 = new File(java.net.URI.create(outFilesData.getBaseFileURIString()+"out_2.csv"));

                        final Map<String, URI> graphs = getGraphs();

                        final DatasetImpl dataset = new DatasetImpl();

                        for (URI graph_1 : graphs.values()) {
                            dataset.addDefaultGraph(graph_1);
                        }

                        this.executeQuery(context, inRdfData, outFile_1, query1, dataset);
                        this.executeQuery(context, inRdfData, outFile_2, query2, dataset);

                        results[i] = this.executeMean(context, outFile_1.getAbsolutePath(), outFile_2.getAbsolutePath());

                    } catch (DataUnitException ex) {
                        context.sendMessage(DPUContext.MessageType.ERROR, "Problem with DataUnit.", "", ex);
                        return;
                    }

                    outFile_1.delete();
                    outFile_2.delete();
                }
            }

            this.executeCSV(context, subject_, property_, results);
        }
    }

    private Map<String, URI> getGraphs() throws DataUnitException {
        final Map<String, URI> graphUris = new HashMap<>();
        try (RDFDataUnit.Iteration iter = inRdfData.getIteration()) {
            while (iter.hasNext()) {
                final RDFDataUnit.Entry entry = iter.next();
                graphUris.put(entry.getSymbolicName(), entry.getDataGraphURI());
            }
        }
        return graphUris;
    }

    private void executeQuery (DPUContext context, RDFDataUnit tmpInRdfData, File outFile, String query, DatasetImpl dataset) {

        RepositoryConnection connection = null;

        try (OutputStream outputStream = new FileOutputStream(outFile)) {

            connection = tmpInRdfData.getConnection();

            final SPARQLResultsCSVWriterFactory writerFactory = new SPARQLResultsCSVWriterFactory();
            final TupleQueryResultWriter resultWriter = writerFactory.getWriter(outputStream);
            TupleQuery querySparql = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);

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

    private void executeCSV (DPUContext context, ArrayList<String> subject, ArrayList<String> property, Double[] results) {

        CSVWriter writer = null;

        try {

            final String outFileUri = outFilesData.addNewFile(config.getFileName());
            VirtualPathHelpers.setVirtualPath(outFilesData, config.getFileName(), config.getFileName());

            final File outFile = new File(java.net.URI.create(outFileUri));
            writer = new CSVWriter(new FileWriter(outFile),';', '"', '\n');

        } catch (DataUnitException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "", e);
        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "I/0 Failed", "", e);
        }

        String [] header = {"subject","property","quality"};
        writer.writeNext(header);

        for (int z = 0; z < results.length; z++) {
            String [] record = {subject.get(z), property.get(z), ""+ results[z]};
            writer.writeNext(record);
        }

        try {
            writer.close();
        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "I/0 Failed", "Error Closing File", e);
        }

    }

    private Double executeMean (DPUContext context, String path_1, String path_2) {

        CSVReader csvReader_1 = null;
        CSVReader csvReader_2 = null;

        Double media = 0.0;

        try {

            csvReader_1 = new CSVReader(new FileReader(path_1));
            csvReader_2 = new CSVReader(new FileReader(path_2));

            String[] row_1 = null;
            String[] row_2 = null;
            int x = 0;
            int y = 0;

            double value_1 = 0;
            double value_2 = 0;


            while((row_1 = csvReader_1.readNext()) != null) {
                if (x == 1) {
                    value_1 = Integer.parseInt(row_1[0]);
                }
                x++;
            }

            while((row_2 = csvReader_2.readNext()) != null) {
                if (y == 1) {
                    value_2 = Integer.parseInt(row_2[0]);
                }
                y++;
            }

            csvReader_1.close();
            csvReader_2.close();

            media = value_2/value_1;

        } catch (IOException e) {
            context.sendMessage(DPUContext.MessageType.ERROR, "I/0 Failed", "", e);
        }

        return media;
    }
}
