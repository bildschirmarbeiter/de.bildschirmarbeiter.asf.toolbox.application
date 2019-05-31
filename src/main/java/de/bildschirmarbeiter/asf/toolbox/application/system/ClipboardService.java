package de.bildschirmarbeiter.asf.toolbox.application.system;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.LogMessage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = ClipboardService.class
)
public class ClipboardService {

    @Reference
    private volatile MessageService messageService;

    private Clipboard clipboard;

    public ClipboardService() {
    }

    @Activate
    private void activate() {
        clipboard = Clipboard.getSystemClipboard();
    }

    @Deactivate
    private void deactivate() {
        clipboard = null;
    }

    public void copyToClipboard(final Iterable<String> strings) {
        final StringBuilder sb = new StringBuilder();
        strings.forEach(string -> sb.append(string).append("\n"));
        final ClipboardContent content = new ClipboardContent();
        content.putString(sb.toString());
        clipboard.setContent(content);
        messageService.send(LogMessage.info(this, "content copied to clipboard"));
    }

}
