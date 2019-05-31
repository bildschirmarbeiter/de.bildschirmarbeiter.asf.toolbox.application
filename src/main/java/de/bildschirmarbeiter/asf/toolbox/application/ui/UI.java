package de.bildschirmarbeiter.asf.toolbox.application.ui;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true
)
public class UI {

    @Reference
    private volatile Screen screen;

    @Reference
    private volatile Stage primaryStage;

    @Reference
    private volatile Root root;

    private static final String TITLE = "ASF Toolbox";

    public UI() {
    }

    @Activate
    private void activate() {
        final Rectangle2D visualBounds = screen.getVisualBounds();
        final Image icon = new Image("icon.png");
        final Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        scene.getStylesheets().add("toolbox.css");
        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());
        primaryStage.setTitle(TITLE);
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Deactivate
    private void deactivate() {
        Platform.runLater(() -> {
            primaryStage.hide();
            primaryStage.setScene(null);
        });
    }

}
