package eu.unifiedviews.plugins.quality.acc6;

import java.util.ArrayList;

public class ACC6Config_V1 {
    
    private ArrayList<String> subject = new ArrayList<>();
    private ArrayList<String> property = new ArrayList<>();
    private ArrayList<String> regularExpression = new ArrayList<>();

    private String path = null; // Only for test purpose
    private String fileName = "Result.csv";

    private int width;
    private int height;

    public ACC6Config_V1() {
        width = 500;
        height = 500;
    }

    public ArrayList<String> getSubject() { return subject; }

    public void setSubject(ArrayList<String> subject) { this.subject = subject; }
    
    public ArrayList<String> getProperty() {
        return property;
    }

    public void setProperty(ArrayList<String> property) {
        this.property = property;
    }

    public ArrayList<String> getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(ArrayList<String> regularExpression) {
        this.regularExpression = regularExpression;
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
