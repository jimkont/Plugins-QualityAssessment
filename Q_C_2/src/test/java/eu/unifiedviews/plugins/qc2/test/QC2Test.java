package eu.unifiedviews.plugins.qc2.test;

import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.plugins.qc2.QC2;
import eu.unifiedviews.plugins.qc2.QC2Config_V1;
import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import java.io.InputStream;
import java.util.ArrayList;

public class QC2Test {

    private static final Logger LOG = LoggerFactory.getLogger(QC2Test.class);

    @Test
    public void executeTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        // Instances of Main & Config classes
        QC2 dpu = new QC2();
        QC2Config_V1 config = new QC2Config_V1();

        // Define the config parameters (if necessary)
        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();

        subject.add(0, "http://schema.org/Preschool");
        subject.add(1, "http://schema.org/Preschool");
        property.add(0, "http://schema.org/address");
        property.add(1, "http://schema.org/telephone");

        // Set the new values
        config.setSubject(subject);
        config.setProperty(property);

        // Set the Path where the result will be saved
        config.setPath("file:/Users/AndreAga/Documents/Sviluppo/UnifiedViews/Portale/backend/finalFiles/");
        // Set the Name of the result file
        config.setFileName("ResultTest.csv");

        dpu.configureDirectly(config);

        // Define Input & Output of the DPU
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableFilesDataUnit output = env.createFilesOutput("output");

        // Set the name of the resource used for the test (located in src/test/resource)
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ScuoleInfanzia.ttl");

        RepositoryConnection connection = null;

        try {
            connection = input.getConnection();
            connection.add(inputStream, "", RDFFormat.TURTLE); // Set the format of the resource

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