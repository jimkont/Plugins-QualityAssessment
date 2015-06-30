package eu.unifiedviews.plugins.quality.numericalrangechecker;

public class NumericalRangeCheckerConfig_V1 {

    private String classuri = "";
    private String property = "";
    private double lowerBound;
    private double upperBound;

    public NumericalRangeCheckerConfig_V1() {

    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    public String getClassUri() {
        return classuri;
    }

    public void setClassUri(String classuri) {
        this.classuri = classuri;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
