package eu.unifiedviews.plugins.quality.improvement.test;

import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;
import eu.unifiedviews.plugins.quality.improvement.Improvement;
import eu.unifiedviews.plugins.quality.improvement.ImprovementConfig_V1;
import org.junit.Test;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;

public class ImprovementTest {

    @Test
    public void executeTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        // Instances of Main & Config classes
        Improvement dpu = new Improvement();
        ImprovementConfig_V1 config = new ImprovementConfig_V1();

        // Set Configuration values

        // Set the new configuration to the dpu
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // X-12
        // X-15
        // X-19
        // X-20
        // X-41

        // Define Input & Output of the DPU
        env.createFilesInputFromResource("input", "it-x20.ttl");
        env.createFilesOutput("output");

        try {

            //env.run(dpu);

        } finally {

            env.release();

        }
    }
}