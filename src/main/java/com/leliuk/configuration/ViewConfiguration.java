package com.leliuk.configuration;

import com.leliuk.controller.HierarchyConstructionController;
import com.leliuk.controller.PriorityEvaluationController;
import com.leliuk.model.view.View;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@AllArgsConstructor
@Configuration
public class ViewConfiguration {
    private static final String HIERARCHY_CONSTRUCTION_VIEW_PATH = "/hierarchy_construction.fxml";
    private static final String PRIORITY_EVALUATION_VIEW_PATH = "/priority_evaluation.fxml";

    private final ApplicationContext context;

    @Bean
    public View<GridPane, HierarchyConstructionController> hierarchyConstructionView() throws IOException {
        return new View<>(initFxmlLoader(HIERARCHY_CONSTRUCTION_VIEW_PATH));
    }

    @Bean
    public View<GridPane, PriorityEvaluationController> priorityEvaluationView() throws IOException {
        return new View<>(initFxmlLoader(PRIORITY_EVALUATION_VIEW_PATH));
    }

    private FXMLLoader initFxmlLoader(String resourcePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        loader.setControllerFactory(context::getBean);
        return loader;
    }
}
