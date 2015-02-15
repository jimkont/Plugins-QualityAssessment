package eu.unifiedviews.plugins.quality.acc4;

public class ACC4Config_V1 {

    private String classuri = "";
    private String property = "";
    private int lowerBound;
    private int upperBound;

    private String path = null; // Only for test purpose
    private String fileName = "Result.csv"; // Only for test purpose

    private int width;
    private int height;

    public ACC4Config_V1() {
        width = 500;
        height = 500;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String targetPath) {
        this.fileName = targetPath;
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
