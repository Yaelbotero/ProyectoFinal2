/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

//Este es el main principal que ejecuta el programa
public class Launcher {
    public static void main(String[] args) {

        System.setProperty("prism.order", "sw");
        System.setProperty("prism.verbose", "true");
        
        MainApp.main(args);
    }
}