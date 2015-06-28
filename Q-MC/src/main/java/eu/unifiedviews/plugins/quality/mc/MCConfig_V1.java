package eu.unifiedviews.plugins.quality.mc;

import java.util.ArrayList;

/**
 * Configuration class for MC.
 *
 * @author Vincenzo Cutrona
 */
public class MCConfig_V1 {

    private ArrayList<String> subject = new ArrayList<>();
    private ArrayList<String> property = new ArrayList<>();
    private ArrayList<String> properties = new ArrayList<>();

    public MCConfig_V1() {
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

    public ArrayList<String> getSubject() {
        return subject;
    }

    public void setSubject(ArrayList<String> subject) {
        this.subject = subject;
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
