package com.leliuk.model.hierarchy;

import lombok.Getter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PriorityMatrix implements Serializable {
    private final HierarchyMember goal;
    private final List<Priority<Double>> priorities;

    public PriorityMatrix(HierarchyMember goal, Collection<HierarchyMember> alternatives) {
        this.goal = HierarchyMember.copyOf(goal);
        this.priorities = Collections.unmodifiableList(
                alternatives.stream()
                        .map(alternative -> new Priority<>(alternative, new Double[alternatives.size()]))
                        .collect(Collectors.toList())
        );
        for (int i = 0; i < alternatives.size(); ++i) {
            for (int j = 0; j < alternatives.size(); ++j) {
                priorities.get(i).getPriorities()[j] = 0.0;
            }
            priorities.get(i).getPriorities()[i] = 1.0;
        }
    }

    public void setPriority(int i, int j, double priority) {
        priorities.get(i).getPriorities()[j] = priority;
        priorities.get(j).getPriorities()[i] = priority;
    }
}
