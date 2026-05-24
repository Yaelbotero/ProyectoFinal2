/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MODELO — SimulacionRepository
 *
 * <p>Repositorio que gestiona las operaciones de persistencia sobre la tabla
 * {@code historial_simulaciones} en MySQL.
 *
 * <p>Cada registro almacena el tipo de daltonismo aplicado, el nombre e imagen
 * del archivo, sus dimensiones y la ruta completa en disco para poder
 * recargarla desde el historial.
 *
 * @author cript
 */
public class SimulacionRepository {

    /**
     * Inserta un nuevo registro en el historial de simulaciones.
     *
     * @param tipoDaltonismo nombre del tipo aplicado (p. ej. {@code "Protanopía"}).
     * @param nombreImagen   nombre corto del archivo (p. ej. {@code "foto.png"}).
     * @param anchoPx        ancho de la imagen en píxeles.
     * @param altoPx         alto de la imagen en píxeles.
     * @param rutaArchivo    ruta absoluta del archivo en disco, o cadena vacía si
     *                       la imagen proviene de la API.
     * @return {@code true} si el registro se insertó correctamente.
     */
    public boolean guardarSimulacion(String tipoDaltonismo, String nombreImagen,
                                     int anchoPx, int altoPx, String rutaArchivo) {
        String sql = "INSERT INTO historial_simulaciones "
                   + "(tipo_daltonismo, nombre_imagen, ancho_px, alto_px, ruta_archivo) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipoDaltonismo);
            stmt.setString(2, nombreImagen);
            stmt.setInt(3, anchoPx);
            stmt.setInt(4, altoPx);
            stmt.setString(5, rutaArchivo);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar simulación: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos los registros del historial ordenados por fecha descendente.
     *
     * @return lista de arreglos {@code String[]} con los campos
     *         [id, fecha, tipo_daltonismo, nombre_imagen, ancho_px, alto_px, ruta_archivo],
     *         o lista vacía si no hay registros o hay error.
     */
    public List<String[]> obtenerHistorial() {
        List<String[]> historial = new ArrayList<>();
        String sql = "SELECT id, fecha, tipo_daltonismo, nombre_imagen, "
                   + "ancho_px, alto_px, ruta_archivo "
                   + "FROM historial_simulaciones ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                historial.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("fecha"),
                    rs.getString("tipo_daltonismo"),
                    rs.getString("nombre_imagen"),
                    String.valueOf(rs.getInt("ancho_px")),
                    String.valueOf(rs.getInt("alto_px")),
                    rs.getString("ruta_archivo")
                });
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
        }
        return historial;
    }

    /**
     * Elimina todos los registros del historial de simulaciones.
     *
     * @return {@code true} si se limpió correctamente.
     */
    public boolean limpiarHistorial() {
        String sql = "DELETE FROM historial_simulaciones";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement()) {

            stmt.executeUpdate(sql);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al limpiar historial: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cuenta el total de simulaciones registradas en el historial.
     *
     * @return número de registros, o {@code -1} si hay error.
     */
    public int contarSimulaciones() {
        String sql = "SELECT COUNT(*) FROM historial_simulaciones";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("Error al contar simulaciones: " + e.getMessage());
        }
        return -1;
    }
}
