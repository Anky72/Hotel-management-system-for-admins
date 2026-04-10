package com.hotel;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class LoginScreen {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    public static void show(Stage stage) {
        // ── Left decorative panel ──────────────────────────────────────────
        VBox leftPanel = new VBox(16);
        leftPanel.setPrefWidth(340);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(60, 40, 60, 40));
        leftPanel.setStyle("-fx-background-color: #1a1a2e;");

        Label hotelIcon = new Label("🏨");
        hotelIcon.setStyle("-fx-font-size: 64px;");

        Label hotelName = new Label("GRAND\nHORIZON");
        hotelName.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #e0c97f;"
                + "-fx-text-alignment: center; -fx-font-family: 'Georgia';");
        hotelName.setTextAlignment(TextAlignment.CENTER);

        Label tagline = new Label("Where Every Stay is a Story");
        tagline.setStyle("-fx-font-size: 13px; -fx-text-fill: #a0a0c0; -fx-font-style: italic;");
        tagline.setTextAlignment(TextAlignment.CENTER);

        Separator sep = new Separator();
        sep.setMaxWidth(120);

        Label info = new Label("Hotel Management System\nv1.0  •  Admin Portal");
        info.setStyle("-fx-font-size: 11px; -fx-text-fill: #606080; -fx-text-alignment: center;");
        info.setTextAlignment(TextAlignment.CENTER);

        leftPanel.getChildren().addAll(hotelIcon, hotelName, tagline, sep, info);

        // ── Right login panel ──────────────────────────────────────────────
        VBox rightPanel = new VBox(20);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(60, 50, 60, 50));
        rightPanel.setStyle("-fx-background-color: #f5f5f0;");
        rightPanel.setPrefWidth(380);

        Label welcome = new Label("Welcome Back");
        welcome.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;"
                + "-fx-font-family: 'Georgia';");

        Label sub = new Label("Sign in to manage your hotel");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

        VBox userBox = new VBox(6);
        Label userLbl = new Label("USERNAME");
        userLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555;");
        TextField userField = new TextField();
        userField.setPromptText("Enter username");
        userField.setStyle(fieldStyle());
        userField.setPrefHeight(42);
        userBox.getChildren().addAll(userLbl, userField);

        VBox passBox = new VBox(6);
        Label passLbl = new Label("PASSWORD");
        passLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555;");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        passField.setStyle(fieldStyle());
        passField.setPrefHeight(42);
        passBox.getChildren().addAll(passLbl, passField);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        Label hint = new Label("Default: admin / admin123");
        hint.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px; -fx-font-style: italic;");

        Button loginBtn = new Button("SIGN IN");
        loginBtn.setPrefWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(46);
        loginBtn.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #e0c97f;"
                + "-fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;"
                + "-fx-background-radius: 6;");
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
                "-fx-background-color: #e0c97f; -fx-text-fill: #1a1a2e;"
                + "-fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;"
                + "-fx-background-radius: 6;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
                "-fx-background-color: #1a1a2e; -fx-text-fill: #e0c97f;"
                + "-fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;"
                + "-fx-background-radius: 6;"));

        Runnable doLogin = () -> {
         String u = userField.getText().trim();
         String p = passField.getText().trim();
         if (u.equals(ADMIN_USER) && p.equals(ADMIN_PASS)) {
         HotelService service = new HotelService();
         DashboardScreen.show(stage, service);
    }    else {
         errorLabel.setText("❌  Invalid username or password.");
         passField.clear();
    }
};
        

        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());

        rightPanel.getChildren().addAll(welcome, sub, userBox, passBox, errorLabel, loginBtn, hint);

        HBox root = new HBox(leftPanel, rightPanel);
        root.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.3)));

        Scene scene = new Scene(root, 720, 480);
        stage.setTitle("Grand Horizon – Hotel Management");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private static String fieldStyle() {
        return "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 6;"
                + "-fx-background-radius: 6; -fx-padding: 8 12; -fx-font-size: 13px;";
    }
}