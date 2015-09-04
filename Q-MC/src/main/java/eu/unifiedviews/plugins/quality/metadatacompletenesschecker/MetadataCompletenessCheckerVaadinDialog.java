package eu.unifiedviews.plugins.quality.metadatacompletenesschecker;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.ArrayList;

/**
 * Vaadin configuration dialog for MetadataCompletenessChecker.
 *
 * @author Vincenzo Cutrona
 */
public class MetadataCompletenessCheckerVaadinDialog extends AbstractDialog<MetadataCompletenessCheckerConfig_V1> {

    private GridLayout propertiesGridLayout;
    private ComboBox properties;

    public MetadataCompletenessCheckerVaadinDialog() {
        super(MetadataCompletenessChecker.class);
    }

    @Override
    public void setConfiguration(MetadataCompletenessCheckerConfig_V1 c) throws DPUConfigException {
        ArrayList<String> property = c.getProperty();

        this.propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();

        for (int i = 0; i < property.size(); ++i) {
            if (!property.get(i).trim().equals("")) {
                this.addColumnToPropertyMapping(property.get(i));
            }
        }
        if (property.size() == 0)
            this.addColumnToPropertyMapping("");

    }

    @Override
    public MetadataCompletenessCheckerConfig_V1 getConfiguration() throws DPUConfigException {
        final MetadataCompletenessCheckerConfig_V1 c = new MetadataCompletenessCheckerConfig_V1();

        ArrayList<String> property = new ArrayList<>();

        String error = fieldsValidation();

        if (!error.isEmpty()) {
            throw new DPUConfigException(error);
        } else {
            for (int row = 1; row < this.propertiesGridLayout.getRows(); ++row) {
                String txtProperty = (String) ((ComboBox) this.propertiesGridLayout.getComponent(0, row)).getValue();
                if (!txtProperty.isEmpty()) {
                    property.add(row - 1, txtProperty);
                }
            }
        }
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

        this.propertiesGridLayout = new GridLayout(2, 2);
        this.propertiesGridLayout.setWidth("100%");
        this.addColumnToPropertyMappingsHeading();

        this.properties = new ComboBox();
        properties.setFilteringMode(FilteringMode.CONTAINS);

        initComboBox();

        this.properties.setNewItemsAllowed(true);
        this.properties.setRequired(true);
        this.properties.setWidth("100%");
        this.propertiesGridLayout.addComponent(properties);

        mainLayout.addComponent(propertiesGridLayout);
        Button btnAddRow = new Button(ctx.tr("MetadataCompletenessChecker.dialog.button.add"));
        btnAddRow.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addColumnToPropertyMapping("");
            }
        });


        HorizontalLayout baseFormLayoutSecond = new HorizontalLayout();
        baseFormLayoutSecond.setSpacing(true);
        baseFormLayoutSecond.addComponent(btnAddRow);
        mainLayout.addComponent(baseFormLayoutSecond);

        setCompositionRoot(mainLayout);
    }

    private void addColumnToPropertyMapping(final String property) {

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

        final Button btnRemoveRow = new Button(ctx.tr("MetadataCompletenessChecker.dialog.button.remove"));
        btnRemoveRow.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                propertiesGridLayout.removeRow(propertiesGridLayout.getComponentArea(btnRemoveRow).getRow1());
                if (propertiesGridLayout.getRows() == 1) {
                    addColumnToPropertyMapping("");
                }
            }
        });
        propertiesGridLayout.addComponent(btnRemoveRow);
    }

    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("MetadataCompletenessChecker.dialog.property")));
        this.propertiesGridLayout.addComponent(new Label(""));
    }

    private void initComboBox() {
        MetadataCompletenessCheckerConfig_V1 c = new MetadataCompletenessCheckerConfig_V1();
        for (int i = 0; i < c.getProperties().size(); ++i)
            properties.addItem(c.getProperties().get(i));
    }

    public String fieldsValidation() {

        String errors = "";

        for (int i = 1; i < propertiesGridLayout.getRows(); ++i) {
            errors = errors + fieldValidation(0, i);
        }
        return errors;
    }

    public String fieldValidation(int column, int row) {
        String txtValue;
        String error = "";

        Component cmp = propertiesGridLayout.getComponent(column, row);

        if (column == 0) {
            ComboBox combo = (ComboBox)cmp;
            txtValue = ((String)(combo.getValue()));

            if (txtValue == null || txtValue.toLowerCase().trim().isEmpty()) {
                error = error + "\n" + ctx.tr("MetadataCompletenessChecker.dialog.error.property.not.filled") + " [Row: " + row + "]";
                combo.setComponentError(new UserError(ctx.tr("MetadataCompletenessChecker.dialog.error.property.not.filled")));
            } else if (!txtValue.toLowerCase().trim().startsWith("http://")) {
                error = error + "\n" + ctx.tr("MetadataCompletenessChecker.dialog.error.property.not.http") + " [Row: " + row + "]";
                combo.setComponentError(new UserError(ctx.tr("MetadataCompletenessChecker.dialog.error.property.not.http")));
            } else if (txtValue.toLowerCase().trim().contains(" ")) {
                error = error + "\n" + ctx.tr("MetadataCompletenessChecker.dialog.error.property.whitespace") + " [Row: " + row + "]";
                combo.setComponentError(new UserError(ctx.tr("MetadataCompletenessChecker.dialog.error.property.whitespace")));
            } else {
                combo.setComponentError(null);
            }

            if (!error.isEmpty())
                combo.removeItem(txtValue);
        }

        return error;
    }
}