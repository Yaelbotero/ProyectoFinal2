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

    // Matrices de conversión RGB ↔ LMS (iluminante D65)

    private static final double[][] RGB_TO_LMS = {
        { 0.31399022, 0.63951294, 0.04649755 },
        { 0.15537241, 0.75789446, 0.08670142 },
        { 0.01775239, 0.10944209, 0.87256922 }
    };

    private static final double[][] LMS_TO_RGB = {
        {  5.47221206, -4.64196010,  0.16963708 },
        { -1.12524190,  2.29317094, -0.16789520 },
        {  0.02980165, -0.19318073,  1.16364789 }
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
        double[][] simulationMatrix = getSimulationMatrix(type);

        int width  = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelReader   reader = originalImage.getPixelReader();
        PixelWriter   writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color original    = reader.getColor(x, y);
                Color transformed = transformColor(original, simulationMatrix);
                writer.setColor(x, y, transformed);
            }
        }

        this.processedImage = result;
        return result;
    }

    // Método abstracto — contrato para subclases
    protected abstract double[][] getSimulationMatrix(ColorBlindType type);


    // Métodos de transformación de color (reutilizables por subclases)
    
    protected Color transformColor(Color color, double[][] simMatrix) {
        double r = linearize(color.getRed());
        double g = linearize(color.getGreen());
        double b = linearize(color.getBlue());

        double[] lms          = multiplyMatrix(RGB_TO_LMS, new double[]{ r, g, b });
        double[] lmsSimulated = multiplyMatrix(simMatrix, lms);
        double[] rgbLinear    = multiplyMatrix(LMS_TO_RGB, lmsSimulated);

        return new Color(
            clamp(gammaCorrect(rgbLinear[0])),
            clamp(gammaCorrect(rgbLinear[1])),
            clamp(gammaCorrect(rgbLinear[2])),
            color.getOpacity()
        );
    }


     //Multiplica una matriz 3×3 por un vector de 3 elementos.

    protected double[] multiplyMatrix(double[][] matrix, double[] vector) {
        double[] result = new double[3];
        for (int i = 0; i < 3; i++) {
            result[i] = matrix[i][0] * vector[0]
                      + matrix[i][1] * vector[1]
                      + matrix[i][2] * vector[2];
        }
        return result;
    }

    protected double linearize(double value) {
        return (value <= 0.04045)
            ? value / 12.92
            : Math.pow((value + 0.055) / 1.055, 2.4);
    }

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
