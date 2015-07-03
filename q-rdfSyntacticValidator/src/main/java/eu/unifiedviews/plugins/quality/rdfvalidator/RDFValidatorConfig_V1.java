package eu.unifiedviews.plugins.quality.rdfvalidator;

public class RDFValidatorConfig_V1 {

    private String v_host = "http://localhost";
    private int v_port = 8080;
    private String v_path = "RDFAlerts";

    public RDFValidatorConfig_V1() {}

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

}
