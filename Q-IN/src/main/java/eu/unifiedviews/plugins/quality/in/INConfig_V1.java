package eu.unifiedviews.plugins.quality.in;

import java.util.ArrayList;

/**
 * Configuration class for IN.
 *
 * @author Vincenzo Cutrona
 */
public class INConfig_V1 {

    private ArrayList<String> property = new ArrayList<>();
    private ArrayList<String> lang = new ArrayList<>();
    private ArrayList<String> langs = new ArrayList<>();

    public INConfig_V1() {
        langs.add("EN");
        langs.add("ES");
        langs.add("DE");
        langs.add("IT");
        langs.add("FR");
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

    public ArrayList<String> getLangs() {
        return langs;
    }
}
