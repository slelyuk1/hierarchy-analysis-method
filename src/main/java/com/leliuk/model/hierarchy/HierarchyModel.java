package com.leliuk.model.hierarchy;

import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
public class HierarchyModel implements Serializable {
    PriorityMatrix goalMatrix;
    PriorityMatrix firstAlternativeMatrix;
    List<PriorityMatrix> otherAlternativeMatrices;
}
