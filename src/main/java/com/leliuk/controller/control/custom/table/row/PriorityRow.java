package com.leliuk.controller.control.custom.table.row;

import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.hierarchy.Priority;
import com.leliuk.utils.MathUtils;
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
                return MathUtils.sum(
                        priorities.stream()
                                .map(priority -> 1 / priority.get())
                                .collect(Collectors.toList())
                );
            }
        });

        localPriority = new SimpleDoubleProperty();
        localPriority.bind(new DoubleBinding() {
            {
                bind(priorities.toArray(new Observable[0]));
            }

            @Override
            protected double computeValue() {
                return MathUtils.localPriority(
                        priorities.stream()
                                .map(ObservableDoubleValue::get)
                                .collect(Collectors.toList())
                );
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
                return MathUtils.normalizedLocalPriority(localPriority.get(),
                        Arrays.stream(localPrioritiesArr)
                                .map(DoubleProperty::doubleValue)
                                .collect(Collectors.toList())
                );
            }
        });

        lambda.bind(new DoubleBinding() {
            {
                bind(localPriorityNormalized, prioritySum);
            }

            @Override
            protected double computeValue() {
                return MathUtils.lambda(prioritySum.get(), localPriorityNormalized.get());
            }
        });
    }
}
