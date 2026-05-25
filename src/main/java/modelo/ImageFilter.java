package modelo;

import javafx.scene.image.Image;

/**
 * Define las operaciones que debe tener cualquier modelo de procesamiento
 * de imagenes. El controlador usa esta interfaz para no depender de una
 * implementacion especifica.
 *
 * @author ColorBlind Helper Team
 * @version 1.0
 */
public interface ImageFilter {

    /**
     * Carga la imagen original que se va a procesar.
     * Borra el resultado anterior si habia uno.
     *
     * @param image imagen a cargar.
     */
    void setOriginalImage(Image image);

    /**
     * Aplica el filtro del tipo de daltonismo indicado sobre la imagen cargada.
     *
     * @param type tipo de daltonismo a simular.
     * @return imagen con el filtro aplicado, o null si no hay imagen cargada.
     */
    Image applyColorBlindFilter(ColorBlindType type);

    /**
     * Indica si hay una imagen cargada lista para procesar.
     *
     * @return true si hay imagen, false si no.
     */
    boolean hasImage();

    /**
     * Devuelve la ultima imagen procesada con un filtro.
     *
     * @return imagen procesada, o null si aun no se aplico ningun filtro.
     */
    Image getProcessedImage();

    /**
     * Devuelve la imagen original cargada.
     *
     * @return imagen original, o null si no se cargo ninguna.
     */
    Image getOriginalImage();

    /**
     * Devuelve el tipo de daltonismo del ultimo filtro aplicado.
     *
     * @return ultimo tipo aplicado, o null si no se aplico ninguno.
     */
    ColorBlindType getCurrentType();
}
