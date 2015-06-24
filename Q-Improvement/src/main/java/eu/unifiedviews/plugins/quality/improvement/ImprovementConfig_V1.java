package eu.unifiedviews.plugins.quality.improvement;

import java.util.ArrayList;

public class ImprovementConfig_V1 {

    private ArrayList<String> prefix = new ArrayList<>();
    private ArrayList<String> uri = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();

    public ImprovementConfig_V1() {}

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
