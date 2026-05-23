/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javafx.scene.image.Image;

public interface ImageFilter {
    void setOriginalImage(Image image);
    Image applyColorBlindFilter(ColorBlindType type);
    boolean hasImage();
    Image getProcessedImage();
    Image getOriginalImage();
    ColorBlindType getCurrentType();
}