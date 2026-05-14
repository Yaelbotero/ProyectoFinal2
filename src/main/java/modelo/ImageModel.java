/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author cript
 */


import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class ImageModel {

    private Image originalImage;
    private Image processedImage;
    private ColorBlindType currentType;

    // Matrices de conversión RGB ↔ LMS adaptado al iluminante D65
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

    // Matrices de simulación de daltonismo en espacio LMS
    private static final double[][] PROTANOPIA_LMS = {
        { 0.00000000, 2.02344354, -2.52580820 },
        { 0.00000000, 1.00000000,  0.00000000 },
        { 0.00000000, 0.00000000,  1.00000000 }
    };

    private static final double[][] DEUTERANOPIA_LMS = {
        { 1.00000000, 0.00000000,  0.00000000 },
        { 0.49420696, 0.00000000,  1.24827352 },
        { 0.00000000, 0.00000000,  1.00000000 }
    };

    private static final double[][] TRITANOPIA_LMS = {
        { 1.00000000, 0.00000000,  0.00000000 },
        { 0.00000000, 1.00000000,  0.00000000 },
        {-0.86744736, 1.86727022,  0.00000000 }
    };

    public Image getOriginalImage() { return originalImage; }
    public Image getProcessedImage() { return processedImage; }
    public ColorBlindType getCurrentType() { return currentType; }

    public void setOriginalImage(Image image) {
        this.originalImage = image;
        this.processedImage = null;
    }

    public boolean hasImage() {
        return originalImage != null;
    }

    public Image applyColorBlindFilter(ColorBlindType type) {
        if (originalImage == null) return null;

        this.currentType = type;
        double[][] simulationMatrix = getSimulationMatrix(type);

        int width  = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        WritableImage result   = new WritableImage(width, height);
        PixelReader   reader   = originalImage.getPixelReader();
        PixelWriter   writer   = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color original     = reader.getColor(x, y);
                Color transformed  = transformColor(original, simulationMatrix);
                writer.setColor(x, y, transformed);
            }
        }

        this.processedImage = result;
        return result;
    }

    private Color transformColor(Color color, double[][] simMatrix) {
        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();

        r = linearize(r);
        g = linearize(g);
        b = linearize(b);

        double[] lms = multiplyMatrix(RGB_TO_LMS, new double[]{ r, g, b });
        double[] lmsSimulated = multiplyMatrix(simMatrix, lms);
        double[] rgbLinear = multiplyMatrix(LMS_TO_RGB, lmsSimulated);

        double rOut = gammaCorrect(rgbLinear[0]);
        double gOut = gammaCorrect(rgbLinear[1]);
        double bOut = gammaCorrect(rgbLinear[2]);

        return new Color(
            clamp(rOut),
            clamp(gOut),
            clamp(bOut),
            color.getOpacity()
        );
    }

    private double[] multiplyMatrix(double[][] matrix, double[] vector) {
        double[] result = new double[3];
        for (int i = 0; i < 3; i++) {
            result[i] = matrix[i][0] * vector[0]
                      + matrix[i][1] * vector[1]
                      + matrix[i][2] * vector[2];
        }
        return result;
    }

    private double linearize(double value) {
        if (value <= 0.04045) {
            return value / 12.92;
        } else {
            return Math.pow((value + 0.055) / 1.055, 2.4);
        }
    }

    private double gammaCorrect(double value) {
        value = clamp(value);
        if (value <= 0.0031308) {
            return value * 12.92;
        } else {
            return 1.055 * Math.pow(value, 1.0 / 2.4) - 0.055;
        }
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private double[][] getSimulationMatrix(ColorBlindType type) {
        switch (type) {
            case PROTANOPIA:   return PROTANOPIA_LMS;
            case DEUTERANOPIA: return DEUTERANOPIA_LMS;
            case TRITANOPIA:   return TRITANOPIA_LMS;
            default: throw new IllegalArgumentException("Tipo no reconocido: " + type);
        }
    }
}
