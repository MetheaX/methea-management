package io.github.metheax.mgt.views.group;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextArea;
import io.github.metheax.domain.entity.TAccount;
import io.github.metheax.domain.entity.TGroup;
import io.github.metheax.mgt.data.service.CustomCrudServiceDataProvider;
import io.github.metheax.mgt.data.service.TAccountService;
import io.github.metheax.mgt.data.service.TGroupService;

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
import com.vaadin.flow.component.textfield.TextField;

@Route(value = "app/group/:id?/:action?(edit)", layout = MainView.class)
@PageTitle("Group")
public class GroupView extends Div implements BeforeEnterObserver {

    private final String TGROUP_ID = "id";
    private final String TGROUP_EDIT_ROUTE_TEMPLATE = "app/group/%s/edit";

    private Grid<TGroup> grid = new Grid<>(TGroup.class, false);

    private TextField groupCode;
    private TextField groupName;
    private TextField groupNameOth;
    private ComboBox<TAccount> account;
    private TextArea remarks;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<TGroup> binder;

    private TGroup group;

    private TGroupService tGroupService;
    private TAccountService accountService;

    public GroupView(@Autowired TGroupService tGroupService, @Autowired TAccountService accountService) {
        addClassNames("group-view", "flex", "flex-col", "h-full");
        this.tGroupService = tGroupService;
        this.accountService = accountService;
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(group -> {
            TAccount account = group.getAccount();
            return account == null ? "-" : account.getAccountName();
        }).setHeader("Account");
        grid.addColumn("groupCode").setAutoWidth(true);
        grid.addColumn("groupName").setAutoWidth(true);
        grid.addColumn("groupNameOth").setAutoWidth(true);
        grid.setDataProvider(new CustomCrudServiceDataProvider<>(tGroupService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TGROUP_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(GroupView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(TGroup.class);

        // Bind fields. This where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.group == null) {
                    this.group = new TGroup();
                    this.group.setId(UUID.randomUUID().toString());
                    this.group.setStatus("A");
                }
                binder.writeBean(this.group);

                tGroupService.update(this.group);
                clearForm();
                refreshGrid();
                Notification.show("TGroup details stored.");
                UI.getCurrent().navigate(GroupView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the tGroup details.");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> tGroupId = event.getRouteParameters().get(TGROUP_ID);
        if (tGroupId.isPresent()) {
            Optional<TGroup> tGroupFromBackend = tGroupService.get(tGroupId.get());
            if (tGroupFromBackend.isPresent()) {
                populateForm(tGroupFromBackend.get());
            } else {
                Notification.show(String.format("The requested tGroup was not found, ID = %s", tGroupId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(GroupView.class);
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
        groupCode = new TextField("Group Code");
        groupName = new TextField("Group Name");
        groupNameOth = new TextField("Group Name Oth");
        account = new ComboBox<>();
        account.setLabel("Account");
        List<TAccount> accounts = accountService.getAllActiveAccount();

        account.setItemLabelGenerator(TAccount::getAccountName);
        account.setItems(accounts);
        account.setClassName("overlay-dropdown");
        remarks = new TextArea("Remarks");
        Component[] fields = new Component[]{account, groupCode, groupName, groupNameOth, remarks};

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

    private void populateForm(TGroup value) {
        this.group = value;
        binder.readBean(this.group);

    }
}
