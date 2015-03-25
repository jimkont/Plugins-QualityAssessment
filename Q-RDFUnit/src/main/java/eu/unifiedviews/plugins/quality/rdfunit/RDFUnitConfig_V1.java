package eu.unifiedviews.plugins.quality.rdfunit;

import java.util.ArrayList;

public class RDFUnitConfig_V1 {

    private ArrayList<String> subject = new ArrayList<>();
    private ArrayList<String> property = new ArrayList<>();
    private ArrayList<String> properties = new ArrayList<>();

    public RDFUnitConfig_V1() {
        properties.add("http://www.w3.org/2000/01/rdf-schema#label");
        properties.add("http://www.w3.org/2000/01/rdf-schema#comment");
        properties.add("http://purl.org/dc/elements/1.1/title");
        properties.add("http://purl.org/rss/1.0/title");
        properties.add("http://purl.org/dc/elements/1.1/description");
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
