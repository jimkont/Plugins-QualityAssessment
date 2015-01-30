package eu.unifiedviews.plugins.quality.cu1;

public class CU1Config_V1 {

    //private String path = null; // Only for test purpose
    //private String fileName = "";  // Only for test purpose

    private int width;
    private int height;

    public CU1Config_V1() {
        width = 500;
        height = 500;
    }

    /*public String getPath() {
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
    }*/

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
