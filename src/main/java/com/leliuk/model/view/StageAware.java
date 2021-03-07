package com.leliuk.model.view;

import javafx.stage.Stage;

import java.util.Optional;

public interface StageAware {
    void setStage(Stage stage);

    Optional<Stage> getStage();
}
