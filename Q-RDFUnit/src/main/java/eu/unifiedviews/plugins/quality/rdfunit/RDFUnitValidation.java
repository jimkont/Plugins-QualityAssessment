package eu.unifiedviews.plugins.quality.rdfunit;

import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.io.writer.RDFStreamWriter;
import org.aksw.rdfunit.io.writer.RDFWriterException;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class RDFUnitValidation {

    private RDFUnitConfiguration configuration;
    private TestSuite testSuite;
    private UserExecContext ctx;

    public RDFUnitValidation(String dataFolder, String datasetURI, UserExecContext _ctx) throws DPUException{

        ctx = _ctx;

        // Fill Schemas from LOV
        RDFUnitUtils.fillSchemaServiceFromLOV();

        // Set the source Dataset
        File file = new File(datasetURI);

        ContextUtils.sendShortInfo(ctx, "RDFUnit.test.init");

        // Set the configuration
        configuration = new RDFUnitConfiguration(datasetURI, dataFolder);
        configuration.setCustomDereferenceURI(file.getAbsolutePath());
        configuration.setTestCaseExecutionType(TestCaseExecutionType.extendedTestCaseResult);
        configuration.setAutoSchemataFromQEF(configuration.getTestSource().getExecutionFactory(), true);

        // Initialize RDFUnit
        org.aksw.rdfunit.RDFUnit rdfUnit = new org.aksw.rdfunit.RDFUnit();
        try {
            rdfUnit.init();
        } catch (RDFReaderException e) {
            throw new DPUException(ctx.tr("RDFUnit.error.init.failed"));
        }

        // Generate TestExecutor
        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor(
                configuration.isAutoTestsEnabled(),
                configuration.isTestCacheEnabled(),
                configuration.isManualTestsEnabled());

        ContextUtils.sendShortInfo(ctx, "RDFUnit.test.generate.test");

        // Generate TestSuite
        testSuite = testGeneratorExecutor.generateTestSuite(
                configuration.getTestFolder(),
                configuration.getTestSource(),
                rdfUnit.getAutoGenerators());
    }

    public String validate() {

        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor(false);
        final TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(configuration.getTestCaseExecutionType());
        final TestSource testSource = configuration.getTestSource();

        ContextUtils.sendShortInfo(ctx, "RDFUnit.test.execute.test");

        testExecutor.addTestExecutorMonitor(testExecutorMonitor);
        testExecutor.execute(testSource, testSuite);

        //OutputStream to get the results as string
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            new RDFStreamWriter(os).write(testExecutorMonitor.getModel());
            return os.toString();
        } catch (RDFWriterException e) {
            return null;
        }
    }
}
