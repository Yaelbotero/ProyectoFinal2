/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 //MODELO — DatabaseConnection

public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/colorblind_helper"
                                         + "?useSSL=false&allowPublicKeyRetrieval=true"
                                         + "&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = ""; 

    // Instancia única de la conexión 
    private static Connection instance;

    // Constructor privado — impide instanciación externa.
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }

     // Cierra la conexión activa si está abierta.
 
    public static void closeConnection() {
        if (instance != null) {
            try {
                if (!instance.isClosed()) {
                    instance.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            } finally {
                instance = null;
            }
        }
    }
}
