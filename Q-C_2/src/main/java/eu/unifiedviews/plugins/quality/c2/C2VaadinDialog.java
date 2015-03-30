package eu.unifiedviews.plugins.quality.c2;

import java.util.ArrayList;

import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class C2VaadinDialog extends AbstractDialog<C2Config_V1> {

    private GridLayout propertiesGridLayout;

    public C2VaadinDialog() {
        super(C2.class);
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

        propertiesGridLayout = new GridLayout(2, 2);
        propertiesGridLayout.setWidth("100%");
        propertiesGridLayout.setColumnExpandRatio(0, 1);
        propertiesGridLayout.setColumnExpandRatio(1, 1);

        this.addColumnToPropertyMappingsHeading();

        TextField txtSubject = new TextField();
        propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        TextField txtProperty = new TextField();
        propertiesGridLayout.addComponent(txtProperty);
        txtProperty.setWidth("100%");

        mainLayout.addComponent(baseFormLayout);
        mainLayout.addComponent(propertiesGridLayout);

        Button btnAddRow = new Button(ctx.tr("C2.button.add"));
        btnAddRow.addClickListener(new ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(ClickEvent event) {
                int lastRow = propertiesGridLayout.getRows() - 1;
                if (lastRow > 0) {
                    String txtSubject = ((TextField) propertiesGridLayout.getComponent(0, lastRow)).getValue();
                    String txtProperty = ((TextField) propertiesGridLayout.getComponent(1, lastRow)).getValue();
                    if (txtSubject.isEmpty() || txtProperty.isEmpty()) {
                        if (txtSubject.isEmpty())
                            ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(new UserError("All field must be filled"));
                        else
                            ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(null);
                        if (txtProperty.isEmpty())
                            ((TextField) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(new UserError("All field must be filled"));
                        else
                            ((TextField) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(null);
                    } else {
                        ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(null);
                        ((TextField) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(null);
                        addColumnToPropertyMapping("", "");
                    }
                } else {
                    addColumnToPropertyMapping("", "");
                }
            }
        });

        Button btnRemoveRow = new Button(ctx.tr("C2.button.remove"));
        btnRemoveRow.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                int lastRow = propertiesGridLayout.getRows() - 1;
                String txtSubject = "";
                String txtProperty = "";
                if (lastRow > 0) {
                    txtSubject = ((TextField) propertiesGridLayout.getComponent(0, lastRow)).getValue();
                    txtProperty = ((TextField) propertiesGridLayout.getComponent(1, lastRow)).getValue();
                    propertiesGridLayout.removeRow(lastRow);
                    addColumnToPropertyMapping("", "");
                }
                if (lastRow > 1 && txtSubject.isEmpty() && txtProperty.isEmpty()) {
                    propertiesGridLayout.removeRow(lastRow - 1);
                }
            }
        });

        Button btnRemoveRows = new Button(ctx.tr("C2.button.remove.all"));
        btnRemoveRows.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                removeAllColumnToPropertyMappings();
                addColumnToPropertyMapping("", "");
            }
        });

        baseFormLayout = new FormLayout();
        baseFormLayout.setSizeUndefined();

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
        txtSubject.setRequired(true);
        propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        TextField txtProperty = new TextField();
        txtProperty.setRequired(true);
        propertiesGridLayout.addComponent(txtProperty);
        txtProperty.setWidth("100%");

        if (subject != null) {
            txtSubject.setValue(subject);
        }

        if (property != null) {
            txtProperty.setValue(property);
        }
    }

    private void removeAllColumnToPropertyMappings() {
        propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();
    }

    private void addColumnToPropertyMappingsHeading() {
        propertiesGridLayout.addComponent(new Label(ctx.tr("C2.subject.uri")));
        propertiesGridLayout.addComponent(new Label(ctx.tr("C2.property.uri")));
    }

    @Override
    protected void setConfiguration(C2Config_V1 config) throws DPUConfigException {

        ArrayList<String> subject = config.getSubject();
        ArrayList<String> property = config.getProperty();

        this.removeAllColumnToPropertyMappings();

        for (int i = 0; i < subject.size(); i++) {
            this.addColumnToPropertyMapping(subject.get(i), property.get(i));
        }

        this.addColumnToPropertyMapping("", "");
    }

    @Override
    protected C2Config_V1 getConfiguration() throws DPUConfigException {

        C2Config_V1 config = new C2Config_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();

        for (int row = 1; row < this.propertiesGridLayout.getRows(); row++) {

            String txtSubject = ((TextField) this.propertiesGridLayout.getComponent(0, row)).getValue();
            String txtProperty = ((TextField) this.propertiesGridLayout.getComponent(1, row)).getValue();

            if (!txtSubject.isEmpty() && !txtProperty.isEmpty()) {
                subject.add(row-1, txtSubject);
                property.add(row-1, txtProperty);
            }
        }

        config.setSubject(subject);
        config.setProperty(property);

        return config;
    }
}
