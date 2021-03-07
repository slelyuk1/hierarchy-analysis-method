package com.leliuk.controller;

import com.leliuk.model.hierarchy.HierarchyAnalysisModel;
import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.view.AbstractStageAware;
import com.leliuk.model.view.View;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HierarchyConstructionController extends AbstractStageAware {
    @FXML
    private TextField goalTextField;
    @FXML
    private ListView<HierarchyMember> criteriaListView;
    @FXML
    private ListView<HierarchyMember> alternativesListView;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button navigateToPrioritiesButton;
    private View<GridPane, PriorityEvaluationController> priorityEvaluationView;

    public void onNavigateToPrioritiesClicked() {
        HierarchyAnalysisModel hierarchyAnalysisModel = HierarchyAnalysisModel.getInstance();
        HierarchyMember goal = new HierarchyMember(goalTextField.getText());
        hierarchyAnalysisModel.createPriorityMatrices(goal, criteriaListView.getItems(), alternativesListView.getItems());
        PriorityEvaluationController priorityEvaluationController = priorityEvaluationView.getController();
        priorityEvaluationController.fillPriorityTable(hierarchyAnalysisModel.getPriorityMatrices());
        Stage stage = getStage()
                .orElseThrow(() -> new IllegalStateException("HierarchyConstructionController is not aware of any stage. Cannot proceed to next view."));
        stage.setScene(new Scene(priorityEvaluationView.getGraphics()));
    }

    @Autowired
    public void setPriorityEvaluationView(View<GridPane, PriorityEvaluationController> priorityEvaluationView) {
        this.priorityEvaluationView = priorityEvaluationView;
    }

    @FXML
    private void onAddButtonClicked() {
        new TextInputDialog().showAndWait().ifPresent(hierarchyMemberName -> {
            HierarchyMember toAdd = new HierarchyMember(hierarchyMemberName);
            if (criteriaListView.isFocused()) {
                criteriaListView.getItems().add(toAdd);
            } else if (alternativesListView.isFocused()) {
                alternativesListView.getItems().add(toAdd);
            } else {
                new Alert(Alert.AlertType.ERROR, "Couldn't add hierarchy member because none of lists is selected!").showAndWait();
            }
        });
    }

    public void onDeleteButtonClicked() {
        List<HierarchyMember> criteria = criteriaListView.getItems();
        List<HierarchyMember> alternatives = alternativesListView.getItems();
        criteriaListView.getSelectionModel().getSelectedIndices().forEach(i -> criteria.remove(i.intValue()));
        alternativesListView.getSelectionModel().getSelectedIndices().forEach(i -> alternatives.remove(i.intValue()));
    }

    @FXML
    private void initialize() {
        prepareCriteriaAndAlternativesListViews();
        prepareNavigateToPrioritiesButton();
        prepareAddButton();
        prepareDeleteButton();

        // todo remove
        goalTextField.setText("Test Goal");
        criteriaListView.getItems().add(new HierarchyMember("Criteria1"));
        criteriaListView.getItems().add(new HierarchyMember("Criteria2"));
        criteriaListView.getItems().add(new HierarchyMember("Criteria3"));
        criteriaListView.getItems().add(new HierarchyMember("Criteria4"));
        alternativesListView.getItems().add(new HierarchyMember("Alternative1"));
        alternativesListView.getItems().add(new HierarchyMember("Alternative2"));
        alternativesListView.getItems().add(new HierarchyMember("Alternative3"));
        alternativesListView.getItems().add(new HierarchyMember("Alternative4"));
    }

    private void prepareCriteriaAndAlternativesListViews() {
        Callback<ListView<HierarchyMember>, ListCell<HierarchyMember>> cellFactory = param -> new ListCell<HierarchyMember>() {
            @Override
            protected void updateItem(HierarchyMember item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                }
            }
        };
        criteriaListView.setCellFactory(cellFactory);
        alternativesListView.setCellFactory(cellFactory);
    }

    private void prepareNavigateToPrioritiesButton() {
        BooleanExpression goalIsEmpty = goalTextField.textProperty().isEmpty();
        navigateToPrioritiesButton.disableProperty().bind(goalIsEmpty);
    }

    private void prepareAddButton() {
        BooleanExpression criteriaListIsNotFocused = criteriaListView.focusedProperty().not();
        BooleanExpression alternativesListIsNotFocused = alternativesListView.focusedProperty().not();
        addButton.disableProperty().bind(criteriaListIsNotFocused.and(alternativesListIsNotFocused));
    }

    private void prepareDeleteButton() {
        BooleanExpression criteriaListIsNotFocused = criteriaListView.focusedProperty().not();
        BooleanExpression alternativesListIsNotFocused = alternativesListView.focusedProperty().not();

        BooleanBinding criteriaIsNotSelected = criteriaListView.getSelectionModel().selectedItemProperty().isNull();
        BooleanBinding alternativeIsNotSelected = alternativesListView.getSelectionModel().selectedItemProperty().isNull();
        deleteButton.disableProperty().bind(
                criteriaIsNotSelected
                        .and(alternativeIsNotSelected)
                        .and(criteriaListIsNotFocused)
                        .and(alternativesListIsNotFocused));
    }
}
