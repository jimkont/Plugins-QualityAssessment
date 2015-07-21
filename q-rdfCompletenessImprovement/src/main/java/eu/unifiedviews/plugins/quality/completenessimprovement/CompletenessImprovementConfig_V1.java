package eu.unifiedviews.plugins.quality.completenessimprovement;

import java.util.ArrayList;

public class CompletenessImprovementConfig_V1 {

    private ArrayList<String> property_source = new ArrayList<>();
    private ArrayList<String> property_target = new ArrayList<>();

    public CompletenessImprovementConfig_V1() {

    }

    public ArrayList<String> getProperty_source() {
        return property_source;
    }

    public void setProperty_source(ArrayList<String> property_source) {
        this.property_source = property_source;
    }

    public ArrayList<String> getProperty_target() {
        return property_target;
    }

    public void setProperty_target(ArrayList<String> property_target) {
        this.property_target = property_target;
    }
}
