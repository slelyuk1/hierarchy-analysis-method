package com.leliuk.model.hierarchy;

import lombok.Getter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PriorityMatrix implements Serializable {
    private HierarchyMember goal;
    private final List<Priority> priorities;

    public PriorityMatrix(HierarchyMember goal, Collection<HierarchyMember> alternatives) {
        this.goal = goal;
        this.priorities = alternatives.stream()
                .map(alternative -> new Priority(alternative, alternatives.size()))
                .collect(Collectors.toList());
        for (int i = 0; i < alternatives.size(); ++i) {
            for (int j = 0; j < alternatives.size(); ++j) {
                priorities.get(i).getValues().set(j, 0.0);
            }
            priorities.get(i).getValues().set(i, 1.0);
        }
    }

    public void setGoal(HierarchyMember goal) {
        this.goal = goal;
    }

    public void update(HierarchyMember goal, List<HierarchyMember> alternatives) {
        setGoal(goal);
        // todo
    }
}
