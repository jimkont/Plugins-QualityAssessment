package eu.unifiedviews.plugins.quality.rdfunit;

import java.util.ArrayList;

public class RDFUnitConfig_V1 {

    private ArrayList<String> prefix = new ArrayList<>();
    private ArrayList<String> uri = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();

    public RDFUnitConfig_V1() {}

    public ArrayList<String> getPrefix() {
        return prefix;
    }

    public void setPrefix(ArrayList<String> prefix) {
        this.prefix = prefix;
    }

    public ArrayList<String> getUri() {
        return uri;
    }

    public void setUri(ArrayList<String> uri) {
        this.uri = uri;
    }

    public ArrayList<String> getUrl() {
        return url;
    }

    public void setUrl(ArrayList<String> url) {
        this.url = url;
    }
}
