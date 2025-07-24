package util; // Package declaration based on your project structure

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

  // --- ACTUALIZA ESTOS VALORES CON LOS DETALLES DE TU BASE DE DATOS POSTGRESQL
  // EN RENDER ---
  // Host de Render (dpg-d20u3jjipnbc73dn46jg-a.oregon-postgres.render.com)
  // Puerto de PostgreSQL (5432)
  // Nombre de la base de datos (pcstore)
  private static final String URL = "jdbc:postgresql://dpg-d20u3jjipnbc73dn46jg-a.oregon-postgres.render.com:5432/pcstore";
  private static final String USER = "pcstore_user"; // Usuario proporcionado por Render
  private static final String PASSWORD = "z1xvCpTQqA8bzanh0smI0pAS7rcBQfKE"; // Contraseña proporcionada por Render

  public static Connection getConnection() throws SQLException {
    try {
      // Cargar el driver JDBC para PostgreSQL
      Class.forName("org.postgresql.Driver"); // CAMBIO: de com.mysql.cj.jdbc.Driver a org.postgresql.Driver
    } catch (ClassNotFoundException e) {
      // Mensaje de error actualizado para PostgreSQL
      System.err.println("Error: Driver JDBC de PostgreSQL no encontrado.");
      throw new SQLException("Driver JDBC de PostgreSQL no encontrado.", e);
    }
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  /**
   * Cierra un objeto Connection de forma segura.
   * Método renombrado para ser más explícito y coincidir con el DAO.
   *
   * @param conn La conexión a cerrar.
   */
  public static void closeConnection(Connection conn) { // CAMBIO: Renombrado de 'close' a 'closeConnection'
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        System.err.println("Error al cerrar la conexión: " + e.getMessage());
      }
    }
  }

  /**
   * Cierra un objeto Statement de forma segura.
   *
   * @param stmt El Statement a cerrar.
   */
  public static void close(Statement stmt) { // Este método 'close' para Statement se mantiene
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException e) {
        System.err.println("Error al cerrar el Statement: " + e.getMessage());
      }
    }
  }

  // Puedes añadir un método similar para cerrar ResultSet si lo necesitas
  // public static void close(ResultSet rs) { ... }
}
