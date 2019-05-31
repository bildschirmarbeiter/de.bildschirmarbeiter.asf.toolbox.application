package de.bildschirmarbeiter.asf.toolbox.application.message.maven;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;
import de.bildschirmarbeiter.asf.toolbox.application.features.Bundle;

public class FindLatestRelease extends AbstractMessage {

    private final Bundle bundle;

    public FindLatestRelease(final Object source, final Bundle bundle) {
        super(source);
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

}
