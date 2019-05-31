package de.bildschirmarbeiter.asf.toolbox.application.maven;

public interface MavenService {

    String findLatestVersion(final String groupId, final String artifactId) throws Exception;

}
