package com.leliuk.controller;

import com.leliuk.controller.control.custom.table.PriorityTableView;
import com.leliuk.model.hierarchy.PriorityMatrix;
import com.leliuk.model.view.AbstractStageAware;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PriorityEvaluationController extends AbstractStageAware {
    @FXML
    private PriorityTableView priorityTableView;
    private Collection<PriorityMatrix> priorityMatrices;

    public void fillPriorityTable(Collection<PriorityMatrix> priorityMatrices) {
        PriorityMatrix toView = priorityMatrices.stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("At least one matrix is expected to be viewed!"));
        priorityTableView.setPriorityMatrix(toView);
        this.priorityMatrices = priorityMatrices;
    }

    @FXML
    private void initialize() {
        preparePriorityTable();
    }

    private void preparePriorityTable() {
        priorityTableView.getSelectionModel().setCellSelectionEnabled(true);
    }
}
