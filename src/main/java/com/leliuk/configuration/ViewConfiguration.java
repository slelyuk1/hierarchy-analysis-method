package com.leliuk.configuration;

import com.leliuk.controller.MainController;
import com.leliuk.model.View;
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
    private static final String MAIN_VIEW_PATH = "/main.fxml";

    private final ApplicationContext context;

    @Bean
    public View<GridPane, MainController> mainWindowView() throws IOException {
        return new View<>(initFxmlLoader(MAIN_VIEW_PATH));
    }

    private FXMLLoader initFxmlLoader(String resourcePath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        loader.setControllerFactory(context::getBean);
        return loader;
    }
}
