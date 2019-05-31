package de.bildschirmarbeiter.asf.toolbox.application.features.updating.ui;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.google.common.eventbus.Subscribe;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.features.Bundle;
import de.bildschirmarbeiter.asf.toolbox.application.features.BundleUpdate;
import de.bildschirmarbeiter.asf.toolbox.application.message.maven.LatestReleaseFound;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.BundlesRead;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.FeaturesUpdated;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.ReadBundles;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.FileRead;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = FeaturesUpdatingViewModel.class
)
public class FeaturesUpdatingViewModel {

    // features.xml
    byte[] bytes;

    final StringProperty content = new SimpleStringProperty();

    final StringProperty path = new SimpleStringProperty();

    final ObservableList<Bundle> bundles = FXCollections.observableArrayList();

    final ObservableList<BundleUpdate> updates = FXCollections.observableArrayList();

    @Reference
    private volatile MessageService messageService;

    public FeaturesUpdatingViewModel() {
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
            path.setValue(event.getPath());
            final ReadBundles command = new ReadBundles(this, bytes);
            messageService.send(command);
        }
    }

    @Subscribe
    public void onBundlesRead(final BundlesRead event) {
        clear();
        final Set<Bundle> set = new TreeSet<>(event.getBundles());
        bundles.addAll(set);
    }

    @Subscribe
    public void onLatestReleaseFound(final LatestReleaseFound event) {
        if (event.getBundle().isOutdated(event.getVersion())) {
            final BundleUpdate bundleUpdate = new BundleUpdate(event.getBundle(), event.getVersion());
            if (!updates.contains(bundleUpdate)) {
                updates.add(bundleUpdate);
                Collections.sort(updates);
            }
        }
    }

    @Subscribe
    public void onFeaturesUpdated(final FeaturesUpdated event) {
        bytes = event.getXml();
        final String xml = new String(bytes, StandardCharsets.UTF_8);
        content.setValue(xml);
    }

    private void clear() {
        bundles.clear();
        updates.clear();
    }

}
