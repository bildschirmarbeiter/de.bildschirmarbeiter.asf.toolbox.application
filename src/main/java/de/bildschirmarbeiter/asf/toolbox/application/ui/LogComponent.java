package de.bildschirmarbeiter.asf.toolbox.application.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import com.google.common.eventbus.Subscribe;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.LogMessage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = LogComponent.class
)
public class LogComponent extends HBox {

    private final Label message;

    private final Label time;

    @Reference
    private volatile MessageService messageService;

    public LogComponent() {
        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);
        final Label message = new Label();
        this.message = message;
        final Label time = new Label();
        time.setTextFill(Color.LIGHTGREY);
        this.time = time;
        getChildren().addAll(
            message,
            time
        );
    }

    @Activate
    private void activate() {
        messageService.register(this);
    }

    @Deactivate
    private void deactivate() {
        messageService.unregister(this);
    }

    @Subscribe
    public void onLogEvent(final LogMessage message) {
        final Paint paint;
        switch (message.getLevel()) {
            case DEBUG:
                paint = Color.GREY;
                break;
            case INFO:
                paint = Color.BLACK;
                break;
            case WARN:
                paint = Color.ORANGE;
                break;
            case ERROR:
                paint = Color.RED;
                break;
            default:
                paint = Color.BLACK;
                break;
        }
        this.message.setTextFill(paint);
        this.message.setText(message.getMessage());
        final String timestamp = String.format("@ %s", message.timestamp());
        time.setText(timestamp);
    }

}
