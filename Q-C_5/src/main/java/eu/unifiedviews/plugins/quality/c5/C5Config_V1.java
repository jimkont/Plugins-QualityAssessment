package eu.unifiedviews.plugins.quality.c5;

import java.util.ArrayList;

public class C5Config_V1 {

    private ArrayList<String> subject = new ArrayList<>();
    private ArrayList<String> property = new ArrayList<>();
    private ArrayList<String> lang = new ArrayList<>();
    private ArrayList<String> properties = new ArrayList<>();
    private ArrayList<String> langs = new ArrayList<>();

    public C5Config_V1() {
        properties.add("http://www.w3.org/2000/01/rdf-schema#label");
        properties.add("http://www.w3.org/2000/01/rdf-schema#comment");
        properties.add("http://purl.org/dc/elements/1.1/title");
        properties.add("http://purl.org/rss/1.0/title");
        properties.add("http://purl.org/dc/elements/1.1/description");
        langs.add("EN");
        langs.add("ES");
        langs.add("DE");
        langs.add("IT");
        langs.add("FR");
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

    public ArrayList<String> getLang() {
        return lang;
    }

    public void setLang(ArrayList<String> lang) {
        this.lang = lang;
    }

    public ArrayList<String> getProperties() {
        return properties;
    }

    public ArrayList<String> getLangs() { return langs; }

}
