package eu.unifiedviews.plugins.quality.completenessimprovement.test;

import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;
import eu.unifiedviews.plugins.quality.completenessimprovement.CompletenessImprovement;
import eu.unifiedviews.plugins.quality.completenessimprovement.CompletenessImprovementConfig_V1;
import org.junit.Test;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;

import java.io.InputStream;
import java.util.ArrayList;

public class CompletenessImprovementTest {

    @Test
    public void executeTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        // Instances of Main & Config classes
        CompletenessImprovement dpu = new CompletenessImprovement();
        CompletenessImprovementConfig_V1 config = new CompletenessImprovementConfig_V1();

        // Set Configuration values
        ArrayList<String> sourceProperty = new ArrayList<>();
        ArrayList<String> targetProperty = new ArrayList<>();

        sourceProperty.add("http://dbpedia.org/property/released");
        targetProperty.add("http://data.linkedmdb.org/resource/movie/initial_release_date");

        sourceProperty.add("http://dbpedia.org/property/language");
        targetProperty.add("http://data.linkedmdb.org/resource/movie/language");

        config.setProperty_source(sourceProperty);
        config.setProperty_target(targetProperty);

        // Set the new configuration to the dpu
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Define Input & Output of the DPU
        WritableRDFDataUnit input_source =  env.createRdfInput("input_source", false);
        WritableRDFDataUnit input_target =  env.createRdfInput("input_target", false);
        WritableRDFDataUnit input_silk =  env.createRdfInput("input_silk", false);
        env.createRdfOutput("output", false);

        InputStream inputStream_source = Thread.currentThread().getContextClassLoader().getResourceAsStream("source.nt");
        InputStream inputStream_target = Thread.currentThread().getContextClassLoader().getResourceAsStream("target.nt");
        InputStream inputStream_silk = Thread.currentThread().getContextClassLoader().getResourceAsStream("data.nt");

        RepositoryConnection connection_source = null;
        RepositoryConnection connection_target = null;
        RepositoryConnection connection_silk = null;

        try {
            connection_source = input_source.getConnection();
            connection_target = input_target.getConnection();
            connection_silk = input_silk.getConnection();

            connection_source.add(inputStream_source, "", RDFFormat.NTRIPLES);
            connection_target.add(inputStream_target, "", RDFFormat.NTRIPLES);
            connection_silk.add(inputStream_silk, "", RDFFormat.NTRIPLES);

            //env.run(dpu);

        } finally {

            if (connection_source != null) {
                try {
                    connection_source.close();
                } catch (Throwable ex) {
                    System.out.println("Error closing SOURCE connection");
                }
            }

            if (connection_target != null) {
                try {
                    connection_target.close();
                } catch (Throwable ex) {
                    System.out.println("Error closing TARGET connection");
                }
            }

            if (connection_silk != null) {
                try {
                    connection_silk.close();
                } catch (Throwable ex) {
                    System.out.println("Error closing SILK connection");
                }
            }

            env.release();
        }
    }
}
