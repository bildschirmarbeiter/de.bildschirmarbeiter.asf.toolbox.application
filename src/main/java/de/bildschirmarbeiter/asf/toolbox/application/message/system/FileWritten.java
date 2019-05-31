package de.bildschirmarbeiter.asf.toolbox.application.message.system;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;

public class FileWritten extends AbstractMessage {

    final String path;

    public FileWritten(final Object source, final String path) {
        super(source);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
