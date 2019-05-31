package de.bildschirmarbeiter.asf.toolbox.application.message.osgi;

import java.util.List;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;
import de.bildschirmarbeiter.asf.toolbox.application.features.Bundle;

public class BundlesRead extends AbstractMessage {

    private final List<Bundle> bundles;

    public BundlesRead(final Object source, final List<Bundle> bundles) {
        super(source);
        this.bundles = bundles;
    }

    public List<Bundle> getBundles() {
        return bundles;
    }

}
