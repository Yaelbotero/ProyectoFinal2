package modelo;

/**
 * Define los tres tipos de daltonismo que la app puede simular.
 * Cada tipo guarda su nombre, una descripcion corta y un color para el boton.
 *
 * @author ColorBlind Helper Team
 * @version 1.0
 */
public enum ColorBlindType {

    /** Dificultad para ver el rojo. Es el tipo menos comun. */
    PROTANOPIA(
        "Protanopia",
        "Dificultad para percibir el color ROJO.\nLos rojos se ven oscuros o negros,\ny se confunden con verdes y amarillos.",
        "#E74C3C"
    ),

    /** Dificultad para ver el verde. Es el tipo mas comun de daltonismo. */
    DEUTERANOPIA(
        "Deuteranopia",
        "Dificultad para percibir el color VERDE.\nEs el tipo mas comun de daltonismo.\nLos verdes y rojos son dificiles de distinguir.",
        "#27AE60"
    ),

    /** Dificultad para ver el azul. Es el tipo mas raro. */
    TRITANOPIA(
        "Tritanopia",
        "Dificultad para percibir el color AZUL.\nEs el tipo mas raro de daltonismo.\nAzules y amarillos se confunden entre si.",
        "#2980B9"
    );

    /** Nombre que aparece en el boton de la interfaz. */
    private final String displayName;

    /** Descripcion que aparece al pasar el mouse sobre el boton. */
    private final String description;

    /** Color del boton en la interfaz (formato hexadecimal). */
    private final String accentColor;

    /**
     * Crea un tipo de daltonismo con su nombre, descripcion y color de boton.
     *
     * @param displayName nombre visible en la interfaz.
     * @param description texto del tooltip del boton.
     * @param accentColor color del boton en hexadecimal.
     */
    ColorBlindType(String displayName, String description, String accentColor) {
        this.displayName = displayName;
        this.description = description;
        this.accentColor = accentColor;
    }

    /**
     * Devuelve el nombre para mostrar en la interfaz.
     * @return nombre del tipo (ej: "Protanopia").
     */
    public String getDisplayName() { return displayName; }

    /**
     * Devuelve la descripcion para el tooltip del boton.
     * @return descripcion del tipo de daltonismo.
     */
    public String getDescription() { return description; }

    /**
     * Devuelve el color hexadecimal del boton en la interfaz.
     * @return color en formato hexadecimal (ej: "#E74C3C").
     */
    public String getAccentColor() { return accentColor; }
}
