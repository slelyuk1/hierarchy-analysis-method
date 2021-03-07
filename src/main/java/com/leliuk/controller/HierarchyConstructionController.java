package com.leliuk.controller;

import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.hierarchy.Priority;
import com.leliuk.model.hierarchy.PriorityMatrix;
import com.leliuk.model.view.AbstractStageAware;
import com.leliuk.model.view.View;
import com.leliuk.service.HierarchyAnalysisService;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

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

    private HierarchyAnalysisService service;
    private View<GridPane, PriorityEvaluationController> priorityEvaluationView;

    public void onNavigateToPrioritiesClicked() {
        HierarchyMember goal = new HierarchyMember(goalTextField.getText());
        List<PriorityMatrix> matrices = service.createPriorityMatrices(goal, criteriaListView.getItems(), alternativesListView.getItems());
        PriorityEvaluationController priorityEvaluationController = priorityEvaluationView.getController();
        priorityEvaluationController.fillPriorityTable(matrices.get(0), matrices.get(1), matrices.subList(2, matrices.size()));
        getStageDangerously().setScene(new Scene(priorityEvaluationView.getGraphics()));
    }

    @Autowired
    public void setPriorityEvaluationView(View<GridPane, PriorityEvaluationController> priorityEvaluationView) {
        this.priorityEvaluationView = priorityEvaluationView;
    }

    @Autowired
    public void setService(HierarchyAnalysisService service) {
        this.service = service;
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

    @FXML
    private void onDeleteButtonClicked() {
        List<HierarchyMember> criteria = criteriaListView.getItems();
        List<HierarchyMember> alternatives = alternativesListView.getItems();
        criteriaListView.getSelectionModel().getSelectedIndices().forEach(i -> criteria.remove(i.intValue()));
        alternativesListView.getSelectionModel().getSelectedIndices().forEach(i -> alternatives.remove(i.intValue()));
    }

    @FXML
    public void onImportClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Hierarchy");
        fileChooser.setInitialDirectory(new File("saved/"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HAM", "*.ham"));
        File chosen = fileChooser.showOpenDialog(getStageDangerously());
        if (chosen != null) {
            service.deserializePriorityMatrices(chosen).ifPresent(model -> {
                goalTextField.setText(model.getGoalMatrix().getGoal().getName());
                criteriaListView.getItems().addAll(
                        model.getGoalMatrix().getPriorities().stream()
                                .map(Priority::getHierarchyMember)
                                .collect(Collectors.toList())
                );
                alternativesListView.getItems().addAll(
                        model.getFirstAlternativeMatrix().getPriorities().stream()
                                .map(Priority::getHierarchyMember)
                                .collect(Collectors.toList())
                );
            });
        }
    }

    @FXML
    private void initialize() {
        prepareCriteriaAndAlternativesListViews();
        prepareNavigateToPrioritiesButton();
        prepareAddButton();
        prepareDeleteButton();
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
