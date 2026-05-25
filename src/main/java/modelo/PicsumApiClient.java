package modelo;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Descarga imagenes aleatorias desde internet usando la API de Picsum Photos.
 * Cada descarga abre su propia conexion y la cierra al terminar.
 *
 * @author ColorBlind Helper Team
 * @version 1.0
 */
public class PicsumApiClient {

    /** Direccion web de la API de imagenes aleatorias. */
    private static final String BASE_URL = "https://picsum.photos";

    /** Ancho por defecto de la imagen descargada: 800 pixeles. */
    private static final int DEFAULT_WIDTH  = 800;

    /** Alto por defecto de la imagen descargada: 600 pixeles. */
    private static final int DEFAULT_HEIGHT = 600;

    /** Tiempo maximo de espera para la descarga en milisegundos. */
    private static final int TIMEOUT_MS = 80;

    /**
     * Descarga una imagen aleatoria con el tamano por defecto (800x600).
     *
     * @return imagen descargada lista para mostrar.
     * @throws Exception si hay error de conexion o la imagen no se puede cargar.
     */
    public Image fetchRandomImage() throws Exception {
        return fetchRandomImage(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Descarga una imagen aleatoria con el tamano indicado.
     *
     * @param width  ancho deseado en pixeles.
     * @param height alto deseado en pixeles.
     * @return imagen descargada lista para mostrar.
     * @throws Exception si hay error de conexion, el servidor no responde
     *                   correctamente, o la imagen no se puede decodificar.
     */
    public Image fetchRandomImage(int width, int height) throws Exception {
        String endpoint = BASE_URL + "/" + width + "/" + height;
        URL url = new URL(endpoint);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT_MS);
        connection.setReadTimeout(TIMEOUT_MS);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("User-Agent", "ColorBlindHelper/1.0");

        int responseCode = connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            connection.disconnect();
            throw new Exception(
                "La API respondio con codigo HTTP " + responseCode
                + ". Verifica tu conexion a internet."
            );
        }

        try (InputStream inputStream = connection.getInputStream()) {
            Image image = new Image(inputStream);
            if (image.isError()) {
                throw new Exception("No se pudo decodificar la imagen recibida desde la API.");
            }
            return image;
        } finally {
            connection.disconnect();
        }
    }
}
