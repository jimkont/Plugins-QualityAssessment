package eu.unifiedviews.plugins.quality.c2;

import java.util.ArrayList;

import com.vaadin.data.Validator;
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

                boolean empty = false;

                for (int i = 1; i < propertiesGridLayout.getRows(); i++) {
                    String txtSubject = ((TextField) propertiesGridLayout.getComponent(0, i)).getValue();
                    String txtProperty = ((TextField) propertiesGridLayout.getComponent(1, i)).getValue();
                    if (txtSubject.isEmpty() || txtProperty.isEmpty()) {
                        empty = true;
                        if (txtSubject.isEmpty())
                            ((TextField) propertiesGridLayout.getComponent(0, i)).setComponentError(new UserError(ctx.tr("C2.error.subject.not.filled")));
                        else
                            ((TextField) propertiesGridLayout.getComponent(0, i)).setComponentError(null);
                        if (txtProperty.isEmpty())
                            ((TextField) propertiesGridLayout.getComponent(1, i)).setComponentError(new UserError(ctx.tr("C2.error.property.not.filled")));
                        else
                            ((TextField) propertiesGridLayout.getComponent(1, i)).setComponentError(null);
                    } else {
                        ((TextField) propertiesGridLayout.getComponent(0, i)).setComponentError(null);
                        ((TextField) propertiesGridLayout.getComponent(1, i)).setComponentError(null);
                    }
                }

                if (!empty) {
                    addColumnToPropertyMapping("http://", "http://");
                }
            }
        });

        /*Button btnRemoveRow = new Button(ctx.tr("C2.button.remove"));
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

                    addColumnToPropertyMapping("http://", "http://");
                }
                if (lastRow > 1 && txtSubject.isEmpty() && txtProperty.isEmpty()) {
                    propertiesGridLayout.removeRow(lastRow - 1);
                }
            }
        });*/

        /*Button btnRemoveRows = new Button(ctx.tr("C2.button.remove.all"));
        btnRemoveRows.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;
            @Override
            public void buttonClick(Button.ClickEvent event) {

                propertiesGridLayout.removeAllComponents();

                addColumnToPropertyMappingsHeading();
                addColumnToPropertyMapping("http://", "http://");
            }
        });*/

        baseFormLayout = new FormLayout();
        baseFormLayout.setSizeUndefined();

        HorizontalLayout baseFormLayoutSecond = new HorizontalLayout();
        baseFormLayoutSecond.setSpacing(true);
        baseFormLayoutSecond.addComponent(btnAddRow);
        //baseFormLayoutSecond.addComponent(btnRemoveRow);
        //baseFormLayoutSecond.addComponent(btnRemoveRows);

        mainLayout.addComponent(baseFormLayoutSecond);

        setCompositionRoot(mainLayout);
    }

    private void addColumnToPropertyMappingsHeading() {
        propertiesGridLayout.addComponent(new Label(ctx.tr("C2.subject.uri")));
        propertiesGridLayout.addComponent(new Label(ctx.tr("C2.property.uri")));
        propertiesGridLayout.addComponent(new Label(""));
    }

    private void addColumnToPropertyMapping(String subject, String property) {

        TextField txtSubject = new TextField();
        txtSubject.setValue(subject);
        txtSubject.setRequired(true);
        txtSubject.setWidth("100%");
        txtSubject.addValidator(fieldValidator("subject"));

        TextField txtProperty = new TextField();
        txtProperty.setValue(property);
        txtProperty.setRequired(true);
        txtProperty.setWidth("100%");
        txtProperty.addValidator(fieldValidator("property"));

        final Button btnRemoveRow = new Button(ctx.tr("C2.button.remove"));
        btnRemoveRow.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                propertiesGridLayout.removeRow(propertiesGridLayout.getComponentArea(btnRemoveRow).getRow1());
            }
        });

        propertiesGridLayout.addComponent(txtSubject);
        propertiesGridLayout.addComponent(txtProperty);
        propertiesGridLayout.addComponent(btnRemoveRow);
    }

    private Validator fieldValidator (final String field) {
        return new Validator() {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(Object value) throws InvalidValueException {

                String myValue = value.toString().toLowerCase().trim();

                if (myValue.isEmpty()) {
                    throw new InvalidValueException("\n"+ ctx.tr("C2.error."+ field +".not.filled"));
                } else if (!myValue.startsWith("http://")) {
                    throw new InvalidValueException("\n"+ ctx.tr("C2.error."+ field +".not.http"));
                } else if (myValue.contains(" ")) {
                    throw new InvalidValueException("\n"+ ctx.tr("C2.error."+ field +".whitespace"));
                }
            }
        };
    }

    @Override
    protected void setConfiguration(C2Config_V1 config) throws DPUConfigException {

        ArrayList<String> subject = config.getSubject();
        ArrayList<String> property = config.getProperty();

        propertiesGridLayout.removeAllComponents();

        this.addColumnToPropertyMappingsHeading();

        if (subject.size() == 0) {
            this.addColumnToPropertyMapping("http://", "http://");
        } else {
            for (int i = 0; i < subject.size(); i++) {
                if (!subject.get(i).trim().equals("") && !property.get(i).trim().equals("")) {
                    this.addColumnToPropertyMapping(subject.get(i), property.get(i));
                }
            }
        }
    }

    @Override
    protected C2Config_V1 getConfiguration() throws DPUConfigException {

        C2Config_V1 config = new C2Config_V1();

        ArrayList<String> subject = new ArrayList<>();
        ArrayList<String> property = new ArrayList<>();

        String validation = "";

        int row = 1;
        while (row < propertiesGridLayout.getRows()) {

            TextField txtSubject = ((TextField) propertiesGridLayout.getComponent(0, row));
            TextField txtProperty = ((TextField) propertiesGridLayout.getComponent(1, row));

            String txtSubjectValue = txtSubject.getValue().trim();
            String txtPropertyValue = txtProperty.getValue().trim();

            if (txtSubjectValue.isEmpty()) {
                validation = validation +"\n"+ ctx.tr("C2.error.subject.not.filled");
            }

            if (txtPropertyValue.isEmpty()) {
                validation = validation +"\n"+ ctx.tr("C2.error.property.not.filled");
            }

            if (!txtSubject.isValid()) {
                try {
                    txtSubject.validate();
                } catch (Validator.InvalidValueException e) {
                    validation = validation + e.getMessage();
                }
            }

            if (!txtProperty.isValid()) {
                try {
                    txtProperty.validate();
                } catch (Validator.InvalidValueException e) {
                    validation = validation + e.getMessage();
                }
            }

            if (validation.isEmpty()) {
                subject.add(row-1, txtSubjectValue);
                property.add(row-1, txtPropertyValue);
            } else {
                throw new DPUConfigException(validation);
            }

            row++;
        }

        config.setSubject(subject);
        config.setProperty(property);

        return config;
    }
}
