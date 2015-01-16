package eu.unifiedviews.plugins.qacc1.test;

import eu.unifiedviews.plugins.qacc1.QACC1;
import eu.unifiedviews.plugins.qacc1.QACC1Config_V1;
import org.junit.Test;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;

public class QACC1Test {

    @Test
    public void executeTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        // Instances of Main & Config classes
        QACC1 dpu = new QACC1();
        QACC1Config_V1 config = new QACC1Config_V1();

        // Set the Path where the result will be saved
        config.setPath("file:/Users/AndreAga/Documents/Sviluppo/UnifiedViews/Portale/backend/finalFiles/");
        // Set the Name of the result file
        config.setFileName("ResultTest.csv");

        dpu.configureDirectly(config);

        // Define Input & Output of the DPU
        env.createFilesInputFromResource("input", "input.nt");
        env.createFilesOutput("output");

        try {

            env.run(dpu);

        } finally {

            env.release();

        }
    }
}