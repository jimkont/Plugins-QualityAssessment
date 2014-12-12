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
    public void firstTestCase() throws Exception {

        TestEnvironment env = new TestEnvironment();

        QC2 dpu = new QC2();
        QC2Config_V1 config = new QC2Config_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();

        subject.add(0, "http://schema.org/Preschool");
        property.add(0, "http://schema.org/address");

        config.setFileName("ResultTest.csv");
        config.setSubject(subject);
        config.setProperty(property);

        dpu.configureDirectly(config);

        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableFilesDataUnit output = env.createFilesOutput("output");

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ScuoleInfanzia.ttl");

        RepositoryConnection connection = null;

        try {
            connection = input.getConnection();
            connection.add(inputStream, "", RDFFormat.TURTLE);

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