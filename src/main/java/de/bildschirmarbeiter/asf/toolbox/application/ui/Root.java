package de.bildschirmarbeiter.asf.toolbox.application.ui;

import java.util.List;
import java.util.ListIterator;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
    service = Root.class
)
public class Root extends BorderPane {

    private final VBox container;

    private final TabPane tabPane;

    @Reference
    private volatile LogComponent logComponent;

    @Reference(
        cardinality = ReferenceCardinality.AT_LEAST_ONE,
        policy = ReferencePolicy.DYNAMIC,
        bind = "addMainView",
        unbind = "removeMainView"
    )
    private volatile List<MainView> mainViews;

    private final Object lock = new Object();

    public Root() {
        container = new VBox();
        container.prefHeightProperty().bind(heightProperty());
        container.prefWidthProperty().bind(widthProperty());
        setCenter(container);
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
    }

    @Activate
    private void activate() {
        container.getChildren().addAll(
            tabPane,
            logComponent
        );
    }

    @Deactivate
    private void deactivate() {
        container.getChildren().clear();
    }

    private void addMainView(final MainView mainView) {
        synchronized (lock) {
            buildTabs();
        }
    }

    private void removeMainView(final MainView mainView) {
        synchronized (lock) {
            buildTabs();
        }
    }

    private void buildTabs() {
        tabPane.getTabs().clear();
        final ListIterator<MainView> iterator = mainViews.listIterator(mainViews.size());
        while (iterator.hasPrevious()) {
            final MainView mainView = iterator.previous();
            final Tab tab = new Tab(mainView.getTitle(), mainView.getNode());
            tabPane.getTabs().add(tab);
        }
    }

}
