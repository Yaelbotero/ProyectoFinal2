/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;


/**
 * Clase de arranque que configura el entorno antes de iniciar la app.
 * Necesaria para que JavaFX funcione correctamente al ejecutar desde un JAR.
 *
 * @author ColorBlind Helper Team
 * @version 1.0
 */
public class Launcher {
    public static void main(String[] args) {

        System.setProperty("prism.order", "sw");
        System.setProperty("prism.verbose", "true");
        
        MainApp.main(args);
    }
}