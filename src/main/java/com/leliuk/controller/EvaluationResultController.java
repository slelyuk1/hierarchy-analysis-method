package com.leliuk.controller;

import com.leliuk.controller.control.custom.table.row.ResultRow;
import com.leliuk.model.hierarchy.HierarchyMember;
import com.leliuk.model.hierarchy.HierarchyModel;
import com.leliuk.model.hierarchy.Priority;
import com.leliuk.model.hierarchy.PriorityMatrix;
import com.leliuk.utils.MathUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class EvaluationResultController {
    @FXML
    private TableView<ResultRow> resultsTableView;

    public void evaluateResults(HierarchyModel model) {
        NormalizedLocalPriority criteriaNormalizedLocalPriorities = new NormalizedLocalPriority(model.getGoalMatrix());
        List<NormalizedLocalPriority> alternativesLocalPriorities = Stream.concat(Stream.of(model.getFirstAlternativeMatrix()), model.getOtherAlternativeMatrices().stream())
                .map(NormalizedLocalPriority::new)
                .collect(Collectors.toList());
        Map<HierarchyMember, Double> globalPriorities = new HashMap<>();
        // todo check formula
        alternativesLocalPriorities.forEach(alternative ->
                alternative.getNormalizedLocalPriorities().forEach((key, value) ->
                        globalPriorities.merge(key,
                                value * criteriaNormalizedLocalPriorities.getNormalizedLocalPriorities().get(alternative.getGoal()),
                                (l, r) -> l + value * criteriaNormalizedLocalPriorities.getNormalizedLocalPriorities().get(alternative.getGoal()))
                ));
        globalPriorities.forEach((key, value) -> resultsTableView.getItems().add(new ResultRow(key.getName(), value)));
    }

    @Getter
    private static class NormalizedLocalPriority {
        private final HierarchyMember goal;
        private final Map<HierarchyMember, Double> normalizedLocalPriorities;

        public NormalizedLocalPriority(PriorityMatrix matrix) {
            goal = matrix.getGoal();
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
}
