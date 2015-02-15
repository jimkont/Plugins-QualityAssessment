package eu.unifiedviews.plugins.quality.acc4;

import com.vaadin.ui.*;

import cz.cuni.mff.xrg.uv.boost.dpu.vaadin.AbstractDialog;
import eu.unifiedviews.dpu.config.DPUConfigException;

public class ACC4VaadinDialog extends AbstractDialog<ACC4Config_V1> {

    private VerticalLayout mainLayout;

    //private GridLayout propertiesGridLayout;

    private FormLayout baseFormLayout;

    //private FormLayout baseFormLayoutSecond;

    //private TextField fileName;

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

        this.mainLayout = new VerticalLayout();
        this.mainLayout.setImmediate(false);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("-1px");
        this.mainLayout.setMargin(true);

        this.baseFormLayout = new FormLayout();
        this.baseFormLayout.setSizeUndefined();

        //fileName = new TextField("File output name:");
        //fileName.setHeight("-1px");
        //fileName.setRequired(true);
        //this.baseFormLayout.addComponent(fileName);

        classUri = new TextField("Class URI:");
        classUri.setHeight("-1px");
        classUri.setRequired(true);
        this.baseFormLayout.addComponent(classUri);

        property = new TextField("Property to Evaluate:");
        property.setHeight("-1px");
        property.setRequired(true);
        this.baseFormLayout.addComponent(property);

        lowerBound = new TextField("Lower Bound:");
        lowerBound.setHeight("-1px");
        lowerBound.setRequired(true);
        this.baseFormLayout.addComponent(lowerBound);

        upperBound = new TextField("Upper Bound:");
        upperBound.setHeight("-1px");
        upperBound.setRequired(true);
        this.baseFormLayout.addComponent(upperBound);

        this.mainLayout.addComponent(baseFormLayout);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);
    }

    @Override
    protected void setConfiguration(ACC4Config_V1 config) throws DPUConfigException {

        //fileName.setValue(config.getFileName());
        classUri.setValue(config.getClassUri());
        property.setValue(config.getProperty());
        lowerBound.setValue(config.getLowerBound()+"");
        upperBound.setValue(config.getUpperBound()+"");

    }

    @Override
    protected ACC4Config_V1 getConfiguration() throws DPUConfigException {

        ACC4Config_V1 config = new ACC4Config_V1();

        //config.setFileName(fileName.getValue());
        config.setClassUri(classUri.getValue());
        config.setProperty(property.getValue());
        config.setLowerBound(Integer.parseInt(lowerBound.getValue()));
        config.setUpperBound(Integer.parseInt(upperBound.getValue()));

        return config;
    }
}
