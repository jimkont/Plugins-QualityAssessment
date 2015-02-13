package eu.unifiedviews.plugins.quality.acc1;

import com.vaadin.ui.*;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

public class ACC1VaadinDialog extends BaseConfigDialog<ACC1Config_V1> {

    private VerticalLayout mainLayout;

    private FormLayout baseFormLayout;

    private TextField fileName;

    private TextField host;
    private TextField port;
    private TextField path;

    public ACC1VaadinDialog() {
        super(ACC1Config_V1.class);

        buildMainLayout();

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);
    }

    private void buildMainLayout() {

        this.setWidth("100%");
        this.setHeight("100%");

        this.mainLayout = new VerticalLayout();
        this.mainLayout.setImmediate(false);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("-1px");
        this.mainLayout.setMargin(true);

        this.baseFormLayout = new FormLayout();
        this.baseFormLayout.setSizeUndefined();

        host = new TextField();
        host.setWidth("100%");
        host.setHeight("-1px");
        host.setCaption("RDFAlerts host: (without final '/')");
        host.setRequired(true);
        mainLayout.addComponent(host);
        mainLayout.setExpandRatio(host, 0.8f);

        port = new TextField();
        port.setWidth("100%");
        port.setHeight("-1px");
        port.setCaption("RDFAlerts port:");
        port.setRequired(true);
        mainLayout.addComponent(port);
        mainLayout.setExpandRatio(port, 0.8f);

        path = new TextField();
        path.setWidth("100%");
        path.setHeight("-1px");
        path.setCaption("RDFAlerts path:");
        path.setRequired(true);
        mainLayout.addComponent(path);
        mainLayout.setExpandRatio(path, 0.8f);

        fileName = new TextField();
        fileName.setWidth("100%");
        fileName.setHeight("-1px");
        fileName.setCaption("File output name:");
        fileName.setRequired(true);
        mainLayout.addComponent(fileName);
        mainLayout.setExpandRatio(fileName, 0.8f);

    }

    @Override
    protected void setConfiguration(ACC1Config_V1 config) throws DPUConfigException {

        fileName.setValue(config.getFileName());

        host.setValue(config.getV_host());
        port.setValue(""+config.getV_port());
        path.setValue(config.getV_path());

    }

    @Override
    protected ACC1Config_V1 getConfiguration() throws DPUConfigException {

        ACC1Config_V1 config = new ACC1Config_V1();

        config.setFileName(fileName.getValue());

        config.setV_host(host.getValue());
        config.setV_port(Integer.parseInt(port.getValue()));
        config.setV_path(path.getValue());

        return config;
    }
}
