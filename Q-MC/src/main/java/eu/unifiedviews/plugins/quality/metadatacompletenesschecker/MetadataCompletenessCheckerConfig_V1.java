package eu.unifiedviews.plugins.quality.metadatacompletenesschecker;

import java.util.ArrayList;

/**
 * Configuration class for MetadataCompletenessChecker.
 *
 * @author Vincenzo Cutrona
 */
public class MetadataCompletenessCheckerConfig_V1 {

    private ArrayList<String> property = new ArrayList<>();
    private ArrayList<String> properties = new ArrayList<>();

    public MetadataCompletenessCheckerConfig_V1() {
        properties.add("http://purl.org/dc/terms/creator");
        properties.add("http://purl.org/dc/terms/description");
        properties.add("http://purl.org/dc/terms/title");
        properties.add("http://purl.org/dc/terms/created");
        properties.add("http://purl.org/dc/terms/modified");
        properties.add("http://purl.org/dc/terms/publisher");
        properties.add("http://purl.org/dc/elements/1.1/coverage");
        properties.add("http://purl.org/dc/terms/format");
        properties.add("http://purl.org/dc/terms/identifier");
        properties.add("http://purl.org/dc/terms/valid");
        properties.add("http://purl.org/dc/terms/license");
    }

    public ArrayList<String> getProperty() {
        return property;
    }

    public void setProperty(ArrayList<String> property) {
        this.property = property;
    }

    public ArrayList<String> getProperties() {
        return properties;
    }

}
