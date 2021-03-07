package com.leliuk.model.hierarchy;

import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class HierarchyAnalysisModel {
    private static HierarchyAnalysisModel INSTANCE;

    private Collection<PriorityMatrix> priorityMatrices;

    public static HierarchyAnalysisModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HierarchyAnalysisModel();
        }
        return INSTANCE;
    }

    public void createPriorityMatrices(HierarchyMember goal, Collection<HierarchyMember> criteria, Collection<HierarchyMember> alternatives) {
        Stream<PriorityMatrix> priorityMatricesStream = criteria.stream()
                .map(c -> new PriorityMatrix(c, alternatives));
        priorityMatrices = Collections.unmodifiableCollection(
                Stream.concat(Stream.of(new PriorityMatrix(goal, criteria)), priorityMatricesStream).collect(Collectors.toList())
        );
    }

    private HierarchyAnalysisModel() {
    }
}
