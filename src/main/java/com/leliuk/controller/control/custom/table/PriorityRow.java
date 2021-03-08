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
public class PriorityRow {
    private final ObjectProperty<HierarchyMember> hierarchyMember;
    private final List<DoubleProperty> priorities;
    private final DoubleProperty prioritySum;
    private final DoubleProperty localPriority;
    private final DoubleProperty localPriorityNormalized;
    private final DoubleProperty lambda;

    public PriorityRow(Priority priority) {
        hierarchyMember = new SimpleObjectProperty<>(priority.getHierarchyMember());
        priorities = priority.getValues().stream()
                .map(priorityElem -> new SimpleDoubleProperty(priorityElem.doubleValue()))
                .collect(Collectors.toList());
        prioritySum = new SimpleDoubleProperty();

        prioritySum.bind(new DoubleBinding() {
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

        localPriority = new SimpleDoubleProperty();
        localPriority.bind(new DoubleBinding() {
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
        localPriorityNormalized = new SimpleDoubleProperty();
        lambda = new SimpleDoubleProperty();
    }

    public void bindLocalPriorities(List<DoubleProperty> localPriorities) {
        // todo do decisioning depending on localPriorities
        DoubleProperty[] localPrioritiesArr = localPriorities.toArray(new DoubleProperty[0]);
        localPriorityNormalized.bind(new DoubleBinding() {
            {
                bind(localPrioritiesArr);
            }

            @Override
            protected double computeValue() {
                double currentLocalPriority = localPriority.doubleValue();
                double localPrioritiesSum = Arrays.stream(localPrioritiesArr)
                        .map(DoubleProperty::doubleValue)
                        .reduce(0.0, Double::sum);
                return currentLocalPriority / localPrioritiesSum;
            }
        });

        lambda.bind(new DoubleBinding() {
            {
                bind(localPriorityNormalized, prioritySum);
            }

            @Override
            protected double computeValue() {
                return prioritySum.getValue() * localPriorityNormalized.getValue();
            }
        });
    }
}
