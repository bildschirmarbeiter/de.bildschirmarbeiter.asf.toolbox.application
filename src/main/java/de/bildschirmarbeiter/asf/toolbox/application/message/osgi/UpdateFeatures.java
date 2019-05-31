package de.bildschirmarbeiter.asf.toolbox.application.message.osgi;

import java.util.List;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;
import de.bildschirmarbeiter.asf.toolbox.application.features.BundleUpdate;

public class UpdateFeatures extends AbstractMessage {

    private final byte[] xml;

    private final List<BundleUpdate> updates;

    public UpdateFeatures(final Object source, final byte[] xml, final List<BundleUpdate> updates) {
        super(source);
        this.xml = xml;
        this.updates = updates;
    }

    public byte[] getXml() {
        return xml;
    }

    public List<BundleUpdate> getUpdates() {
        return updates;
    }

}
