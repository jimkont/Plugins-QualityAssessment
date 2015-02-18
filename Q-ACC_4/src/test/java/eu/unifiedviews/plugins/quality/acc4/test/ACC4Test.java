package eu.unifiedviews.plugins.quality.acc4.test;

import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.plugins.quality.acc4.ACC4;
import eu.unifiedviews.plugins.quality.acc4.ACC4Config_V1;
import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import java.io.InputStream;
import java.util.ArrayList;

public class ACC4Test {

    private static final Logger LOG = LoggerFactory.getLogger(ACC4Test.class);

    @Test
    public void executeTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        // Instances of Main & Config classes
        ACC4 dpu = new ACC4();
        ACC4Config_V1 config = new ACC4Config_V1();

        // Define the config parameters (if necessary)
        String classUri = "http://unifiedviews.eu/ontology/Meteo";
        String propertyUri = "http://comsode.disco.unimib.it/resource/dataset/meteo/minTemperature";
        int lowerBound = 0;
        int upperBound = 2;

        // Set the new values
        config.setClassUri(classUri);
        config.setProperty(propertyUri);
        config.setLowerBound(lowerBound);
        config.setUpperBound(upperBound);

        // Set the Path where the result will be saved (UNCOMMENT IT)
        //config.setPath("file:/Users/AndreAga/Documents/Sviluppo/UnifiedViews/Portale/backend/finalFiles/");
        // Set the Name of the result file (UNCOMMENT IT)
        //config.setFileName("ResultTest.ttl");

        //dpu.configureDirectly(config);


        // Define Input & Output of the DPU
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        //env.createRdfFDataUnit()
        //env.createFilesOutput("output");
        //env.createRdfOutput("outRdfData", false);
        env.createRdfOutput("output", false);

        // Set the name of the resource used for the test (located in src/test/resource)
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("it_x_14-dataset-meteo.ttl");

        RepositoryConnection connection = null;

        try {
            connection = input.getConnection();
            //connection.add(inputStream, "", RDFFormat.TURTLE); // Set the format of the resource

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