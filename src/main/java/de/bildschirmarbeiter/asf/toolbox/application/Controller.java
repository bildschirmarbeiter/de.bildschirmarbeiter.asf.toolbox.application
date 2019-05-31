package de.bildschirmarbeiter.asf.toolbox.application;

import java.util.List;

import javafx.application.Platform;

import com.google.common.eventbus.Subscribe;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.features.Bundle;
import de.bildschirmarbeiter.asf.toolbox.application.features.Feature;
import de.bildschirmarbeiter.asf.toolbox.application.features.FeatureService;
import de.bildschirmarbeiter.asf.toolbox.application.maven.MavenService;
import de.bildschirmarbeiter.asf.toolbox.application.message.maven.FindLatestRelease;
import de.bildschirmarbeiter.asf.toolbox.application.message.maven.LatestReleaseFound;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.BundlesRead;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.FeaturesRead;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.FeaturesUpdated;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.ReadBundles;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.ReadFeatures;
import de.bildschirmarbeiter.asf.toolbox.application.message.osgi.UpdateFeatures;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.FileRead;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.FileWritten;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.LogMessage;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.ReadFile;
import de.bildschirmarbeiter.asf.toolbox.application.message.system.WriteFile;
import de.bildschirmarbeiter.asf.toolbox.application.system.FileService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import static de.bildschirmarbeiter.asf.toolbox.application.Helper.message;

@Component(
    immediate = true
)
public class Controller {

    @Reference
    private volatile FeatureService featureService;

    @Reference
    private volatile MessageService messageService;

    @Reference
    private volatile MavenService mavenService;

    @Reference
    private volatile FileService fileService;

    @Activate
    private void activate() {
        messageService.register(this);
    }

    @Deactivate
    private void deactivate() {
        messageService.unregister(this);
    }

    public Controller() {
    }

    @Subscribe
    public void onReadBundles(final ReadBundles command) {
        new Thread() {
            public void run() {
                try {
                    final List<Bundle> result = featureService.readBundles(command.getXml());
                    final BundlesRead event = new BundlesRead(this, result);
                    postMessage(event);
                } catch (Exception e) {
                    postMessage(LogMessage.error(this, e.toString()));
                }
            }
        }.start();
    }

    @Subscribe
    public void onReadFeatures(final ReadFeatures command) {
        new Thread() {
            public void run() {
                try {
                    final List<Feature> result = featureService.readFeatures(command.getXml());
                    final FeaturesRead event = new FeaturesRead(this, result);
                    postMessage(event);
                } catch (Exception e) {
                    postMessage(LogMessage.error(this, e.toString()));
                }
            }
        }.start();
    }

    @Subscribe
    public void onUpdateFeatures(final UpdateFeatures command) {
        new Thread() {
            public void run() {
                try {
                    final byte[] result = featureService.updateBundles(command.getXml(), command.getUpdates());
                    final FeaturesUpdated event = new FeaturesUpdated(this, result);
                    postMessage(event);
                } catch (Exception e) {
                    postMessage(LogMessage.error(this, e.toString()));
                }
            }
        }.start();
    }

    @Subscribe
    public void onFindLatestRelease(final FindLatestRelease command) {
        final Bundle bundle = command.getBundle();
        final String name = String.format("Thread %s/%s (@%s)", bundle.getGroupId(), bundle.getArtifactId(), command.timestamp());
        new Thread(name) {
            public void run() {
                try {
                    final String version = mavenService.findLatestVersion(bundle.getGroupId(), bundle.getArtifactId());
                    final LatestReleaseFound event = new LatestReleaseFound(this, bundle, version);
                    postMessage(event);
                    // postMessage(LogMessage.info(this, message("latest version for %s/%s found: %s", bundle.getGroupId(), bundle.getArtifactId(), version)));
                } catch (Exception e) {
                    postMessage(LogMessage.error(this, message("getting latest version for bundle %s/%s failed: %s", bundle.getGroupId(), bundle.getArtifactId(), e)));
                }
            }
        }.start();
    }

    @Subscribe
    public void onReadFile(final ReadFile command) {
        final String path = command.getPath();
        new Thread() {
            public void run() {
                try {
                    final byte[] bytes = fileService.readFile(path);
                    final FileRead event = new FileRead(command.source(), path, bytes);
                    postMessage(event);
                    postMessage(LogMessage.info(this, message("read %s bytes from file %s", bytes.length, path)));
                } catch (Exception e) {
                    postMessage(LogMessage.error(this, message("reading file %s failed: %s", path, e)));
                }
            }
        }.start();
    }

    @Subscribe
    public void onWriteFile(final WriteFile command) {
        final String path = command.getPath();
        new Thread() {
            public void run() {
                try {
                    final String p = fileService.writeFile(path, command.getBytes());
                    final FileWritten event = new FileWritten(this, p);
                    postMessage(event);
                    postMessage(LogMessage.info(this, message("wrote %s bytes to file %s", command.getBytes().length, path)));
                } catch (Exception e) {
                    postMessage(LogMessage.error(this, message("writing file %s failed: %s", path, e)));
                }
            }
        }.start();
    }

    private void postMessage(final Object event) {
        Platform.runLater(() -> messageService.send(event));
    }

}
