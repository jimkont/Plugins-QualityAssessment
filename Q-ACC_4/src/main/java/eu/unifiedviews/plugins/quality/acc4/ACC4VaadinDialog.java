package eu.unifiedviews.plugins.quality.acc4;

import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class ACC4VaadinDialog extends AbstractDialog<ACC4Config_V1> {

    private TextField classUri;
    private TextField property;
    private TextField lowerBound;
    private TextField upperBound;

    public ACC4VaadinDialog() {
        super(ACC4.class);
    }

    @Override
    protected void buildDialogLayout() {
        this.setWidth("100%");
        this.setHeight("100%");

        VerticalLayout mainLayout;
        FormLayout baseFormLayout;

        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        baseFormLayout = new FormLayout();
        baseFormLayout.setSizeUndefined();

        classUri = new TextField(ctx.tr("ACC4.class.uri"));
        classUri.setHeight("-1px");
        classUri.setRequired(true);
        baseFormLayout.addComponent(classUri);

        property = new TextField(ctx.tr("ACC4.property"));
        property.setHeight("-1px");
        property.setRequired(true);
        baseFormLayout.addComponent(property);

        lowerBound = new TextField(ctx.tr("ACC4.lower.bound"));
        lowerBound.setHeight("-1px");
        lowerBound.setRequired(true);
        baseFormLayout.addComponent(lowerBound);

        upperBound = new TextField(ctx.tr("ACC4.upper.bound"));
        upperBound.setHeight("-1px");
        upperBound.setRequired(true);
        baseFormLayout.addComponent(upperBound);

        mainLayout.addComponent(baseFormLayout);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(ACC4Config_V1 config) throws DPUConfigException {
        classUri.setValue(config.getClassUri());
        property.setValue(config.getProperty());
        lowerBound.setValue(config.getLowerBound()+"");
        upperBound.setValue(config.getUpperBound()+"");
    }

    @Override
    protected ACC4Config_V1 getConfiguration() throws DPUConfigException {

        ACC4Config_V1 config = new ACC4Config_V1();

        config.setClassUri(classUri.getValue());
        config.setProperty(property.getValue());
        config.setLowerBound(Double.parseDouble(lowerBound.getValue()));
        config.setUpperBound(Double.parseDouble(upperBound.getValue()));

        return config;
    }
}