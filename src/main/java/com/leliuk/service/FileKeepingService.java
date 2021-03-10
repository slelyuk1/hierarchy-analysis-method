package com.leliuk.service;

import com.leliuk.configuration.FileKeepingConfiguration;
import com.leliuk.model.hierarchy.HierarchyModel;
import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class FileKeepingService {
    private final FileKeepingConfiguration configuration;
    private final SerializationService serializationService;

    public boolean saveHierarchyModel(@NotNull HierarchyModel model) {
        try {
            File fileForKeeping = model.getSerializationFile().orElseGet(() -> model.getGoal()
                    .map(goal -> getFileForKeeping(goal.getName()))
                    .orElse(getFileForKeeping())
            );
            serializationService.serializeObject(model, fileForKeeping);
            return true;
        } catch (IOException e) {
            log.error("An error occurred during serialization of hierarchy model:", e);
        }
        return false;
    }

    public Optional<HierarchyModel> retrieveHierarchyModel(@NotNull File toRetrieveFrom) {
        try {
            return Optional.of(serializationService.<HierarchyModel>deserializeObject(toRetrieveFrom));
        } catch (IOException | ClassNotFoundException e) {
            log.error("An error occurred during serialization of HierarchyModel:", e);
        }
        return Optional.empty();
    }


    @PostConstruct
    public void createSerializationLocation() throws IOException {
        Files.createDirectories(configuration.getDefaultPath());
    }

    private File getFileForKeeping(String fileName) {
        return Paths.get(configuration.getDefaultPath().toString(), fileName + configuration.getDefaultExtension()).toFile();
    }

    private File getFileForKeeping() {
        return getFileForKeeping(configuration.getDefaultFileName());
    }
}
