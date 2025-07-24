package model.dao; // Package declaration based on your project structure

import model.entity.Componente; // Import the base Componente class
import model.entity.TarjetaMadre; // Import specific component types
import model.entity.Microprocesador;
import model.entity.MemoriaRAM; // Assuming 'Ram' in your entity is 'MemoriaRAM' in DB/code
import model.entity.DiscoDuro;
import util.DatabaseConnection; // Import your DatabaseConnection utility

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal; // For handling monetary values like price

public class ComponenteDAO {

  // --- Insert Methods ---

  /**
   * Inserts a new component into the database, handling both the common
   * 'Componente' table
   * and the specific subtype table (e.g., TarjetaMadre, Microprocesador).
   * Uses a transaction to ensure data integrity.
   *
   * @param componente The Componente object to insert (must be a concrete
   *                   subclass).
   * @return The inserted Componente object with its generated ComponenteID.
   * @throws SQLException             If a database access error occurs.
   * @throws IllegalArgumentException If the component type is unknown.
   */
  public Componente insertComponente(Componente componente) throws SQLException {
    String sqlComponente = "INSERT INTO Componente (NumeroSerie, TipoComponente, Precio, CantidadDisponible) VALUES (?, ?, ?, ?) RETURNING ComponenteID;";
    String sqlSubtipo = "";

    // Determine the specific SQL for the subtype based on the component's type
    switch (componente.getTipoComponente()) {
      case "TarjetaMadre":
        sqlSubtipo = "INSERT INTO TarjetaMadre (ComponenteID, Marca, Modelo, SocketMicro, TipoMemoriaRam, ConexionesDisco) VALUES (?, ?, ?, ?, ?, ?);";
        break;
      case "Microprocesador":
        sqlSubtipo = "INSERT INTO Microprocesador (ComponenteID, Marca, Modelo, Socket, Velocidad) VALUES (?, ?, ?, ?, ?);";
        break;
      case "MemoriaRAM": // Assuming 'MemoriaRAM' is the type string for 'Ram' entity
        sqlSubtipo = "INSERT INTO MemoriaRAM (ComponenteID, Marca, Capacidad, TipoMemoria) VALUES (?, ?, ?, ?);";
        break;
      case "DiscoDuro":
        sqlSubtipo = "INSERT INTO DiscoDuro (ComponenteID, Marca, Modelo, Capacidad, TipoConexion) VALUES (?, ?, ?, ?, ?);";
        break;
      default:
        throw new IllegalArgumentException("Tipo de componente desconocido: " + componente.getTipoComponente());
    }

    Connection conn = null;
    PreparedStatement pstmtComponente = null;
    PreparedStatement pstmtSubtipo = null;
    ResultSet rs = null;

    try {
      conn = DatabaseConnection.getConnection();
      conn.setAutoCommit(false); // Start transaction to ensure atomicity

      // 1. Insert into the common Componente table
      pstmtComponente = conn.prepareStatement(sqlComponente);
      pstmtComponente.setString(1, componente.getNumeroSerie());
      pstmtComponente.setString(2, componente.getTipoComponente());
      pstmtComponente.setBigDecimal(3, componente.getPrecio());
      pstmtComponente.setInt(4, componente.getCantidadDisponible());
      rs = pstmtComponente.executeQuery(); // Use executeQuery for RETURNING in PostgreSQL

      int componenteID = -1;
      if (rs.next()) {
        componenteID = rs.getInt(1); // Get the generated ID
        componente.setComponenteID(componenteID); // Assign the ID back to the object
      } else {
        throw new SQLException("Failed to retrieve generated ComponenteID.");
      }

      // 2. Insert into the specific subtype table
      pstmtSubtipo = conn.prepareStatement(sqlSubtipo);
      pstmtSubtipo.setInt(1, componenteID); // Use the ID generated from the first insert

      switch (componente.getTipoComponente()) {
        case "TarjetaMadre":
          TarjetaMadre tm = (TarjetaMadre) componente;
          pstmtSubtipo.setString(2, tm.getMarca());
          pstmtSubtipo.setString(3, tm.getModelo());
          pstmtSubtipo.setString(4, tm.getSocketMicro());
          pstmtSubtipo.setString(5, tm.getTipoMemoriaRam());
          pstmtSubtipo.setString(6, tm.getConexionesDisco());
          break;
        case "Microprocesador":
          Microprocesador mp = (Microprocesador) componente;
          pstmtSubtipo.setString(2, mp.getMarca());
          pstmtSubtipo.setString(3, mp.getModelo());
          pstmtSubtipo.setString(4, mp.getSocket());
          pstmtSubtipo.setString(5, mp.getVelocidad());
          break;
        case "MemoriaRAM":
          MemoriaRAM mr = (MemoriaRAM) componente;
          pstmtSubtipo.setString(2, mr.getMarca());
          pstmtSubtipo.setString(3, mr.getCapacidad());
          pstmtSubtipo.setString(4, mr.getTipoMemoria());
          break;
        case "DiscoDuro":
          DiscoDuro dd = (DiscoDuro) componente;
          pstmtSubtipo.setString(2, dd.getMarca());
          pstmtSubtipo.setString(3, dd.getModelo());
          pstmtSubtipo.setString(4, dd.getCapacidad());
          pstmtSubtipo.setString(5, dd.getTipoConexion());
          break;
      }
      pstmtSubtipo.executeUpdate();

      conn.commit(); // Commit the transaction if both inserts are successful
      return componente;
    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback(); // Rollback if any error occurs
      }
      throw e; // Re-throw the exception
    } finally {
      // Close resources in finally block
      if (rs != null)
        rs.close();
      if (pstmtComponente != null)
        pstmtComponente.close();
      if (pstmtSubtipo != null)
        pstmtSubtipo.close();
      if (conn != null)
        conn.setAutoCommit(true); // Restore auto-commit mode
      DatabaseConnection.closeConnection(conn); // Use your utility to close connection
    }
  }

  // --- Update Methods ---

  /**
   * Updates an existing component in the database, handling both the common
   * 'Componente' table
   * and the specific subtype table. Uses a transaction for data integrity.
   *
   * @param componente The Componente object with updated data.
   * @throws SQLException             If a database access error occurs.
   * @throws IllegalArgumentException If the component type is unknown.
   */
  public void updateComponente(Componente componente) throws SQLException {
    String sqlComponente = "UPDATE Componente SET NumeroSerie = ?, TipoComponente = ?, Precio = ?, CantidadDisponible = ? WHERE ComponenteID = ?;";
    String sqlSubtipo = "";

    // Determine the specific SQL for the subtype
    switch (componente.getTipoComponente()) {
      case "TarjetaMadre":
        sqlSubtipo = "UPDATE TarjetaMadre SET Marca = ?, Modelo = ?, SocketMicro = ?, TipoMemoriaRam = ?, ConexionesDisco = ? WHERE ComponenteID = ?;";
        break;
      case "Microprocesador":
        sqlSubtipo = "UPDATE Microprocesador SET Marca = ?, Modelo = ?, Socket = ?, Velocidad = ? WHERE ComponenteID = ?;";
        break;
      case "MemoriaRAM":
        sqlSubtipo = "UPDATE MemoriaRAM SET Marca = ?, Capacidad = ?, TipoMemoria = ? WHERE ComponenteID = ?;";
        break;
      case "DiscoDuro":
        sqlSubtipo = "UPDATE DiscoDuro SET Marca = ?, Modelo = ?, Capacidad = ?, TipoConexion = ? WHERE ComponenteID = ?;";
        break;
      default:
        throw new IllegalArgumentException("Tipo de componente desconocido: " + componente.getTipoComponente());
    }

    Connection conn = null;
    PreparedStatement pstmtComponente = null;
    PreparedStatement pstmtSubtipo = null;

    try {
      conn = DatabaseConnection.getConnection();
      conn.setAutoCommit(false); // Start transaction

      // 1. Update the common Componente table
      pstmtComponente = conn.prepareStatement(sqlComponente);
      pstmtComponente.setString(1, componente.getNumeroSerie());
      pstmtComponente.setString(2, componente.getTipoComponente());
      pstmtComponente.setBigDecimal(3, componente.getPrecio());
      pstmtComponente.setInt(4, componente.getCantidadDisponible());
      pstmtComponente.setInt(5, componente.getComponenteID());
      pstmtComponente.executeUpdate();

      // 2. Update the specific subtype table
      pstmtSubtipo = conn.prepareStatement(sqlSubtipo);
      // Parameters for subtype specific fields
      switch (componente.getTipoComponente()) {
        case "TarjetaMadre":
          TarjetaMadre tm = (TarjetaMadre) componente;
          pstmtSubtipo.setString(1, tm.getMarca());
          pstmtSubtipo.setString(2, tm.getModelo());
          pstmtSubtipo.setString(3, tm.getSocketMicro());
          pstmtSubtipo.setString(4, tm.getTipoMemoriaRam());
          pstmtSubtipo.setString(5, tm.getConexionesDisco());
          break;
        case "Microprocesador":
          Microprocesador mp = (Microprocesador) componente;
          pstmtSubtipo.setString(1, mp.getMarca());
          pstmtSubtipo.setString(2, mp.getModelo());
          pstmtSubtipo.setString(3, mp.getSocket());
          pstmtSubtipo.setString(4, mp.getVelocidad());
          break;
        case "MemoriaRAM":
          MemoriaRAM mr = (MemoriaRAM) componente;
          pstmtSubtipo.setString(1, mr.getMarca());
          pstmtSubtipo.setString(2, mr.getCapacidad());
          pstmtSubtipo.setString(3, mr.getTipoMemoria());
          break;
        case "DiscoDuro":
          DiscoDuro dd = (DiscoDuro) componente;
          pstmtSubtipo.setString(1, dd.getMarca());
          pstmtSubtipo.setString(2, dd.getModelo());
          pstmtSubtipo.setString(3, dd.getCapacidad());
          pstmtSubtipo.setString(4, dd.getTipoConexion());
          break;
      }
      pstmtSubtipo.setInt(pstmtSubtipo.getParameterMetaData().getParameterCount(), componente.getComponenteID()); // Set
                                                                                                                  // ID
                                                                                                                  // as
                                                                                                                  // the
                                                                                                                  // last
                                                                                                                  // parameter
      pstmtSubtipo.executeUpdate();

      conn.commit(); // Commit the transaction
    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback(); // Rollback on error
      }
      throw e; // Re-throw the exception
    } finally {
      // Close resources
      if (pstmtComponente != null)
        pstmtComponente.close();
      if (pstmtSubtipo != null)
        pstmtSubtipo.close();
      if (conn != null)
        conn.setAutoCommit(true); // Restore auto-commit
      DatabaseConnection.closeConnection(conn);
    }
  }

  // --- Delete Method ---

  /**
   * Deletes a component from the database by its ID.
   * Thanks to ON DELETE CASCADE, deleting from 'Componente' table
   * will automatically delete the corresponding row in the subtype table.
   *
   * @param componenteID The ID of the component to delete.
   * @throws SQLException If a database access error occurs.
   */
  public void deleteComponente(int componenteID) throws SQLException {
    String sql = "DELETE FROM Componente WHERE ComponenteID = ?;";

    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = DatabaseConnection.getConnection();
      pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, componenteID);
      pstmt.executeUpdate();
    } finally {
      if (pstmt != null)
        pstmt.close();
      DatabaseConnection.closeConnection(conn);
    }
  }

  // --- Query Methods ---

  /**
   * Retrieves a specific component by its ID and constructs it into the correct
   * concrete object type (e.g., TarjetaMadre, Microprocesador).
   *
   * @param id The ID of the component to retrieve.
   * @return The Componente object if found, or null.
   * @throws SQLException If a database access error occurs.
   */
  public Componente getComponenteById(int id) throws SQLException {
    // SQL query to join Componente with all its subtypes to retrieve all possible
    // data
    String sql = "SELECT c.*, " +
        "tm.Marca AS tm_marca, tm.Modelo AS tm_modelo, tm.SocketMicro, tm.TipoMemoriaRam, tm.ConexionesDisco, " +
        "mp.Marca AS mp_marca, mp.Modelo AS mp_modelo, mp.Socket, mp.Velocidad, " +
        "mr.Marca AS mr_marca, mr.Capacidad AS mr_capacidad, mr.TipoMemoria, " +
        "dd.Marca AS dd_marca, dd.Modelo AS dd_modelo, dd.Capacidad AS dd_capacidad, dd.TipoConexion " +
        "FROM Componente c " +
        "LEFT JOIN TarjetaMadre tm ON c.ComponenteID = tm.ComponenteID " +
        "LEFT JOIN Microprocesador mp ON c.ComponenteID = mp.ComponenteID " +
        "LEFT JOIN MemoriaRAM mr ON c.ComponenteID = mr.ComponenteID " +
        "LEFT JOIN DiscoDuro dd ON c.ComponenteID = dd.ComponenteID " +
        "WHERE c.ComponenteID = ?;";

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Componente componente = null;

    try {
      conn = DatabaseConnection.getConnection();
      pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, id);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        componente = buildComponenteFromResultSet(rs);
      }
    } finally {
      if (rs != null)
        rs.close();
      if (pstmt != null)
        pstmt.close();
      DatabaseConnection.closeConnection(conn);
    }
    return componente;
  }

  /**
   * Retrieves all components from the database, building them into their correct
   * concrete object types.
   *
   * @return A list of all Componente objects.
   * @throws SQLException If a database access error occurs.
   */
  public List<Componente> getAllComponentes() throws SQLException {
    String sql = "SELECT c.*, " +
        "tm.Marca AS tm_marca, tm.Modelo AS tm_modelo, tm.SocketMicro, tm.TipoMemoriaRam, tm.ConexionesDisco, " +
        "mp.Marca AS mp_marca, mp.Modelo AS mp_modelo, mp.Socket, mp.Velocidad, " +
        "mr.Marca AS mr_marca, mr.Capacidad AS mr_capacidad, mr.TipoMemoria, " +
        "dd.Marca AS dd_marca, dd.Modelo AS dd_modelo, dd.Capacidad AS dd_capacidad, dd.TipoConexion " +
        "FROM Componente c " +
        "LEFT JOIN TarjetaMadre tm ON c.ComponenteID = tm.ComponenteID " +
        "LEFT JOIN Microprocesador mp ON c.ComponenteID = mp.ComponenteID " +
        "LEFT JOIN MemoriaRAM mr ON c.ComponenteID = mr.ComponenteID " +
        "LEFT JOIN DiscoDuro dd ON c.ComponenteID = dd.ComponenteID;";

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<Componente> componentes = new ArrayList<>();

    try {
      conn = DatabaseConnection.getConnection();
      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();

      while (rs.next()) {
        componentes.add(buildComponenteFromResultSet(rs));
      }
    } finally {
      if (rs != null)
        rs.close();
      if (pstmt != null)
        pstmt.close();
      DatabaseConnection.closeConnection(conn);
    }
    return componentes;
  }

  /**
   * Retrieves components filtered by their type.
   *
   * @param tipo The type of component to filter by (e.g., "TarjetaMadre",
   *             "Microprocesador").
   * @return A list of Componente objects of the specified type.
   * @throws SQLException If a database access error occurs.
   */
  public List<Componente> getComponentesByType(String tipo) throws SQLException {
    String sql = "SELECT c.*, " +
        "tm.Marca AS tm_marca, tm.Modelo AS tm_modelo, tm.SocketMicro, tm.TipoMemoriaRam, tm.ConexionesDisco, " +
        "mp.Marca AS mp_marca, mp.Modelo AS mp_modelo, mp.Socket, mp.Velocidad, " +
        "mr.Marca AS mr_marca, mr.Capacidad AS mr_capacidad, mr.TipoMemoria, " +
        "dd.Marca AS dd_marca, dd.Modelo AS dd_modelo, dd.Capacidad AS dd_capacidad, dd.TipoConexion " +
        "FROM Componente c " +
        "LEFT JOIN TarjetaMadre tm ON c.ComponenteID = tm.ComponenteID " +
        "LEFT JOIN Microprocesador mp ON c.ComponenteID = mp.ComponenteID " +
        "LEFT JOIN MemoriaRAM mr ON c.ComponenteID = mr.ComponenteID " +
        "LEFT JOIN DiscoDuro dd ON c.ComponenteID = dd.ComponenteID " +
        "WHERE c.TipoComponente = ?;";

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<Componente> componentes = new ArrayList<>();

    try {
      conn = DatabaseConnection.getConnection();
      pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, tipo);
      rs = pstmt.executeQuery();

      while (rs.next()) {
        componentes.add(buildComponenteFromResultSet(rs));
      }
    } finally {
      if (rs != null)
        rs.close();
      if (pstmt != null)
        pstmt.close();
      DatabaseConnection.closeConnection(conn);
    }
    return componentes;
  }

  /**
   * Helper method to construct a concrete Componente object from a ResultSet row.
   * This method reads common and specific columns and instantiates the correct
   * subclass.
   *
   * @param rs The ResultSet containing the component data.
   * @return A concrete Componente subclass instance.
   * @throws SQLException             If a database access error occurs.
   * @throws IllegalArgumentException If an unknown component type is encountered.
   */
  private Componente buildComponenteFromResultSet(ResultSet rs) throws SQLException {
    int id = rs.getInt("ComponenteID");
    String numeroSerie = rs.getString("NumeroSerie");
    String tipoComponente = rs.getString("TipoComponente");
    BigDecimal precio = rs.getBigDecimal("Precio");
    int cantidadDisponible = rs.getInt("CantidadDisponible");

    // Note: The 'Modelo' field is common to several subclasses but not in the base
    // Componente.
    // We will retrieve it from the specific subtype's columns.
    // Also, 'Marca' is common to all subtypes, but not in the base Componente.
    // We will retrieve it from the specific subtype's columns for consistency with
    // your entity classes.

    switch (tipoComponente) {
      case "TarjetaMadre":
        return new TarjetaMadre(
            id,
            numeroSerie,
            precio,
            cantidadDisponible,
            rs.getString("tm_marca"), // Marca from TarjetaMadre table
            rs.getString("tm_modelo"), // Modelo from TarjetaMadre table
            rs.getString("SocketMicro"),
            rs.getString("TipoMemoriaRam"),
            rs.getString("ConexionesDisco"));
      case "Microprocesador":
        return new Microprocesador(
            id,
            numeroSerie,
            precio,
            cantidadDisponible,
            rs.getString("mp_marca"), // Marca from Microprocesador table
            rs.getString("mp_modelo"), // Modelo from Microprocesador table
            rs.getString("Socket"),
            rs.getString("Velocidad"));
      case "MemoriaRAM": // Corresponds to Ram entity
        return new MemoriaRAM(
            id,
            numeroSerie,
            precio,
            cantidadDisponible,
            rs.getString("mr_marca"), // Marca from MemoriaRAM table
            rs.getString("mr_capacidad"), // Capacidad from MemoriaRAM table
            rs.getString("TipoMemoria"));
      case "DiscoDuro":
        return new DiscoDuro(
            id,
            numeroSerie,
            precio,
            cantidadDisponible,
            rs.getString("dd_marca"), // Marca from DiscoDuro table
            rs.getString("dd_modelo"), // Modelo from DiscoDuro table
            rs.getString("dd_capacidad"),
            rs.getString("TipoConexion"));
      default:
        throw new IllegalArgumentException(
            "Tipo de componente desconocido o no implementado en DAO: " + tipoComponente);
    }
  }
}
