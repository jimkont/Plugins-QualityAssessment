package eu.unifiedviews.plugins.dputemplate;

/**
 * Put your DPU's configuration here.
 * You can optionally implement {@link #isValid()} to provide possibility
 * to validate the configuration.
 * <b>This class must have default (parameter less) constructor!</b>
 */
public class DPUTemplateConfig_V1 {

    private int width;

    private int height;

    // DPUTemplateConfig must provide public non-parametric constructor
    public DPUTemplateConfig_V1() {
        width = 100;
        height = 100;
    }

    public DPUTemplateConfig_V1(int w, int h) {
        width = w;
        height = h;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
