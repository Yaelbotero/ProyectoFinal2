package modelo;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Maneja la conexion a la base de datos SQLite donde se guarda el historial
 * de simulaciones. Solo existe una conexion activa a la vez en toda la app.
 *
 * @author ColorBlind Helper Team
 * @version 1.0
 */
public class DatabaseConnection {

    /** Nombre de la carpeta donde se guarda la base de datos en el equipo del usuario. */
    private static final String APP_DIR = ".colorblind_helper";

    /** Nombre del archivo de base de datos. */
    private static final String DB_FILE = "historial.db";

    /** La conexion activa. Es null si todavia no se ha abierto ninguna. */
    private static Connection instance;

    /** Constructor privado para evitar que se creen instancias de esta clase. */
    private DatabaseConnection() {}

    /**
     * Devuelve la conexion a la base de datos.
     * Si no hay ninguna abierta, la crea automaticamente.
     *
     * @return conexion lista para usarse.
     * @throws SQLException si no se puede abrir el archivo de base de datos.
     */
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            String dbPath = getDbPath();
            instance = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            initializeSchema(instance);
        }
        return instance;
    }

    /**
     * Cierra la conexion a la base de datos y libera los recursos.
     * Se debe llamar cuando la aplicacion se cierra.
     */
    public static void closeConnection() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) instance.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexion: " + e.getMessage());
            } finally {
                instance = null;
            }
        }
    }

    /**
     * Calcula donde guardar el archivo de base de datos en el equipo del usuario.
     * Si la carpeta no existe, la crea automaticamente.
     *
     * @return ruta completa del archivo historial.db.
     */
    private static String getDbPath() {
        String home   = System.getProperty("user.home");
        File   appDir = new File(home, APP_DIR);
        if (!appDir.exists()) appDir.mkdirs();
        return new File(appDir, DB_FILE).getAbsolutePath();
    }

    /**
     * Crea la tabla del historial si todavia no existe.
     * Es seguro llamarlo varias veces porque no borra datos existentes.
     *
     * @param conn conexion activa a la base de datos.
     * @throws SQLException si ocurre un error al crear la tabla.
     */
    private static void initializeSchema(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS historial_simulaciones ("
                   + "id              INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "fecha           DATETIME DEFAULT (datetime('now','localtime')), "
                   + "tipo_daltonismo TEXT NOT NULL, "
                   + "nombre_imagen   TEXT NOT NULL, "
                   + "ancho_px        INTEGER NOT NULL, "
                   + "alto_px         INTEGER NOT NULL, "
                   + "ruta_archivo    TEXT NOT NULL DEFAULT ''"
                   + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
