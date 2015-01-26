package eu.unifiedviews.plugins.quality.cu1.test;

import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.plugins.quality.cu1.CU1;
import eu.unifiedviews.plugins.quality.cu1.CU1Config_V1;
import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import java.io.InputStream;

public class CU1Test {

    private static final Logger LOG = LoggerFactory.getLogger(CU1Test.class);

    @Test
    public void executeTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        // Instances of Main & Config classes
        CU1 dpu = new CU1();
        CU1Config_V1 config = new CU1Config_V1();

        // Set the Path where the result will be saved
        //config.setPath("file:/Users/AndreAga/Documents/Sviluppo/Progetti/UnifiedViews/Core/backend/finalFiles/");
        // Set the Name of the result file
        //config.setFileName("ResultTest.csv");

        dpu.configureDirectly(config);

        // Define Input & Output of the DPU
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        env.createFilesOutput("output");

        // Set the name of the resource used for the test (located in src/test/resource)
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("CTIA_2-coi-sankce-metadata.trig");

        RepositoryConnection connection = null;

        try {
            connection = input.getConnection();
            connection.add(inputStream, "", RDFFormat.TRIG); // Set the format of the resource

            env.run(dpu);

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }

            env.release();
        }
    }
}