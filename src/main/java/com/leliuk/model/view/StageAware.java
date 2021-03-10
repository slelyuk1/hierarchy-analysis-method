package com.leliuk.model.view;

import javafx.stage.Stage;

import java.util.Optional;

public interface StageAware {
    Optional<Stage> getStage();

    void setStage(Stage stage);
}
