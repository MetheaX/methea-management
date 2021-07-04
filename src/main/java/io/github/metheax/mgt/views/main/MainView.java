package io.github.metheax.mgt.views.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * The main view is a top-level placeholder for other views.
 */
@PWA(name = "Methea Management", shortName = "Methea Management", enableInstallPrompt = false)
@Theme(themeFolder = "metheamanagement")
public class MainView extends AppLayout {

    public MainView() {
        addToNavbar(true, createHeaderContent());
    }

    private Component createHeaderContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setClassName("sidemenu-header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        Icon icon = VaadinIcon.CLUSTER.create();
        layout.add(new Button("Home", icon));

        MenuBar menuBar = new MenuBar();

        menuBar.setOpenOnHover(true);

        MenuItem accessControl = menuBar.addItem("Access Control");
        MenuItem systemConfiguration = menuBar.addItem("System Configuration");
        menuBar.addItem("Sign Out", e -> this.getParent().flatMap(Component::getUI).orElseGet(null).getPage().setLocation("/logout"));

        SubMenu projectSubMenu = accessControl.getSubMenu();
        projectSubMenu.addItem("Account").addClickListener(e -> {
            this.getUI().ifPresent(ui -> ui.navigate("app/organization"));
        });
        projectSubMenu.addItem("Group").addClickListener(e -> {
            this.getUI().ifPresent(ui -> ui.navigate("app/group"));
        });

        systemConfiguration.getSubMenu().addItem("Scheduler");
        systemConfiguration.getSubMenu().addItem("Sys Param");

        layout.add(menuBar);
        layout.add(new Avatar("Methea"));
        return layout;
    }
}
