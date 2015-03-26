package eu.unifiedviews.plugins.quality.rdfunit.test;

import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;
import eu.unifiedviews.plugins.quality.rdfunit.RDFUnit;
import eu.unifiedviews.plugins.quality.rdfunit.RDFUnitConfig_V1;
import org.junit.Test;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;

import java.util.ArrayList;

public class RDFUnitTest {

    @Test
    public void executeTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        // Instances of Main & Config classes
        RDFUnit dpu = new RDFUnit();
        RDFUnitConfig_V1 config = new RDFUnitConfig_V1();

        // Set Configuration values

        // Set the new configuration to the dpu
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Define Input & Output of the DPU
        env.createFilesInputFromResource("input","Elezioni.ttl");
        env.createRdfOutput("output", false);

        try {

            env.run(dpu);

        } finally {

            env.release();

        }
    }
}