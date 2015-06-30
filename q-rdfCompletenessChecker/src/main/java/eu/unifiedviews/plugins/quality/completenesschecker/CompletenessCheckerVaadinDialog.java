package eu.unifiedviews.plugins.quality.completenesschecker;

import java.util.ArrayList;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class CompletenessCheckerVaadinDialog extends AbstractDialog<CompletenessCheckerConfig_V1> {

    private GridLayout propertiesGridLayout;

    public CompletenessCheckerVaadinDialog() {
        super(CompletenessChecker.class);
    }

    @Override
    protected void buildDialogLayout() {

        VerticalLayout mainLayout = new VerticalLayout();
        FormLayout baseFormLayout = new FormLayout();

        mainLayout.setSpacing(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        baseFormLayout.setSizeUndefined();

        propertiesGridLayout = new GridLayout(3, 2);
        propertiesGridLayout.setWidth("100%");
        propertiesGridLayout.setColumnExpandRatio(0, 3.0f);
        propertiesGridLayout.setColumnExpandRatio(1, 3.0f);
        propertiesGridLayout.setColumnExpandRatio(2, 1.0f);

        mainLayout.addComponent(baseFormLayout);
        mainLayout.addComponent(propertiesGridLayout);

        Button btnAddRow = new Button(ctx.tr("C2.button.add"));
        btnAddRow.addClickListener(new ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(ClickEvent event) {
                addColumnToPropertyMapping("", "");
            }
        });

        baseFormLayout = new FormLayout();
        baseFormLayout.setSizeUndefined();

        HorizontalLayout baseFormLayoutSecond = new HorizontalLayout();
        baseFormLayoutSecond.setSpacing(true);
        baseFormLayoutSecond.addComponent(btnAddRow);

        mainLayout.addComponent(baseFormLayoutSecond);

        setCompositionRoot(mainLayout);
    }

    private void addColumnToPropertyMappingsHeading() {
        propertiesGridLayout.addComponent(new Label(ctx.tr("C2.subject.uri")));
        propertiesGridLayout.addComponent(new Label(ctx.tr("C2.property.uri")));
        propertiesGridLayout.addComponent(new Label(""));
    }

    private void addColumnToPropertyMapping(String subject, String property) {

        final TextField txtSubject = new TextField();
        txtSubject.setValue(subject);
        txtSubject.setRequired(true);
        txtSubject.setWidth("100%");
        txtSubject.setInputPrompt("http://");
        txtSubject.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                int row = propertiesGridLayout.getComponentArea(txtSubject).getRow1();
                fieldValidation(0, row);
            }
        });

        final TextField txtProperty = new TextField();
        txtProperty.setValue(property);
        txtProperty.setRequired(true);
        txtProperty.setWidth("100%");
        txtProperty.setInputPrompt("http://");
        txtProperty.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                int row = propertiesGridLayout.getComponentArea(txtProperty).getRow1();
                fieldValidation(1, row);
            }
        });

        final Button btnRemoveRow = new Button(ctx.tr("C2.button.remove"));
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

        propertiesGridLayout.addComponent(txtSubject);
        propertiesGridLayout.addComponent(txtProperty);
        propertiesGridLayout.addComponent(btnRemoveRow);
    }


    public String fieldsValidation () {

        String errors = "";

        for (int i = 1; i < propertiesGridLayout.getRows(); i++) {
            errors = errors + fieldValidation(0, i);
            errors = errors + fieldValidation(1, i);
        }

        return errors;
    }

    public String fieldValidation (int column, int row) {
        TextField txt = (TextField) propertiesGridLayout.getComponent(column, row);
        String txtValue = txt.getValue().toLowerCase().trim();

        String type;
        if (column == 0) {
            type = "subject";
        } else {
            type = "property";
        }

        String error = "";

        if (txtValue.isEmpty()) {
            error = error + "\n" + ctx.tr("C2.error."+ type +".not.filled") +" [Row: "+ row +"]";
            txt.setComponentError(new UserError(ctx.tr("C2.error."+ type +".not.filled")));
        } else if (!txtValue.startsWith("http://")) {
            error = error + "\n" + ctx.tr("C2.error."+ type +".not.filled") +" [Row: "+ row +"]";
            txt.setComponentError(new UserError(ctx.tr("C2.error."+ type +".not.http")));
        } else if (txtValue.contains(" ")) {
            error = error + "\n" + ctx.tr("C2.error."+ type +".not.filled") +" [Row: "+ row +"]";
            txt.setComponentError(new UserError(ctx.tr("C2.error."+ type +".whitespace")));
        } else {
            txt.setComponentError(null);
        }

        return error;
    }

    @Override
    protected void setConfiguration(CompletenessCheckerConfig_V1 config) throws DPUConfigException {

        ArrayList<String> subject = config.getSubject();
        ArrayList<String> property = config.getProperty();

        propertiesGridLayout.removeAllComponents();

        this.addColumnToPropertyMappingsHeading();

        if (subject.size() == 0) {
            this.addColumnToPropertyMapping("", "");
        } else {
            for (int i = 0; i < subject.size(); i++) {
                if (!subject.get(i).trim().equals("") && !property.get(i).trim().equals("")) {
                    this.addColumnToPropertyMapping(subject.get(i), property.get(i));
                }
            }
        }
    }

    @Override
    protected CompletenessCheckerConfig_V1 getConfiguration() throws DPUConfigException {

        CompletenessCheckerConfig_V1 config = new CompletenessCheckerConfig_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();

        String error = fieldsValidation();

        if (!error.isEmpty()) {
            throw new DPUConfigException(error);
        } else {
            for (int row = 1; row < propertiesGridLayout.getRows(); row++) {
                TextField txtSubject = ((TextField) propertiesGridLayout.getComponent(0, row));
                TextField txtProperty = ((TextField) propertiesGridLayout.getComponent(1, row));

                String txtSubjectValue = txtSubject.getValue().trim();
                String txtPropertyValue = txtProperty.getValue().trim();

                subject.add(row - 1, txtSubjectValue);
                property.add(row - 1, txtPropertyValue);
            }
        }

        config.setSubject(subject);
        config.setProperty(property);

        return config;
    }
}
