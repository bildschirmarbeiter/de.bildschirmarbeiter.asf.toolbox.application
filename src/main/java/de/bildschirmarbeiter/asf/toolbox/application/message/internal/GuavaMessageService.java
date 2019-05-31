package de.bildschirmarbeiter.asf.toolbox.application.message.internal;

import com.google.common.eventbus.EventBus;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import org.osgi.service.component.annotations.Component;

@Component(
    service = MessageService.class
)
public class GuavaMessageService extends EventBus implements MessageService {

    @Override
    public void send(final Object message) {
        post(message);
    }

}
