/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testUnitarios;

import modelo.ColorBlindType;
import modelo.ImageModel;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


 //Test Unitario — ImageModel
    //solo lógica pura de modelo.

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Unitario — ImageModel")
public class ImageModelTest {

    private ImageModel model;

    @BeforeEach
    void setUp() {
        model = new ImageModel();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESTADO INICIAL
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(1)
    @DisplayName("1. Estado inicial: hasImage() = false")
    void testHasImageFalseAlInicio() {
        assertFalse(model.hasImage());
    }

    @Test @Order(2)
    @DisplayName("2. Estado inicial: getOriginalImage() = null")
    void testOriginalImageNullAlInicio() {
        assertNull(model.getOriginalImage());
    }

    @Test @Order(3)
    @DisplayName("3. Estado inicial: getProcessedImage() = null")
    void testProcessedImageNullAlInicio() {
        assertNull(model.getProcessedImage());
    }

    @Test @Order(4)
    @DisplayName("4. Estado inicial: getCurrentType() = null")
    void testCurrentTypeNullAlInicio() {
        assertNull(model.getCurrentType());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // setOriginalImage — VALORES DE ENTRADA/SALIDA
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(5)
    @DisplayName("5. setOriginalImage: hasImage() pasa a true")
    void testSetOriginalImageActivaHasImage() {
        model.setOriginalImage(imagen(10, 10));
        assertTrue(model.hasImage());
    }

    @Test @Order(6)
    @DisplayName("6. setOriginalImage: la imagen queda almacenada")
    void testSetOriginalImageAlmacenaImagen() {
        Image img = imagen(30, 20);
        model.setOriginalImage(img);
        assertNotNull(model.getOriginalImage());
        assertEquals(30, (int) model.getOriginalImage().getWidth());
        assertEquals(20, (int) model.getOriginalImage().getHeight());
    }

    @Test @Order(7)
    @DisplayName("7. setOriginalImage: limpia processedImage al reemplazar")
    void testSetOriginalImageLimpiaProcesada() {
        model.setOriginalImage(imagen(10, 10));
        model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);
        assertNotNull(model.getProcessedImage(), "Debe existir imagen procesada antes");

        model.setOriginalImage(imagen(5, 5));   // reemplazar
        assertNull(model.getProcessedImage(), "processedImage debe limpiarse al recargar");
    }

    @Test @Order(8)
    @DisplayName("8. setOriginalImage: acepta imágenes de 1×1 px (mínimo válido)")
    void testSetOriginalImageUnicoPicel() {
        model.setOriginalImage(imagen(1, 1));
        assertTrue(model.hasImage());
        assertEquals(1, (int) model.getOriginalImage().getWidth());
        assertEquals(1, (int) model.getOriginalImage().getHeight());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // applyColorBlindFilter — VALORES DE ENTRADA/SALIDA
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(9)
    @DisplayName("9. applyColorBlindFilter: preserva ancho y alto — Protanopía")
    void testProtanopiaDimensiones() {
        model.setOriginalImage(imagen(64, 48));
        Image result = model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);
        assertNotNull(result);
        assertEquals(64, (int) result.getWidth());
        assertEquals(48, (int) result.getHeight());
    }

    @Test @Order(10)
    @DisplayName("10. applyColorBlindFilter: preserva ancho y alto — Deuteranopía")
    void testDeuteranopiaDimensiones() {
        model.setOriginalImage(imagen(50, 50));
        Image result = model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA);
        assertNotNull(result);
        assertEquals(50, (int) result.getWidth());
        assertEquals(50, (int) result.getHeight());
    }

    @Test @Order(11)
    @DisplayName("11. applyColorBlindFilter: preserva ancho y alto — Tritanopía")
    void testTritanopiaDimensiones() {
        model.setOriginalImage(imagen(80, 60));
        Image result = model.applyColorBlindFilter(ColorBlindType.TRITANOPIA);
        assertNotNull(result);
        assertEquals(80, (int) result.getWidth());
        assertEquals(60, (int) result.getHeight());
    }

    @Test @Order(12)
    @DisplayName("12. applyColorBlindFilter: actualiza getCurrentType()")
    void testActualizaCurrentType() {
        model.setOriginalImage(imagen(10, 10));
        model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA);
        assertEquals(ColorBlindType.DEUTERANOPIA, model.getCurrentType());
    }

    @Test @Order(13)
    @DisplayName("13. applyColorBlindFilter: almacena getProcessedImage()")
    void testAlmacenaProcessedImage() {
        model.setOriginalImage(imagen(10, 10));
        model.applyColorBlindFilter(ColorBlindType.TRITANOPIA);
        assertNotNull(model.getProcessedImage());
    }

    @Test @Order(14)
    @DisplayName("14. applyColorBlindFilter: todos los píxeles de salida están en [0.0, 1.0]")
    void testPixelesEnRangoValido() {
        WritableImage img = new WritableImage(8, 8);
        // Rellenar con colores extremos y variados
        Color[] extremos = { Color.RED, Color.GREEN, Color.BLUE,
                             Color.WHITE, Color.BLACK, Color.YELLOW,
                             Color.CYAN, Color.MAGENTA };
        int i = 0;
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++)
                img.getPixelWriter().setColor(x, y, extremos[(i++) % extremos.length]);

        for (ColorBlindType type : ColorBlindType.values()) {
            model.setOriginalImage(img);
            Image result = model.applyColorBlindFilter(type);

            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    Color out = result.getPixelReader().getColor(x, y);
                    assertTrue(out.getRed()   >= 0.0 && out.getRed()   <= 1.0,
                        type + " px(" + x + "," + y + ") R fuera de rango: " + out.getRed());
                    assertTrue(out.getGreen() >= 0.0 && out.getGreen() <= 1.0,
                        type + " px(" + x + "," + y + ") G fuera de rango: " + out.getGreen());
                    assertTrue(out.getBlue()  >= 0.0 && out.getBlue()  <= 1.0,
                        type + " px(" + x + "," + y + ") B fuera de rango: " + out.getBlue());
                }
            }
        }
    }

    @Test @Order(15)
    @DisplayName("15. applyColorBlindFilter: preserva la opacidad original")
    void testPreservaOpacidad() {
        WritableImage img = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                img.getPixelWriter().setColor(x, y, new Color(0.5, 0.3, 0.8, 0.75));

        model.setOriginalImage(img);
        Image result = model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);

        Color out = result.getPixelReader().getColor(2, 2);
        assertEquals(0.75, out.getOpacity(), 0.001, "La opacidad debe conservarse exactamente");
    }

    @Test @Order(16)
    @DisplayName("16. applyColorBlindFilter: negro es invariante para los 3 filtros")
    void testNegroInvariante() {
        WritableImage img = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                img.getPixelWriter().setColor(x, y, Color.BLACK);

        for (ColorBlindType type : ColorBlindType.values()) {
            model.setOriginalImage(img);
            Image result = model.applyColorBlindFilter(type);
            Color out = result.getPixelReader().getColor(2, 2);
            assertTrue(colorCercano(out, Color.BLACK, 0.01),
                "Negro debe ser invariante para " + type.getDisplayName()
                + " — salida: " + out);
        }
    }

    @Test @Order(17)
    @DisplayName("17. applyColorBlindFilter: Protanopía transforma el rojo puro")
    void testProtanopiaTransformaRojo() {
        WritableImage img = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                img.getPixelWriter().setColor(x, y, Color.RED);

        model.setOriginalImage(img);
        Image result = model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);
        Color out = result.getPixelReader().getColor(2, 2);

        assertFalse(colorCercano(out, Color.RED, 0.01),
            "Protanopía debe transformar el rojo puro (no puede salir igual)");
    }

    @Test @Order(18)
    @DisplayName("18. applyColorBlindFilter: Protanopía ≠ Deuteranopía para imagen con rojo puro")
    void testProtanopiaDiferenteDeuteranopia() {
        // Rojo puro maximiza la diferencia entre Protanopía y Deuteranopía
        WritableImage img = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                img.getPixelWriter().setColor(x, y, Color.RED);

        model.setOriginalImage(img);
        Image proto = model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);

        model.setOriginalImage(img);
        Image deutera = model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA);

        Color cp = proto.getPixelReader().getColor(2, 2);
        Color cd = deutera.getPixelReader().getColor(2, 2);
        assertFalse(colorCercano(cp, cd, 0.02),
            "Protanopía y Deuteranopía deben producir resultados distintos sobre rojo puro");
    }

    @Test @Order(19)
    @DisplayName("19. applyColorBlindFilter: imagen 1×1 roja con Protanopía — salida válida")
    void testUnPicelRojoProtanopia() {
        WritableImage img = new WritableImage(1, 1);
        img.getPixelWriter().setColor(0, 0, Color.RED);
        model.setOriginalImage(img);

        Image result = model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);
        assertNotNull(result);
        assertEquals(1, (int) result.getWidth());
        assertEquals(1, (int) result.getHeight());

        Color out = result.getPixelReader().getColor(0, 0);
        assertTrue(out.getRed()   >= 0.0 && out.getRed()   <= 1.0);
        assertTrue(out.getGreen() >= 0.0 && out.getGreen() <= 1.0);
        assertTrue(out.getBlue()  >= 0.0 && out.getBlue()  <= 1.0);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MANEJO DE ERRORES Y CASOS BORDE
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(20)
    @DisplayName("20. applyColorBlindFilter: retorna null si no hay imagen cargada")
    void testFiltroSinImagenRetornaNull() {
        assertNull(model.applyColorBlindFilter(ColorBlindType.PROTANOPIA));
        assertNull(model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA));
        assertNull(model.applyColorBlindFilter(ColorBlindType.TRITANOPIA));
    }

    @Test @Order(21)
    @DisplayName("21. applyColorBlindFilter: no lanza excepción con imagen completamente transparente")
    void testImagenTotalmenteTransparente() {
        WritableImage img = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                img.getPixelWriter().setColor(x, y, new Color(0.5, 0.5, 0.5, 0.0));

        model.setOriginalImage(img);
        assertDoesNotThrow(() -> model.applyColorBlindFilter(ColorBlindType.TRITANOPIA));
    }

    @Test @Order(22)
    @DisplayName("22. applyColorBlindFilter: no lanza excepción con imagen de colores extremos")
    void testImagenColoresExtremos() {
        WritableImage img = new WritableImage(2, 2);
        img.getPixelWriter().setColor(0, 0, Color.WHITE);
        img.getPixelWriter().setColor(1, 0, Color.BLACK);
        img.getPixelWriter().setColor(0, 1, new Color(1.0, 0.0, 0.0, 1.0));
        img.getPixelWriter().setColor(1, 1, new Color(0.0, 0.0, 1.0, 1.0));

        model.setOriginalImage(img);
        for (ColorBlindType type : ColorBlindType.values()) {
            model.setOriginalImage(img);
            assertDoesNotThrow(() -> model.applyColorBlindFilter(type),
                "No debe lanzar excepción para " + type.getDisplayName());
        }
    }

    @Test @Order(23)
    @DisplayName("23. setOriginalImage: llamadas múltiples consecutivas no corrompen el estado")
    void testSetOriginalImageMultiplesVeces() {
        for (int i = 1; i <= 5; i++) {
            model.setOriginalImage(imagen(i * 10, i * 10));
        }
        assertTrue(model.hasImage());
        assertEquals(50, (int) model.getOriginalImage().getWidth());
        assertNull(model.getProcessedImage());
    }

    @Test @Order(24)
    @DisplayName("24. applyColorBlindFilter: aplicar el mismo filtro dos veces da igual resultado")
    void testMismoFiltroIdempotente() {
        model.setOriginalImage(imagenGradiente(10, 10));
        Image r1 = model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA);

        model.setOriginalImage(imagenGradiente(10, 10));
        Image r2 = model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA);

        Color c1 = r1.getPixelReader().getColor(5, 5);
        Color c2 = r2.getPixelReader().getColor(5, 5);
        assertTrue(colorCercano(c1, c2, 0.001),
            "El mismo filtro sobre la misma imagen debe dar siempre el mismo resultado");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UTILIDADES
    // ═══════════════════════════════════════════════════════════════════════

    private Image imagen(int w, int h) {
        WritableImage img = new WritableImage(w, h);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                img.getPixelWriter().setColor(x, y, new Color(0.5, 0.5, 0.5, 1.0));
        return img;
    }

    private Image imagenGradiente(int w, int h) {
        WritableImage img = new WritableImage(w, h);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                img.getPixelWriter().setColor(x, y,
                    new Color((double) x / w, (double) y / h, 1.0 - (double) x / w, 1.0));
        return img;
    }

    private boolean colorCercano(Color a, Color b, double tol) {
        return Math.abs(a.getRed()   - b.getRed())   <= tol
            && Math.abs(a.getGreen() - b.getGreen()) <= tol
            && Math.abs(a.getBlue()  - b.getBlue())  <= tol;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MATRICES MACHADO 2009 — comportamiento específico
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(25)
    @DisplayName("25. Protanopía: el rojo puro se oscurece notablemente (característica Machado)")
    void testProtanopiaOscureceRojo() {
        WritableImage img = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                img.getPixelWriter().setColor(x, y, Color.RED);

        model.setOriginalImage(img);
        Image result = model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);
        Color out = result.getPixelReader().getColor(2, 2);

        // Con Machado, el rojo puro en Protanopía produce luminosidad baja
        double luminancia = 0.2126 * out.getRed() + 0.7152 * out.getGreen() + 0.0722 * out.getBlue();
        assertTrue(luminancia < 0.4,
            "Protanopía debe oscurecer el rojo puro — luminancia obtenida: " + luminancia);
    }

    @Test @Order(26)
    @DisplayName("26. Tritanopía: el azul puro se transforma significativamente (característica Machado)")
    void testTritanopiaTransformaAzul() {
        WritableImage img = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                img.getPixelWriter().setColor(x, y, Color.BLUE);

        model.setOriginalImage(img);
        Image result = model.applyColorBlindFilter(ColorBlindType.TRITANOPIA);
        Color out = result.getPixelReader().getColor(2, 2);

        assertFalse(colorCercano(out, Color.BLUE, 0.05),
            "Tritanopía debe transformar el azul puro significativamente");
    }

    @Test @Order(27)
    @DisplayName("27. applyColorBlindFilter(String): sobrecarga acepta nombre en minúsculas")
    void testSobrecargaStringMinusculas() {
        model.setOriginalImage(imagen(10, 10));
        assertDoesNotThrow(() -> model.applyColorBlindFilter("protanopia"),
            "La sobrecarga String debe aceptar minúsculas y convertirlas internamente");
        assertEquals(ColorBlindType.PROTANOPIA, model.getCurrentType());
    }

    @Test @Order(28)
    @DisplayName("28. applyColorBlindFilter(String): nombre inválido lanza IllegalArgumentException")
    void testSobrecargaStringInvalida() {
        model.setOriginalImage(imagen(10, 10));
        assertThrows(IllegalArgumentException.class,
            () -> model.applyColorBlindFilter("DALTONISMO"),
            "Nombre de tipo inválido debe lanzar IllegalArgumentException");
    }

    @Test @Order(29)
    @DisplayName("29. Los tres filtros producen resultados distintos entre sí")
    void testTresFiltrosDistintos() {
        // Rojo puro: maximiza diferencia entre Protanopía y Deuteranopía
        WritableImage imgRoja = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                imgRoja.getPixelWriter().setColor(x, y, Color.RED);

        model.setOriginalImage(imgRoja);
        Image proto = model.applyColorBlindFilter(ColorBlindType.PROTANOPIA);

        model.setOriginalImage(imgRoja);
        Image deutera = model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA);

        // Azul puro: maximiza diferencia con Tritanopía
        WritableImage imgAzul = new WritableImage(4, 4);
        for (int y = 0; y < 4; y++)
            for (int x = 0; x < 4; x++)
                imgAzul.getPixelWriter().setColor(x, y, Color.BLUE);

        model.setOriginalImage(imgAzul);
        Image trita = model.applyColorBlindFilter(ColorBlindType.TRITANOPIA);

        model.setOriginalImage(imgAzul);
        Image deuteraAzul = model.applyColorBlindFilter(ColorBlindType.DEUTERANOPIA);

        Color cp  = proto.getPixelReader().getColor(2, 2);
        Color cd  = deutera.getPixelReader().getColor(2, 2);
        Color ct  = trita.getPixelReader().getColor(2, 2);
        Color cdA = deuteraAzul.getPixelReader().getColor(2, 2);

        assertFalse(colorCercano(cp, cd,  0.02), "Protanopía ≠ Deuteranopía sobre rojo");
        assertFalse(colorCercano(ct, cdA, 0.02), "Tritanopía ≠ Deuteranopía sobre azul");
    }
}