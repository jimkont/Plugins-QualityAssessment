package eu.unifiedviews.plugins.quality.rdfunit;

import com.opencsv.CSVWriter;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.helpers.dpu.extension.rdf.simple.WritableSimpleRdf;
import eu.unifiedviews.helpers.dpu.rdf.EntityBuilder;
import org.aksw.rdfunit.tests.results.DatasetOverviewResults;
import org.openrdf.model.*;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

@DPU.AsQuality
public class RDFUnit extends AbstractDpu<RDFUnitConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(RDFUnit.class);

    public static final String VALIDITY_GRAPH_SYMBOLIC_NAME = "validityQualityGraph";

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableRDFDataUnit outRdfData;

    @ExtensionInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf report;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    private static ValueFactory valueFactory;

	public RDFUnit() {
		super(RDFUnitVaadinDialog.class, ConfigHistory.noHistory(RDFUnitConfig_V1.class));
	}
		
    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "RDFUnit.message");

        valueFactory = report.getValueFactory();

        String dpuDir =  ctx.getExecMasterContext().getDpuContext().getDpuInstanceDirectory();
        String tempFile = dpuDir +"ResultRDFUnit.ttl";

        ArrayList<String> prefix = config.getPrefix();
        ArrayList<String> uri = config.getUri();
        ArrayList<String> url = config.getUrl();

        FilesDataUnit.Iteration filesInput;
        FilesDataUnit.Entry file;
        String dataset;
        String schema;

        try {

            // Getting input file
            filesInput = inFilesData.getIteration();
            file = filesInput.next();

            if (file.getFileURIString().substring(0,5).equals("file:")) {
                dataset = file.getFileURIString().substring(5);
            } else {
                dataset = file.getFileURIString();
            }

            // Generate Local Schema with configuration values
            schema = createCSVSchema(dpuDir, prefix, uri, url);

        } catch (DataUnitException | IOException e) {
            throw new DPUException(ctx.tr("RDFUnit.error.dataunit"), e);
        }

        // Initialize and Execute the RDFUnit Tool
        RDFUnitValidation dataValidator = new RDFUnitValidation(dpuDir, dataset, schema, ctx);
        String result = dataValidator.validate();

        DatasetOverviewResults testOverviewResults = dataValidator.getOverviewResults();

        // Create a temp file with the result of rdfunit
        File output = new File(tempFile);
        try {

            output.getParentFile().mkdirs();
            if (output.exists()) output.delete();

            if (output.createNewFile()) {
                FileWriter fw = new FileWriter(tempFile);
                fw.write(result);
                fw.flush();
                fw.close();
            }

        } catch (IOException e) {
            throw new DPUException(ctx.tr("RDFUnit.error.save"), e);
        }

        // Create a local repository to load the result graph
        Repository repository = new SailRepository(new MemoryStore());
        RepositoryConnection connection;
        try {
            repository.initialize();

            connection = repository.getConnection();
            connection.begin();
            connection.add(output, file.getFileURIString(), RDFFormat.TURTLE);
            connection.commit();

            LOG.info("{} triples have been extracted from {}", connection.size(), output.toString());

        } catch (IOException | RepositoryException | RDFParseException | DataUnitException e) {
            throw new DPUException(ctx.tr("RDFUnit.error.repo.extract"), e);
        }

        // Set output.
        final RDFDataUnit.Entry outputGraph = faultTolerance.execute(new FaultTolerance.ActionReturn<RDFDataUnit.Entry>() {
            @Override
            public RDFDataUnit.Entry action() throws Exception {
                return RdfDataUnitUtils.addGraph(outRdfData, VALIDITY_GRAPH_SYMBOLIC_NAME);
            }
        });
        report.setOutput(outputGraph);

        /////////////////////////////////////////
        /////////////////////////////////////////

        final String rut = "http://rdfunit.aksw.org/ns/core#";
        final String prov = "http://www.w3.org/ns/prov#";
        final String rlog = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#";
        final String spin = "http://spinrdf.org/spin#";

        String[] testSummary = {
                rut +"source",
                rut +"testsRun",
                rut +"testsSuceedded",
                rut +"testsError",
                rut +"testsFailed",
                rut +"totalIndividualErrors",
        };

        /////////////////////////////////////////
        /////////////////////////////////////////

        final EntityBuilder dpuTestSummary;
        final ArrayList<EntityBuilder> dpuTestErrors = new ArrayList<>();

        try {

            // Prepare execution time variable
            final SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            final Date reportDate = reportDateFormat.parse(reportDateFormat.format(new Date()));

            // Query to extract the result summary subject
            final String querySummary =
                    "SELECT ?s " +
                    "WHERE {" +
                        "?s a <"+ rut +"TestExecution> . " +
                    "}";

            TupleQuery tupleQuerySummary = connection.prepareTupleQuery(QueryLanguage.SPARQL, querySummary);
            TupleQueryResult resultQuerySummary = tupleQuerySummary.evaluate();

            // Get result from query
            BindingSet s_bs = resultQuerySummary.next();
            Value s_subject = s_bs.getValue("s");

            // Create summary entity
            dpuTestSummary = new EntityBuilder(valueFactory.createURI(s_subject.stringValue()), valueFactory);
            dpuTestSummary
                    .property(RDF.TYPE, valueFactory.createURI(rut +"TestExecution"))
                    .property(RDF.TYPE, valueFactory.createURI(prov + "Activity"))
                    .property(valueFactory.createURI(testSummary[0]), valueFactory.createURI(file.getFileURIString()))
                    .property(valueFactory.createURI(testSummary[1]), valueFactory.createLiteral((int) testOverviewResults.getTotalTests()))
                    .property(valueFactory.createURI(testSummary[2]), valueFactory.createLiteral((int) testOverviewResults.getSuccessfullTests()))
                    .property(valueFactory.createURI(testSummary[3]), valueFactory.createLiteral((int) testOverviewResults.getErrorTests()))
                    .property(valueFactory.createURI(testSummary[4]), valueFactory.createLiteral((int) testOverviewResults.getFailedTests()))
                    .property(valueFactory.createURI(testSummary[5]), valueFactory.createLiteral((int) testOverviewResults.getIndividualErrors()))
                    .property(DCTERMS.DATE, valueFactory.createLiteral(reportDate));

            // Query to extract the errors subjects
            final String queryGetSubjects =
                    "SELECT ?s " +
                    "WHERE {" +
                        "?s a <"+ rut +"TestCaseResult> . " +
                    "}";

            TupleQuery tupleQueryGetSubjects = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryGetSubjects);
            TupleQueryResult resultQueryGetSubjects = tupleQueryGetSubjects.evaluate();

            int x = 0;

            while (resultQueryGetSubjects.hasNext()) {

                // Get result from query
                BindingSet e_bs = resultQueryGetSubjects.next();
                Value e_subject = e_bs.getValue("s");

                String queryGetError =
                        "SELECT ?p ?o " +
                        "WHERE {" +
                            "<"+ e_subject.stringValue() +"> ?p ?o . " +
                        "}";

                TupleQuery tupleQueryError = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryGetError);
                TupleQueryResult resultQueryError = tupleQueryError.evaluate();

                // Create error entity
                final EntityBuilder dpuTestError = new EntityBuilder(valueFactory.createURI(e_subject.stringValue()), valueFactory);

                while (resultQueryError.hasNext()) {

                    BindingSet _e_bs = resultQueryError.next();
                    Value e_predicate = _e_bs.getValue("p");
                    Value e_object = _e_bs.getValue("o");

                    if (!e_predicate.stringValue().equals(DCTERMS.DATE.toString()) &&
                        !e_predicate.stringValue().equals(rut+"testCase") &&
                        !e_object.stringValue().equals(rut+"RLOGTestCaseResult") &&
                        !e_object.stringValue().equals(rut+"ExtendedTestCaseResult")) {

                        dpuTestError
                                .property(valueFactory.createURI(e_predicate.stringValue()), e_object);
                    }
                }

                dpuTestErrors.add(x, dpuTestError);
                x++;
            }

            // Close connection and repository
            connection.close();
            repository.shutDown();

        } catch (DataUnitException | RepositoryException | QueryEvaluationException | MalformedQueryException | ParseException e) {
            throw new DPUException(ctx.tr("RDFUnit.error"), e);
        }

        // Add summary entity to output graph
        report.add(dpuTestSummary.asStatements());
        for (EntityBuilder dpuTestError : dpuTestErrors) {
            report.add(dpuTestError.asStatements());
        }

        /////////////////////////////////////////////////////////////////////////////////////////////
        /*PrintStream out;
        try {
            out = new PrintStream(new FileOutputStream("/Users/AndreAga/Desktop/output.txt"));
            System.setOut(out);
            for (Object o : dpuTestSummary.asStatements()) System.out.println(o.toString());
            System.out.println();
            System.out.println();
            for (EntityBuilder dpuTestError : dpuTestErrors) {
                for (Object o : dpuTestError.asStatements()) System.out.println(o.toString());
                System.out.println();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    private String createCSVSchema(String directory, ArrayList<String> _prefix, ArrayList<String> _uri, ArrayList<String> _url) throws IOException {

        // Create a new CSV file with custom ontology configuration
        File schema = new File(directory +"schema.csv");
        schema.getParentFile().mkdirs();
        schema.createNewFile();

        CSVWriter writer = new CSVWriter(new FileWriter(schema), ',', CSVWriter.NO_QUOTE_CHARACTER);

        // Write all custom prefixes
        for (int i = 0; i < _prefix.size(); i++) {
            String[] prefix = {_prefix.get(i), _uri.get(i), _url.get(i)};
            writer.writeNext(prefix);
        }

        writer.close();

        return schema.getAbsolutePath();
    }

    /**
     * Creates observation for entity.
     *
     * @param value
     * @param observationIndex
     * @return EntityBuilder
     * @throws DPUException
     */
    /*private EntityBuilder createObservation(double value, int observationIndex) throws DPUException {
        String obs = String.format(RDFUnitVocabulary.EX_OBSERVATIONS, observationIndex);
        String obsBNode = obs + "/bnode_" + observationIndex;
        final EntityBuilder observationEntity = new EntityBuilder(
                valueFactory.createURI(obs), valueFactory);

        // Prepare variables.
        final SimpleDateFormat reportDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        final Date reportDate;
        try {
            reportDate = reportDateFormat.parse(reportDateFormat.format(new Date()));
        } catch (ParseException ex) {
            throw new DPUException(ctx.tr("RDFUnit.error.date.parse.failed"), ex);
        }

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, QualityOntology.QB_OBSERVATION)
                .property(QualityOntology.DAQ_COMPUTED_ON, valueFactory.createURI(obsBNode))
                .property(DC.DATE, valueFactory.createLiteral(reportDate))
                .property(QualityOntology.DAQ_VALUE, valueFactory.createLiteral(value));

        return observationEntity;
    }*/
    /**
     * Creates observation for entity.
     *
     * @param subject
     * @param property
     * @param observationIndex
     * @return EntityBuilder
     * @throws DPUException
     */
    /*private EntityBuilder createObservationBNode(String subject, String property, int observationIndex) throws DPUException {
        String obs = String.format(RDFUnitVocabulary.EX_OBSERVATIONS, observationIndex) + "/bnode_" + observationIndex;
        final EntityBuilder observationEntity = new EntityBuilder(valueFactory.createURI(obs), valueFactory);

        // Set the observation.
        observationEntity
                .property(RDF.TYPE, RDF.STATEMENT)
                .property(RDF.SUBJECT, valueFactory.createLiteral(subject))
                .property(RDF.PROPERTY, valueFactory.createLiteral(property));
        
        return observationEntity;
    }*/
}