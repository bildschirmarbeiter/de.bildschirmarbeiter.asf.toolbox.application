package de.bildschirmarbeiter.asf.toolbox.application.message.maven;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;
import de.bildschirmarbeiter.asf.toolbox.application.features.Bundle;

public class LatestReleaseFound extends AbstractMessage {

    private final Bundle bundle;

    private final String version;

    public LatestReleaseFound(final Object source, final Bundle bundle, final String version) {
        super(source);
        this.bundle = bundle;
        this.version = version;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public String getVersion() {
        return version;
    }

}
