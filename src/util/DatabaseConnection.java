package util; // Asegúrate de que este paquete coincida con tu estructura (puede ser com.yourcompany.inventario.util)

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

  // --- ¡ACTUALIZA ESTOS VALORES CON LOS DETALLES REALES DE TU BASE DE DATOS
  // POSTGRESQL EN RENDER! ---
  // Estos son los valores que te proporcioné anteriormente como ejemplo.
  // Asegúrate de que coincidan con los que Render te dio para tu base de datos.
  private static final String URL = "jdbc:postgresql://dpg-d20u3jjipnbc73dn46jg-a.oregon-postgres.render.com:5432/pcstore";
  private static final String USER = "pcstore_user";
  private static final String PASSWORD = "z1xvCpTQqA8bzanh0smI0pAS7rcBQfKE";

  /**
   * Obtiene una nueva conexión a la base de datos.
   *
   * @return Objeto Connection.
   * @throws SQLException Si ocurre un error al conectar.
   */
  public static Connection getConnection() throws SQLException {
    try {
      // Cargar el driver JDBC para PostgreSQL
      Class.forName("org.postgresql.Driver"); // CAMBIO: Asegúrate de usar el driver de PostgreSQL
    } catch (ClassNotFoundException e) {
      System.err.println("Error: Driver JDBC de PostgreSQL no encontrado.");
      throw new SQLException("Driver JDBC de PostgreSQL no encontrado.", e);
    }
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  /**
   * Cierra un objeto Connection de forma segura.
   *
   * @param conn La conexión a cerrar.
   */
  public static void closeConnection(Connection conn) { // Renombrado a closeConnection para consistencia
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
  public static void close(Statement stmt) {
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException e) {
        System.err.println("Error al cerrar el Statement: " + e.getMessage());
      }
    }
  }
}
