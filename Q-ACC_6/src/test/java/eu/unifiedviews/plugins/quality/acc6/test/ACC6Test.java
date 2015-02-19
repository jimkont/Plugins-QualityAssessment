package eu.unifiedviews.plugins.quality.acc6.test;

import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.plugins.quality.acc6.ACC6;
import eu.unifiedviews.plugins.quality.acc6.ACC6Config_V1;
import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import java.io.InputStream;
import java.util.ArrayList;

public class ACC6Test {

    private static final Logger LOG = LoggerFactory.getLogger(ACC6Test.class);

    @Test
    public void executeTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        // Instances of Main & Config classes
        ACC6 dpu = new ACC6();
        ACC6Config_V1 config = new ACC6Config_V1();

        // Define the config parameters (if necessary)
        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();
        ArrayList<String> regExp = new ArrayList<>();

        subject.add(0, "http://schema.org/Preschool");
        property.add(0, "http://schema.org/telephone");
        regExp.add(0, "[0-9][0-9]/[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
        

        // Set the new values
        config.setSubject(subject);
        config.setProperty(property);
        config.setRegularExpression(regExp);

        // Set the Path where the result will be saved (UNCOMMENT IT)
        //config.setPath("file:/Users/AndreAga/Documents/Sviluppo/UnifiedViews/Portale/backend/finalFiles/");
        // Set the Name of the result file (UNCOMMENT IT)
        //config.setFileName("ResultTest.csv");

        // dpu.configureDirectly(config);

        // Define Input & Output of the DPU
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        //env.createFilesOutput("output");
        env.createRdfOutput("output", false);

        // Set the name of the resource used for the test (located in src/test/resource)
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ScuoleInfanzia.ttl");

        RepositoryConnection connection = null;

        try {
            connection = input.getConnection();
            connection.add(inputStream, "", RDFFormat.TURTLE); // Set the format of the resource

            //env.run(dpu);

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