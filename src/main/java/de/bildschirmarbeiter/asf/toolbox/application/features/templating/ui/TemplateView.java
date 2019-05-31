package de.bildschirmarbeiter.asf.toolbox.application.features.templating.ui;

import java.io.File;
import java.nio.charset.StandardCharsets;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.features.Bundle;
import de.bildschirmarbeiter.asf.toolbox.application.features.Feature;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.LogMessage;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.ReadFile;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.WriteFile;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static de.bildschirmarbeiter.asf.toolbox.application.Helper.message;

@Component(
    service = TemplateView.class
)
public class TemplateView extends VBox {

    @Reference
    private volatile TemplateViewModel model;

    @Reference
    private volatile MessageService messageService;

    public TemplateView() {
    }

    @Activate
    protected void activate() {
        setSpacing(10);
        build();
        messageService.register(this);
    }

    private void build() {
        final Label title = new Label("Features");

        final TextArea templateView = new TextArea();
        templateView.setStyle("-fx-font-family: monospace");
        templateView.setEditable(false);
        templateView.setPromptText("drop template here");
        templateView.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        // TODO templateView.setOnDragExited(Event::consume);
        templateView.setOnDragDropped(this::handleTemplateDropped);
        templateView.textProperty().bind(model.content);
        templateView.setPrefHeight(2000); // TODO

        final Tab templateTab = new Tab("Template", templateView);

        final TextArea resultView = new TextArea();
        resultView.setStyle("-fx-font-family: monospace");
        resultView.textProperty().bind(model.result);
        final Tab resultTab = new Tab("Result", resultView);

        final ListView<Feature> featuresView = new ListView<>(model.features);
        final Tab featuresTab = new Tab("Features", featuresView);

        final ListView<Bundle> bundlesView = new ListView<>(model.bundles);
        final Tab bundlesTab = new Tab("Bundles", bundlesView);

        final TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
            templateTab,
            resultTab,
            featuresTab,
            bundlesTab
        );

        final Button saveButton = new Button("save file");
        saveButton.setOnAction(event -> {
            final FileChooser fileChooser = new FileChooser();
            final File file = fileChooser.showSaveDialog(getScene().getWindow());
            if (file != null) {
                final byte[] bytes = model.result.getValueSafe().getBytes(StandardCharsets.UTF_8);
                final WriteFile command = new WriteFile(this, file.getPath(), bytes);
                messageService.send(command);
            }
        });

        final HBox actionBar = new HBox();
        actionBar.setAlignment(Pos.BASELINE_RIGHT);
        actionBar.setSpacing(10);
        actionBar.getChildren().addAll(saveButton);

        getChildren().addAll(title, tabPane, actionBar);

    }

    private void handleTemplateDropped(final DragEvent event) {
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

}
