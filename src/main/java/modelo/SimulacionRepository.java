package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Maneja todas las operaciones de la tabla de historial en la base de datos.
 * El controlador usa esta clase para guardar, consultar y borrar simulaciones
 * sin escribir SQL directamente.
 *
 * @author ColorBlind Helper Team
 * @version 1.0
 */
public class SimulacionRepository {

    /**
     * Guarda una nueva simulacion en el historial.
     * La fecha se registra automaticamente por la base de datos.
     *
     * @param tipoDaltonismo tipo aplicado (ej: "Protanopia").
     * @param nombreImagen   nombre del archivo de imagen procesado.
     * @param anchoPx        ancho de la imagen en pixeles.
     * @param altoPx         alto de la imagen en pixeles.
     * @param rutaArchivo    ruta del archivo en disco. Vacia si vino de la API.
     * @return true si se guardo correctamente, false si hubo un error.
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
            stmt.setInt   (3, anchoPx);
            stmt.setInt   (4, altoPx);
            stmt.setString(5, rutaArchivo);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al guardar simulacion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Devuelve todas las simulaciones guardadas, de la mas reciente a la mas antigua.
     * Cada simulacion es un arreglo de 7 datos:
     * [0]=id, [1]=fecha, [2]=tipo, [3]=imagen, [4]=ancho, [5]=alto, [6]=ruta.
     *
     * @return lista de simulaciones, o lista vacia si no hay ninguna o hubo error.
     */
    public List<String[]> obtenerHistorial() {
        List<String[]> historial = new ArrayList<>();
        String sql = "SELECT id, fecha, tipo_daltonismo, nombre_imagen, "
                   + "ancho_px, alto_px, ruta_archivo "
                   + "FROM historial_simulaciones ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

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
     * Borra todas las simulaciones del historial.
     * Esta operacion no se puede deshacer.
     *
     * @return true si se borro correctamente, false si hubo un error.
     */
    public boolean limpiarHistorial() {
        String sql = "DELETE FROM historial_simulaciones";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al limpiar historial: " + e.getMessage());
            return false;
        }
    }

    /**
     * Devuelve cuantas simulaciones hay guardadas en total.
     *
     * @return numero de simulaciones, o -1 si hubo un error.
     */
    public int contarSimulaciones() {
        String sql = "SELECT COUNT(*) FROM historial_simulaciones";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("Error al contar simulaciones: " + e.getMessage());
        }
        return -1;
    }
}
