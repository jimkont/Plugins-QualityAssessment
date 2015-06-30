package eu.unifiedviews.plugins.quality.rdfvalidator;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.regex.Pattern;

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
        host.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                fieldValidation(host);
            }
        });
        mainLayout.addComponent(host);
        mainLayout.setExpandRatio(host, 0.8f);

        port = new TextField();
        port.setWidth("100%");
        port.setHeight("-1px");
        port.setCaption(ctx.tr("ACC1.rdf.alerts.port"));
        port.setRequired(true);
        port.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                fieldValidation(port);
            }
        });
        mainLayout.addComponent(port);
        mainLayout.setExpandRatio(port, 0.8f);

        path = new TextField();
        path.setWidth("100%");
        path.setHeight("-1px");
        path.setCaption(ctx.tr("ACC1.rdf.alerts.path"));
        path.setRequired(true);
        path.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                fieldValidation(path);
            }
        });
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

        String error = fieldsValidation();

        if (!error.isEmpty()) {
            throw new DPUConfigException(error);
        } else {

            config.setV_host(host.getValue());
            config.setV_port(Integer.parseInt(port.getValue()));
            config.setV_path(path.getValue());
        }

        return config;
    }

    public String fieldValidation(TextField tf) {
        String txtValue = tf.getValue().toLowerCase().trim();
        String error = "";


        final String portNumberPattern = "([0-9]{1,4})";

        if (tf == this.host) {
            if (txtValue.isEmpty()) {
                error = error + "\n" + ctx.tr("ACC1.error.host.not.filled");
                tf.setComponentError(new UserError(ctx.tr("ACC1.error.host.not.filled")));
            } else if (txtValue.endsWith("/")) {
                error = error + "\n" + ctx.tr("ACC1.error.end.slash");
                tf.setComponentError(new UserError(ctx.tr("ACC1.error.end.slash")));
            } else {
                tf.setComponentError(null);
            }
        } else if (tf == this.path) {
            if (txtValue.isEmpty()) {
                error = error + "\n" + ctx.tr("ACC1.error.path.not.filled");
                tf.setComponentError(new UserError(ctx.tr("ACC1.error.path.not.filled")));
            } else {
                tf.setComponentError(null);
            }
        } else if (tf == this.port){
            if (!Pattern.matches(portNumberPattern, txtValue)) {
                error = error + "\n" + ctx.tr("ACC1.error.port.not.number");
                tf.setComponentError(new UserError(ctx.tr("ACC1.error.port.not.number")));
            } else {
                tf.setComponentError(null);
            }
        }

        return error;
    }

    public String fieldsValidation () {
        return fieldValidation(this.host) + fieldValidation(this.port) +
                fieldValidation(this.path);
    }
}
