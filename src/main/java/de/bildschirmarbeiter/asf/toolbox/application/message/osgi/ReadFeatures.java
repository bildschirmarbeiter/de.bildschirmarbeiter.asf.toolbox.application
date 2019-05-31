package de.bildschirmarbeiter.asf.toolbox.application.message.osgi;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;

public class ReadFeatures extends AbstractMessage {

    private final byte[] xml;

    public ReadFeatures(final Object source, final byte[] xml) {
        super(source);
        this.xml = xml;
    }

    public byte[] getXml() {
        return xml;
    }

}
