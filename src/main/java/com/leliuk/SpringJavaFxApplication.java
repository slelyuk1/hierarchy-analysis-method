package com.leliuk;

import com.leliuk.controller.HierarchyConstructionController;
import com.leliuk.model.view.StageAware;
import com.leliuk.model.view.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringJavaFxApplication extends Application {

    private ConfigurableApplicationContext context;
    private View<VBox, HierarchyConstructionController> mainView;

    @Override
    public void init() {
        context = SpringApplication.run(getClass(), getParameters().getRaw().toArray(new String[0]));
        context.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage primaryStage) {
        populateStageAwareWithStage(primaryStage);
        primaryStage.setScene(new Scene(mainView.getGraphics()));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }

    public static void main(String[] args) {
        Application.launch(SpringJavaFxApplication.class, args);
    }

    @Autowired
    public void setMainView(View<VBox, HierarchyConstructionController> mainView) {
        this.mainView = mainView;
    }

    private void populateStageAwareWithStage(Stage stage) {
        context.getBeanFactory().getBeansOfType(StageAware.class).values().forEach(stageAware -> stageAware.setStage(stage));
    }
}
