package eu.unifiedviews.plugins.quality.completenesschecker;

import java.util.ArrayList;

public class CompletenessCheckerConfig_V1 {

    private ArrayList<String> subject = new ArrayList<>();
    private ArrayList<String> property = new ArrayList<>();

    public CompletenessCheckerConfig_V1() {

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
}
