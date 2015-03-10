package eu.unifiedviews.plugins.quality.c2;

import java.util.ArrayList;

public class C2Config_V1 {

    private ArrayList<String> subject = new ArrayList<>();
    private ArrayList<String> property = new ArrayList<>();

    private int width;
    private int height;

    public C2Config_V1() {
        width = 500;
        height = 500;
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
