/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javafx.scene.image.Image;

 //MODELO — ImageModel

public class ImageModel extends BaseFilter {

    public Image applyColorBlindFilter(String typeName) {
        return applyColorBlindFilter(ColorBlindType.valueOf(typeName.toUpperCase()));
    }

    @Override
    protected double[][] getSimulationMatrix(ColorBlindType type) {
        switch (type) {
            case PROTANOPIA:   return PROTANOPIA_RGB;
            case DEUTERANOPIA: return DEUTERANOPIA_RGB;
            case TRITANOPIA:   return TRITANOPIA_RGB;
            default: throw new IllegalArgumentException("Tipo no reconocido: " + type);
        }
    }
}