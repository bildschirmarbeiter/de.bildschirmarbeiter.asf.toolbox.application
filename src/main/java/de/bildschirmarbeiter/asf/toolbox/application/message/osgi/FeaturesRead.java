package de.bildschirmarbeiter.asf.toolbox.application.message.osgi;

import java.util.List;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;
import de.bildschirmarbeiter.asf.toolbox.application.features.Feature;

public class FeaturesRead extends AbstractMessage {

    private final List<Feature> features;

    public FeaturesRead(final Object source, final List<Feature> features) {
        super(source);
        this.features = features;
    }

    public List<Feature> getFeatures() {
        return features;
    }

}
