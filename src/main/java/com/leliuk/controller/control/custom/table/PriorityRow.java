package com.leliuk.controller.control.custom.table;

import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.hierarchy.Priority;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PriorityRow<T extends Number> {
    private final ObjectProperty<HierarchyMember> hierarchyMember;
    private final List<DoubleProperty> priorities;
    private final DoubleProperty sum;
    private final DoubleProperty consistencyMagnitude;

    public PriorityRow(Priority<T> priority) {
        hierarchyMember = new SimpleObjectProperty<>(priority.getHierarchyMember());
        priorities = Arrays.stream(priority.getPriorities())
                .map(priorityElem -> new SimpleDoubleProperty(priorityElem.doubleValue()))
                .collect(Collectors.toList());
        sum = new SimpleDoubleProperty();

        sum.bind(new DoubleBinding() {
            {
                bind(priorities.toArray(new Observable[0]));
            }

            @Override
            protected double computeValue() {
                return priorities.stream()
                        .map(priority -> 1 / priority.get())
                        .reduce(0.0, Double::sum);
            }
        });

        consistencyMagnitude = new SimpleDoubleProperty();
        consistencyMagnitude.bind(new DoubleBinding() {
            {
                bind(priorities.toArray(new Observable[0]));
            }

            @Override
            protected double computeValue() {
                double multiplied = priorities.stream()
                        .map(ObservableDoubleValue::get)
                        .reduce(1.0, (l, r) -> l * r);
                return Math.pow(multiplied, 1.0 / priorities.size());
            }
        });
    }
}
