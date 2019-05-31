package de.bildschirmarbeiter.asf.toolbox.application.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import org.osgi.service.component.annotations.Component;

@Component(
    service = MainView.class,
    property = {
        "service.ranking:Integer=-1"
    }
)
public class AboutView implements MainView {

    private final VBox node;

    private static final String TITLE = "About";

    public AboutView() {
        final Label label = new Label("Toolbox for ASF projects.");
        final VBox container = new VBox();
        container.setPadding(new Insets(20, 20, 20, 20));
        container.getChildren().add(
            label
        );
        this.node = container;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public Node getNode() {
        return node;
    }

}
