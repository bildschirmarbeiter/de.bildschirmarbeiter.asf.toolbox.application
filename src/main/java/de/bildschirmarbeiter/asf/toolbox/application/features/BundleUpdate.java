package de.bildschirmarbeiter.asf.toolbox.application.features;

import java.util.Objects;

public class BundleUpdate implements Comparable<BundleUpdate> {

    private final Bundle bundle;

    private final String version;

    public BundleUpdate(final Bundle bundle, final String version) {
        this.bundle = bundle;
        this.version = version;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final BundleUpdate bundleUpdate = (BundleUpdate) object;
        return Objects.equals(bundle, bundleUpdate.getBundle()) && Objects.equals(version, bundleUpdate.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(bundle, version);
    }

    @Override
    public int compareTo(final BundleUpdate bundleUpdate) {
        int result = bundle.compareTo(bundleUpdate.getBundle());
        if (result == 0) {
            result = version.compareTo(bundleUpdate.getVersion());
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", bundle, version);
    }

}
