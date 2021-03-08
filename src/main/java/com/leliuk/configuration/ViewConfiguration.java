package com.leliuk.configuration;

import com.leliuk.controller.EvaluationResultController;
import com.leliuk.controller.HierarchyConstructionController;
import com.leliuk.controller.PriorityEvaluationController;
import com.leliuk.model.view.View;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@AllArgsConstructor
@Configuration
public class ViewConfiguration {
    private static final String HIERARCHY_CONSTRUCTION_VIEW_PATH = "/fxml/hierarchy_construction.fxml";
    private static final String PRIORITY_EVALUATION_VIEW_PATH = "/fxml/priority_evaluation.fxml";
    private static final String EVALUATION_RESULT_VIEW_PATH = "/fxml/evaluation_result.fxml";

    private final ApplicationContext context;

    @Bean
    public View<VBox, HierarchyConstructionController> hierarchyConstructionView() throws IOException {
        return new View<>(initFxmlLoader(HIERARCHY_CONSTRUCTION_VIEW_PATH));
    }

    @Bean
    public View<GridPane, PriorityEvaluationController> priorityEvaluationView() throws IOException {
        return new View<>(initFxmlLoader(PRIORITY_EVALUATION_VIEW_PATH));
    }

    @Bean
    public View<GridPane, EvaluationResultController> evaluationResultView() throws IOException {
        return new View<>(initFxmlLoader(EVALUATION_RESULT_VIEW_PATH));
    }

    private FXMLLoader initFxmlLoader(String resourcePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        loader.setControllerFactory(context::getBean);
        return loader;
    }
}
