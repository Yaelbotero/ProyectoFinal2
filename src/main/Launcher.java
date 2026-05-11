/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

public class Launcher {
    public static void main(String[] args) {
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.verbose", "true");
           //linea opcional pero implementada debido a que se presentaba un error
        System.setProperty("java.library.path", "C:/javafx-sdk-25.0.3/bin");
        
        MainApp.main(args);
    }
}
