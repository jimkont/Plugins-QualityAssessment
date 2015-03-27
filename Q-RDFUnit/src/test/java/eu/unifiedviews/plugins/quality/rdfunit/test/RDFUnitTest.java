package eu.unifiedviews.plugins.quality.rdfunit.test;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;
import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;
import eu.unifiedviews.plugins.quality.rdfunit.RDFUnit;
import eu.unifiedviews.plugins.quality.rdfunit.RDFUnitConfig_V1;
import org.junit.Test;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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

        config.setPrefix(prefix);
        config.setUri(uri);
        config.setUrl(url);

        // Set the new configuration to the dpu
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Define Input & Output of the DPU
        env.createFilesInputFromResource("input", "Scuole.ttl");
        env.createRdfOutput("output", false);

        try {

            //env.run(dpu);

        } finally {

            env.release();

        }
    }
}