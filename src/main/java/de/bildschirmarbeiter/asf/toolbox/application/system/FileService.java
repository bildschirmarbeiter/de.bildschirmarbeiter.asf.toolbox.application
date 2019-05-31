package de.bildschirmarbeiter.asf.toolbox.application.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.service.component.annotations.Component;

@Component(
    service = FileService.class
)
public class FileService {

    public byte[] readFile(final String path) throws IOException {
        final Path p = Paths.get(path);
        return Files.readAllBytes(p);
    }

    public String writeFile(final String path, final byte[] bytes) throws IOException {
        final Path p = Paths.get(path);
        return Files.write(p, bytes).toString();
    }

}
