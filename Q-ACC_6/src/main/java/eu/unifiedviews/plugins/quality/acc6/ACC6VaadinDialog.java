package eu.unifiedviews.plugins.quality.acc6;

import java.util.ArrayList;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.shared.ui.combobox.FilteringMode;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.localization.Messages;

import cz.cuni.mff.xrg.uv.boost.dpu.vaadin.AbstractDialog;

public class ACC6VaadinDialog extends AbstractDialog<ACC6Config_V1> {

    private VerticalLayout mainLayout;

    private GridLayout propertiesGridLayout;

    private TextField fileName;

    private ComboBox regExp;

    private Messages messages;
    
    public ACC6VaadinDialog() {
        super(ACC6.class);
    }
    
    @Override
    public void buildDialogLayout() {

        this.messages = new Messages(getContext().getLocale(), this.getClass().getClassLoader());
        
        this.setWidth("100%");
        this.setHeight("100%");
        
        this.mainLayout = new VerticalLayout();
        this.mainLayout.setImmediate(false);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("-1px");
        this.mainLayout.setMargin(true);

        FormLayout baseFormLayout = new FormLayout();
        baseFormLayout.setSizeUndefined();

        fileName = new TextField(messages.getString("dpu.ACC6VaadinDialog.file.output"));
        fileName.setHeight("-1px");
        fileName.setRequired(true);
        baseFormLayout.addComponent(fileName);

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

        this.mainLayout.addComponent(baseFormLayout);
        this.mainLayout.addComponent(propertiesGridLayout);

        Button btnAddRow = new Button("Add");
        btnAddRow.addClickListener(new ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(ClickEvent event) {
                addColumnToPropertyMapping("", "", "");
            }
        });

        FormLayout baseFormLayoutSecond = new FormLayout();
        baseFormLayoutSecond.setSizeUndefined();
        baseFormLayoutSecond.addComponent(btnAddRow);
        this.mainLayout.addComponent(baseFormLayoutSecond);

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
        this.propertiesGridLayout.addComponent(new Label(messages.getString("dpu.ACC6VaadinDialog.resource.type")));
        this.propertiesGridLayout.addComponent(new Label(messages.getString("dpu.ACC6VaadinDialog.property")));
        this.propertiesGridLayout.addComponent(new Label(messages.getString("dpu.ACC6VaadinDialog.regular.expression")));
    }
    
    private void initComboBox(){
        this.regExp.addItem(/*"Postal code:"*/"[0-9][0-9][0-9][0-9][0-9]");
        this.regExp.addItem(/*"Email address: */"^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
        this.regExp.addItem(/*"DateTime: */"^(?ni:(?=\\d)((?'year'((1[6-9])|([2-9]\\d))\\d\\d)(?'sep'[/.-])(?'month'0?[1-9]|1[012])\\2(?'day'((?<!(\\2((0?[2469])|11)\\2))31)|(?<!\\2(0?2)\\2)(29|30)|((?<=((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|(16|[2468][048]|[3579][26])00)\\2\\3\\2)29)|((0?[1-9])|(1\\d)|(2[0-8])))(?:(?=\\x20\\d)\\x20|$))?((?<time>((0?[1-9]|1[012])(:[0-5]\\d){0,2}(\\x20[AP]M))|([01]\\d|2[0-3])(:[0-5]\\d){1,2}))?)$");
        this.regExp.addItem(/*"German postal code: */"\\b((?:0[1-46-9]\\d{3})|(?:[1-357-9]\\d{4})|(?:[4][0-24-9]\\d{3})|(?:[6][013-9]\\d{3}))\\b");
        this.regExp.addItem(/*"Telephone number (10 digit):*/"^(\\([2-9]|[2-9])(\\d{2}|\\d{2}\\))(-|.|\\s)?\\d{3}(-|.|\\s)?\\d{4}$");
        this.regExp.addItem(/*"Italian mobile phone number: */"^([+]39)?((38[{8,9}|0])|(34[{7-9}|0])|(36[6|8|0])|(33[{3-9}|0])|(32[{8,9}]))([\\d]{7})$");
        this.regExp.addItem(/*"Currency: */"^(?!\\u00a2)");
    }

    @Override
    protected void setConfiguration(ACC6Config_V1 config) throws DPUConfigException {

        ArrayList<String> subject = config.getSubject();
        ArrayList<String> property = config.getProperty();
        ArrayList<String> regExp = config.getRegularExpression();
        fileName.setValue(config.getFileName());

        this.removeAllColumnToPropertyMappings();
        
        for (int i = 0; i < subject.size(); ++i) {
            this.addColumnToPropertyMapping(subject.get(i), property.get(i), regExp.get(i));
        }

        this.addColumnToPropertyMapping("", "", "");
    }

    @Override
    protected ACC6Config_V1 getConfiguration() throws DPUConfigException {

        ACC6Config_V1 config = new ACC6Config_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();
        ArrayList<String> regExp = new ArrayList<>();
        

        for (int row = 1; row < this.propertiesGridLayout.getRows(); row++) {

            String txtSubject = ((TextField) this.propertiesGridLayout.getComponent(0, row)).getValue();
            String txtProperty = ((TextField) this.propertiesGridLayout.getComponent(1, row)).getValue();
            String txtRegExp = (String)((ComboBox) this.propertiesGridLayout.getComponent(2, row)).getValue();

            if (!txtSubject.isEmpty() && !txtProperty.isEmpty() && !txtRegExp.isEmpty()) {
                subject.add(row-1, txtSubject);
                property.add(row-1, txtProperty);
                regExp.add(row-1, txtRegExp);
            }
        }

        config.setSubject(subject);
        config.setProperty(property);
        config.setRegularExpression(regExp);
        config.setFileName(fileName.getValue());

        return config;
    }
}
