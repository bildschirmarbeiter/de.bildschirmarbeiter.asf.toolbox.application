package de.bildschirmarbeiter.asf.toolbox.application.features;

import java.util.Objects;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class Bundle implements Comparable<Bundle> {

    private final String groupId;

    private final String artifactId;

    private final String version;

    public Bundle(final String groupId, final String artifactId, final String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public boolean isOutdated(final String version) {
        if (version != null && !this.version.startsWith("${")) {
            final ComparableVersion currentVersion = new ComparableVersion(this.version);
            final ComparableVersion latestVersion = new ComparableVersion(version);
            return latestVersion.compareTo(currentVersion) > 0;
        }
        return false;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final Bundle bundle = (Bundle) object;
        return Objects.equals(groupId, bundle.groupId) && Objects.equals(artifactId, bundle.artifactId) && Objects.equals(version, bundle.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }

    @Override
    public int compareTo(final Bundle bundle) {
        int result = groupId.compareTo(bundle.getGroupId());
        if (result == 0) {
            result = artifactId.compareTo(bundle.getArtifactId());
            if (result == 0) {
                result = version.compareTo(bundle.getVersion());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s/%s/%s", groupId, artifactId, version);
    }

}
