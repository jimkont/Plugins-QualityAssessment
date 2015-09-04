package eu.unifiedviews.plugins.quality.metadatacompletenesschecker;

import eu.unifiedviews.plugins.quality.qualitygraph.QualityOntology.QualityOntology;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;


/**
 * Define vocabulary used by this DPU.
 *
 * @author Vincenzo Cutrona
 */
public class MetadataCompletenessCheckerVocabulary {

    public static final String EX_OBSERVATIONS = QualityOntology.EX + "obs%d";

    public static final URI EX_DPU_NAME;

    static {
        final ValueFactory factory = ValueFactoryImpl.getInstance();

        EX_DPU_NAME = factory.createURI(QualityOntology.EX + "MC");
    }

}