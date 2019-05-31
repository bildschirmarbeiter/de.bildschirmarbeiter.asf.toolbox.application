package de.bildschirmarbeiter.asf.toolbox.application.message.system;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;

public class ReadFile extends AbstractMessage {

    final String path;

    public ReadFile(final Object source, final String path) {
        super(source);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
