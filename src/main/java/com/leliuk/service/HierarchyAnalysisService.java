package com.leliuk.service;

import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.hierarchy.HierarchyModel;
import com.leliuk.model.hierarchy.PriorityMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HierarchyAnalysisService {
    public HierarchyModel createHierarchyModel(HierarchyMember goal, List<HierarchyMember> criteria, List<HierarchyMember> alternatives) {
        List<PriorityMatrix> priorityMatrices = criteria.stream()
                .map(c -> new PriorityMatrix(c, alternatives))
                .collect(Collectors.toList());
        return new HierarchyModel(goal.getName(),
                new PriorityMatrix(goal, criteria),
                priorityMatrices.get(0),
                new ArrayList<>(priorityMatrices.subList(1, priorityMatrices.size()))
        );
    }

    public void mergeHierarchyModel(HierarchyModel model,
                                    HierarchyMember goal, List<HierarchyMember> criteria, List<HierarchyMember> alternatives) {
        model.getGoalMatrix().update(goal, criteria);
        model.getFirstAlternativeMatrix().update(criteria.get(0), alternatives);
        List<PriorityMatrix> otherAlternativeMatrices = model.getOtherAlternativeMatrices();
        for (int i = 1; i < otherAlternativeMatrices.size(); ++i) {
            otherAlternativeMatrices.get(i).update(criteria.get(i), alternatives);
        }
    }

    public void serializePriorityMatrices(HierarchyModel priorityMatrices) {
        try (FileOutputStream out = new FileOutputStream(priorityMatrices.getFilePath());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
            objectOutputStream.writeObject(priorityMatrices);
        } catch (IOException e) {
            log.error("An error occurred during serialization of priority matrices:", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<HierarchyModel> deserializePriorityMatrices(File file) {
        try (FileInputStream out = new FileInputStream(file);
             ObjectInputStream objectOutputStream = new ObjectInputStream(out)) {
            return Optional.of((HierarchyModel) objectOutputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            log.error("An error occurred during serialization of priority matrices:", e);
        }
        return Optional.empty();
    }
}
