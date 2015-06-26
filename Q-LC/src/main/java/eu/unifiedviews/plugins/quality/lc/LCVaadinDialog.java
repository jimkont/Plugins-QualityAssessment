package eu.unifiedviews.plugins.quality.lc;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for LC.
 *
 * @author Vincenzo Cutrona
 */
public class LCVaadinDialog extends AbstractDialog<LCConfig_V1> {

    public LCVaadinDialog() {
        super(LC.class);
    }

    @Override
    public void setConfiguration(LCConfig_V1 c) throws DPUConfigException {

    }

    @Override
    public LCConfig_V1 getConfiguration() throws DPUConfigException {
        final LCConfig_V1 c = new LCConfig_V1();

        return c;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        setCompositionRoot(mainLayout);
    }
}
