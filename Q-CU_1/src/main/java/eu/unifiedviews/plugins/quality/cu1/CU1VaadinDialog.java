package eu.unifiedviews.plugins.quality.cu1;

import com.vaadin.ui.*;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

public class CU1VaadinDialog extends BaseConfigDialog<CU1Config_V1> {

    private VerticalLayout mainLayout;

    private TextField fileName;

    public CU1VaadinDialog() {
        super(CU1Config_V1.class);

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

        //FormLayout baseFormLayout = new FormLayout();
        //baseFormLayout.setSizeUndefined();

        //fileName = new TextField("File output name:");
        //fileName.setHeight("-1px");
        //fileName.setRequired(true);
        //baseFormLayout.addComponent(fileName);

        //this.mainLayout.addComponent(baseFormLayout);
    }

    @Override
    protected void setConfiguration(CU1Config_V1 config) throws DPUConfigException {

        //fileName.setValue(config.getFileName());
        
    }

    @Override
    protected CU1Config_V1 getConfiguration() throws DPUConfigException {

        CU1Config_V1 config = new CU1Config_V1();

        //config.setFileName(fileName.getValue());

        return config;
    }
}
