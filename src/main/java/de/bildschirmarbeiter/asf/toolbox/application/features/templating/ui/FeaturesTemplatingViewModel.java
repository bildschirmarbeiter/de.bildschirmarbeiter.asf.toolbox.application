package de.bildschirmarbeiter.asf.toolbox.application.features.templating.ui;

import java.nio.charset.StandardCharsets;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.google.common.eventbus.Subscribe;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.ReadFeatures;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.FileRead;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = FeaturesTemplatingViewModel.class
)
public class FeaturesTemplatingViewModel {

    // features.xml
    byte[] bytes;

    final StringProperty content = new SimpleStringProperty();

    final StringProperty path = new SimpleStringProperty();

    @Reference
    private volatile MessageService messageService;

    public FeaturesTemplatingViewModel() {
    }

    @Activate
    protected void activate() {
        messageService.register(this);
    }

    @Subscribe
    public void onFileRead(final FileRead event) {
        if (this == event.source()) {
            bytes = event.getBytes();
            content.setValue(new String(bytes, StandardCharsets.UTF_8));
            path.setValue(path.toString());
            final ReadFeatures command = new ReadFeatures(this, bytes);
            messageService.send(command);
        }
    }

}
