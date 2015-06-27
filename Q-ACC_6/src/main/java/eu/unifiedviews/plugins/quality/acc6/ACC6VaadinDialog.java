package eu.unifiedviews.plugins.quality.acc6;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.ArrayList;
import java.util.Map;

public class ACC6VaadinDialog extends AbstractDialog<ACC6Config_V1> {

    private GridLayout propertiesGridLayout;

    private ComboBox regExp;
    private TextArea detailsText;

    public ACC6VaadinDialog() {
        super(ACC6.class);
    }

    @Override
    public void setConfiguration(ACC6Config_V1 c) throws DPUConfigException {
        ArrayList<String> subject = c.getSubject();
        ArrayList<String> property = c.getProperty();
        ArrayList<String> regExp = c.getRegularExpression();

        propertiesGridLayout.removeAllComponents();

        this.addColumnToPropertyMappingsHeading();

        for (int i = 0; i < subject.size(); ++i) {
            if (!subject.get(i).trim().equals("") && !property.get(i).trim().equals("") && !regExp.get(i).trim().equals("")) {
                this.addColumnToPropertyMapping(subject.get(i), property.get(i), regExp.get(i));
            }
        }
        if (subject.size() == 0)
            this.addColumnToPropertyMapping("", "", "");
    }

    @Override
    public ACC6Config_V1 getConfiguration() throws DPUConfigException {
        final ACC6Config_V1 c = new ACC6Config_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();
        ArrayList<String> regExp = new ArrayList<>();

        String error = fieldsValidation();

        if (!error.isEmpty()) {
            throw new DPUConfigException(error);
        } else {
            for (int row = 1; row < propertiesGridLayout.getRows(); row++) {
                String txtSubject = ((TextField) this.propertiesGridLayout.getComponent(0, row)).getValue();
                String txtProperty = ((TextField) this.propertiesGridLayout.getComponent(1, row)).getValue();
                String txtRegExp = (String)((ComboBox) this.propertiesGridLayout.getComponent(2, row)).getValue();

                subject.add(row-1, txtSubject);
                property.add(row-1, txtProperty);
                regExp.add(row-1, txtRegExp);

            }
        }

        c.setSubject(subject);
        c.setProperty(property);
        c.setRegularExpression(regExp);

        return c;
    }

    public String fieldValidation(int column, int row) {
        String txtValue;
        String error = "";

        Component cmp = propertiesGridLayout.getComponent(column, row);

        if (column == 0) {
            TextField txt = (TextField)cmp;
            txtValue = txt.getValue().toLowerCase().trim();
            if (txtValue.isEmpty()) {
                error = error + "\n" + ctx.tr("ACC6.error.subject.not.filled") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("ACC6.error.subject.not.filled")));
            } else if (!txtValue.startsWith("http://")) {
                error = error + "\n" + ctx.tr("ACC6.error.subject.not.http") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("ACC6.error.subject.not.http")));
            } else if (txtValue.contains(" ")) {
                error = error + "\n" + ctx.tr("ACC6.error.subject.whitespace") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("ACC6.error.subject.whitespace")));
            } else {
                txt.setComponentError(null);
            }
        } else if (column == 1) {
            TextField txt = (TextField)cmp;
            txtValue = txt.getValue().toLowerCase().trim();
            if (txtValue.isEmpty()) {
                error = error + "\n" + ctx.tr("ACC6.error.property.not.filled") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("ACC6.error.property.not.filled")));
            } else if (!txtValue.startsWith("http://")) {
                error = error + "\n" + ctx.tr("ACC6.error.property.not.http") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("ACC6.error.property.not.http")));
            } else if (txtValue.contains(" ")) {
                error = error + "\n" + ctx.tr("ACC6.error.property.whitespace") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("ACC6.error.property.whitespace")));
            } else {
                txt.setComponentError(null);
            }
        } else {
            ComboBox combo = (ComboBox)cmp;
            txtValue = ((String)(combo.getValue()));

            ACC6Config_V1 c = new ACC6Config_V1();
            Map<String, String> filters = c.getFilters();

            if (txtValue == null || txtValue.toLowerCase().trim().isEmpty()) {
                error = error + "\n" + ctx.tr("ACC6.error.regex.not.filled") + " [Row: " + row + "]";
                combo.setComponentError(new UserError(ctx.tr("ACC6.error.regex.not.filled")));
            } else if (txtValue.toLowerCase().trim().contains(" ") && !filters.containsKey(txtValue)) {
                error = error + "\n" + ctx.tr("ACC6.error.regex.whitespace") + " [Row: " + row + "]";
                combo.setComponentError(new UserError(ctx.tr("ACC6.error.regex.whitespace")));
            } else {
                combo.setComponentError(null);
            }

            if (!error.isEmpty())
                combo.removeItem(txtValue);
        }

        return error;
    }

    public String fieldsValidation () {

        String errors = "";

        for (int i = 1; i < propertiesGridLayout.getRows(); i++) {
            errors = errors + fieldValidation(0, i);
            errors = errors + fieldValidation(1, i);
            errors = errors + fieldValidation(2, i);
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
        this.propertiesGridLayout = new GridLayout(4, 2);
        this.propertiesGridLayout.setWidth("100%");
        this.addColumnToPropertyMappingsHeading();

        TextField txtSubject = new TextField();
        txtSubject.setRequired(true);
        this.propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        TextField txtProperty = new TextField();
        txtProperty.setRequired(true);
        this.propertiesGridLayout.addComponent(txtProperty);
        txtProperty.setWidth("100%");

        this.regExp = new ComboBox();
        this.regExp.setFilteringMode(FilteringMode.CONTAINS);

        initComboBox();

        this.regExp.setNewItemsAllowed(true);
        this.regExp.setRequired(true);
        this.propertiesGridLayout.addComponent(regExp);
        this.regExp.setWidth("100%");

        mainLayout.addComponent(baseFormLayout);
        mainLayout.addComponent(propertiesGridLayout);

        Button btnAddRow = new Button(ctx.tr("ACC6.button.add"));
        btnAddRow.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addColumnToPropertyMapping("", "", "");
            }
        });


        Label detailsLabel = new Label(ctx.tr("ACC6.details.label"));
        this.detailsText = new TextArea();
        this.detailsText.setWidth("100%");
        this.detailsText.setReadOnly(true);


        VerticalLayout baseFormLayoutSecond = new VerticalLayout();
        baseFormLayoutSecond.setSpacing(true);
        baseFormLayoutSecond.addComponent(detailsLabel);
        baseFormLayoutSecond.addComponent(this.detailsText);
        baseFormLayoutSecond.addComponent(btnAddRow);
        mainLayout.addComponent(baseFormLayoutSecond);

        setCompositionRoot(mainLayout);
    }

    private void addColumnToPropertyMapping(final String subject, final String property, final String regExp) {

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
        this.propertiesGridLayout.addComponent(txtSubject);

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
        this.propertiesGridLayout.addComponent(txtProperty);

        final ComboBox copy = new ComboBox();
        copy.setContainerDataSource(this.regExp);
        copy.setNewItemsAllowed(true);
        copy.setRequired(true);
        copy.setWidth("100%");
        copy.setFilteringMode(FilteringMode.CONTAINS);
        if (regExp != null && !regExp.isEmpty()) {
            copy.addItem(regExp);
            copy.setValue(regExp);
        }

        copy.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                int row = propertiesGridLayout.getComponentArea(copy).getRow1();
                String error = fieldValidation(2, row);
                if (error.equals(""))
                    setRegexDetails(copy.getValue().toString());
            }
        });
        copy.addFocusListener(new FieldEvents.FocusListener() {
            @Override
            public void focus(FieldEvents.FocusEvent focusEvent) {
                if (((ComboBox) (focusEvent.getComponent())).getValue() != null)
                    setRegexDetails(((ComboBox) (focusEvent.getComponent())).getValue().toString());
            }
        });

        copy.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (valueChangeEvent.getProperty().getValue() != null)
                    setRegexDetails(valueChangeEvent.getProperty().getValue().toString());
            }
        });

        this.propertiesGridLayout.addComponent(copy);

        final Button btnRemoveRow = new Button(ctx.tr("ACC6.button.remove"));
        btnRemoveRow.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                propertiesGridLayout.removeRow(propertiesGridLayout.getComponentArea(btnRemoveRow).getRow1());
                if (propertiesGridLayout.getRows() == 1) {
                    addColumnToPropertyMapping("", "", "");
                }
            }
        });
        propertiesGridLayout.addComponent(btnRemoveRow);
    }

    private void setRegexDetails(String regex) {
        String value = ctx.tr("ACC6." + regex.replace(" ","."));
        if (value.equals("ACC6." + regex.replace(" ",".")))
            value = ctx.tr("ACC6.Default");
        this.detailsText.setReadOnly(false);
        this.detailsText.setValue(value);
        this.detailsText.setReadOnly(true);
    }

    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("ACC6.resource.type")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("ACC6.property")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("ACC6.regular.expression")));
        propertiesGridLayout.addComponent(new Label(""));
    }

    private void initComboBox(){
        ACC6Config_V1 c = new ACC6Config_V1();
        for (String key: c.getFilters().keySet())
            regExp.addItem(key);
    }
}