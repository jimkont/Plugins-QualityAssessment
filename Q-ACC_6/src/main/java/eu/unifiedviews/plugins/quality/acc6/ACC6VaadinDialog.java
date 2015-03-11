package eu.unifiedviews.plugins.quality.acc6;

import com.vaadin.server.UserError;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.ArrayList;

/**
 * Vaadin configuration dialog for ACC6.
 *
 * @author Vincenzo Cutrona
 */
public class ACC6VaadinDialog extends AbstractDialog<ACC6Config_V1> {

    private GridLayout propertiesGridLayout;

    private ComboBox regExp;

    public ACC6VaadinDialog() {
        super(ACC6.class);
    }

    @Override
    public void setConfiguration(ACC6Config_V1 c) throws DPUConfigException {
        ArrayList<String> subject = c.getSubject();
        ArrayList<String> property = c.getProperty();
        ArrayList<String> regExp = c.getRegularExpression();

        if (subject.size() > 0) {

            this.removeAllColumnToPropertyMappings();

            for (int i = 0; i < subject.size(); ++i) {
                this.addColumnToPropertyMapping(subject.get(i), property.get(i), regExp.get(i));
            }

            this.addColumnToPropertyMapping("", "", "");
        }
    }

    @Override
    public ACC6Config_V1 getConfiguration() throws DPUConfigException {
        final ACC6Config_V1 c = new ACC6Config_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();
        ArrayList<String> regExp = new ArrayList<>();

        for (int row = 1; row < this.propertiesGridLayout.getRows(); ++row) {

            String txtSubject = ((TextField) this.propertiesGridLayout.getComponent(0, row)).getValue();
            String txtProperty = ((TextField) this.propertiesGridLayout.getComponent(1, row)).getValue();
            String txtRegExp = (String)((ComboBox) this.propertiesGridLayout.getComponent(2, row)).getValue();

            if (!txtSubject.isEmpty() && !txtProperty.isEmpty() && !txtRegExp.isEmpty()) {
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
        this.propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        TextField txtProperty = new TextField();
        this.propertiesGridLayout.addComponent(txtProperty);
        txtProperty.setWidth("100%");

        this.regExp = new ComboBox();
        this.regExp.setFilteringMode(FilteringMode.CONTAINS);

        initComboBox();

        this.regExp.setNewItemsAllowed(true);
        this.propertiesGridLayout.addComponent(regExp);
        this.regExp.setWidth("100%");

        mainLayout.addComponent(baseFormLayout);
        mainLayout.addComponent(propertiesGridLayout);

        Button btnAddRow = new Button(ctx.tr("ACC6.button.add"));
        btnAddRow.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                int lastRow = propertiesGridLayout.getRows() - 1;
                if (lastRow > 0) {
                    String txtSubject = ((TextField) propertiesGridLayout.getComponent(0, lastRow)).getValue();
                    String txtProperty = ((TextField) propertiesGridLayout.getComponent(1, lastRow)).getValue();
                    String txtRegExp = (String) ((ComboBox) propertiesGridLayout.getComponent(2, lastRow)).getValue();

                    if (txtSubject.isEmpty() || txtProperty.isEmpty() || txtRegExp == null) {
                        if (txtSubject.isEmpty())
                            ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(new UserError("All field must be filled"));
                        else
                            ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(null);
                        if (txtProperty.isEmpty())
                            ((TextField) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(new UserError("All field must be filled"));
                        else
                            ((TextField) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(null);
                        if (txtRegExp == null)
                            ((ComboBox) propertiesGridLayout.getComponent(2, lastRow)).setComponentError(new UserError("All field must be filled"));
                        else
                            ((ComboBox) propertiesGridLayout.getComponent(2, lastRow)).setComponentError(null);
                    } else {
                        ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(null);
                        ((TextField) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(null);
                        ((ComboBox) propertiesGridLayout.getComponent(2, lastRow)).setComponentError(null);

                        addColumnToPropertyMapping("", "", "");
                    }
                } else {
                    addColumnToPropertyMapping("", "", "");
                }
            }
        });

        Button btnRemoveRow = new Button(ctx.tr("ACC6.button.remove"));
        btnRemoveRow.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                int lastRow = propertiesGridLayout.getRows() - 1;
                String txtSubject = "";
                String txtProperty = "";
                String txtRegExp = null;
                if (lastRow > 0) {
                    txtSubject = ((TextField) propertiesGridLayout.getComponent(0, lastRow)).getValue();
                    txtProperty = ((TextField) propertiesGridLayout.getComponent(1, lastRow)).getValue();
                    txtRegExp = (String) ((ComboBox) propertiesGridLayout.getComponent(2, lastRow)).getValue();
                    propertiesGridLayout.removeRow(lastRow);
                    addColumnToPropertyMapping("", "", "");
                }
                if (lastRow > 1 && txtSubject.isEmpty() && txtProperty.isEmpty() && txtRegExp == null) {
                    propertiesGridLayout.removeRow(lastRow - 1);
                }
            }
        });

        Button btnRemoveRows = new Button(ctx.tr("ACC6.button.remove.all"));
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

    private void addColumnToPropertyMapping(String subject, String property, String regExp) {

        TextField txtSubject = new TextField();
        this.propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        TextField txtProperty = new TextField();
        this.propertiesGridLayout.addComponent(txtProperty);
        txtProperty.setWidth("100%");

        ComboBox copy = new ComboBox();
        copy.setContainerDataSource(this.regExp);
        copy.setNewItemsAllowed(true);
        this.propertiesGridLayout.addComponent(copy);
        copy.setWidth("100%");

        if (subject != null) {
            txtSubject.setValue(subject);
        }

        if (property != null) {
            txtProperty.setValue(property);
        }

        if (regExp != null && !regExp.isEmpty()) {
            copy.addItem(regExp);
            copy.setValue(regExp);
        }
    }

    private void removeAllColumnToPropertyMappings() {
        this.propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();
    }

    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("ACC6.resource.type")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("ACC6.property")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("ACC6.regular.expression")));
    }

    private void initComboBox(){
        ACC6Config_V1 c = new ACC6Config_V1();
        for (String key: c.getFilters().keySet())
            regExp.addItem(key);
    }
}