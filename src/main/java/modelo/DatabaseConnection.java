/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


 //MODELO — DatabaseConnection

public class DatabaseConnection {

    private static final String APP_DIR  = ".colorblind_helper";

    private static final String DB_FILE  = "historial.db";

    private static Connection instance;

    private DatabaseConnection() {}


    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            String dbPath = getDbPath();
            instance = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            initializeSchema(instance);
        }
        return instance;
    }

     //Cierra la conexión activa si está abierta. Llamar al cerrar la aplicación para liberar recursos.
    public static void closeConnection() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) instance.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            } finally {
                instance = null;
            }
        }
    }

    private static String getDbPath() {
        String home  = System.getProperty("user.home");
        File   appDir = new File(home, APP_DIR);

        if (!appDir.exists()) appDir.mkdirs();

        return new File(appDir, DB_FILE).getAbsolutePath();
    }

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
