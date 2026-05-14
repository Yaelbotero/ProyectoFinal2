/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author cript
 */


/**
 * Enumeración que representa los tres tipos de daltonismo soportados.
 * 
 * - PROTANOPIA:   Ausencia de conos sensibles al rojo (cono L)
 * - DEUTERANOPIA: Ausencia de conos sensibles al verde (cono M)
 * - TRITANOPIA:   Ausencia de conos sensibles al azul (cono S)
 */
public enum ColorBlindType {

    PROTANOPIA(
        "Protanopía",
        "Dificultad para percibir el color ROJO.\nLos rojos se ven oscuros o negros,\ny se confunden con verdes y amarillos.",
        "#E74C3C"
    ),

    DEUTERANOPIA(
        "Deuteranopía",
        "Dificultad para percibir el color VERDE.\nEs el tipo más común de daltonismo.\nLos verdes y rojos son difíciles de distinguir.",
        "#27AE60"
    ),

    TRITANOPIA(
        "Tritanopía",
        "Dificultad para percibir el color AZUL.\nEs el tipo más raro de daltonismo.\nAzules y amarillos se confunden entre sí.",
        "#2980B9"
    );

    private final String displayName;
    private final String description;
    private final String accentColor;

    ColorBlindType(String displayName, String description, String accentColor) {
        this.displayName = displayName;
        this.description = description;
        this.accentColor = accentColor;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getAccentColor() { return accentColor; }
}
