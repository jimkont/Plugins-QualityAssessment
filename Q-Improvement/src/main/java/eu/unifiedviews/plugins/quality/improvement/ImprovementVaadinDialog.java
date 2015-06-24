package eu.unifiedviews.plugins.quality.improvement;

import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class ImprovementVaadinDialog extends AbstractDialog<ImprovementConfig_V1> {

    private GridLayout propertiesGridLayout;

    public ImprovementVaadinDialog() {
        super(Improvement.class);
    }

    @Override
    public void setConfiguration(ImprovementConfig_V1 c) throws DPUConfigException {

/*        ArrayList<String> prefix = c.getPrefix();
        ArrayList<String> uri = c.getUri();
        ArrayList<String> url = c.getUrl();

        if (prefix.size() > 0) {
            this.removeAllColumnToPropertyMappings();
            for (int i = 0; i < prefix.size(); ++i) {
                this.addColumnToPropertyMapping(prefix.get(i), uri.get(i), url.get(i));
            }
            this.addColumnToPropertyMapping("", "", "");
        }*/

    }

    @Override
    public ImprovementConfig_V1 getConfiguration() throws DPUConfigException {

        final ImprovementConfig_V1 c = new ImprovementConfig_V1();

        /*ArrayList<String> prefix = new ArrayList<>();
        ArrayList<String> uri = new ArrayList<>();
        ArrayList<String> url = new ArrayList<>();

        for (int row = 1; row < this.propertiesGridLayout.getRows(); ++row) {

            String txtPrefix = ((TextField) this.propertiesGridLayout.getComponent(0, row)).getValue();
            String txtUri = ((TextField) this.propertiesGridLayout.getComponent(1, row)).getValue();
            String txtUrl = ((TextField) this.propertiesGridLayout.getComponent(2, row)).getValue();

            if (!txtPrefix.isEmpty() && !txtUri.isEmpty() && !txtUrl.isEmpty()) {
                prefix.add(row - 1, txtPrefix);
                uri.add(row - 1, txtUri);
                url.add(row - 1, txtUrl);
            }
        }

        c.setPrefix(prefix);
        c.setUri(uri);
        c.setUrl(url);*/

        return c;
    }

    @Override
    public void buildDialogLayout() {

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        /*FormLayout baseFormLayout = new FormLayout();
        baseFormLayout.setSizeUndefined();
        this.propertiesGridLayout = new GridLayout(3, 2);
        this.propertiesGridLayout.setWidth("100%");
        this.addColumnToPropertyMappingsHeading();

        TextField txtPrefix = new TextField();
        this.propertiesGridLayout.addComponent(txtPrefix);
        txtPrefix.setWidth("100%");

        TextField txtUri = new TextField();
        this.propertiesGridLayout.addComponent(txtUri);
        txtUri.setWidth("100%");

        TextField txtUrl = new TextField();
        this.propertiesGridLayout.addComponent(txtUrl);
        txtUrl.setWidth("100%");

        mainLayout.addComponent(baseFormLayout);
        mainLayout.addComponent(propertiesGridLayout);

        Button btnAddRow = new Button(ctx.tr("RDFUnit.button.add"));
        btnAddRow.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {

                int lastRow = propertiesGridLayout.getRows() - 1;
                if (lastRow > 0) {

                    String txtPrefix = ((TextField) propertiesGridLayout.getComponent(0, lastRow)).getValue();
                    String txtUri = ((TextField) propertiesGridLayout.getComponent(1, lastRow)).getValue();
                    String txtUrl = ((TextField) propertiesGridLayout.getComponent(2, lastRow)).getValue();

                    if (txtPrefix.isEmpty() || txtUri.isEmpty() || txtUrl.isEmpty()) {

                        if (txtPrefix.isEmpty())
                            ((TextField) propertiesGridLayout
                                    .getComponent(0, lastRow))
                                    .setComponentError(new UserError(ctx.tr("RDFUnit.empty.field")));
                        else
                            ((TextField) propertiesGridLayout
                                    .getComponent(0, lastRow))
                                    .setComponentError(null);

                        if (txtUri.isEmpty())
                            ((TextField) propertiesGridLayout
                                    .getComponent(1, lastRow))
                                    .setComponentError(new UserError(ctx.tr("RDFUnit.empty.field")));
                        else
                            ((TextField) propertiesGridLayout
                                    .getComponent(1, lastRow))
                                    .setComponentError(null);

                        if (txtUrl.isEmpty())
                            ((TextField) propertiesGridLayout
                                    .getComponent(2, lastRow))
                                    .setComponentError(new UserError(ctx.tr("RDFUnit.empty.field")));
                        else
                            ((TextField) propertiesGridLayout
                                    .getComponent(2, lastRow))
                                    .setComponentError(null);
                    } else {

                        ((TextField) propertiesGridLayout.getComponent(0, lastRow)).setComponentError(null);
                        ((TextField) propertiesGridLayout.getComponent(1, lastRow)).setComponentError(null);
                        ((TextField) propertiesGridLayout.getComponent(2, lastRow)).setComponentError(null);

                        addColumnToPropertyMapping("", "", "");
                    }
                } else {
                    addColumnToPropertyMapping("", "", "");
                }
            }
        });

        Button btnRemoveRow = new Button(ctx.tr("RDFUnit.button.remove"));
        btnRemoveRow.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8609995802749728232L;

            @Override
            public void buttonClick(Button.ClickEvent event) {

                int lastRow = propertiesGridLayout.getRows() - 1;

                String txtPrefix = "";
                String txtUri = "";
                String txtUrl = "";

                if (lastRow > 0) {
                    txtPrefix = ((TextField) propertiesGridLayout.getComponent(0, lastRow)).getValue();
                    txtUri = ((TextField) propertiesGridLayout.getComponent(1, lastRow)).getValue();
                    txtUrl = ((TextField) propertiesGridLayout.getComponent(2, lastRow)).getValue();

                    propertiesGridLayout.removeRow(lastRow);
                    addColumnToPropertyMapping("", "", "");
                }

                if (lastRow > 1 && txtPrefix.isEmpty() && txtUri.isEmpty() && txtUrl.isEmpty()) {
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
                addColumnToPropertyMapping("", "", "");
            }
        });

        HorizontalLayout baseFormLayoutSecond = new HorizontalLayout();
        baseFormLayoutSecond.setSpacing(true);
        baseFormLayoutSecond.addComponent(btnAddRow);
        baseFormLayoutSecond.addComponent(btnRemoveRow);
        baseFormLayoutSecond.addComponent(btnRemoveRows);

        mainLayout.addComponent(baseFormLayoutSecond);*/
        setCompositionRoot(mainLayout);
    }

/*    private void addColumnToPropertyMappingsHeading() {
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("RDFUnit.prefix")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("RDFUnit.uri")));
        this.propertiesGridLayout.addComponent(new Label(ctx.tr("RDFUnit.url")));
    }*/

/*    private void addColumnToPropertyMapping(String prefix, String uri, String url) {

        TextField txtPrefix = new TextField();
        this.propertiesGridLayout.addComponent(txtPrefix);
        txtPrefix.setWidth("100%");

        TextField txtUri = new TextField();
        this.propertiesGridLayout.addComponent(txtUri);
        txtUri.setWidth("100%");

        TextField txtUrl = new TextField();
        this.propertiesGridLayout.addComponent(txtUrl);
        txtUrl.setWidth("100%");

        if (prefix != null) txtPrefix.setValue(prefix);
        if (uri != null) txtUri.setValue(uri);
        if (url != null) txtUrl.setValue(url);
    }

    private void removeAllColumnToPropertyMappings() {
        this.propertiesGridLayout.removeAllComponents();
        this.addColumnToPropertyMappingsHeading();
    }*/
}