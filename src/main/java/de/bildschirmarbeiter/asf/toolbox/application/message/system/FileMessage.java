package de.bildschirmarbeiter.asf.toolbox.application.message.system;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;

abstract class FileMessage extends AbstractMessage {

    final String path;

    final byte[] bytes;

    FileMessage(final Object source, final String path, final byte[] bytes) {
        super(source);
        this.path = path;
        this.bytes = bytes;
    }

    public String getPath() {
        return path;
    }

    public byte[] getBytes() {
        return bytes;
    }

}
