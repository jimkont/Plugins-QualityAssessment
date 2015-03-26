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
        ArrayList<String> prefix = new ArrayList<>();
        ArrayList<String> uri = new ArrayList<>();
        ArrayList<String> url = new ArrayList<>();

        prefix.add("comsode");
        prefix.add("disco");
        uri.add("http://comsode.disco.unimib.it/resource/dataset/meteo/");
        uri.add("http://disco.unimib.it/resource/dataset/meteo/");
        url.add("http://comsode.disco.unimib.it/resource/dataset/meteo/ontology.owl");
        url.add("http://disco.unimib.it/resource/dataset/meteo/ontology.owl");

        config.setPrefix(prefix);
        config.setUri(uri);
        config.setUrl(url);

        // Set the new configuration to the dpu
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Define Input & Output of the DPU
        env.createFilesInputFromResource("input","Meteo.ttl");
        env.createRdfOutput("output", false);

        try {

            env.run(dpu);

        } finally {

            env.release();

        }
    }
}