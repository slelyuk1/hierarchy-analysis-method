package com.leliuk.controller.control.custom.table;

import com.leliuk.configuration.RandomConsistencyValuesConfiguration;
import com.leliuk.controller.control.custom.table.row.PriorityRow;
import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.other.Priority;
import com.leliuk.model.other.PriorityMatrix;
import com.leliuk.utils.MathUtils;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class PriorityTableView extends TableView<PriorityRow> {
    private final DoubleProperty consistencyIndex;
    private final DoubleProperty consistencyValue;
    private RandomConsistencyValuesConfiguration randomConsistencyValuesConfiguration;

    public PriorityTableView() {
        consistencyIndex = new SimpleDoubleProperty();
        consistencyValue = new SimpleDoubleProperty();
    }

    public void setRandomConsistencyValues(RandomConsistencyValuesConfiguration randomConsistencyValuesConfiguration) {
        this.randomConsistencyValuesConfiguration = randomConsistencyValuesConfiguration;
    }

    public void setPriorityMatrix(PriorityMatrix matrix) {
        getItems().clear();
        List<PriorityRow> rows = matrix.getPriorities().stream()
                .map(PriorityRow::new)
                .collect(Collectors.toList());
        List<DoubleProperty> localPriorities = rows.stream()
                .map(PriorityRow::getLocalPriority)
                .collect(Collectors.toList());
        rows.forEach(row -> {
            row.bindLocalPriorities(localPriorities);
            getItems().add(row);
        });
        bindConsistencyIndex();
        bindConsistencyValue();

        buildColumns(matrix);
    }

    protected TableColumn<PriorityRow, String> createAlternativeNameColumn(String goalName) {
        TableColumn<PriorityRow, String> alternativeNameColumn = new TableColumn<>(goalName);
        alternativeNameColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getHierarchyMember().get().getName()));
        alternativeNameColumn.setEditable(false);
        alternativeNameColumn.setSortable(false);
        return alternativeNameColumn;
    }

    protected TableColumn<PriorityRow, Number> createPriorityColumn(String name, final int columnIndex) {
        TableColumn<PriorityRow, Number> column = new TableColumn<>(name);
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
            } else {
                selected.set(toSet);
                selectedReversed.set(toSet == 0 ? 0 : 1 / toSet);
            }
        });
        column.setCellFactory(TextFieldTableCell.forTableColumn(new NonExceptionalNumberStringConverter()));
        column.setSortable(false);
        return column;
    }

    protected TableColumn<PriorityRow, Number> createLocalPriorityColumn() {
        TableColumn<PriorityRow, Number> column = new TableColumn<>("Local Priority");
        column.setCellValueFactory(param -> param.getValue().getLocalPriority());
        column.setSortable(false);
        column.setEditable(false);
        return column;
    }

    protected TableColumn<PriorityRow, Number> createLocalPriorityNormalizedColumn() {
        TableColumn<PriorityRow, Number> column = new TableColumn<>("Local Priority Normalized");
        column.setCellValueFactory(param -> param.getValue().getLocalPriorityNormalized());
        column.setSortable(false);
        column.setEditable(false);
        return column;
    }

    protected TableColumn<PriorityRow, Number> createLambdaColumn() {
        TableColumn<PriorityRow, Number> column = new TableColumn<>("λ");
        column.setCellValueFactory(param -> param.getValue().getLambda());
        column.setSortable(false);
        column.setEditable(false);
        return column;
    }

    private void bindConsistencyIndex() {
        DoubleProperty[] lamdas = getItems().stream()
                .map(PriorityRow::getLambda)
                .toArray(DoubleProperty[]::new);
        consistencyIndex.bind(new DoubleBinding() {
            {
                bind(lamdas);
            }

            @Override
            protected double computeValue() {
                return MathUtils.consistencyIndex(
                        Arrays.stream(lamdas)
                                .map(DoubleProperty::get)
                                .collect(Collectors.toList())
                );
            }
        });
    }

    private void bindConsistencyValue() {

        consistencyValue.bind(new DoubleBinding() {
            {
                bind(consistencyIndex);
            }

            @Override
            protected double computeValue() {
                int index = getItems().size() - 1;
                double randomConsistencyValue = randomConsistencyValuesConfiguration.getRandomConsistencyValues().get(index);
                if (randomConsistencyValue == 0.0) {
                    return 0.0;
                }
                return consistencyIndex.get() / randomConsistencyValue;
            }
        });
    }

    private void buildColumns(PriorityMatrix matrix) {
        getColumns().clear();
        HierarchyMember goal = matrix.getGoal()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Matrix %s doesn't have goal and this is illegal!", matrix)));
        getColumns().add(createAlternativeNameColumn(goal.getName()));
        List<Priority> alternatives = matrix.getPriorities();
        for (int i = 0; i < alternatives.size(); ++i) {
            Priority priority = alternatives.get(i);
            getColumns().add(createPriorityColumn(priority.getHierarchyMember().getName(), i));
        }
        getColumns().add(createLocalPriorityColumn());
        getColumns().add(createLocalPriorityNormalizedColumn());
        getColumns().add(createLambdaColumn());
    }
}
