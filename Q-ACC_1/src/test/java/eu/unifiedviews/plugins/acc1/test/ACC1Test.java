package eu.unifiedviews.plugins.acc1.test;

import eu.unifiedviews.plugins.acc1.ACC1;
import eu.unifiedviews.plugins.acc1.ACC1Config_V1;
import org.junit.Test;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;

public class ACC1Test {

    @Test
    public void executeTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        // Instances of Main & Config classes
        ACC1 dpu = new ACC1();
        ACC1Config_V1 config = new ACC1Config_V1();

        // Set the Path where the result will be saved
        //config.setPath("file:/Users/AndreAga/Documents/Sviluppo/UnifiedViews/Portale/backend/finalFiles/");
        // Set the Name of the result file
        //config.setFileName("ResultTest.csv");

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