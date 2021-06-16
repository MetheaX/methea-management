package io.github.metheax.mgt.views.organization;

import java.util.Optional;

import io.github.metheax.domain.entity.TAccount;
import io.github.metheax.mgt.data.service.CustomCrudServiceDataProvider;
import io.github.metheax.mgt.data.service.TAccountService;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import io.github.metheax.mgt.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.textfield.TextField;

@Route(value = "app/organization/:id?/:action?(edit)", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Organization")
public class OrganizationView extends Div implements BeforeEnterObserver {

    private final String TACCOUNT_ID = "id";
    private final String TACCOUNT_EDIT_ROUTE_TEMPLATE = "app/organization/%s/edit";

    private Grid<TAccount> grid = new Grid<>(TAccount.class, false);

    private TextField accountCode;
    private TextField accountName;
    private TextField accountNameOth;
    private TextField accountEmail;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<TAccount> binder;

    private TAccount account;

    private TAccountService accountService;

    public OrganizationView(@Autowired TAccountService accountService) {
        addClassNames("organization-view", "flex", "flex-col", "h-full");
        this.accountService = accountService;
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("accountCode").setAutoWidth(true);
        grid.addColumn("accountName").setAutoWidth(true);
        grid.addColumn("accountNameOth").setAutoWidth(true);
        grid.addColumn("accountEmail").setAutoWidth(true);
        grid.setDataProvider(new CustomCrudServiceDataProvider(accountService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TACCOUNT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(OrganizationView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(TAccount.class);

        // Bind fields. This where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.account == null) {
                    this.account = new TAccount();
                }
                binder.writeBean(this.account);

                accountService.update(this.account);
                clearForm();
                refreshGrid();
                Notification.show("TAccount details stored.");
                UI.getCurrent().navigate(OrganizationView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the tAccount details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> tAccountId = event.getRouteParameters().get(TACCOUNT_ID);
        if (tAccountId.isPresent()) {
            Optional<TAccount> tAccountFromBackend = accountService.get(tAccountId.get());
            if (tAccountFromBackend.isPresent()) {
                populateForm(tAccountFromBackend.get());
            } else {
                Notification.show(String.format("The requested tAccount was not found, ID = %d", tAccountId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(OrganizationView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        accountCode = new TextField("First Name");
        accountName = new TextField("Last Name");
        accountNameOth = new TextField("Email");
        accountEmail = new TextField("Phone");
        Component[] fields = new Component[]{accountCode, accountName, accountNameOth, accountEmail};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(TAccount value) {
        this.account = value;
        binder.readBean(this.account);

    }
}
