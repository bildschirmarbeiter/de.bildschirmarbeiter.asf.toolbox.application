package de.bildschirmarbeiter.asf.toolbox.application.features;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

public class Feature {

    final String name;

    final List<String> features = new LinkedList<>();

    final List<Bundle> bundles = new LinkedList<>();

    final List<Configuration> configurations = new LinkedList<>();

    public Feature(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getJavaName() {
        return forJava(name);
    }

    public void addFeature(final String feature) {
        features.add(feature);
    }

    public List<String> getFeatures() {
        return features;
    }

    public void addBundle(final Bundle bundle) {
        bundles.add(bundle);
    }

    public List<Bundle> getBundles() {
        return bundles;
    }

    public void addConfiguration(final Configuration configuration) {
        configurations.add(configuration);
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public static String forJava(final String name) {
        String javaName = WordUtils.capitalize(name, '-');
        javaName = javaName.replace("-", "");
        javaName = WordUtils.uncapitalize(javaName);
        return javaName;
    }

    @Override
    public String toString() {
        return String.format("%s (features: %s, bundles: %s, configurations: %s)", name, features.size(), bundles.size(), configurations.size());
    }

}
