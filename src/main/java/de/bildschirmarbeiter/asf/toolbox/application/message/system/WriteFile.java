package de.bildschirmarbeiter.asf.toolbox.application.message.system;

public class WriteFile extends FileMessage {

    public WriteFile(final Object source, final String path, final byte[] bytes) {
        super(source, path, bytes);
    }

}
