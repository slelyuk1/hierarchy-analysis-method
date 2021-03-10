package com.leliuk.model.view;

import javafx.stage.Stage;

import java.util.Optional;


public abstract class AbstractStageAware implements StageAware {
    private Stage stage;

    @Override
    public Optional<Stage> getStage() {
        return Optional.ofNullable(stage);
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected Stage getStageDangerously() {
        return getStage()
                .orElseThrow(() -> new IllegalStateException("HierarchyConstructionController is not aware of any stage. Cannot proceed to next view."));
    }
}
