package de.bildschirmarbeiter.asf.toolbox.application.features.updating.ui;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.features.Bundle;
import de.bildschirmarbeiter.asf.toolbox.application.features.BundleUpdate;
import de.bildschirmarbeiter.asf.toolbox.application.message.maven.FindLatestRelease;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.UpdateFeatures;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.LogMessage;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.ReadFile;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.WriteFile;
import de.bildschirmarbeiter.asf.toolbox.application.ui.MainView;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static de.bildschirmarbeiter.asf.toolbox.application.Helper.message;

@Component(
    service = MainView.class,
    property = {
        "service.ranking:Integer=2147483647"
    }
)
public class FeaturesUpdatingView extends VBox implements MainView {

    @Reference
    private volatile FeaturesUpdatingViewModel model;

    @Reference
    private volatile MessageService messageService;

    private VBox node;

    private static final String TITLE = "Features: Updating";

    public FeaturesUpdatingView() {
    }

    @Activate
    public void activate() {
        final Pane body = build();
        final VBox container = new VBox();
        container.getChildren().addAll(body);
        this.node = container;
        messageService.register(this);
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public Node getNode() {
        return node;
    }

    private Pane build() {
        final VBox fileView = buildFileView();
        final VBox bundlesView = buildBundlesUpdatesView();
        // body
        final GridPane body = new GridPane();
        body.setPadding(new Insets(10, 10, 10, 10));
        body.setHgap(10);
        body.setVgap(10);
        final ColumnConstraints left = new ColumnConstraints();
        left.setPercentWidth(50);
        body.getColumnConstraints().addAll(left, left);
        body.add(fileView, 0, 0);
        body.add(bundlesView, 1, 0);
        return body;
    }

    private VBox buildFileView() {
        final Label title = new Label("Features");

        final TextArea textArea = new TextArea();
        textArea.setStyle("-fx-font-family: monospace");
        textArea.setEditable(false);
        textArea.setPromptText("drop source (src/main/feature/) feature.xml here");
        textArea.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        // TODO textArea.setOnDragExited(Event::consume);
        textArea.setOnDragDropped(this::handleFeaturesDropped);
        textArea.textProperty().bind(model.content);
        textArea.setPrefHeight(2000); // TODO

        final Button saveButton = new Button("save file");
        saveButton.setOnAction(event -> {
            final File initial = new File(model.path.get());
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(initial.getParentFile());
            fileChooser.setInitialFileName(initial.getName());
            final File file = fileChooser.showSaveDialog(node.getScene().getWindow());
            if (file != null) {
                final WriteFile command = new WriteFile(this, file.getPath(), model.bytes);
                messageService.send(command);
            }
        });

        final HBox actionBar = new HBox();
        actionBar.setAlignment(Pos.BASELINE_RIGHT);
        actionBar.setSpacing(10);
        actionBar.getChildren().addAll(saveButton);

        final VBox main = new VBox();
        main.setPadding(new Insets(10, 10, 10, 10));
        main.setSpacing(10);
        main.getChildren().addAll(title, textArea, actionBar);
        return main;
    }

    private VBox buildBundlesUpdatesView() {
        final VBox bundlesView = buildBundlesView();
        final VBox updatesView = buildUpdatesView();
        final VBox main = new VBox();
        main.getChildren().addAll(bundlesView, updatesView);
        return main;
    }

    private VBox buildBundlesView() {
        final Label title = new Label("Bundles");

        final ListView<Bundle> bundlesList = new ListView<>(model.bundles);

        final Button findButton = new Button("find new bundles");
        findButton.setOnAction(event -> {
            findNewBundles();
        });

        final HBox actionBar = new HBox();
        actionBar.setAlignment(Pos.BASELINE_RIGHT);
        actionBar.setSpacing(10);
        actionBar.getChildren().addAll(findButton);

        final VBox main = new VBox();
        main.setPadding(new Insets(10, 10, 10, 10));
        main.setSpacing(10);
        main.getChildren().addAll(title, bundlesList, actionBar);
        return main;
    }

    private VBox buildUpdatesView() {
        final Label title = new Label("Updates");

        final ListView<BundleUpdate> updatesList = new ListView<>(model.updates);

        final Button updateButton = new Button("update features");
        updateButton.setOnAction(event -> {
            updateFeature();
        });

        final HBox actionBar = new HBox();
        actionBar.setAlignment(Pos.BASELINE_RIGHT);
        actionBar.setSpacing(10);
        actionBar.getChildren().addAll(updateButton);

        final VBox main = new VBox();
        main.setPadding(new Insets(10, 10, 10, 10));
        main.setSpacing(10);
        main.getChildren().addAll(title, updatesList, actionBar);
        return main;
    }

    private void handleFeaturesDropped(final DragEvent event) {
        final Dragboard dragboard = event.getDragboard();
        boolean dropCompleted = false;
        if (dragboard.hasFiles()) {
            if (dragboard.getFiles().size() > 1) {
                messageService.send(LogMessage.warn(this, message("processing multiple files is not supported")));
            }
            final String path = dragboard.getFiles().get(0).getPath();
            final ReadFile command = new ReadFile(model, path);
            messageService.send(command);
            dropCompleted = true;
        }
        event.setDropCompleted(dropCompleted);
        event.consume();
    }

    private void findNewBundles() {
        for (final Bundle bundle : model.bundles) {
            final FindLatestRelease command = new FindLatestRelease(this, bundle);
            messageService.send(command);
        }
    }

    private void updateFeature() {
        final UpdateFeatures command = new UpdateFeatures(this, model.bytes, model.updates);
        messageService.send(command);
    }

}
