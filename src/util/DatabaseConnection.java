package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

  private static final String URL = "jdbc:mysql://localhost:3306/pcstore?useSSL=false&serverTimezone=UTC";
  private static final String USER = "root";
  private static final String PASSWORD = "123456";

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
