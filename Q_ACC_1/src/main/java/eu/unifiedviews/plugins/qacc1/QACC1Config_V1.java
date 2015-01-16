package eu.unifiedviews.plugins.qacc1;

public class QACC1Config_V1 {

    private String path = null; // Only for test purpose
    private String fileName = "Result.csv";

    private String v_host = "http://localhost";
    private int v_port = 8080;
    private String v_path = "RDFAlerts";

    private int width;
    private int height;

    public QACC1Config_V1() {
        width = 500;
        height = 500;
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

    public String getV_host() {
        return v_host;
    }

    public void setV_host(String v_host) {
        this.v_host = v_host;
    }

    public int getV_port() {
        return v_port;
    }

    public void setV_port(int v_port) {
        this.v_port = v_port;
    }

    public String getV_path() {
        return v_path;
    }

    public void setV_path(String v_path) {
        this.v_path = v_path;
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
