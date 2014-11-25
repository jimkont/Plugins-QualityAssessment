package eu.unifiedviews.plugins.dputemplate;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class DPUTemplateVaadinDialog extends BaseConfigDialog<DPUTemplateConfig_V1> {

    public DPUTemplateVaadinDialog() {
        super(DPUTemplateConfig_V1.class);
    }

    @Override
    public void setConfiguration(DPUTemplateConfig_V1 conf) throws DPUConfigException {
        // TODO : load configuration from function parameter into dialog
    }

    @Override
    public DPUTemplateConfig_V1 getConfiguration() throws DPUConfigException {
        // TODO : gather information from dialog and store them into configuration, then return it
        return null;
    }

}
