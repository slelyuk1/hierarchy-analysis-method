package com.leliuk.model.hierarchy;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class HierarchyModel implements Serializable {
    private String filePath;
    private PriorityMatrix goalMatrix;
    private PriorityMatrix firstAlternativeMatrix;
    private List<PriorityMatrix> otherAlternativeMatrices;
}
