package eu.unifiedviews.plugins.c2;

import java.util.ArrayList;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

public class C2VaadinDialog extends BaseConfigDialog<C2Config_V1> {

    private VerticalLayout mainLayout;

    private GridLayout propertiesGridLayout;

    private FormLayout baseFormLayout;

    private FormLayout baseFormLayoutSecond;

    private TextField fileName;

    public C2VaadinDialog() {
        super(C2Config_V1.class);

        buildMainLayout();
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);
    }

    private void buildMainLayout() {

        this.setWidth("100%");
        this.setHeight("100%");

        this.mainLayout = new VerticalLayout();
        this.mainLayout.setImmediate(false);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("-1px");
        this.mainLayout.setMargin(true);

        this.baseFormLayout = new FormLayout();
        this.baseFormLayout.setSizeUndefined();

        fileName = new TextField("File output name:");
        fileName.setHeight("-1px");
        fileName.setRequired(true);
        this.baseFormLayout.addComponent(fileName);

        this.propertiesGridLayout = new GridLayout(2, 2);
        this.propertiesGridLayout.setWidth("100%");
        this.propertiesGridLayout.setColumnExpandRatio(0, 1);
        this.propertiesGridLayout.setColumnExpandRatio(1, 1);

        this.addColumnToPropertyMappingsHeading();

        TextField txtSubject = new TextField();
        this.propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        TextField txtProperty = new TextField();
        this.propertiesGridLayout.addComponent(txtProperty);
        txtProperty.setWidth("100%");

        this.mainLayout.addComponent(baseFormLayout);
        this.mainLayout.addComponent(propertiesGridLayout);

        Button btnAddRow = new Button("Add");
        btnAddRow.addClickListener(new ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(ClickEvent event) {
                addColumnToPropertyMapping("", "");
            }
        });

        this.baseFormLayout = new FormLayout();
        this.baseFormLayout.setSizeUndefined();
        this.baseFormLayoutSecond = new FormLayout();
        this.baseFormLayoutSecond.setSizeUndefined();
        this.baseFormLayoutSecond.addComponent(btnAddRow);
        this.mainLayout.addComponent(baseFormLayoutSecond);
    }

    private void addColumnToPropertyMapping(String subject, String property) {

        TextField txtSubject = new TextField();
        this.propertiesGridLayout.addComponent(txtSubject);
        txtSubject.setWidth("100%");

        TextField txtProperty = new TextField();
        this.propertiesGridLayout.addComponent(txtProperty);
        txtProperty.setWidth("100%");

        if (subject != null) {
            txtSubject.setValue(subject);
        }

        if (property != null) {
            txtProperty.setValue(property);
        }
    }

    private void removeAllColumnToPropertyMappings() {
        this.propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();
    }

    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label("Subject Type URI:"));
        this.propertiesGridLayout.addComponent(new Label("Property URI:"));
    }

    @Override
    protected void setConfiguration(C2Config_V1 config) throws DPUConfigException {

        ArrayList<String> subject = config.getSubject();
        ArrayList<String> property = config.getProperty();
        fileName.setValue(config.getFileName());

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
        config.setFileName(fileName.getValue());

        return config;
    }
}
