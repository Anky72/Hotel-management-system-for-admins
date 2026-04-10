package com.hotel;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginScreen.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}