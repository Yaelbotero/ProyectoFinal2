/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testUnitarios;

/**
 *
 * @author RYZEN
 */
import controlador.MainController;
import modelo.ColorBlindType;
import modelo.ImageModel;
import vista.MainView;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;


 //Test Unitario — MainController
    //Test final de la logíca entera del programa

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Unitario — MainController")
public class MainControllerTest {

    private ImageModel model;
    private MainView   view;
    private Stage      stage;

    @Start
    void start(Stage stage) {
        this.stage = stage;
        this.model = new ImageModel();
        this.view  = new MainView(stage);
        new MainController(model, view, stage);
        stage.setScene(view.getScene());
        stage.show();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // INICIALIZACIÓN
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(1)
    @DisplayName("1. El controlador se instancia sin excepciones")
    void testInstanciasinExcepcion() {
        assertDoesNotThrow(() -> new MainController(new ImageModel(), view, stage));
    }

    @Test @Order(2)
    @DisplayName("2. La vista tiene escena no nula tras inicialización")
    void testVistaTieneEscena() {
        assertNotNull(view.getScene());
    }

    @Test @Order(3)
    @DisplayName("3. El modelo comienza sin imagen")
    void testModeloSinImagenInicial() {
        assertFalse(model.hasImage());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LÓGICA DE FILTRO — VALORES DE ENTRADA/SALIDA
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(4)
    @DisplayName("4. Al aplicar filtro con imagen cargada, el modelo actualiza processedImage")
    void testFiltroActualizaProcessedImage() throws InterruptedException {
        runFx(() -> {
            model.setOriginalImage(imagenGradiente(30, 30));
            view.displayOriginalImage(model.getOriginalImage());
        });

        runFx(() -> {
            Image result = model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);
            view.displayProcessedImage(result);
        });

        assertNotNull(model.getProcessedImage());
        assertEquals(ColorBlindType.PROTANOPIA, model.getCurrentType());
    }

    @Test @Order(5)
    @DisplayName("5. Al aplicar Deuteranopía, getCurrentType() refleja el tipo correcto")
    void testCurrentTypeDespuesDeuteranopia() throws InterruptedException {
        runFx(() -> model.setOriginalImage(imagenGradiente(20, 20)));
        runFx(() -> model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA));

        assertEquals(ColorBlindType.DEUTERANOPIA, model.getCurrentType());
    }

    @Test @Order(6)
    @DisplayName("6. Al aplicar Tritanopía, getCurrentType() refleja el tipo correcto")
    void testCurrentTypeDespuesTritanopia() throws InterruptedException {
        runFx(() -> model.setOriginalImage(imagenGradiente(20, 20)));
        runFx(() -> model.applyColorBlindFilter(ColorBlindType.TRITANOPIA));

        assertEquals(ColorBlindType.TRITANOPIA, model.getCurrentType());
    }

    @Test @Order(7)
    @DisplayName("7. Cambiar de filtro actualiza getCurrentType() al último aplicado")
    void testCambiarFiltroActualizaTipo() throws InterruptedException {
        runFx(() -> model.setOriginalImage(imagenGradiente(20, 20)));
        runFx(() -> model.applyColorBlindFilter(ColorBlindType.PROTANOPIA));
        assertEquals(ColorBlindType.PROTANOPIA, model.getCurrentType());

        runFx(() -> model.applyColorBlindFilter(ColorBlindType.TRITANOPIA));
        assertEquals(ColorBlindType.TRITANOPIA, model.getCurrentType());
    }

    @Test @Order(8)
    @DisplayName("8. La imagen procesada tiene las mismas dimensiones que la original")
    void testProcessedImageMismasDimensiones() throws InterruptedException {
        runFx(() -> model.setOriginalImage(imagenGradiente(45, 35)));
        runFx(() -> model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA));

        assertNotNull(model.getProcessedImage());
        assertEquals(45, (int) model.getProcessedImage().getWidth());
        assertEquals(35, (int) model.getProcessedImage().getHeight());
    }

    @Test @Order(9)
    @DisplayName("9. Aplicar filtros consecutivos con distintas imágenes no corrompe el estado")
    void testFiltrosConsecutivosDistintasImagenes() throws InterruptedException {
        for (int i = 1; i <= 3; i++) {
            final int size = i * 10;
            runFx(() -> model.setOriginalImage(imagenGradiente(size, size)));
            runFx(() -> model.applyColorBlindFilter(ColorBlindType.PROTANOPIA));
        }

        assertNotNull(model.getProcessedImage());
        assertEquals(30, (int) model.getProcessedImage().getWidth());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESTADO DE LA VISTA — VALORES DE ENTRADA/SALIDA
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(10)
    @DisplayName("10. showStatus no lanza excepción con mensaje normal")
    void testShowStatusMensajeNormal() {
        assertDoesNotThrow(() -> runFx(() ->
            view.showStatus("Imagen cargada correctamente.")));
    }

    @Test @Order(11)
    @DisplayName("11. showStatus no lanza excepción con cadena vacía")
    void testShowStatusCadenaVacia() {
        assertDoesNotThrow(() -> runFx(() -> view.showStatus("")));
    }

    @Test @Order(12)
    @DisplayName("12. setFilterButtonsEnabled(true) no lanza excepción")
    void testHabilitarBotonesFiltro() {
        assertDoesNotThrow(() -> runFx(() -> {
            view.setFilterButtonsEnabled(true);
        }));
    }

    @Test @Order(13)
    @DisplayName("13. setFilterButtonsEnabled(false) no lanza excepción")
    void testDeshabilitarBotonesFiltro() {
        assertDoesNotThrow(() -> runFx(() -> {
            view.setFilterButtonsEnabled(false);
        }));
    }

    @Test @Order(14)
    @DisplayName("14. setResetEnabled alterna sin excepción")
    void testAlternarResetEnabled() {
        assertDoesNotThrow(() -> runFx(() -> {
            view.setResetEnabled(true);
            view.setResetEnabled(false);
        }));
    }

    @Test @Order(15)
    @DisplayName("15. clearProcessedImage no lanza excepción aunque no haya imagen procesada")
    void testClearProcessedImageSinImagen() {
        assertDoesNotThrow(() -> runFx(() -> view.clearProcessedImage()));
    }

    @Test @Order(16)
    @DisplayName("16. displayOriginalImage no lanza excepción con imagen válida")
    void testDisplayOriginalImageValida() {
        assertDoesNotThrow(() -> runFx(() ->
            view.displayOriginalImage(imagenGradiente(20, 20))));
    }

    @Test @Order(17)
    @DisplayName("17. displayProcessedImage no lanza excepción con imagen válida")
    void testDisplayProcessedImageValida() {
        assertDoesNotThrow(() -> runFx(() ->
            view.displayProcessedImage(imagenGradiente(20, 20))));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MANEJO DE ERRORES Y CASOS BORDE
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(18)
    @DisplayName("18. applyColorBlindFilter sin imagen retorna null (modelo no explota)")
    void testFiltroSinImagenNoExplota() throws InterruptedException {
        ImageModel modelVacio = new ImageModel();
        AtomicBoolean lanzaExcepcion = new AtomicBoolean(false);

        runFx(() -> {
            try {
                modelVacio.applyColorBlindFilter(ColorBlindType.PROTANOPIA);
            } catch (Exception e) {
                lanzaExcepcion.set(true);
            }
        });

        assertFalse(lanzaExcepcion.get(),
            "applyColorBlindFilter no debe lanzar excepción si no hay imagen");
        assertNull(modelVacio.getProcessedImage());
    }

    @Test @Order(19)
    @DisplayName("19. setProcessing(true) y setProcessing(false) no lanzan excepción")
    void testSetProcessingAlterna() {
        assertDoesNotThrow(() -> runFx(() -> {
            view.setProcessing(true);
            view.setProcessing(false);
        }));
    }

    @Test @Order(20)
    @DisplayName("20. showStatus con mensaje muy largo no lanza excepción")
    void testShowStatusMensajeLargo() {
        String largo = "A".repeat(10_000);
        assertDoesNotThrow(() -> runFx(() -> view.showStatus(largo)));
    }

    @Test @Order(21)
    @DisplayName("21. Múltiples llamadas a clearProcessedImage son seguras")
    void testClearProcessedImageMultiplesVeces() {
        assertDoesNotThrow(() -> runFx(() -> {
            for (int i = 0; i < 10; i++) view.clearProcessedImage();
        }));
    }

    @Test @Order(22)
    @DisplayName("22. Flujo completo cargar → filtrar → reset no deja estado inconsistente")
    void testFlujoCompletoEstadoConsistente() throws InterruptedException {
        // Cargar
        runFx(() -> {
            model.setOriginalImage(imagenGradiente(40, 40));
            view.displayOriginalImage(model.getOriginalImage());
            view.setFilterButtonsEnabled(true);
        });

        // Filtrar
        runFx(() -> {
            Image result = model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);
            view.displayProcessedImage(result);
            view.setResetEnabled(true);
        });

        assertTrue(model.hasImage());
        assertNotNull(model.getProcessedImage());
        assertEquals(ColorBlindType.PROTANOPIA, model.getCurrentType());

        // Reset
        runFx(() -> {
            view.clearProcessedImage();
            view.setResetEnabled(false);
        });

        // El modelo conserva las imágenes; solo la vista se limpia
        assertTrue(model.hasImage());
        assertNotNull(model.getProcessedImage(), "El modelo mantiene la imagen procesada tras reset de vista");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UTILIDADES
    // ═══════════════════════════════════════════════════════════════════════

    private Image imagenGradiente(int w, int h) {
        WritableImage img = new WritableImage(w, h);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                img.getPixelWriter().setColor(x, y,
                    new Color((double) x / w, (double) y / h, 1.0 - (double) x / w, 1.0));
        return img;
    }

    private void runFx(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try { action.run(); }
            finally { latch.countDown(); }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout en FX thread");
    }
}
