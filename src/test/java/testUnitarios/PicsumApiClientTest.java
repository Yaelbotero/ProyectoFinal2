/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testUnitarios;

import modelo.PicsumApiClient;
import javafx.scene.image.Image;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integración — PicsumApiClient")
public class PicsumApiClientTest {

    private PicsumApiClient client;

    @BeforeEach
    void setUp() {
        client = new PicsumApiClient();
    }

    // VALIDACIÓN DE PARÁMETROS (sin red)
    
    @Test @Order(1)
    @DisplayName("1. fetchRandomImage con ancho 0 lanza IllegalArgumentException")
    void testAnchoZeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> client.fetchRandomImage(0, 600),
            "Ancho 0 debe lanzar IllegalArgumentException");
    }

    @Test @Order(2)
    @DisplayName("2. fetchRandomImage con alto 0 lanza IllegalArgumentException")
    void testAltoZeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> client.fetchRandomImage(800, 0),
            "Alto 0 debe lanzar IllegalArgumentException");
    }

    @Test @Order(3)
    @DisplayName("3. fetchRandomImage con dimensiones negativas lanza IllegalArgumentException")
    void testDimensionesNegativasLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> client.fetchRandomImage(-100, -100),
            "Dimensiones negativas deben lanzar IllegalArgumentException");
    }

    @Test @Order(4)
    @DisplayName("4. fetchRandomImage con ancho negativo y alto válido lanza IllegalArgumentException")
    void testAnchoNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> client.fetchRandomImage(-1, 600),
            "Ancho negativo debe lanzar IllegalArgumentException");
    }

    @Test @Order(5)
    @DisplayName("5. Instanciar PicsumApiClient no lanza excepción")
    void testInstanciarClienteNoLanzaExcepcion() {
        assertDoesNotThrow(() -> new PicsumApiClient());
    }

    // INTEGRACIÓN CON API REAL (requiere conexión a internet)
    
    @Test @Order(6)
    @Tag("integration")
    @DisplayName("6. fetchRandomImage() retorna imagen no nula con dimensiones por defecto")
    void testFetchRetornaImagenNoNula() throws Exception {
        Image image = client.fetchRandomImage();

        assertNotNull(image, "La imagen recibida no debe ser null");
        assertFalse(image.isError(), "La imagen no debe tener error de decodificación");
    }

    @Test @Order(7)
    @Tag("integration")
    @DisplayName("7. fetchRandomImage() retorna imagen con ancho mayor que 0")
    void testFetchImagenTieneAncho() throws Exception {
        Image image = client.fetchRandomImage();

        assertTrue(image.getWidth() > 0,
            "La imagen debe tener ancho mayor que 0, fue: " + image.getWidth());
    }

    @Test @Order(8)
    @Tag("integration")
    @DisplayName("8. fetchRandomImage() retorna imagen con alto mayor que 0")
    void testFetchImagenTieneAlto() throws Exception {
        Image image = client.fetchRandomImage();

        assertTrue(image.getHeight() > 0,
            "La imagen debe tener alto mayor que 0, fue: " + image.getHeight());
    }

    @Test @Order(9)
    @Tag("integration")
    @DisplayName("9. fetchRandomImage(ancho, alto) respeta las dimensiones solicitadas")
    void testFetchConDimensionesPersonalizadas() throws Exception {
        Image image = client.fetchRandomImage(400, 300);

        assertNotNull(image);
        assertFalse(image.isError());
        assertTrue(image.getWidth() > 0);
        assertTrue(image.getHeight() > 0);
    }

    @Test @Order(10)
    @Tag("integration")
    @DisplayName("10. Dos llamadas consecutivas retornan imágenes válidas sin errores")
    void testDosLlamadasConsecutivasExitosas() throws Exception {
        Image imagen1 = client.fetchRandomImage(200, 200);
        Image imagen2 = client.fetchRandomImage(200, 200);

        assertNotNull(imagen1, "Primera imagen no debe ser null");
        assertNotNull(imagen2, "Segunda imagen no debe ser null");
        assertFalse(imagen1.isError(), "Primera imagen no debe tener error");
        assertFalse(imagen2.isError(), "Segunda imagen no debe tener error");
    }
}