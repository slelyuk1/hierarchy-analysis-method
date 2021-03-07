package com.leliuk.controller.control.custom.table;

import com.leliuk.model.hierarchy.Priority;
import com.leliuk.model.hierarchy.PriorityMatrix;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;
import java.util.Objects;

public class PriorityTableView extends TableView<PriorityRow<Double>> {

    private PriorityMatrix matrix;

    public void setPriorityMatrix(PriorityMatrix matrix) {
        getItems().clear();
        matrix.getPriorities().stream()
                .map(PriorityRow::new)
                .forEach(getItems()::add);
        this.matrix = matrix;
        refresh();
    }

    protected TableColumn<PriorityRow<Double>, String> createAlternativeNameColumn(String goalName) {
        TableColumn<PriorityRow<Double>, String> alternativeNameColumn = new TableColumn<>(goalName);
        alternativeNameColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getHierarchyMember().get().getName()));
        alternativeNameColumn.setEditable(false);
        alternativeNameColumn.setSortable(false);
        return alternativeNameColumn;
    }

    protected TableColumn<PriorityRow<Double>, Number> createPriorityColumn(String name, final int columnIndex) {
        TableColumn<PriorityRow<Double>, Number> column = new TableColumn<>(name);
        column.setCellValueFactory(param -> param.getValue().getPriorities().get(columnIndex));
        column.setOnEditCommit(event -> {
            double toSet = Math.round(
                    Objects.nonNull(event.getNewValue()) ? event.getNewValue().doubleValue() : event.getOldValue().doubleValue()
            );
            if (toSet < 0) {
                toSet = 0;
            }
            if (toSet > 10) {
                toSet = 10;
            }
            TablePosition<?, ?> position = event.getTablePosition();
            int rowIndex = position.getRow();

            DoubleProperty selected = getItems().get(rowIndex).getPriorities().get(columnIndex);
            DoubleProperty selectedReversed = getItems().get(columnIndex).getPriorities().get(rowIndex);
            // this should be done if no changes were done
            selected.set(Double.MIN_VALUE);
            selectedReversed.set(Double.MIN_VALUE);
            if (columnIndex == rowIndex) {
                selected.set(1);
            }  else {
                selected.set(toSet);
                selectedReversed.set(toSet == 0 ? 0 : 1 / toSet);
            }
        });
        column.setCellFactory(TextFieldTableCell.forTableColumn(new NonExceptionalNumberStringConverter()));
        column.setSortable(false);
        return column;
    }

    @Override
    public void refresh() {
        super.refresh();
        buildColumns();
    }

    private void buildColumns() {
        getColumns().clear();
        getColumns().add(createAlternativeNameColumn(matrix.getGoal().getName()));
        List<Priority<Double>> alternatives = matrix.getPriorities();
        for (int i = 0; i < alternatives.size(); ++i) {
            Priority<Double> priority = alternatives.get(i);
            getColumns().add(createPriorityColumn(priority.getHierarchyMember().getName(), i));
        }
    }
}
