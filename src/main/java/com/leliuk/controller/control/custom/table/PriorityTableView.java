package com.leliuk.controller.control.custom.table;

import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.hierarchy.Priority;
import com.leliuk.model.hierarchy.PriorityMatrix;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PriorityTableView extends TableView<PriorityTableView.Row<Double>> {

    public void setPriorityMatrix(PriorityMatrix matrix) {
        getItems().clear();
        getColumns().clear();
        getColumns().add(createAlternativeNameColumn());
        List<Priority<Double>> alternatives = matrix.getPriorities();
        for (int i = 0; i < alternatives.size(); ++i) {
            Priority<Double> priority = alternatives.get(i);
            getColumns().add(createPriorityColumn(priority.getHierarchyMember().getName(), i, alternatives.size()));
        }
        matrix.getPriorities().stream()
                .map(Row::new)
                .forEach(getItems()::add);
    }

    protected TableColumn<Row<Double>, String> createAlternativeNameColumn() {
        TableColumn<Row<Double>, String> alternativeNameColumn = new TableColumn<>();
        alternativeNameColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getHierarchyMember().get().getName()));
        alternativeNameColumn.setEditable(false);
        alternativeNameColumn.setSortable(false);
        return alternativeNameColumn;
    }

    protected TableColumn<Row<Double>, Number> createPriorityColumn(String name, final int columnIndex, final int rowsQuantity) {
        TableColumn<Row<Double>, Number> column = new TableColumn<>(name);
        column.setCellFactory(new Callback<TableColumn<Row<Double>, Number>, TableCell<Row<Double>, Number>>() {
            private int counter = 0;

            @Override
            public TableCell<Row<Double>, Number> call(TableColumn<Row<Double>, Number> param) {
                TableCell<Row<Double>, Number> tableCell =
                        TextFieldTableCell.<Row<Double>, Number>forTableColumn(new CustomNumberStringConverter()).call(param);
                if (counter > columnIndex) {
                    tableCell.setEditable(false);
                    // todo make normally
                    if (counter <= rowsQuantity) {
                        tableCell.setStyle("-fx-background-color: gainsboro");
                    }
                }
                ++counter;
                return tableCell;
            }
        });
        column.setCellValueFactory(param -> param.getValue().getPriorities().get(columnIndex));
        column.setOnEditCommit(event -> {
            double toSet = Objects.nonNull(event.getNewValue()) ? event.getNewValue().doubleValue() : event.getOldValue().doubleValue();
            TablePosition<?, ?> position = event.getTablePosition();
            getItems().get(position.getRow()).getPriorities().get(position.getColumn() - 1).set(toSet);
            getItems().get(position.getColumn() - 1).getPriorities().get(position.getRow()).set(toSet);
        });
        column.setSortable(false);
        return column;
    }

    @Getter
    public static class Row<T extends Number> {
        private final ObjectProperty<HierarchyMember> hierarchyMember;
        private final List<DoubleProperty> priorities;

        public Row(Priority<T> priority) {
            hierarchyMember = new SimpleObjectProperty<>(priority.getHierarchyMember());
            priorities = Arrays.stream(priority.getPriorities())
                    .map(priorityElem -> new SimpleDoubleProperty(priorityElem.doubleValue()))
                    .collect(Collectors.toList());
        }
    }
}
