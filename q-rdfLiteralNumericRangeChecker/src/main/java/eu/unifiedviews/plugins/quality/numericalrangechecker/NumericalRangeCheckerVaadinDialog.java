package eu.unifiedviews.plugins.quality.numericalrangechecker;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.regex.Pattern;

public class NumericalRangeCheckerVaadinDialog extends AbstractDialog<NumericalRangeCheckerConfig_V1> {

    private TextField classUri;
    private TextField property;
    private TextField lowerBound;
    private TextField upperBound;

    public NumericalRangeCheckerVaadinDialog() {
        super(NumericalRangeChecker.class);
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
        classUri.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                fieldValidation(classUri);
            }
        });
        baseFormLayout.addComponent(classUri);

        property = new TextField(ctx.tr("ACC4.property"));
        property.setHeight("-1px");
        property.setRequired(true);
        property.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                fieldValidation(property);
            }
        });
        baseFormLayout.addComponent(property);

        lowerBound = new TextField(ctx.tr("ACC4.lower.bound"));
        lowerBound.setHeight("-1px");
        lowerBound.setRequired(true);
        lowerBound.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                fieldValidation(lowerBound);
            }
        });
        baseFormLayout.addComponent(lowerBound);

        upperBound = new TextField(ctx.tr("ACC4.upper.bound"));
        upperBound.setHeight("-1px");
        upperBound.setRequired(true);
        upperBound.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                fieldValidation(upperBound);
            }
        });
        baseFormLayout.addComponent(upperBound);

        mainLayout.addComponent(baseFormLayout);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(NumericalRangeCheckerConfig_V1 config) throws DPUConfigException {
        classUri.setValue(config.getClassUri());
        property.setValue(config.getProperty());
        lowerBound.setValue(config.getLowerBound() + "");
        upperBound.setValue(config.getUpperBound() + "");
    }

    @Override
    protected NumericalRangeCheckerConfig_V1 getConfiguration() throws DPUConfigException {

        NumericalRangeCheckerConfig_V1 config = new NumericalRangeCheckerConfig_V1();

        String error = fieldsValidation();

        if (!error.isEmpty()) {
            throw new DPUConfigException(error);
        } else {
            config.setClassUri(classUri.getValue());
            config.setProperty(property.getValue());
            config.setLowerBound(Double.parseDouble(lowerBound.getValue()));
            config.setUpperBound(Double.parseDouble(upperBound.getValue()));
        }
        return config;
    }

    public String fieldValidation(TextField tf) {
        String txtValue = tf.getValue().toLowerCase().trim();
        String error = "";


        final String decimalPattern = "([0-9]+)((\\.([0-9]+))?)";

        if (tf == this.classUri) {
            if (txtValue.isEmpty()) {
                error = error + "\n" + ctx.tr("ACC4.error.subject.not.filled");
                tf.setComponentError(new UserError(ctx.tr("ACC4.error.subject.not.filled")));
            } else if (!txtValue.startsWith("http://")) {
                error = error + "\n" + ctx.tr("ACC4.error.subject.not.http");
                tf.setComponentError(new UserError(ctx.tr("ACC4.error.subject.not.http")));
            } else if (txtValue.contains(" ")) {
                error = error + "\n" + ctx.tr("ACC4.error.subject.whitespace");
                tf.setComponentError(new UserError(ctx.tr("ACC4.error.subject.whitespace")));
            } else {
                tf.setComponentError(null);
            }
        } else if (tf == this.property) {
            if (txtValue.isEmpty()) {
                error = error + "\n" + ctx.tr("ACC4.error.property.not.filled");
                tf.setComponentError(new UserError(ctx.tr("ACC4.error.property.not.filled")));
            } else if (!txtValue.startsWith("http://")) {
                error = error + "\n" + ctx.tr("ACC4.error.property.not.http");
                tf.setComponentError(new UserError(ctx.tr("ACC4.error.property.not.http")));
            } else if (txtValue.contains(" ")) {
                error = error + "\n" + ctx.tr("ACC4.error.property.whitespace");
                tf.setComponentError(new UserError(ctx.tr("ACC4.error.property.whitespace")));
            } else {
                tf.setComponentError(null);
            }
        } else if (tf == this.lowerBound){
            if (!Pattern.matches(decimalPattern, txtValue)) {
                error = error + "\n" + ctx.tr("ACC4.error.lower.bound.not.number");
                tf.setComponentError(new UserError(ctx.tr("ACC4.error.lower.bound.not.number")));
            } else {
                tf.setComponentError(null);
            }
        } else if (tf == this.upperBound){
            if (!Pattern.matches(decimalPattern, txtValue)) {
                error = error + "\n" + ctx.tr("ACC4.error.upper.bound.not.number");
                tf.setComponentError(new UserError(ctx.tr("ACC4.error.upper.bound.not.number")));
            } else {
                tf.setComponentError(null);
            }
        }

        return error;
    }

    public String fieldsValidation () {
        return fieldValidation(this.classUri) + fieldValidation(this.property) +
                fieldValidation(this.lowerBound) + fieldValidation(this.upperBound);
    }
}