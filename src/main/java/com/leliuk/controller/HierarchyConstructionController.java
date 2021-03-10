package com.leliuk.controller;

import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.hierarchy.HierarchyModel;
import com.leliuk.model.other.Priority;
import com.leliuk.model.view.AbstractStageAware;
import com.leliuk.model.view.View;
import com.leliuk.service.FileKeepingService;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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

    private FileKeepingService fileKeepingService;

    private View<GridPane, PriorityEvaluationController> priorityEvaluationView;
    private HierarchyModel model;

    @Autowired
    public void setPriorityEvaluationView(View<GridPane, PriorityEvaluationController> priorityEvaluationView) {
        this.priorityEvaluationView = priorityEvaluationView;
    }

    @Autowired
    public void setFileKeepingService(FileKeepingService fileKeepingService) {
        this.fileKeepingService = fileKeepingService;
    }

    public void onNavigateToPrioritiesClicked() {
        model = getActualModel();
        PriorityEvaluationController priorityEvaluationController = priorityEvaluationView.getController();
        priorityEvaluationController.setHierarchyModel(model);
        getStageDangerously().setScene(new Scene(priorityEvaluationView.getGraphics()));
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


    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        stage.setOnCloseRequest(event -> {
            String goalName = goalTextField.getText();
            HierarchyMember goal = StringUtils.hasText(goalName) ? new HierarchyMember(goalName) : null;
            if (model != null) {
                model.update(goal, criteriaListView.getItems(), alternativesListView.getItems());
            } else {
                model = new HierarchyModel(goal, criteriaListView.getItems(), alternativesListView.getItems());
            }
            fileKeepingService.saveHierarchyModel(model);
        });
    }

    @FXML
    private void onImportClicked() {
        // todo check when imported and added a new value
        getFileFromUser(true).ifPresent(fileForImport ->
                fileKeepingService.retrieveHierarchyModel(fileForImport).ifPresent(model -> {
                    this.model = model;
                    clearAllInfo();
                    model.setSerializationFile(fileForImport);
                    goalTextField.setText(model.getGoal().map(HierarchyMember::getName).orElse(""));
                    model.getGoalMatrix().ifPresent(goalMatrix -> criteriaListView.getItems().addAll(
                            goalMatrix.getPriorities().stream()
                                    .map(Priority::getHierarchyMember)
                                    .filter(hierarchyMember -> !criteriaListView.getItems().contains(hierarchyMember))
                                    .collect(Collectors.toList())
                    ));
                    model.getAlternativeMatrices().stream().findFirst().ifPresent(alternativeMatrix -> alternativesListView.getItems().addAll(
                            alternativeMatrix.getPriorities().stream()
                                    .map(Priority::getHierarchyMember)
                                    .filter(hierarchyMember -> !alternativesListView.getItems().contains(hierarchyMember))
                                    .collect(Collectors.toList())
                    ));
                    // todo remove when update will be implemented
                    if (!navigateToPrioritiesButton.isDisabled()) {
                        onNavigateToPrioritiesClicked();
                    }
                })
        );
    }

    @FXML
    private void onExportClicked() {
        getFileFromUser(false).ifPresent(fileForExport -> {
            model = getActualModel();
            model.setSerializationFile(fileForExport);
            if (!fileKeepingService.saveHierarchyModel(model)) {
                new Alert(Alert.AlertType.ERROR, "Unfortunately, this file cannot be exported!").showAndWait();
            }
        });

    }

    private HierarchyModel getActualModel() {
        String goalName = goalTextField.getText();
        HierarchyMember goal = StringUtils.hasText(goalName) ? new HierarchyMember(goalTextField.getText()) : null;
        if (Objects.nonNull(model)) {
            // todo uncomment when update is implemented
            model.setGoal(goal);
            // model.update(goal, criteriaListView.getItems(), alternativesListView.getItems());
            return model;
        } else {
            return new HierarchyModel(goal, criteriaListView.getItems(), alternativesListView.getItems());
        }
    }

    private Optional<File> getFileFromUser(boolean forImport) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(forImport ? "Import Hierarchy" : "Export Hierarchy");
        fileChooser.setInitialDirectory(new File("saved/"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HAM", "*.ham"));
        if (forImport) {
            return Optional.ofNullable(fileChooser.showOpenDialog(getStageDangerously()));
        }
        return Optional.ofNullable(fileChooser.showSaveDialog(getStageDangerously()));
    }

    private void clearAllInfo() {
        goalTextField.clear();
        criteriaListView.getItems().clear();
        alternativesListView.getItems().clear();
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
