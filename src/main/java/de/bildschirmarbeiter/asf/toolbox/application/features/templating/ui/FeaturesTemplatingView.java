package de.bildschirmarbeiter.asf.toolbox.application.features.templating.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.LogMessage;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.ReadFile;
import de.bildschirmarbeiter.asf.toolbox.application.ui.MainView;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static de.bildschirmarbeiter.asf.toolbox.application.Helper.message;

@Component(
    service = MainView.class,
    property = {
        "service.ranking:Integer=2147483646"
    }
)
public class FeaturesTemplatingView implements MainView {

    @Reference
    private volatile FeaturesTemplatingViewModel model;

    @Reference
    private volatile TemplateView templateView;

    @Reference
    private volatile MessageService messageService;

    private VBox node;

    private static final String TITLE = "Features: Templating";

    public FeaturesTemplatingView() {
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
    public VBox getNode() {
        return node;
    }

    private Pane build() {
        final VBox fileView = buildFileView();
        // body
        final GridPane body = new GridPane();
        body.setPadding(new Insets(10, 10, 10, 10));
        body.setHgap(10);
        body.setVgap(10);
        final ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        body.getColumnConstraints().addAll(cc, cc);
        body.add(fileView, 0, 0);
        body.add(templateView, 1, 0);
        return body;
    }

    private VBox buildFileView() {
        final Label title = new Label("Features");

        final TextArea textArea = new TextArea();
        textArea.setStyle("-fx-font-family: monospace");
        textArea.setEditable(false);
        textArea.setPromptText("drop final (target/feature/) feature.xml here");
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

        final Label pathLabel = new Label();
        pathLabel.textProperty().bind(model.path);

        final VBox main = new VBox();
        main.setPadding(new Insets(10, 10, 10, 10));
        main.setSpacing(10);
        main.getChildren().addAll(title, textArea);
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

}
