/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author cript
 */


public class Launcher {
    public static void main(String[] args) {
        // Estas líneas opcionales para la renderización por software (sw) 
        // puedes mantenerlas si tienes problemas de tarjeta gráfica, 
        // pero la ruta del SDK ya no es necesaria.
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.verbose", "true");
        
        MainApp.main(args);
    }
}