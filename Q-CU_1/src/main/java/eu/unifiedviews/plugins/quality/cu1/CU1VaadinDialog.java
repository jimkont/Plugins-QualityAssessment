package eu.unifiedviews.plugins.quality.cu1;

import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class CU1VaadinDialog extends AbstractDialog<CU1Config_V1> {

    public CU1VaadinDialog() {
        super(CU1.class);
    }

    @Override
    protected void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);
    }

    @Override
    protected void setConfiguration(CU1Config_V1 config) throws DPUConfigException {

    }

    @Override
    protected CU1Config_V1 getConfiguration() throws DPUConfigException {
        CU1Config_V1 config = new CU1Config_V1();
        return config;
    }
}