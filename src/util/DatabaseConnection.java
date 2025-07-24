package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

  private static final String URL = "jdbc:postgresql://dpg-d20u3jjipnbc73dn46jg-a.oregon-postgres.render.com:5432/pcstore";
  private static final String USER = "pcstore_user";
  private static final String PASSWORD = "z1xvCpTQqA8bzanh0smI0pAS7rcBQfKE";

  public static Connection getConnection() throws SQLException {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      System.err.println("Error: Driver JDBC de MySQL no encontrado.");
      throw new SQLException("Driver JDBC de MySQL no encontrado.", e);
    }
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  public static void close(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        System.err.println("Error al cerrar la conexión: " + e.getMessage());
      }
    }
  }

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
