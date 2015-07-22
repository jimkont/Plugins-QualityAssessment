package eu.unifiedviews.plugins.quality.completenessimprovement;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.ArrayList;

public class CompletenessImprovementVaadinDialog extends AbstractDialog<CompletenessImprovementConfig_V1> {

    private GridLayout propertiesGridLayout;

    public CompletenessImprovementVaadinDialog() {
        super(CompletenessImprovement.class);
    }

    @Override
    public void setConfiguration(CompletenessImprovementConfig_V1 c) throws DPUConfigException {
        ArrayList<String> property_source = c.getProperty_source();
        ArrayList<String> property_target = c.getProperty_target();

        propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();

        for (int i = 0; i < property_source.size(); ++i) {
            if (!property_source.get(i).trim().equals("") && !property_target.get(i).trim().equals("")) {
                this.addColumnToPropertyMapping(property_source.get(i), property_target.get(i));
            }
        }
        if (property_source.size() == 0)
            this.addColumnToPropertyMapping("", "");
    }

    @Override
    public CompletenessImprovementConfig_V1 getConfiguration() throws DPUConfigException {
        final CompletenessImprovementConfig_V1 c = new CompletenessImprovementConfig_V1();

        ArrayList<String> property_source = new ArrayList<>();
        ArrayList<String> property_target = new ArrayList<>();

        String error = fieldsValidation();

        if (!error.isEmpty()) {
            throw new DPUConfigException(error);
        } else {
            for (int row = 1; row < this.propertiesGridLayout.getRows(); ++row) {

                String txtPropertySource = ((TextField) this.propertiesGridLayout.getComponent(0, row)).getValue();
                String txtPropertyTarget = ((TextField) this.propertiesGridLayout.getComponent(1, row)).getValue();

                if (!txtPropertySource.isEmpty() && !txtPropertyTarget.isEmpty()) {
                    property_source.add(row - 1, txtPropertySource);
                    property_target.add(row - 1, txtPropertyTarget);
                }
            }
        }

        c.setProperty_source(property_source);
        c.setProperty_target(property_target);

        return c;
    }

    public String fieldValidation(int column, int row) {

        String txtValue;
        String error = "";

        Component cmp = propertiesGridLayout.getComponent(column, row);

        TextField txt = (TextField) cmp;
        txtValue = txt.getValue().toLowerCase().trim();

        if (column == 0) {
            if (txtValue.isEmpty()) {
                error = error + "\n" + ctx.tr("C3.error.property_source.not.filled") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("C3.error.subject.not.filled")));
            } else if (!txtValue.startsWith("http://")) {
                error = error + "\n" + ctx.tr("C3.error.property_source.not.http") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("C3.error.property_source.not.http")));
            } else if (txtValue.contains(" ")) {
                error = error + "\n" + ctx.tr("C3.error.property_source.whitespace") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("C3.error.property_source.whitespace")));
            } else {
                txt.setComponentError(null);
            }
        } else if (column == 1) {
            if (txtValue.isEmpty()) {
                error = error + "\n" + ctx.tr("C3.error.property_target.not.filled") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("C3.error.property_target.not.filled")));
            } else if (!txtValue.startsWith("http://")) {
                error = error + "\n" + ctx.tr("C3.error.property_target.not.http") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("C3.error.property_target.not.http")));
            } else if (txtValue.contains(" ")) {
                error = error + "\n" + ctx.tr("C3.error.property_target.whitespace") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("C3.error.property_target.whitespace")));
            } else {
                txt.setComponentError(null);
            }
        }

        return error;
    }

    public String fieldsValidation () {

        String errors = "";

        for (int i = 1; i < propertiesGridLayout.getRows(); ++i) {
            errors = errors + fieldValidation(0, i);
            errors = errors + fieldValidation(1, i);
        }
        return errors;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        FormLayout baseFormLayout = new FormLayout();
        baseFormLayout.setSizeUndefined();
        this.propertiesGridLayout = new GridLayout(3, 2);
        this.propertiesGridLayout.setWidth("100%");
        this.addColumnToPropertyMappingsHeading();

        TextField txtPropertySource = new TextField();
        txtPropertySource.setRequired(true);
        this.propertiesGridLayout.addComponent(txtPropertySource);
        txtPropertySource.setWidth("100%");

        TextField txtPropertyTarget = new TextField();
        txtPropertyTarget.setRequired(true);
        this.propertiesGridLayout.addComponent(txtPropertyTarget);
        txtPropertyTarget.setWidth("100%");

        mainLayout.addComponent(baseFormLayout);
        mainLayout.addComponent(propertiesGridLayout);

        Button btnAddRow = new Button(ctx.tr("C3.button.add"));
        btnAddRow.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addColumnToPropertyMapping("", "");
            }
        });

        HorizontalLayout baseFormLayoutSecond = new HorizontalLayout();
        baseFormLayoutSecond.setSpacing(true);
        baseFormLayoutSecond.addComponent(btnAddRow);
        mainLayout.addComponent(baseFormLayoutSecond);

        setCompositionRoot(mainLayout);
    }

    private void addColumnToPropertyMapping(String property_source, String property_target) {

        final TextField txtPropertySource = new TextField();
        txtPropertySource.setValue(property_source);
        txtPropertySource.setRequired(true);
        txtPropertySource.setWidth("100%");
        txtPropertySource.setInputPrompt("http://");
        txtPropertySource.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                int row = propertiesGridLayout.getComponentArea(txtPropertySource).getRow1();
                fieldValidation(0, row);
            }
        });
        this.propertiesGridLayout.addComponent(txtPropertySource);

        final TextField txtPropertyTarget = new TextField();
        txtPropertyTarget.setValue(property_target);
        txtPropertyTarget.setRequired(true);
        txtPropertyTarget.setWidth("100%");
        txtPropertyTarget.setInputPrompt("http://");
        txtPropertyTarget.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                int row = propertiesGridLayout.getComponentArea(txtPropertyTarget).getRow1();
                fieldValidation(1, row);
            }
        });
        this.propertiesGridLayout.addComponent(txtPropertyTarget);

        final Button btnRemoveRow = new Button(ctx.tr("C3.button.remove"));
        btnRemoveRow.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                propertiesGridLayout.removeRow(propertiesGridLayout.getComponentArea(btnRemoveRow).getRow1());
                if (propertiesGridLayout.getRows() == 1) {
                    addColumnToPropertyMapping("", "");
                }
            }
        });
        propertiesGridLayout.addComponent(btnRemoveRow);
    }

    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("C3.property_source.uri")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("C3.property_target.uri")));
        this.propertiesGridLayout.addComponent(new Label(""));
    }
}
