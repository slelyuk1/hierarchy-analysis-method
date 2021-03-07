package com.leliuk.controller;

import com.leliuk.controller.control.custom.table.PriorityRow;
import com.leliuk.controller.control.custom.table.PriorityTableView;
import com.leliuk.model.hierarchy.HierarchyModel;
import com.leliuk.model.hierarchy.Priority;
import com.leliuk.model.hierarchy.PriorityMatrix;
import com.leliuk.model.view.AbstractStageAware;
import com.leliuk.service.HierarchyAnalysisService;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class PriorityEvaluationController extends AbstractStageAware {
    @FXML
    private PriorityTableView priorityTableView;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;

    private HierarchyAnalysisService service;

    private IntegerProperty selectedMatrixIndex;
    private List<PriorityMatrix> priorityMatrices;

    public void fillPriorityTable(PriorityMatrix first, PriorityMatrix second, List<PriorityMatrix> other) {
        priorityMatrices = new ArrayList<>(other.size() + 2);
        priorityMatrices.add(first);
        priorityMatrices.add(second);
        priorityMatrices.addAll(other);
        nextButton.disableProperty().bind(selectedMatrixIndex.greaterThan(priorityMatrices.size() - 2));
        selectAnotherMatrix(0);
        prepareStage();
    }

    @Autowired
    public void setService(HierarchyAnalysisService service) {
        this.service = service;
    }

    @FXML
    private void onPreviousClicked() {
        selectAnotherMatrix(selectedMatrixIndex.get() - 1);
    }

    @FXML
    private void onNextClicked() {
        selectAnotherMatrix(selectedMatrixIndex.get() + 1);
    }

    private void selectAnotherMatrix(int index) {
        PriorityMatrix toView = priorityMatrices.get(index);
        selectedMatrixIndex.set(index);
        priorityTableView.setPriorityMatrix(toView);
    }

    private void moveInfoToPriorityMatrix() {
        List<Priority<Double>> priorities = priorityMatrices.get(selectedMatrixIndex.get()).getPriorities();
        List<PriorityRow<Double>> rows = priorityTableView.getItems();
        for (int i = 0; i < rows.size(); ++i) {
            Double[] curPriority = priorities.get(i).getPriorities();
            List<DoubleProperty> row = rows.get(i).getPriorities();
            for (int j = 0; j < row.size(); ++j) {
                curPriority[j] = row.get(j).getValue();
            }
        }
    }

    @FXML
    private void initialize() {
        preparePriorityTable();
    }

    private void preparePriorityTable() {
        priorityTableView.getSelectionModel().setCellSelectionEnabled(true);
        selectedMatrixIndex = new SimpleIntegerProperty(0);
        previousButton.disableProperty().bind(selectedMatrixIndex.lessThan(1));
    }

    private void prepareStage() {
        Stage stage = getStage().orElseThrow(() -> new IllegalStateException("PriorityEvaluationController is not aware of any stage!"));
        stage.setOnCloseRequest(event -> {
            moveInfoToPriorityMatrix();
            HierarchyModel model = new HierarchyModel(
                    priorityMatrices.get(0), priorityMatrices.get(1), new ArrayList<>(priorityMatrices.subList(2, priorityMatrices.size())));
            service.serializePriorityMatrices(new File("saved/savedState.ham"), model);
        });
    }
}
