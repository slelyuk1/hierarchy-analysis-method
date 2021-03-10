package com.leliuk.model.other;

import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.utils.MathUtils;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class NormalizedLocalPriority {
    private final HierarchyMember goal;
    private final Map<HierarchyMember, Double> normalizedLocalPriorities;

    public NormalizedLocalPriority(PriorityMatrix matrix) {
        goal = matrix.getGoal()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Matrix %s doesn't have goal and this is illegal!", matrix)));
        normalizedLocalPriorities = matrix.getPriorities().stream()
                .collect(Collectors.toMap(
                        Priority::getHierarchyMember,
                        priority -> MathUtils.localPriority(priority.getValues())
                ));
        double localPrioritiesSum = MathUtils.sum(normalizedLocalPriorities.values());
        normalizedLocalPriorities.forEach((
                key, value) -> normalizedLocalPriorities.put(key, MathUtils.normalizedLocalPriority(value, localPrioritiesSum))
        );
    }
}
