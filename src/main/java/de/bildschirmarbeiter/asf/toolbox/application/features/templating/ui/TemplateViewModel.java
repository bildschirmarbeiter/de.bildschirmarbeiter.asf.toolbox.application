package de.bildschirmarbeiter.asf.toolbox.application.features.templating.ui;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.eventbus.Subscribe;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.features.Bundle;
import de.bildschirmarbeiter.asf.toolbox.application.features.Feature;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.FeaturesRead;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.FileRead;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.LogMessage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = TemplateViewModel.class
)
public class TemplateViewModel {

    // template
    byte[] bytes;

    final StringProperty content = new SimpleStringProperty();

    final ObservableList<Feature> features = FXCollections.observableArrayList();

    final ObservableList<Bundle> bundles = FXCollections.observableArrayList();

    final StringProperty result = new SimpleStringProperty();

    @Reference
    private volatile Handlebars handlebars;

    @Reference
    private volatile MessageService messageService;

    public TemplateViewModel() {
    }

    @Activate
    public void activate() {
        messageService.register(this);
    }

    @Subscribe
    public void onFileRead(final FileRead event) {
        if (this == event.source()) {
            bytes = event.getBytes();
            content.setValue(new String(bytes, StandardCharsets.UTF_8));
            processTemplate();
        }
    }

    @Subscribe
    public void onFeaturesRead(final FeaturesRead event) {
        features.clear();
        bundles.clear();
        final List<Feature> features = event.getFeatures();
        final TreeSet<Bundle> bundles = new TreeSet<>();
        for (final Feature feature : features) {
            bundles.addAll(feature.getBundles());
        }
        this.features.addAll(features);
        this.bundles.addAll(bundles);
        processTemplate();
    }

    private void processTemplate() {
        try {
            final Template template = handlebars.compileInline(content.getValueSafe());
            final Map<String, Object> context = new HashMap<>();
            context.put("features", features);
            context.put("bundles", bundles);
            final String result = template.apply(context);
            this.result.setValue(result);
        } catch (Exception e) {
            messageService.send(LogMessage.error(this, e.getMessage()));
        }
    }

}
