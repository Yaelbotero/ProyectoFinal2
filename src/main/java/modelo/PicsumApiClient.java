/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


//MODELO — PicsumApiClient

public class PicsumApiClient {

    private static final String BASE_URL = "https://picsum.photos";
    private static final int DEFAULT_WIDTH  = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int TIMEOUT_MS = 8_000;

    public Image fetchRandomImage() throws Exception {
        return fetchRandomImage(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }


    // Obtiene una imagen aleatoria de la API de Picsum con las dimensiones indicadas.

    public Image fetchRandomImage(int width, int height) throws Exception {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException(
                "Las dimensiones deben ser mayores que 0. Recibido: " + width + "x" + height
            );
        }

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
                "La API respondió con código HTTP " + responseCode
                + ". Verifica tu conexión a internet."
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
