package eu.unifiedviews.plugins.quality.lc;

import java.util.ArrayList;

/**
 * Configuration class for LC.
 *
 * @author Vincenzo Cutrona
 */
public class LCConfig_V1 {

    private ArrayList<String> properties = new ArrayList<>();

    public LCConfig_V1() {
        properties.add("http://www.w3.org/1999/xhtml/license");
        properties.add("http://purl.org/dc/elements/1.1/licence");
        properties.add("http://creativecommons.org/ns/license");
        properties.add("http://purl.org/dc/elements/1.1/rights");
        properties.add("http://dbpedia.org/ontology/license");
        properties.add("http://purl.org/dc/terms/license");
        properties.add("http://dbpedia.org/property/license");
        properties.add("http://usefulinc.com/ns/doap/license");
        properties.add("http://purl.org/dc/terms/rights");
    }

    public ArrayList<String> getProperties() {
        return properties;
    }
}
