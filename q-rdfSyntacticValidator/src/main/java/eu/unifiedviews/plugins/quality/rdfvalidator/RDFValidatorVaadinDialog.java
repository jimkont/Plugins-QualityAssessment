package eu.unifiedviews.plugins.quality.rdfvalidator;

import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class RDFValidatorVaadinDialog extends AbstractDialog<RDFValidatorConfig_V1> {

    private TextField host;
    private TextField port;
    private TextField path;

    public RDFValidatorVaadinDialog() {
        super(RDFValidator.class);
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
        host.setCaption(ctx.tr("ACC1.rdf.alerts.host"));
        host.setRequired(true);
        mainLayout.addComponent(host);
        mainLayout.setExpandRatio(host, 0.8f);

        port = new TextField();
        port.setWidth("100%");
        port.setHeight("-1px");
        port.setCaption(ctx.tr("ACC1.rdf.alerts.port"));
        port.setRequired(true);
        mainLayout.addComponent(port);
        mainLayout.setExpandRatio(port, 0.8f);

        path = new TextField();
        path.setWidth("100%");
        path.setHeight("-1px");
        path.setCaption(ctx.tr("ACC1.rdf.alerts.path"));
        path.setRequired(true);
        mainLayout.addComponent(path);
        mainLayout.setExpandRatio(path, 0.8f);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(RDFValidatorConfig_V1 config) throws DPUConfigException {

        host.setValue(config.getV_host());
        port.setValue(""+config.getV_port());
        path.setValue(config.getV_path());
    }

    @Override
    protected RDFValidatorConfig_V1 getConfiguration() throws DPUConfigException {

        RDFValidatorConfig_V1 config = new RDFValidatorConfig_V1();

        config.setV_host(host.getValue());
        config.setV_port(Integer.parseInt(port.getValue()));
        config.setV_path(path.getValue());

        return config;
    }
}
