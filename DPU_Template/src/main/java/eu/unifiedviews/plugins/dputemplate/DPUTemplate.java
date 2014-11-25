package eu.unifiedviews.plugins.dputemplate;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU.AsTransformer;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

// TODO 1: You can choose AsLoader or AsExtractor instead of AsTransformer
@AsTransformer
public class DPUTemplate extends ConfigurableBase<DPUTemplateConfig_V1> implements ConfigDialogProvider<DPUTemplateConfig_V1> {
// If you do not want the dialog, use the following DPU declaration
// and remove getConfigurationDialog function
//public class DPUTemplate implements DPU {

    @DataUnit.AsInput(name = "input")
    public RDFDataUnit rdfInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public DPUTemplate() {
        super(DPUTemplateConfig_V1.class);
    }

    @Override
    public AbstractConfigDialog<DPUTemplateConfig_V1> getConfigurationDialog() {
        return new DPUTemplateVaadinDialog();
    }

    // TODO 2: Implement the method execute being called when the DPU is launched
    @Override
    public void execute(DPUContext context) throws DPUException {
        // DPU's configuration is accessible under 'this.config'
        // DPU's context is accessible under 'context'
        // DPU's data units are accessible under 'rdfInput' and 'filesOutput'

        //Let's write simple RDF graph to file transformer DPU
        //It will export each RDF data graph from rdfInput to single RDF+XML file on the filesOutput
        //Copy any metadata from graph to file to be neat to others using them
        //And finally, we will generate one new file, which name is configured by user in dialog
        //and it will contain list of symbolicName;graphUri;fileLocation for each graph-file pair on each line
        //it is of no practical meaning, just to show the API

    }

}
