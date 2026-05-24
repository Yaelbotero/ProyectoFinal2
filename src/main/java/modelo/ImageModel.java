/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javafx.scene.image.Image;


 //MODELO — ImageModel


public class ImageModel extends BaseFilter {

    // Matrices de simulación de daltonismo en espacio LMS

    // Matriz LMS para simular Protanopía (ausencia de cono L — rojo).
    private static final double[][] PROTANOPIA_LMS = {
        { 0.00000000, 2.02344354, -2.52580820 },
        { 0.00000000, 1.00000000,  0.00000000 },
        { 0.00000000, 0.00000000,  1.00000000 }
    };

    // Matriz LMS para simular Deuteranopía (ausencia de cono M — verde).
    private static final double[][] DEUTERANOPIA_LMS = {
        { 1.00000000, 0.00000000,  0.00000000 },
        { 0.49420696, 0.00000000,  1.24827352 },
        { 0.00000000, 0.00000000,  1.00000000 }
    };

    // Matriz LMS para simular Tritanopía (ausencia de cono S — azul). 
    private static final double[][] TRITANOPIA_LMS = {
        { 1.00000000, 0.00000000,  0.00000000 },
        { 0.00000000, 1.00000000,  0.00000000 },
        {-0.86744736, 1.86727022,  0.00000000 }
    };

    // Métodos públicos

    public Image applyColorBlindFilter(String typeName) {
        return applyColorBlindFilter(ColorBlindType.valueOf(typeName.toUpperCase()));
    }

    // Implementación del método abstracto

    @Override
    protected double[][] getSimulationMatrix(ColorBlindType type) {
        switch (type) {
            case PROTANOPIA:   return PROTANOPIA_LMS;
            case DEUTERANOPIA: return DEUTERANOPIA_LMS;
            case TRITANOPIA:   return TRITANOPIA_LMS;
            default: throw new IllegalArgumentException("Tipo no reconocido: " + type);
        }
    }
}