package eu.unifiedviews.plugins.quality.acc4;

public class ACC4Config_V1 {

    private String classuri = "";
    private String property = "";
    private double lowerBound;
    private double upperBound;

    private int width;
    private int height;

    public ACC4Config_V1() {
        width = 500;
        height = 500;
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
