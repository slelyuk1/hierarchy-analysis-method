package com.leliuk.controller;

import com.leliuk.controller.control.custom.table.row.ResultRow;
import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.hierarchy.HierarchyModel;
import com.leliuk.model.other.NormalizedLocalPriority;
import com.leliuk.model.other.PriorityMatrix;
import com.leliuk.model.view.AbstractStageAware;
import com.leliuk.service.FileKeepingService;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EvaluationResultController extends AbstractStageAware {
    @FXML
    private TableView<ResultRow> resultsTableView;

    private FileKeepingService fileKeepingService;

    private HierarchyModel model;

    @Autowired
    public void setFileKeepingService(FileKeepingService fileKeepingService) {
        this.fileKeepingService = fileKeepingService;
    }

    public void evaluateResults(HierarchyModel model) {
        this.model = model;
        PriorityMatrix goalMatrix = model.getGoalMatrix()
                .orElseThrow(() -> new IllegalStateException("Hierarchy model results cannot be evaluated without goal priority matrix!"));
        NormalizedLocalPriority criteriaNormalizedLocalPriorities = new NormalizedLocalPriority(goalMatrix);
        List<NormalizedLocalPriority> alternativesLocalPriorities = model.getAlternativeMatrices().stream()
                .map(NormalizedLocalPriority::new)
                .collect(Collectors.toList());
        Map<HierarchyMember, Double> globalPriorities = new HashMap<>();
        alternativesLocalPriorities.forEach(alternative ->
                alternative.getNormalizedLocalPriorities().forEach((key, value) -> {
                    double criteriaNormalizedLocalPriority =
                            criteriaNormalizedLocalPriorities.getNormalizedLocalPriorities().getOrDefault(alternative.getGoal(), Double.NaN);
                    globalPriorities.merge(key,
                            value * criteriaNormalizedLocalPriority,
                            (l, r) -> l + value * criteriaNormalizedLocalPriority);
                }));
        globalPriorities.forEach((key, value) -> resultsTableView.getItems().add(new ResultRow(key.getName(), value)));
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        stage.setOnCloseRequest(event -> fileKeepingService.saveHierarchyModel(model));
    }
}
