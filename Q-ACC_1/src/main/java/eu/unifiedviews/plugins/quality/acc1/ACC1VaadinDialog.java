package eu.unifiedviews.plugins.quality.acc1;

import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class ACC1VaadinDialog extends AbstractDialog<ACC1Config_V1> {

    private TextField host;
    private TextField port;
    private TextField path;

    public ACC1VaadinDialog() {
        super(ACC1.class);
    }

    @Override
    protected void buildDialogLayout() {

        this.setWidth("100%");
        this.setHeight("100%");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

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

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);

    }

    @Override
    protected void setConfiguration(ACC1Config_V1 config) throws DPUConfigException {

        host.setValue(config.getV_host());
        port.setValue(""+config.getV_port());
        path.setValue(config.getV_path());

    }

    @Override
    protected ACC1Config_V1 getConfiguration() throws DPUConfigException {

        ACC1Config_V1 config = new ACC1Config_V1();

        config.setV_host(host.getValue());
        config.setV_port(Integer.parseInt(port.getValue()));
        config.setV_path(path.getValue());

        return config;
    }
}
