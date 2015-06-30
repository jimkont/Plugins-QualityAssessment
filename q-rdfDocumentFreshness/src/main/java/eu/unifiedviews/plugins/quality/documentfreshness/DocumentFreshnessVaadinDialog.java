package eu.unifiedviews.plugins.quality.documentfreshness;

import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class DocumentFreshnessVaadinDialog extends AbstractDialog<DocumentFreshnessConfig_V1> {

    public DocumentFreshnessVaadinDialog() {
        super(DocumentFreshness.class);
    }

    @Override
    protected void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(DocumentFreshnessConfig_V1 config) throws DPUConfigException {

    }

    @Override
    protected DocumentFreshnessConfig_V1 getConfiguration() throws DPUConfigException {
        DocumentFreshnessConfig_V1 config = new DocumentFreshnessConfig_V1();
        return config;
    }
}
