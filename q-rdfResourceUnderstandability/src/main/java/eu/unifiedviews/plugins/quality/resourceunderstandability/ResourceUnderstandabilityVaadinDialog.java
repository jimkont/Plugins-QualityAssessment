package eu.unifiedviews.plugins.quality.resourceunderstandability;

import com.vaadin.server.UserError;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.ArrayList;

public class ResourceUnderstandabilityVaadinDialog extends AbstractDialog<ResourceUnderstandabilityConfig_V1> {

    private GridLayout propertiesGridLayout;
    private ComboBox properties;
    private ComboBox langs;

    public ResourceUnderstandabilityVaadinDialog() {
        super(ResourceUnderstandability.class);
    }

    @Override
    public void setConfiguration(ResourceUnderstandabilityConfig_V1 c) throws DPUConfigException {
        ArrayList<String> subject = c.getSubject();
        ArrayList<String> property = c.getProperty();
        ArrayList<String> lang = c.getLang();
        if (subject.size() > 0) {
            this.removeAllColumnToPropertyMappings();
            for (int i = 0; i < subject.size(); ++i) {
                this.addColumnToPropertyMapping(subject.get(i), property.get(i), lang.get(i));
            }
            this.addColumnToPropertyMapping("", "", "");
        }
    }

    @Override
    public ResourceUnderstandabilityConfig_V1 getConfiguration() throws DPUConfigException {
        final ResourceUnderstandabilityConfig_V1 c = new ResourceUnderstandabilityConfig_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();
        ArrayList<String> lang = new ArrayList<>();

        for (int row = 1; row < this.propertiesGridLayout.getRows(); ++row) {
            String txtSubject = ((TextField) this.propertiesGridLayout.getComponent(0, row)).getValue();
            String txtProperty = (String)((ComboBox) this.propertiesGridLayout.getComponent(1, row)).getValue();
            String txtLang = (String)((ComboBox) this.propertiesGridLayout.getComponent(2, row)).getValue();
            if (!txtSubject.isEmpty() && !txtProperty.isEmpty()) {
                subject.add(row-1, txtSubject);
                property.add(row-1, txtProperty);
                lang.add(row-1, txtLang);
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
        this.propertiesGridLayout = new GridLayout(3, 2);
        this.propertiesGridLayout.setWidth("100%");
        this.addColumnToPropertyMappingsHeading();

        TextField txtSubject = new TextField();
        txtSubject.setRequired(true);
        this.propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        this.properties = new ComboBox();
        this.properties.setFilteringMode(FilteringMode.CONTAINS);
        this.langs = new ComboBox();
        this.langs.setFilteringMode(FilteringMode.CONTAINS);
        initComboBox();
        this.properties.setNewItemsAllowed(true);
        this.properties.setRequired(true);
        this.propertiesGridLayout.addComponent(properties);
        this.properties.setWidth("100%");
        this.langs.setNewItemsAllowed(true);
        this.propertiesGridLayout.addComponent(langs);
        this.langs.setWidth("100%");

        mainLayout.addComponent(baseFormLayout);
        mainLayout.addComponent(propertiesGridLayout);
        Button btnAddRow = new Button(ctx.tr("C5.button.add"));
        btnAddRow.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                int lastRow = propertiesGridLayout.getRows() - 1;
                if (lastRow > 0) {
                    String txtSubject = ((TextField) propertiesGridLayout.getComponent(0, lastRow)).getValue();
                    String txtProperty = (String) ((ComboBox) propertiesGridLayout.getComponent(1, lastRow)).getValue();
                    if (txtSubject.isEmpty() || txtProperty == null) {
                        if (txtSubject.isEmpty())
                            ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(new UserError(ctx.tr("C5.input.empty")));
                        else
                            ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(null);
                        if (txtProperty == null)
                            ((ComboBox) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(new UserError(ctx.tr("C5.input.empty")));
                        else
                            ((ComboBox) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(null);
                    } else {
                        ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(null);
                        ((ComboBox) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(null);
                        addColumnToPropertyMapping("", "", "");
                    }
                } else {
                    addColumnToPropertyMapping("", "", "");
                }
            }
        });

        Button btnRemoveRow = new Button(ctx.tr("C5.button.remove"));
        btnRemoveRow.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                int lastRow = propertiesGridLayout.getRows() - 1;
                String txtSubject = "";
                String txtProperty = null;
                String txtLang = null;
                if (lastRow > 0) {
                    txtSubject = ((TextField) propertiesGridLayout.getComponent(0, lastRow)).getValue();
                    txtProperty = (String) ((ComboBox) propertiesGridLayout.getComponent(1, lastRow)).getValue();
                    txtLang = (String) ((ComboBox) propertiesGridLayout.getComponent(2, lastRow)).getValue();
                    propertiesGridLayout.removeRow(lastRow);
                    addColumnToPropertyMapping("", "", "");
                }
                if (lastRow > 1 && txtSubject.isEmpty() && txtProperty == null && txtLang == null) {
                    propertiesGridLayout.removeRow(lastRow - 1);
                }
            }
        });
        Button btnRemoveRows = new Button(ctx.tr("C5.button.remove.all"));
        btnRemoveRows.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                removeAllColumnToPropertyMappings();
                addColumnToPropertyMapping("", "", "");
            }
        });

        HorizontalLayout baseFormLayoutSecond = new HorizontalLayout();
        baseFormLayoutSecond.setSpacing(true);
        baseFormLayoutSecond.addComponent(btnAddRow);
        baseFormLayoutSecond.addComponent(btnRemoveRow);
        baseFormLayoutSecond.addComponent(btnRemoveRows);
        mainLayout.addComponent(baseFormLayoutSecond);

        setCompositionRoot(mainLayout);
    }

    private void addColumnToPropertyMapping(String subject, String property, String lang) {
        TextField txtSubject = new TextField();
        txtSubject.setRequired(true);
        this.propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        ComboBox copy = new ComboBox();
        copy.setContainerDataSource(this.properties);
        copy.setNewItemsAllowed(true);
        copy.setRequired(true);
        this.propertiesGridLayout.addComponent(copy);
        copy.setWidth("100%");

        ComboBox copy2 = new ComboBox();
        copy2.setContainerDataSource(this.langs);
        copy2.setNewItemsAllowed(true);
        this.propertiesGridLayout.addComponent(copy2);
        copy2.setWidth("100%");
        if (subject != null) {
            txtSubject.setValue(subject);
        }
        if (property != null && !property.isEmpty()) {
            copy.addItem(property);
            copy.setValue(property);
        }
        if (lang != null && !lang.isEmpty()) {
            copy2.addItem(lang);
            copy2.setValue(lang);
        }
    }

    private void removeAllColumnToPropertyMappings() {
        this.propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();
    }

    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("C5.resource.type")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("C5.property")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("C5.lang.tag")));
    }

    private void initComboBox(){
        ResourceUnderstandabilityConfig_V1 c = new ResourceUnderstandabilityConfig_V1();
        for (int i = 0; i < c.getProperties().size(); ++i)
            properties.addItem(c.getProperties().get(i));
        for (int i = 0; i < c.getLangs().size(); ++i)
            langs.addItem(c.getLangs().get(i));
    }
}
