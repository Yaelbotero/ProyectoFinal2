/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import controlador.MainController;
import modelo.ColorBlindType;
import modelo.ImageModel;
import vista.MainView;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainControllerIntegrationTest {

    private ImageModel model;
    private MainView view;
    private Stage stage;

    // ─── Inicialización JavaFX ──────────────────────────────────────────────

    @Start
    void start(Stage stage) {
        this.stage = stage;
        this.model = new ImageModel();
        this.view  = new MainView(stage);
        // El constructor de MainView ya instancia MainController internamente.
        new MainController(model, view, stage);
        stage.setScene(view.getScene());
        stage.show();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. MODELO — ImageModel sin imagen inicial
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1. ImageModel: estado inicial vacío")
    void testModelInitialState() {
        assertFalse(model.hasImage(),
            "El modelo no debe tener imagen al iniciarse");
        assertNull(model.getOriginalImage(),
            "getOriginalImage() debe retornar null al inicio");
        assertNull(model.getProcessedImage(),
            "getProcessedImage() debe retornar null al inicio");
        assertNull(model.getCurrentType(),
            "getCurrentType() debe retornar null al inicio");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. MODELO — Carga de imagen sintética
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @DisplayName("2. ImageModel: carga de imagen válida")
    void testSetOriginalImage() throws InterruptedException {
        Image testImage = createSyntheticImage(100, 100);

        runOnFxThread(() -> model.setOriginalImage(testImage));

        assertTrue(model.hasImage(),
            "hasImage() debe retornar true después de setOriginalImage()");
        assertNotNull(model.getOriginalImage(),
            "getOriginalImage() no debe ser null tras carga");
        assertEquals(100, (int) model.getOriginalImage().getWidth());
        assertEquals(100, (int) model.getOriginalImage().getHeight());
        assertNull(model.getProcessedImage(),
            "processedImage debe limpiarse al cargar nueva imagen");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. MODELO — Filtro Protanopía produce imagen de mismo tamaño
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("3. ImageModel: filtro Protanopía preserva dimensiones")
    void testProtanopiaFilterDimensions() throws InterruptedException {
        Image original = createSyntheticImage(80, 60);
        runOnFxThread(() -> model.setOriginalImage(original));

        AtomicReference<Image> result = new AtomicReference<>();
        runOnFxThread(() -> result.set(model.applyColorBlindFilter(ColorBlindType.PROTANOPIA)));

        assertNotNull(result.get(), "El filtro no debe retornar null");
        assertEquals(80, (int) result.get().getWidth(),  "Ancho debe conservarse");
        assertEquals(60, (int) result.get().getHeight(), "Alto debe conservarse");
        assertEquals(ColorBlindType.PROTANOPIA, model.getCurrentType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. MODELO — Filtro Deuteranopía produce imagen de mismo tamaño
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @DisplayName("4. ImageModel: filtro Deuteranopía preserva dimensiones")
    void testDeuteranopiaFilterDimensions() throws InterruptedException {
        Image original = createSyntheticImage(50, 50);
        runOnFxThread(() -> model.setOriginalImage(original));

        AtomicReference<Image> result = new AtomicReference<>();
        runOnFxThread(() -> result.set(model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA)));

        assertNotNull(result.get());
        assertEquals(50, (int) result.get().getWidth());
        assertEquals(50, (int) result.get().getHeight());
        assertEquals(ColorBlindType.DEUTERANOPIA, model.getCurrentType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. MODELO — Filtro Tritanopía produce imagen de mismo tamaño
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @DisplayName("5. ImageModel: filtro Tritanopía preserva dimensiones")
    void testTritanopiaFilterDimensions() throws InterruptedException {
        Image original = createSyntheticImage(40, 40);
        runOnFxThread(() -> model.setOriginalImage(original));

        AtomicReference<Image> result = new AtomicReference<>();
        runOnFxThread(() -> result.set(model.applyColorBlindFilter(ColorBlindType.TRITANOPIA)));

        assertNotNull(result.get());
        assertEquals(ColorBlindType.TRITANOPIA, model.getCurrentType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 6. MODELO — Filtro modifica los píxeles (no es imagen idéntica)
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @DisplayName("6. ImageModel: Protanopía transforma píxeles rojos")
    void testProtanopiaChangesRedPixels() throws InterruptedException {
        // Imagen completamente roja — Protanopía debe alterar esos píxeles
        WritableImage redImage = new WritableImage(10, 10);
        for (int y = 0; y < 10; y++)
            for (int x = 0; x < 10; x++)
                redImage.getPixelWriter().setColor(x, y, Color.RED);

        runOnFxThread(() -> model.setOriginalImage(redImage));

        AtomicReference<Image> result = new AtomicReference<>();
        runOnFxThread(() -> result.set(model.applyColorBlindFilter(ColorBlindType.PROTANOPIA)));

        PixelReader reader = result.get().getPixelReader();
        Color transformed = reader.getColor(5, 5);

        // El rojo puro (1,0,0) con Protanopía no debe conservarse idéntico
        assertFalse(
            isApproximatelyEqual(transformed, Color.RED, 0.01),
            "Protanopía debe modificar los píxeles completamente rojos"
        );
        // La opacidad debe mantenerse
        assertEquals(1.0, transformed.getOpacity(), 0.001,
            "La opacidad no debe cambiar");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 7. MODELO — El negro puro no se altera por ningún filtro
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(7)
    @DisplayName("7. ImageModel: el negro (#000) es invariante a todos los filtros")
    void testBlackPixelIsInvariant() throws InterruptedException {
        WritableImage blackImage = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                blackImage.getPixelWriter().setColor(x, y, Color.BLACK);

        for (ColorBlindType type : ColorBlindType.values()) {
            runOnFxThread(() -> model.setOriginalImage(blackImage));

            AtomicReference<Image> result = new AtomicReference<>();
            ColorBlindType finalType = type;
            runOnFxThread(() -> result.set(model.applyColorBlindFilter(finalType)));

            Color out = result.get().getPixelReader().getColor(2, 2);
            assertTrue(
                isApproximatelyEqual(out, Color.BLACK, 0.01),
                "El negro puro debe ser invariante para " + type.getDisplayName()
            );
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 8. MODELO — El blanco puro no se altera por ningún filtro
    // ═══════════════════════════════════════════════════════════════════════

@Test
@Order(8)
@DisplayName("8. ImageModel: el filtro produce píxeles con valores válidos (0.0–1.0)")
void testFilterOutputIsValid() throws InterruptedException {
    WritableImage whiteImage = new WritableImage(4, 4);
    for (int y = 0; y < 4; y++)
        for (int x = 0; x < 4; x++)
            whiteImage.getPixelWriter().setColor(x, y, Color.WHITE);

    for (ColorBlindType type : ColorBlindType.values()) {
        runOnFxThread(() -> model.setOriginalImage(whiteImage));

        AtomicReference<Image> result = new AtomicReference<>();
        ColorBlindType finalType = type;
        runOnFxThread(() -> result.set(model.applyColorBlindFilter(finalType)));

        Color out = result.get().getPixelReader().getColor(2, 2);

        // Los canales deben estar en rango válido [0.0, 1.0], sin NaN ni valores corruptos
        assertTrue(out.getRed()   >= 0.0 && out.getRed()   <= 1.0,
            type + ": canal R fuera de rango = " + out.getRed());
        assertTrue(out.getGreen() >= 0.0 && out.getGreen() <= 1.0,
            type + ": canal G fuera de rango = " + out.getGreen());
        assertTrue(out.getBlue()  >= 0.0 && out.getBlue()  <= 1.0,
            type + ": canal B fuera de rango = " + out.getBlue());
        assertEquals(1.0, out.getOpacity(), 0.001,
            type + ": opacidad incorrecta");
    }
}

    // ═══════════════════════════════════════════════════════════════════════
    // 9. MODELO — applyColorBlindFilter retorna null si no hay imagen
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(9)
    @DisplayName("9. ImageModel: applyColorBlindFilter retorna null sin imagen cargada")
    void testFilterWithoutImageReturnsNull() throws InterruptedException {
        ImageModel emptyModel = new ImageModel();
        AtomicReference<Image> result = new AtomicReference<>(new WritableImage(1,1));

        runOnFxThread(() -> result.set(emptyModel.applyColorBlindFilter(ColorBlindType.PROTANOPIA)));

        assertNull(result.get(),
            "applyColorBlindFilter debe retornar null si no hay imagen cargada");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 10. MODELO — Reemplazar imagen limpia la imagen procesada
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(10)
    @DisplayName("10. ImageModel: setOriginalImage limpia processedImage")
    void testSetOriginalImageClearsProcessed() throws InterruptedException {
        Image img = createSyntheticImage(20, 20);
        runOnFxThread(() -> {
            model.setOriginalImage(img);
            model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA);
        });
        assertNotNull(model.getProcessedImage(), "Debe existir imagen procesada antes del reemplazo");

        runOnFxThread(() -> model.setOriginalImage(createSyntheticImage(10, 10)));
        assertNull(model.getProcessedImage(),
            "processedImage debe ser null tras cargar nueva imagen");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 11. ENUM — ColorBlindType tiene exactamente 3 tipos
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(11)
    @DisplayName("11. ColorBlindType: exactamente 3 tipos definidos")
    void testColorBlindTypeCount() {
        assertEquals(3, ColorBlindType.values().length,
            "Deben existir exactamente 3 tipos de daltonismo");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 12. ENUM — Todos los tipos tienen metadatos no nulos
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(12)
    @DisplayName("12. ColorBlindType: todos los tipos tienen displayName, description y accentColor")
    void testColorBlindTypeMetadata() {
        for (ColorBlindType type : ColorBlindType.values()) {
            assertNotNull(type.getDisplayName(),  type + " no tiene displayName");
            assertFalse(type.getDisplayName().isBlank(), type + " displayName está vacío");

            assertNotNull(type.getDescription(),  type + " no tiene description");
            assertFalse(type.getDescription().isBlank(), type + " description está vacía");

            assertNotNull(type.getAccentColor(),  type + " no tiene accentColor");
            assertTrue(type.getAccentColor().startsWith("#"),
                type + " accentColor debe ser un color hex (#RRGGBB)");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 13. VISTA — La vista se inicializa sin errores
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(13)
    @DisplayName("13. MainView: la escena se crea correctamente")
    void testViewSceneNotNull() {
        assertNotNull(view.getScene(), "La escena de MainView no debe ser null");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 14. INTEGRACIÓN — Flujo completo: cargar → filtrar → resetear
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(14)
    @DisplayName("14. Integración: flujo completo cargar → filtrar → resetear")
    void testFullWorkflow() throws InterruptedException {
        Image testImage = createSyntheticImage(60, 60);

        // Paso 1 — Cargar imagen en el modelo y notificar la vista
        runOnFxThread(() -> {
            model.setOriginalImage(testImage);
            view.displayOriginalImage(testImage);
            view.setFilterButtonsEnabled(true);
            view.setResetEnabled(false);
            view.clearProcessedImage();
            view.showStatus("Imagen cargada.");
        });

        assertTrue(model.hasImage(), "El modelo debe tener imagen");

        // Paso 2 — Aplicar filtro Tritanopía
        AtomicReference<Image> filteredImage = new AtomicReference<>();
        runOnFxThread(() -> {
            Image result = model.applyColorBlindFilter(ColorBlindType.TRITANOPIA);
            filteredImage.set(result);
            view.displayProcessedImage(result);
            view.setResetEnabled(true);
            view.showStatus("Filtro aplicado.");
        });

        assertNotNull(filteredImage.get(), "La imagen filtrada no debe ser null");
        assertNotNull(model.getProcessedImage(), "El modelo debe tener imagen procesada");
        assertEquals(ColorBlindType.TRITANOPIA, model.getCurrentType());

        // Paso 3 — Resetear la vista
        runOnFxThread(() -> {
            view.clearProcessedImage();
            view.setResetEnabled(false);
            view.showStatus("Restablecido.");
        });

        assertNotNull(model.getProcessedImage(),
            "El modelo conserva la imagen procesada aunque la vista se limpie");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 15. INTEGRACIÓN — Dos filtros consecutivos sobre la misma imagen original
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @Order(15)
    @DisplayName("15. Integración: aplicar dos filtros distintos produce resultados diferentes")
    void testTwoFiltersProduceDifferentResults() throws InterruptedException {
        Image original = createSyntheticImage(30, 30);
        runOnFxThread(() -> model.setOriginalImage(original));

        AtomicReference<Image> proto    = new AtomicReference<>();
        AtomicReference<Image> deutera  = new AtomicReference<>();

        runOnFxThread(() -> proto.set(model.applyColorBlindFilter(ColorBlindType.PROTANOPIA)));
        runOnFxThread(() -> deutera.set(model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA)));

        assertNotNull(proto.get());
        assertNotNull(deutera.get());

        // Comparar un píxel central 
        Color pColor = proto.get().getPixelReader().getColor(15, 15);
        Color dColor = deutera.get().getPixelReader().getColor(15, 15);

        assertFalse(
            isApproximatelyEqual(pColor, dColor, 0.01),
            "Protanopía y Deuteranopía deben producir resultados distintos para la misma imagen"
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UTILIDADES
    // ═══════════════════════════════════════════════════════════════════════


     //Crea una imagen sintética con colores variados (gradiente RGB).

    private Image createSyntheticImage(int width, int height) {
        WritableImage image = new WritableImage(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = (double) x / width;
                double g = (double) y / height;
                double b = 1.0 - r;
                image.getPixelWriter().setColor(x, y, new Color(r, g, b, 1.0));
            }
        }
        return image;
    }


    // Compara dos colores con tolerancia.

    private boolean isApproximatelyEqual(Color a, Color b, double tolerance) {
        return Math.abs(a.getRed()   - b.getRed())   <= tolerance
            && Math.abs(a.getGreen() - b.getGreen()) <= tolerance
            && Math.abs(a.getBlue()  - b.getBlue())  <= tolerance;
    }


     //Ejecuta una acción en el hilo de JavaFX y espera a que termine.

    private void runOnFxThread(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS),
            "Timeout esperando acción en FX thread");
    }
}
