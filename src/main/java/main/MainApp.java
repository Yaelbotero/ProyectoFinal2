/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import vista.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada principal de la aplicación ColorBlind Helper.
 * Inicializa JavaFX y lanza la ventana principal.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainView mainView = new MainView(primaryStage);
        Scene scene = mainView.getScene();

        primaryStage.setTitle("ColorBlind Helper — Simulador de Daltonismo");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
