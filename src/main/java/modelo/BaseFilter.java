package modelo;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Clase base que contiene la logica comun para simular daltonismo.
 * Procesa la imagen pixel por pixel aplicando una transformacion matematica
 * que imita como ve los colores una persona con cada tipo de daltonismo.
 *
 * No se puede usar directamente. La subclase ImageModel es la que se instancia.
 *
 * @author ColorBlind Helper Team
 * @version 1.0
 */
public abstract class BaseFilter implements ImageFilter {

    /** Imagen original cargada por el usuario. */
    protected Image originalImage;

    /** Imagen resultado despues de aplicar el filtro. */
    protected Image processedImage;

    /** Tipo de daltonismo del ultimo filtro aplicado. */
    protected ColorBlindType currentType;

    /** Tabla de conversion de colores para simular protanopia (sin rojo). */
    static final double[][] PROTANOPIA_RGB = {
        { 0.152286,  1.052583, -0.204868 },
        { 0.114503,  0.786281,  0.099216 },
        {-0.003882, -0.048116,  1.051998 }
    };

    /** Tabla de conversion de colores para simular deuteranopia (sin verde). */
    static final double[][] DEUTERANOPIA_RGB = {
        { 0.367322,  0.860646, -0.227968 },
        { 0.280085,  0.672501,  0.047413 },
        {-0.011820,  0.042940,  0.968881 }
    };

    /** Tabla de conversion de colores para simular tritanopia (sin azul). */
    static final double[][] TRITANOPIA_RGB = {
        { 1.255528, -0.076749, -0.178779 },
        {-0.078411,  0.930809,  0.147602 },
        { 0.004733,  0.691367,  0.303900 }
    };

    /**
     * Guarda la imagen original y borra el resultado anterior.
     *
     * @param image imagen a cargar.
     */
    @Override
    public void setOriginalImage(Image image) {
        this.originalImage  = image;
        this.processedImage = null;
    }

    /** Devuelve true si hay una imagen cargada. */
    @Override
    public boolean hasImage() { return originalImage != null; }

    /** Devuelve la imagen original cargada. */
    @Override
    public Image getOriginalImage() { return originalImage; }

    /** Devuelve la ultima imagen procesada con filtro. */
    @Override
    public Image getProcessedImage() { return processedImage; }

    /** Devuelve el tipo de daltonismo del ultimo filtro aplicado. */
    @Override
    public ColorBlindType getCurrentType() { return currentType; }

    /**
     * Recorre cada pixel de la imagen y le aplica la transformacion de color
     * correspondiente al tipo de daltonismo. Guarda y devuelve el resultado.
     *
     * @param type tipo de daltonismo a simular.
     * @return imagen con el filtro aplicado, o null si no hay imagen cargada.
     */
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

    /**
     * Devuelve la tabla de conversion de colores para el tipo de daltonismo dado.
     * Cada subclase decide que tabla usar segun el tipo.
     *
     * @param type tipo de daltonismo.
     * @return tabla de conversion 3x3.
     */
    protected abstract double[][] getSimulationMatrix(ColorBlindType type);

    /**
     * Transforma el color de un pixel para simular el tipo de daltonismo.
     * Mantiene la opacidad original del pixel.
     *
     * @param color color original del pixel.
     * @param simMatrix tabla de conversion del tipo de daltonismo.
     * @return nuevo color con la simulacion aplicada.
     */
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

    /**
     * Aplica la tabla de conversion al color del pixel.
     * Mezcla los canales rojo, verde y azul segun los pesos de la tabla.
     *
     * @param matrix tabla de conversion 3x3.
     * @param vector valores [R, G, B] del pixel.
     * @return nuevos valores [R, G, B] despues de la conversion.
     */
    protected double[] multiplyMatrix(double[][] matrix, double[] vector) {
        double[] result = new double[3];
        for (int i = 0; i < 3; i++) {
            result[i] = matrix[i][0] * vector[0]
                      + matrix[i][1] * vector[1]
                      + matrix[i][2] * vector[2];
        }
        return result;
    }

    /**
     * Convierte el valor de color a escala lineal para poder hacer
     * calculos matematicos correctos sobre el.
     *
     * @param value valor de color entre 0.0 y 1.0.
     * @return valor en escala lineal.
     */
    protected double linearize(double value) {
        return (value <= 0.04045)
            ? value / 12.92
            : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    /**
     * Convierte el valor de vuelta a escala normal para que la
     * pantalla lo muestre correctamente.
     *
     * @param value valor en escala lineal.
     * @return valor de color entre 0.0 y 1.0.
     */
    protected double gammaCorrect(double value) {
        value = clamp(value);
        return (value <= 0.0031308)
            ? value * 12.92
            : 1.055 * Math.pow(value, 1.0 / 2.4) - 0.055;
    }

    /**
     * Asegura que el valor de color este entre 0.0 y 1.0.
     * Evita colores invalidos por errores de calculo.
     *
     * @param value valor a limitar.
     * @return valor entre 0.0 y 1.0.
     */
    protected double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
