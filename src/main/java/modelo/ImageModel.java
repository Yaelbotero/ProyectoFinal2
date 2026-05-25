package modelo;

import javafx.scene.image.Image;

/**
 * Implementacion concreta del modelo de procesamiento de imagenes.
 * Se encarga de elegir la tabla de conversion correcta segun el tipo
 * de daltonismo que se quiera simular.
 *
 * @author ColorBlind Helper Team
 * @version 1.0
 */
public class ImageModel extends BaseFilter {

    /**
     * Aplica el filtro usando el nombre del tipo como texto en lugar del enum.
     * Util para pruebas o cuando el tipo llega como cadena de texto.
     *
     * @param typeName nombre del tipo en cualquier formato (ej: "protanopia").
     * @return imagen con el filtro aplicado, o null si no hay imagen cargada.
     */
    public Image applyColorBlindFilter(String typeName) {
        return applyColorBlindFilter(ColorBlindType.valueOf(typeName.toUpperCase()));
    }

    /**
     * Devuelve la tabla de conversion de colores segun el tipo de daltonismo.
     *
     * @param type tipo de daltonismo seleccionado.
     * @return tabla de conversion 3x3 correspondiente.
     * @throws IllegalArgumentException si el tipo no existe.
     */
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
