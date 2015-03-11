package eu.unifiedviews.plugins.quality.c2;

import java.util.ArrayList;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class C2VaadinDialog extends AbstractDialog<C2Config_V1> {

    private GridLayout propertiesGridLayout;

    private Notification warn = new Notification(
            "Empty Field",
            "A field is left blank, so it will be removed.",
            Notification.Type.WARNING_MESSAGE
    );

    public C2VaadinDialog() {
        super(C2.class);
    }

    @Override
    protected void buildDialogLayout() {

        this.setWidth("100%");
        this.setHeight("100%");

        VerticalLayout mainLayout = new VerticalLayout();
        FormLayout baseFormLayout = new FormLayout();
        FormLayout baseFormLayoutSecond = new FormLayout();

        mainLayout.setImmediate(false);
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

        Button btnAddRow = new Button("Add");
        btnAddRow.addClickListener(new ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(ClickEvent event) {
                checkValues();
                addColumnToPropertyMapping("", "");
            }
        });

        baseFormLayout = new FormLayout();
        baseFormLayout.setSizeUndefined();

        baseFormLayoutSecond.setSizeUndefined();
        baseFormLayoutSecond.addComponent(btnAddRow);

        mainLayout.addComponent(baseFormLayoutSecond);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);

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
        propertiesGridLayout.addComponent(new Label("Subject URI:"));
        propertiesGridLayout.addComponent(new Label("Property URI:"));
    }

    private void checkValues() {

        boolean empty_1 = false;
        boolean empty_2 = false;

        int row = 1;
        boolean stop = false;

        while (row < propertiesGridLayout.getRows() && !stop) {

            String txtSubject = ((TextField) propertiesGridLayout.getComponent(0, row)).getValue();
            String txtProperty = ((TextField) propertiesGridLayout.getComponent(1, row)).getValue();

            if (txtSubject.isEmpty()) empty_1 = true;
            if (txtProperty.isEmpty()) empty_2 = true;

            if ((empty_1 || empty_2) && !(empty_1 && empty_2)) stop = true;

            row++;
        }

        if (stop) warn.show(Page.getCurrent());
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

        checkValues();

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
