package com.leliuk.controller;

import com.leliuk.configuration.RandomConsistencyValuesConfiguration;
import com.leliuk.controller.control.custom.table.PriorityTableView;
import com.leliuk.controller.control.custom.table.row.PriorityRow;
import com.leliuk.model.hierarchy.HierarchyModel;
import com.leliuk.model.other.Priority;
import com.leliuk.model.other.PriorityMatrix;
import com.leliuk.model.view.AbstractStageAware;
import com.leliuk.model.view.View;
import com.leliuk.service.FileKeepingService;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    @FXML
    private Label consistencyIndexLabel;
    @FXML
    private Label consistencyValueLabel;
    @FXML
    private Button globalPrioritiesButton;

    private RandomConsistencyValuesConfiguration randomConsistencyValuesConfiguration;
    private FileKeepingService fileKeepingService;

    private View<GridPane, EvaluationResultController> evaluationResultView;
    private IntegerProperty selectedMatrixIndex;
    private HierarchyModel model;
    private List<PriorityMatrix> priorityMatrices;

    @Autowired
    public void setRandomConsistencyValuesConfiguration(RandomConsistencyValuesConfiguration randomConsistencyValuesConfiguration) {
        this.randomConsistencyValuesConfiguration = randomConsistencyValuesConfiguration;
    }

    @Autowired
    public void setEvaluationResultView(View<GridPane, EvaluationResultController> evaluationResultView) {
        this.evaluationResultView = evaluationResultView;
    }

    @Autowired
    public void setFileKeepingService(FileKeepingService fileKeepingService) {
        this.fileKeepingService = fileKeepingService;
    }

    public void setHierarchyModel(HierarchyModel model) {
        this.model = model;
        priorityTableView.setRandomConsistencyValues(randomConsistencyValuesConfiguration);
        PriorityMatrix goalMatrix = model.getGoalMatrix()
                .orElseThrow(() -> new IllegalStateException("Priority evaluation cannot be started without goal matrix!"));
        if (model.getAlternativeMatrices().isEmpty()) {
            throw new IllegalStateException("Priority evaluation cannot be started without at least one alternative matrix!");
        }
        priorityMatrices = new ArrayList<>(model.getAlternativeMatrices().size() + 1);
        priorityMatrices.add(goalMatrix);
        priorityMatrices.addAll(model.getAlternativeMatrices());
        prepareNextButton();
        prepareGlobalPrioritiesButton();
        selectAnotherMatrix(0);
        consistencyIndexLabel.textProperty().bind(priorityTableView.getConsistencyIndex().asString());
        consistencyValueLabel.textProperty().bind(priorityTableView.getConsistencyValue().asString());
        prepareStage();
    }

    @FXML
    private void onPreviousClicked() {
        selectAnotherMatrix(selectedMatrixIndex.get() - 1);
    }

    @FXML
    private void onNextClicked() {
        selectAnotherMatrix(selectedMatrixIndex.get() + 1);
    }

    @FXML
    private void onGlobalPrioritiesClicked() {
        evaluationResultView.getController().evaluateResults(model);
        getStageDangerously().setScene(new Scene(evaluationResultView.getGraphics()));
    }

    private void selectAnotherMatrix(int index) {
        moveInfoToPriorityMatrix();
        PriorityMatrix toView = priorityMatrices.get(index);
        selectedMatrixIndex.set(index);
        priorityTableView.setPriorityMatrix(toView);
        priorityTableView.refresh();
    }

    private void moveInfoToPriorityMatrix() {
        List<Priority> priorities = priorityMatrices.get(selectedMatrixIndex.get()).getPriorities();
        List<PriorityRow> rows = priorityTableView.getItems();
        for (int i = 0; i < rows.size(); ++i) {
            List<Double> curPriority = priorities.get(i).getValues();
            List<DoubleProperty> row = rows.get(i).getPriorities();
            for (int j = 0; j < row.size(); ++j) {
                curPriority.set(j, row.get(j).getValue());
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

    private void prepareNextButton() {
        BooleanBinding disableWhenPrioritiesAreInconsistent = new BooleanBinding() {
            {
                bind(priorityTableView.getConsistencyValue(), priorityTableView.getConsistencyIndex());
            }

            @Override
            protected boolean computeValue() {
                double consistencyIndex = priorityTableView.getConsistencyIndex().get();
                double consistencyValue = priorityTableView.getConsistencyValue().get();
                return Double.isNaN(consistencyIndex) || Double.isNaN(consistencyValue);
            }
        };
        nextButton.disableProperty().bind(
                selectedMatrixIndex.greaterThan(priorityMatrices.size() - 2).or(disableWhenPrioritiesAreInconsistent)
        );
    }

    private void prepareGlobalPrioritiesButton() {
        BooleanBinding disableWhenPrioritiesAreInconsistent = new BooleanBinding() {
            {
                bind(priorityTableView.getConsistencyValue(), priorityTableView.getConsistencyIndex());
            }

            @Override
            protected boolean computeValue() {
                double consistencyIndex = priorityTableView.getConsistencyIndex().get();
                double consistencyValue = priorityTableView.getConsistencyValue().get();
                return Double.isNaN(consistencyIndex) || Double.isNaN(consistencyValue);
            }
        };
        globalPrioritiesButton.disableProperty().bind(selectedMatrixIndex.lessThan(priorityMatrices.size() - 1).or(disableWhenPrioritiesAreInconsistent));
    }

    private void prepareStage() {
        Stage stage = getStage().orElseThrow(() -> new IllegalStateException("PriorityEvaluationController is not aware of any stage!"));
        stage.setOnCloseRequest(event -> {
            moveInfoToPriorityMatrix();
            fileKeepingService.saveHierarchyModel(model);
        });
    }
}
