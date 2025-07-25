package model.dao;

import model.entity.*;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Clase de Acceso a Datos para la entidad Componente.
 * Contiene la lógica CRUD y la carga optimizada de componentes y sus subtipos.
 */
public class ComponenteDAO {

  // --- MÉTODOS CRUD (CREATE, UPDATE, DELETE) ---
  // Nota: Estos métodos son transaccionales para asegurar la integridad de los
  // datos.

  public void addComponente(Componente componente) throws SQLException {
    String sqlComponente = "INSERT INTO Componente (NumeroSerie, TipoComponente, Precio, CantidadDisponible) VALUES (?, ?, ?, ?) RETURNING ComponenteID;";
    String sqlSubtipo = "";
    Connection conn = null;
    PreparedStatement pstmtComponente = null;
    PreparedStatement pstmtSubtipo = null;
    ResultSet rs = null;

    try {
      conn = DatabaseConnection.getConnection();
      conn.setAutoCommit(false); // Iniciar transacción

      pstmtComponente = conn.prepareStatement(sqlComponente);
      pstmtComponente.setString(1, componente.getNumeroSerie());
      pstmtComponente.setString(2, componente.getTipoComponente());
      pstmtComponente.setBigDecimal(3, componente.getPrecio());
      pstmtComponente.setInt(4, componente.getCantidadDisponible());
      rs = pstmtComponente.executeQuery();

      int componenteID = -1;
      if (rs.next()) {
        componenteID = rs.getInt(1);
        componente.setComponenteID(componenteID);
      } else {
        throw new SQLException("Error al obtener el ID del componente insertado.");
      }

      // Insertar en tabla de subtipo
      switch (componente.getTipoComponente()) {
        case "TarjetaMadre":
          sqlSubtipo = "INSERT INTO TarjetaMadre (ComponenteID, Marca, Modelo, SocketMicro, TipoMemoriaRam, ConexionesDisco) VALUES (?, ?, ?, ?, ?, ?);";
          pstmtSubtipo = conn.prepareStatement(sqlSubtipo);
          TarjetaMadre tm = (TarjetaMadre) componente;
          pstmtSubtipo.setString(2, tm.getMarca());
          pstmtSubtipo.setString(3, tm.getModelo());
          pstmtSubtipo.setString(4, tm.getSocketMicro());
          pstmtSubtipo.setString(5, tm.getTipoMemoriaRam());
          pstmtSubtipo.setString(6, tm.getConexionesDisco());
          break;
        // Agrega los otros 'case' para Microprocesador, MemoriaRAM, DiscoDuro aquí...
        default:
          throw new IllegalArgumentException("Tipo de componente desconocido: " + componente.getTipoComponente());
      }
      pstmtSubtipo.setInt(1, componenteID);
      pstmtSubtipo.executeUpdate();

      conn.commit();
    } catch (SQLException e) {
      if (conn != null)
        conn.rollback();
      throw e;
    } finally {
      if (rs != null)
        rs.close();
      if (pstmtSubtipo != null)
        pstmtSubtipo.close();
      if (pstmtComponente != null)
        pstmtComponente.close();
      if (conn != null) {
        conn.setAutoCommit(true);
        DatabaseConnection.closeConnection(conn);
      }
    }
  }

  public void updateComponente(Componente componente) throws SQLException {
    // Implementar la lógica completa de actualización aquí, similar a addComponente
    // pero con UPDATE.
  }

  public void deleteComponente(int componenteID) throws SQLException {
    String sql = "DELETE FROM Componente WHERE ComponenteID = ?;";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, componenteID);
      pstmt.executeUpdate();
    }
  }

  // --- MÉTODOS DE LECTURA OPTIMIZADOS (Solución al problema N+1) ---

  private String buildFullComponentQuery() {
    return "SELECT c.ComponenteID, c.NumeroSerie, c.TipoComponente, c.Precio, c.CantidadDisponible, " +
        "tm.Marca AS tm_marca, tm.Modelo AS tm_modelo, tm.SocketMicro, tm.TipoMemoriaRam, tm.ConexionesDisco, " +
        "mp.Marca AS mp_marca, mp.Modelo AS mp_modelo, mp.Socket, mp.Velocidad, " +
        "mr.Marca AS mr_marca, mr.Capacidad AS mr_capacidad, mr.TipoMemoria AS mr_tipomemoria, " +
        "dd.Marca AS dd_marca, dd.Modelo AS dd_modelo, dd.Capacidad AS dd_capacidad, dd.TipoConexion AS dd_tipoconexion "
        +
        "FROM componente c " +
        "LEFT JOIN tarjetamadre tm ON c.componenteid = tm.componenteid " +
        "LEFT JOIN microprocesador mp ON c.componenteid = mp.componenteid " +
        "LEFT JOIN memoriaram mr ON c.componenteid = mr.componenteid " +
        "LEFT JOIN discoduro dd ON c.componenteid = dd.componenteid ";
  }

  private Componente mapRowToComponente(ResultSet rs) throws SQLException {
    int componenteID = rs.getInt("ComponenteID");
    String tipoComponente = rs.getString("TipoComponente");
    String numeroSerie = rs.getString("NumeroSerie");
    BigDecimal precio = rs.getBigDecimal("Precio");
    int cantidadDisponible = rs.getInt("CantidadDisponible");
    Componente componente = null;

    switch (tipoComponente) {
      case "TarjetaMadre":
        componente = new TarjetaMadre(componenteID, numeroSerie, precio, cantidadDisponible,
            rs.getString("tm_marca"), rs.getString("tm_modelo"), rs.getString("SocketMicro"),
            rs.getString("TipoMemoriaRam"), rs.getString("ConexionesDisco"));
        break;
      case "Microprocesador":
        componente = new Microprocesador(componenteID, numeroSerie, precio, cantidadDisponible,
            rs.getString("mp_marca"), rs.getString("mp_modelo"), rs.getString("Socket"),
            rs.getString("Velocidad"));
        break;
      case "MemoriaRAM":
        componente = new MemoriaRAM(componenteID, numeroSerie, precio, cantidadDisponible,
            rs.getString("mr_marca"), rs.getString("mr_capacidad"), rs.getString("mr_tipomemoria"));
        break;
      case "DiscoDuro":
        componente = new DiscoDuro(componenteID, numeroSerie, precio, cantidadDisponible,
            rs.getString("dd_marca"), rs.getString("dd_modelo"), rs.getString("dd_capacidad"),
            rs.getString("dd_tipoconexion"));
        break;
    }
    return componente;
  }

  public Componente getComponenteById(int componenteID) throws SQLException {
    String sql = buildFullComponentQuery() + " WHERE c.ComponenteID = ?;";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, componenteID);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapRowToComponente(rs);
        }
      }
    }
    return null;
  }

  public List<Componente> getAllComponentes() throws SQLException {
    List<Componente> componentes = new ArrayList<>();
    String sql = buildFullComponentQuery() + ";";
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        componentes.add(mapRowToComponente(rs));
      }
    }
    return componentes;
  }

  public List<Componente> getComponentesByType(String tipoComponente) throws SQLException {
    List<Componente> componentes = new ArrayList<>();
    String sql = buildFullComponentQuery() + " WHERE c.TipoComponente = ?;";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, tipoComponente);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          componentes.add(mapRowToComponente(rs));
        }
      }
    }
    return componentes;
  }
}