package eu.unifiedviews.plugins.quality.mc;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.ArrayList;

/**
 * Vaadin configuration dialog for MC.
 *
 * @author Vincenzo Cutrona
 */
public class MCVaadinDialog extends AbstractDialog<MCConfig_V1> {

    private GridLayout propertiesGridLayout;
    private ComboBox properties;
    private ComboBox langs;

    public MCVaadinDialog() {
        super(MC.class);
    }

    @Override
    public void setConfiguration(MCConfig_V1 c) throws DPUConfigException {
        ArrayList<String> subject = c.getSubject();
        ArrayList<String> property = c.getProperty();
        ArrayList<String> lang = c.getLang();

        this.propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();

        for (int i = 0; i < subject.size(); ++i) {
            if (!subject.get(i).trim().equals("") && !property.get(i).trim().equals("")) {
                this.addColumnToPropertyMapping(subject.get(i), property.get(i), lang.get(i));
            }
        }
        if (subject.size() == 0)
            this.addColumnToPropertyMapping("", "", "");

    }

    @Override
    public MCConfig_V1 getConfiguration() throws DPUConfigException {
        final MCConfig_V1 c = new MCConfig_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();
        ArrayList<String> lang = new ArrayList<>();

        String error = fieldsValidation();

        if (!error.isEmpty()) {
            throw new DPUConfigException(error);
        } else {
            for (int row = 1; row < this.propertiesGridLayout.getRows(); ++row) {
                String txtSubject = ((TextField) this.propertiesGridLayout.getComponent(0, row)).getValue();
                String txtProperty = (String) ((ComboBox) this.propertiesGridLayout.getComponent(1, row)).getValue();
                String txtLang = (String) ((ComboBox) this.propertiesGridLayout.getComponent(2, row)).getValue();
                if (!txtSubject.isEmpty() && !txtProperty.isEmpty()) {
                    subject.add(row - 1, txtSubject);
                    property.add(row - 1, txtProperty);
                    lang.add(row - 1, txtLang);
                }
            }
        }
        c.setSubject(subject);
        c.setProperty(property);
        c.setLang(lang);
        return c;
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

        this.properties = new ComboBox();
        this.langs = new ComboBox();
        properties.setFilteringMode(FilteringMode.CONTAINS);
        langs.setFilteringMode(FilteringMode.CONTAINS);


        initComboBox();

        this.properties.setNewItemsAllowed(true);
        this.properties.setRequired(true);
        this.properties.setWidth("100%");
        this.propertiesGridLayout.addComponent(properties);

        this.langs.setNewItemsAllowed(true);
        this.langs.setWidth("100%");
        this.propertiesGridLayout.addComponent(langs);

        mainLayout.addComponent(baseFormLayout);
        mainLayout.addComponent(propertiesGridLayout);
        Button btnAddRow = new Button(ctx.tr("MC.button.add"));
        btnAddRow.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addColumnToPropertyMapping("", "", "");
            }
        });


        HorizontalLayout baseFormLayoutSecond = new HorizontalLayout();
        baseFormLayoutSecond.setSpacing(true);
        baseFormLayoutSecond.addComponent(btnAddRow);
        mainLayout.addComponent(baseFormLayoutSecond);

        setCompositionRoot(mainLayout);
    }

    private void addColumnToPropertyMapping(final String subject, final String property, final String lang) {

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

        final ComboBox copy = new ComboBox();
        copy.setContainerDataSource(this.properties);
        copy.setNewItemsAllowed(true);
        copy.setRequired(true);
        copy.setWidth("100%");
        copy.setFilteringMode(FilteringMode.CONTAINS);

        if (property != null && !property.isEmpty()) {
            copy.addItem(property);
            copy.setValue(property);
        }
        copy.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                int row = propertiesGridLayout.getComponentArea(copy).getRow1();
                fieldValidation(1, row);
            }
        });
        this.propertiesGridLayout.addComponent(copy);

        final ComboBox copy2 = new ComboBox();
        copy2.setContainerDataSource(this.langs);
        copy2.setNewItemsAllowed(true);
        copy2.setWidth("100%");
        copy2.setFilteringMode(FilteringMode.CONTAINS);
        if (lang != null && !lang.isEmpty()) {
            copy2.addItem(lang);
            copy2.setValue(lang);
        }
        copy2.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent blurEvent) {
                int row = propertiesGridLayout.getComponentArea(copy2).getRow1();
                fieldValidation(2, row);
            }
        });
        this.propertiesGridLayout.addComponent(copy2);

        final Button btnRemoveRow = new Button(ctx.tr("MC.button.remove"));
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

    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("MC.resource.type")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("MC.property")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("MC.lang.tag")));
        this.propertiesGridLayout.addComponent(new Label(""));
    }

    private void initComboBox() {
        MCConfig_V1 c = new MCConfig_V1();
        for (int i = 0; i < c.getProperties().size(); ++i)
            properties.addItem(c.getProperties().get(i));
        for (int i = 0; i < c.getLangs().size(); ++i)
            langs.addItem(c.getLangs().get(i));
    }

    public String fieldsValidation() {

        String errors = "";

        for (int i = 1; i < propertiesGridLayout.getRows(); ++i) {
            errors = errors + fieldValidation(0, i);
            errors = errors + fieldValidation(1, i);
            errors = errors + fieldValidation(2, i);
        }
        return errors;
    }

    public String fieldValidation(int column, int row) {
        String txtValue;
        String error = "";

        Component cmp = propertiesGridLayout.getComponent(column, row);

        if (column == 0) {
            TextField txt = (TextField)cmp;
            txtValue = txt.getValue().toLowerCase().trim();
            if (txtValue.isEmpty()) {
                error = error + "\n" + ctx.tr("MC.error.subject.not.filled") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("MC.error.subject.not.filled")));
            } else if (!txtValue.startsWith("http://")) {
                error = error + "\n" + ctx.tr("MC.error.subject.not.http") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("MC.error.subject.not.http")));
            } else if (txtValue.contains(" ")) {
                error = error + "\n" + ctx.tr("MC.error.subject.whitespace") + " [Row: " + row + "]";
                txt.setComponentError(new UserError(ctx.tr("MC.error.subject.whitespace")));
            } else {
                txt.setComponentError(null);
            }
        } else if (column == 1) {
            ComboBox combo = (ComboBox)cmp;
            txtValue = ((String)(combo.getValue()));

            if (txtValue == null || txtValue.toLowerCase().trim().isEmpty()) {
                error = error + "\n" + ctx.tr("MC.error.property.not.filled") + " [Row: " + row + "]";
                combo.setComponentError(new UserError(ctx.tr("MC.error.property.not.filled")));
            } else if (!txtValue.toLowerCase().trim().startsWith("http://")) {
                error = error + "\n" + ctx.tr("MC.error.property.not.http") + " [Row: " + row + "]";
                combo.setComponentError(new UserError(ctx.tr("MC.error.property.not.http")));
            } else if (txtValue.toLowerCase().trim().contains(" ")) {
                error = error + "\n" + ctx.tr("MC.error.property.whitespace") + " [Row: " + row + "]";
                combo.setComponentError(new UserError(ctx.tr("MC.error.property.whitespace")));
            } else {
                combo.setComponentError(null);
            }

            if (!error.isEmpty())
                combo.removeItem(txtValue);

        } else {
            ComboBox combo = (ComboBox)cmp;
            txtValue = ((String)(combo.getValue()));

            if (txtValue != null && txtValue.toLowerCase().trim().contains(" ")) {
                error = error + "\n" + ctx.tr("MC.error.lang.whitespace") + " [Row: " + row + "]";
                combo.setComponentError(new UserError(ctx.tr("MC.error.lang.whitespace")));
            } else {
                combo.setComponentError(null);
            }

            if (!error.isEmpty())
                combo.removeItem(txtValue);

        }

        return error;
    }
}