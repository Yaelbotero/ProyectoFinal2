/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

 //MODELO ABSTRACTO — BaseFilter

public abstract class BaseFilter implements ImageFilter {

    protected Image originalImage;

    protected Image processedImage;

    protected ColorBlindType currentType;

    static final double[][] PROTANOPIA_RGB = {
        { 0.152286,  1.052583, -0.204868 },
        { 0.114503,  0.786281,  0.099216 },
        {-0.003882, -0.048116,  1.051998 }
    };

    static final double[][] DEUTERANOPIA_RGB = {
        { 0.367322,  0.860646, -0.227968 },
        { 0.280085,  0.672501,  0.047413 },
        {-0.011820,  0.042940,  0.968881 }
    };

    static final double[][] TRITANOPIA_RGB = {
        { 1.255528, -0.076749, -0.178779 },
        {-0.078411,  0.930809,  0.147602 },
        { 0.004733,  0.691367,  0.303900 }
    };

    // Implementaciones de ImageFilter (comunes a todas las subclases)

    @Override
    public void setOriginalImage(Image image) {
        this.originalImage  = image;
        this.processedImage = null;
    }

    @Override
    public boolean hasImage() {
        return originalImage != null;
    }

    @Override
    public Image getOriginalImage() { return originalImage; }

    @Override
    public Image getProcessedImage() { return processedImage; }

    @Override
    public ColorBlindType getCurrentType() { return currentType; }

    @Override
    public Image applyColorBlindFilter(ColorBlindType type) {
        if (originalImage == null) return null;

        this.currentType = type;
        double[][] simMatrix = getSimulationMatrix(type);

        int width  = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelReader   reader = originalImage.getPixelReader();
        PixelWriter   writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color original    = reader.getColor(x, y);
                Color transformed = transformColor(original, simMatrix);
                writer.setColor(x, y, transformed);
            }
        }

        this.processedImage = result;
        return result;
    }

    // Método abstracto — contrato para subclases

     //Devuelve la matriz de simulación RGB correspondiente al tipo de daltonismo.

    protected abstract double[][] getSimulationMatrix(ColorBlindType type);

    // Métodos de transformación (reutilizables por subclases)
    
     //Transforma un píxel aplicando la matriz de simulación sobre RGB lineal.

    protected Color transformColor(Color color, double[][] simMatrix) {
        double r = linearize(color.getRed());
        double g = linearize(color.getGreen());
        double b = linearize(color.getBlue());

        double[] rgb = multiplyMatrix(simMatrix, new double[]{ r, g, b });

        return new Color(
            clamp(gammaCorrect(rgb[0])),
            clamp(gammaCorrect(rgb[1])),
            clamp(gammaCorrect(rgb[2])),
            color.getOpacity()
        );
    }

    protected double[] multiplyMatrix(double[][] matrix, double[] vector) {
        double[] result = new double[3];
        for (int i = 0; i < 3; i++) {
            result[i] = matrix[i][0] * vector[0]
                      + matrix[i][1] * vector[1]
                      + matrix[i][2] * vector[2];
        }
        return result;
    }
    
     //Convierte un valor sRGB comprimido a valor lineal (elimina gamma).

    protected double linearize(double value) {
        return (value <= 0.04045)
            ? value / 12.92
            : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    // Aplica corrección gamma para convertir un valor lineal a sRGB.
    
    protected double gammaCorrect(double value) {
        value = clamp(value);
        return (value <= 0.0031308)
            ? value * 12.92
            : 1.055 * Math.pow(value, 1.0 / 2.4) - 0.055;
    }

    protected double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
