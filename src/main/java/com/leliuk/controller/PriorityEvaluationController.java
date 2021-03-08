package com.leliuk.controller;

import com.leliuk.configuration.RandomConsistencyValuesConfiguration;
import com.leliuk.controller.control.custom.table.PriorityTableView;
import com.leliuk.controller.control.custom.table.row.PriorityRow;
import com.leliuk.model.hierarchy.HierarchyModel;
import com.leliuk.model.hierarchy.Priority;
import com.leliuk.model.hierarchy.PriorityMatrix;
import com.leliuk.model.view.AbstractStageAware;
import com.leliuk.model.view.View;
import com.leliuk.service.HierarchyAnalysisService;
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

    private HierarchyAnalysisService service;
    private RandomConsistencyValuesConfiguration randomConsistencyValuesConfiguration;
    private View<GridPane, EvaluationResultController> evaluationResultView;

    private IntegerProperty selectedMatrixIndex;
    private HierarchyModel model;
    private List<PriorityMatrix> priorityMatrices;

    public void setHierarchyModel(HierarchyModel model) {
        this.model = model;
        priorityTableView.setRandomConsistencyValues(randomConsistencyValuesConfiguration);
        priorityMatrices = new ArrayList<>(model.getOtherAlternativeMatrices().size() + 2);
        priorityMatrices.add(model.getGoalMatrix());
        priorityMatrices.add(model.getFirstAlternativeMatrix());
        priorityMatrices.addAll(model.getOtherAlternativeMatrices());
        prepareNextButton();
        prepareGlobalPrioritiesButton();
        selectAnotherMatrix(0);
        consistencyIndexLabel.textProperty().bind(priorityTableView.getConsistencyIndex().asString());
        consistencyValueLabel.textProperty().bind(priorityTableView.getConsistencyValue().asString());
        prepareStage();
    }

    @Autowired
    public void setRandomConsistencyValuesConfiguration(RandomConsistencyValuesConfiguration randomConsistencyValuesConfiguration) {
        this.randomConsistencyValuesConfiguration = randomConsistencyValuesConfiguration;
    }

    @Autowired
    public void setService(HierarchyAnalysisService service) {
        this.service = service;
    }

    @Autowired
    public void setEvaluationResultView(View<GridPane, EvaluationResultController> evaluationResultView) {
        this.evaluationResultView = evaluationResultView;
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
            HierarchyModel model = new HierarchyModel("saved/savedState.ham",
                    priorityMatrices.get(0),
                    priorityMatrices.get(1),
                    new ArrayList<>(priorityMatrices.subList(2, priorityMatrices.size())));
            service.serializePriorityMatrices(model);
        });
    }
}
