package com.leliuk.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Configuration
public class FileKeepingConfiguration {
    private final Path defaultPath;
    private final String defaultExtension;
    private final String defaultFileName;

    FileKeepingConfiguration(@Value("${file-keeping.default-keeping-location}") String defaultPath,
                             @Value("${file-keeping.extension}") String defaultExtension,
                             @Value("${file-keeping.default-keeping-file-name}") String defaultFileName) throws IOException {
        this.defaultPath = Paths.get(defaultPath);
        this.defaultExtension = defaultExtension;
        this.defaultFileName = defaultFileName;
        Files.createDirectories(this.defaultPath);
    }
}
