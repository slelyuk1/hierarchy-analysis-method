package com.leliuk.model.hierarchy;

import com.leliuk.model.other.PriorityMatrix;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class HierarchyModel implements Serializable {
    private File serializationFile;
    private HierarchyMember goal;
    private PriorityMatrix goalMatrix;
    private List<PriorityMatrix> alternativeMatrices;

    public HierarchyModel(@Nullable HierarchyMember goal, List<HierarchyMember> criteria, List<HierarchyMember> alternatives) {
        this.goal = goal;
        this.goalMatrix = new PriorityMatrix(goal, criteria);
        this.alternativeMatrices = criteria.stream()
                .map(c -> new PriorityMatrix(c, alternatives))
                .collect(Collectors.toList());
    }

    public void update(@Nullable HierarchyMember goal, List<HierarchyMember> criteria, List<HierarchyMember> alternatives) {
        // todo real update
        this.goal = goal;
        goalMatrix = new PriorityMatrix(goal, criteria);
        alternativeMatrices = criteria.stream()
                .map(c -> new PriorityMatrix(c, alternatives))
                .collect(Collectors.toList());
    }

    public Optional<HierarchyMember> getGoal() {
        return Optional.ofNullable(goal);
    }

    public void setGoal(HierarchyMember goal) {
        this.goal = goal;
        if (Objects.nonNull(goalMatrix)) {
            goalMatrix.setGoal(goal);
        }
    }

    public Optional<File> getSerializationFile() {
        return Optional.ofNullable(serializationFile);
    }

    public Optional<PriorityMatrix> getGoalMatrix() {
        return Optional.ofNullable(goalMatrix);
    }

    public List<PriorityMatrix> getAlternativeMatrices() {
        return Collections.unmodifiableList(alternativeMatrices);
    }
}
