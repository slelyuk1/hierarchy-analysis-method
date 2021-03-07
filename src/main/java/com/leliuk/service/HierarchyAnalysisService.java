package com.leliuk.service;

import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.hierarchy.HierarchyModel;
import com.leliuk.model.hierarchy.PriorityMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class HierarchyAnalysisService {
    public List<PriorityMatrix> createPriorityMatrices(HierarchyMember goal, List<HierarchyMember> criteria, List<HierarchyMember> alternatives) {
        Stream<PriorityMatrix> priorityMatricesStream = criteria.stream()
                .map(c -> new PriorityMatrix(c, alternatives));
        return Collections.unmodifiableList(
                Stream.concat(Stream.of(new PriorityMatrix(goal, criteria)), priorityMatricesStream).collect(Collectors.toList())
        );
    }

    public void serializePriorityMatrices(File file, HierarchyModel priorityMatrices) {
        try (FileOutputStream out = new FileOutputStream(file);
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
