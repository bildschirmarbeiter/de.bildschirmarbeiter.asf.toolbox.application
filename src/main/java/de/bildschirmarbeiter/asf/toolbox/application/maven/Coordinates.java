package de.bildschirmarbeiter.asf.toolbox.application.maven;

// <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>
public class Coordinates {

    final String groupId;

    final String artifactId;

    final String version;

    final String extension;

    final String classifier;

    public Coordinates(final String[] coordinates) {
        this.groupId = coordinates[0];
        this.artifactId = coordinates[1];
        this.version = coordinates[2];
        this.extension = coordinates[3];
        this.classifier = coordinates[4];
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

    public String getExtension() {
        return extension;
    }

    public String getClassifier() {
        return classifier;
    }

}
