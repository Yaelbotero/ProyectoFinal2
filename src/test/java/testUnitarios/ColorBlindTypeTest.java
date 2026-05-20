/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testUnitarios;

import modelo.ColorBlindType;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


 //Test Unitario — ColorBlindType

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Unitario — ColorBlindType")
public class ColorBlindTypeTest {

    // ═══════════════════════════════════════════════════════════════════════
    // VALORES DE ENTRADA/SALIDA
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(1)
    @DisplayName("1. Existen exactamente 3 tipos de daltonismo")
    void testCantidadTipos() {
        assertEquals(3, ColorBlindType.values().length);
    }

    @Test @Order(2)
    @DisplayName("2. Existen los tres tipos esperados: PROTANOPIA, DEUTERANOPIA, TRITANOPIA")
    void testTiposEsperadosExisten() {
        assertDoesNotThrow(() -> ColorBlindType.valueOf("PROTANOPIA"));
        assertDoesNotThrow(() -> ColorBlindType.valueOf("DEUTERANOPIA"));
        assertDoesNotThrow(() -> ColorBlindType.valueOf("TRITANOPIA"));
    }

    @Test @Order(3)
    @DisplayName("3. Todos los displayName son no nulos y no vacíos")
    void testDisplayNameNoVacio() {
        for (ColorBlindType type : ColorBlindType.values()) {
            assertNotNull(type.getDisplayName(),
                type + ": displayName no debe ser null");
            assertFalse(type.getDisplayName().isBlank(),
                type + ": displayName no debe estar vacío");
        }
    }

    @Test @Order(4)
    @DisplayName("4. displayName de Protanopía es correcto")
    void testDisplayNameProtanopia() {
        assertEquals("Protanopía", ColorBlindType.PROTANOPIA.getDisplayName());
    }

    @Test @Order(5)
    @DisplayName("5. displayName de Deuteranopía es correcto")
    void testDisplayNameDeuteranopia() {
        assertEquals("Deuteranopía", ColorBlindType.DEUTERANOPIA.getDisplayName());
    }

    @Test @Order(6)
    @DisplayName("6. displayName de Tritanopía es correcto")
    void testDisplayNameTritanopia() {
        assertEquals("Tritanopía", ColorBlindType.TRITANOPIA.getDisplayName());
    }

    @Test @Order(7)
    @DisplayName("7. Todas las descriptions son no nulas y no vacías")
    void testDescriptionNoVacia() {
        for (ColorBlindType type : ColorBlindType.values()) {
            assertNotNull(type.getDescription(),
                type + ": description no debe ser null");
            assertFalse(type.getDescription().isBlank(),
                type + ": description no debe estar vacía");
        }
    }

    @Test @Order(8)
    @DisplayName("8. Todas las accentColor son no nulas y no vacías")
    void testAccentColorNoVacio() {
        for (ColorBlindType type : ColorBlindType.values()) {
            assertNotNull(type.getAccentColor(),
                type + ": accentColor no debe ser null");
            assertFalse(type.getAccentColor().isBlank(),
                type + ": accentColor no debe estar vacío");
        }
    }

    @Test @Order(9)
    @DisplayName("9. Todas las accentColor tienen formato hex #RRGGBB")
    void testAccentColorFormato() {
        for (ColorBlindType type : ColorBlindType.values()) {
            String color = type.getAccentColor();
            assertTrue(color.matches("^#[0-9A-Fa-f]{6}$"),
                type + ": accentColor debe tener formato #RRGGBB, valor actual: " + color);
        }
    }

    @Test @Order(10)
    @DisplayName("10. accentColor de Protanopía es rojo (#E74C3C)")
    void testAccentColorProtanopia() {
        assertEquals("#E74C3C", ColorBlindType.PROTANOPIA.getAccentColor());
    }

    @Test @Order(11)
    @DisplayName("11. accentColor de Deuteranopía es verde (#27AE60)")
    void testAccentColorDeuteranopia() {
        assertEquals("#27AE60", ColorBlindType.DEUTERANOPIA.getAccentColor());
    }

    @Test @Order(12)
    @DisplayName("12. accentColor de Tritanopía es azul (#2980B9)")
    void testAccentColorTritanopia() {
        assertEquals("#2980B9", ColorBlindType.TRITANOPIA.getAccentColor());
    }

    @Test @Order(13)
    @DisplayName("13. description de Protanopía menciona el color ROJO")
    void testDescriptionProtanopiaMencionaRojo() {
        assertTrue(
            ColorBlindType.PROTANOPIA.getDescription().toUpperCase().contains("ROJO"),
            "La descripción de Protanopía debe mencionar ROJO"
        );
    }

    @Test @Order(14)
    @DisplayName("14. description de Deuteranopía menciona el color VERDE")
    void testDescriptionDeuteranopiaMencionaVerde() {
        assertTrue(
            ColorBlindType.DEUTERANOPIA.getDescription().toUpperCase().contains("VERDE"),
            "La descripción de Deuteranopía debe mencionar VERDE"
        );
    }

    @Test @Order(15)
    @DisplayName("15. description de Tritanopía menciona el color AZUL")
    void testDescriptionTritanopiaMencionaAzul() {
        assertTrue(
            ColorBlindType.TRITANOPIA.getDescription().toUpperCase().contains("AZUL"),
            "La descripción de Tritanopía debe mencionar AZUL"
        );
    }

    @Test @Order(16)
    @DisplayName("16. Todos los displayName son únicos entre sí")
    void testDisplayNamesUnicos() {
        ColorBlindType[] types = ColorBlindType.values();
        for (int i = 0; i < types.length; i++)
            for (int j = i + 1; j < types.length; j++)
                assertNotEquals(types[i].getDisplayName(), types[j].getDisplayName(),
                    "displayName duplicado entre " + types[i] + " y " + types[j]);
    }

    @Test @Order(17)
    @DisplayName("17. Todos los accentColor son únicos entre sí")
    void testAccentColoresUnicos() {
        ColorBlindType[] types = ColorBlindType.values();
        for (int i = 0; i < types.length; i++)
            for (int j = i + 1; j < types.length; j++)
                assertNotEquals(types[i].getAccentColor(), types[j].getAccentColor(),
                    "accentColor duplicado entre " + types[i] + " y " + types[j]);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MANEJO DE ERRORES Y CASOS BORDE
    // ═══════════════════════════════════════════════════════════════════════

    @Test @Order(18)
    @DisplayName("18. valueOf con nombre inválido lanza IllegalArgumentException")
    void testValueOfNombreInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> ColorBlindType.valueOf("INEXISTENTE"),
            "valueOf con nombre inválido debe lanzar IllegalArgumentException");
    }

    @Test @Order(19)
    @DisplayName("19. valueOf es sensible a mayúsculas (minúsculas lanzan excepción)")
    void testValueOfCaseSensitive() {
        assertThrows(IllegalArgumentException.class,
            () -> ColorBlindType.valueOf("protanopia"),
            "valueOf con minúsculas debe lanzar IllegalArgumentException");
    }

    @Test @Order(20)
    @DisplayName("20. valueOf con cadena vacía lanza IllegalArgumentException")
    void testValueOfCadenaVacia() {
        assertThrows(IllegalArgumentException.class,
            () -> ColorBlindType.valueOf(""),
            "valueOf con cadena vacía debe lanzar IllegalArgumentException");
    }

    @Test @Order(21)
    @DisplayName("21. Los ordinales son 0, 1, 2 en orden PROTANOPIA → DEUTERANOPIA → TRITANOPIA")
    void testOrdinales() {
        assertEquals(0, ColorBlindType.PROTANOPIA.ordinal());
        assertEquals(1, ColorBlindType.DEUTERANOPIA.ordinal());
        assertEquals(2, ColorBlindType.TRITANOPIA.ordinal());
    }

    @Test @Order(22)
    @DisplayName("22. name() retorna el identificador exacto del enum")
    void testName() {
        assertEquals("PROTANOPIA",   ColorBlindType.PROTANOPIA.name());
        assertEquals("DEUTERANOPIA", ColorBlindType.DEUTERANOPIA.name());
        assertEquals("TRITANOPIA",   ColorBlindType.TRITANOPIA.name());
    }

    @Test @Order(23)
    @DisplayName("23. Ningún campo retorna null después de múltiples llamadas consecutivas")
    void testGettersEstables() {
        for (int i = 0; i < 100; i++) {
            for (ColorBlindType type : ColorBlindType.values()) {
                assertNotNull(type.getDisplayName());
                assertNotNull(type.getDescription());
                assertNotNull(type.getAccentColor());
            }
        }
    }
}
