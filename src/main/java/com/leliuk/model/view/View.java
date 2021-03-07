package com.leliuk.model.view;

import javafx.fxml.FXMLLoader;
import lombok.Value;

import java.io.IOException;

@Value
public class View<V, C> {

    V graphics;
    C controller;

    public View(FXMLLoader loader) throws IOException {
        graphics = loader.load();
        controller = loader.getController();
    }
}
