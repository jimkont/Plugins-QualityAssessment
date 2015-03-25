package eu.unifiedviews.plugins.quality.rdfunit;

import com.vaadin.server.UserError;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.ArrayList;

public class RDFUnitVaadinDialog extends AbstractDialog<RDFUnitConfig_V1> {

    private GridLayout propertiesGridLayout;
    private ComboBox properties;

    public RDFUnitVaadinDialog() {
        super(RDFUnit.class);
    }

    @Override
    public void setConfiguration(RDFUnitConfig_V1 c) throws DPUConfigException {
        ArrayList<String> subject = c.getSubject();
        ArrayList<String> property = c.getProperty();
        if (subject.size() > 0) {
            this.removeAllColumnToPropertyMappings();
            for (int i = 0; i < subject.size(); ++i) {
                this.addColumnToPropertyMapping(subject.get(i), property.get(i));
            }
            this.addColumnToPropertyMapping("", "");
        }
    }

    @Override
    public RDFUnitConfig_V1 getConfiguration() throws DPUConfigException {
        final RDFUnitConfig_V1 c = new RDFUnitConfig_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();

        for (int row = 1; row < this.propertiesGridLayout.getRows(); ++row) {
            String txtSubject = ((TextField) this.propertiesGridLayout.getComponent(0, row)).getValue();
            String txtProperty = (String)((ComboBox) this.propertiesGridLayout.getComponent(1, row)).getValue();
            if (!txtSubject.isEmpty() && !txtProperty.isEmpty()) {
                subject.add(row-1, txtSubject);
                property.add(row-1, txtProperty);
            }
        }
        c.setSubject(subject);
        c.setProperty(property);
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
        this.propertiesGridLayout = new GridLayout(2, 2);
        this.propertiesGridLayout.setWidth("100%");
        this.addColumnToPropertyMappingsHeading();

        TextField txtSubject = new TextField();
        this.propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        this.properties = new ComboBox();
        this.properties.setFilteringMode(FilteringMode.CONTAINS);
        initComboBox();
        this.properties.setNewItemsAllowed(true);
        this.propertiesGridLayout.addComponent(properties);
        this.properties.setWidth("100%");

        mainLayout.addComponent(baseFormLayout);
        mainLayout.addComponent(propertiesGridLayout);
        Button btnAddRow = new Button(ctx.tr("RDFUnit.button.add"));
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
                            ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(new UserError("All field must be filled"));
                        else
                            ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(null);
                        if (txtProperty == null)
                            ((ComboBox) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(new UserError("All field must be filled"));
                        else
                            ((ComboBox) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(null);
                    } else {
                        ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(null);
                        ((ComboBox) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(null);
                        addColumnToPropertyMapping("", "");
                    }
                } else {
                    addColumnToPropertyMapping("", "");
                }
            }
        });

        Button btnRemoveRow = new Button(ctx.tr("RDFUnit.button.remove"));
        btnRemoveRow.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                int lastRow = propertiesGridLayout.getRows() - 1;
                String txtSubject = "";
                String txtProperty = null;
                if (lastRow > 0) {
                    txtSubject = ((TextField) propertiesGridLayout.getComponent(0, lastRow)).getValue();
                    txtProperty = (String) ((ComboBox) propertiesGridLayout.getComponent(1, lastRow)).getValue();
                    propertiesGridLayout.removeRow(lastRow);
                    addColumnToPropertyMapping("", "");
                }
                if (lastRow > 1 && txtSubject.isEmpty() && txtProperty == null) {
                    propertiesGridLayout.removeRow(lastRow - 1);
                }
            }
        });
        Button btnRemoveRows = new Button(ctx.tr("RDFUnit.button.remove.all"));
        btnRemoveRows.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                removeAllColumnToPropertyMappings();
                addColumnToPropertyMapping("", "");
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

    private void addColumnToPropertyMapping(String subject, String property) {
        TextField txtSubject = new TextField();
        this.propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        ComboBox copy = new ComboBox();
        copy.setContainerDataSource(this.properties);
        copy.setNewItemsAllowed(true);
        this.propertiesGridLayout.addComponent(copy);
        copy.setWidth("100%");
        if (subject != null) {
            txtSubject.setValue(subject);
        }
        if (property != null && !property.isEmpty()) {
            copy.addItem(property);
            copy.setValue(property);
        }
    }

    private void removeAllColumnToPropertyMappings() {
        this.propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();
    }

    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("RDFUnit.resource.type")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("RDFUnit.property")));
    }

    private void initComboBox(){
        RDFUnitConfig_V1 c = new RDFUnitConfig_V1();
        for (int i = 0; i < c.getProperties().size(); ++i)
            properties.addItem(c.getProperties().get(i));
    }
}
