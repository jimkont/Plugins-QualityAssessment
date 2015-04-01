package eu.unifiedviews.plugins.quality.acc1;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;

public class ACC1Vocabulary {

    public static final String EX_OBSERVATIONS = QualityOntology.EX + "obs%d";

    public static final URI EX_DPU_NAME;

    static {
        final ValueFactory factory = ValueFactoryImpl.getInstance();

        EX_DPU_NAME = factory.createURI(QualityOntology.EX + "Q-ACC1");
    }

}