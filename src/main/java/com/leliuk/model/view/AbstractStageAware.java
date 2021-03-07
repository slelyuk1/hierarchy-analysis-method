package com.leliuk.model.view;

import javafx.stage.Stage;

import java.util.Optional;


public abstract class AbstractStageAware implements StageAware {
    private Stage stage;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Optional<Stage> getStage() {
        return Optional.ofNullable(stage);
    }
}
