package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DatabaseConnection;

public class ClienteDAO {

  public void insert(Cliente cliente) throws SQLException {
    String sql = "INSERT INTO Cliente (Nombre, Apellido, Email, Telefono) VALUES (?, ?, ?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, cliente.getNombre());
      stmt.setString(2, cliente.getApellido());
      stmt.setString(3, cliente.getEmail());
      stmt.setString(4, cliente.getTelefono());
      stmt.executeUpdate();

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          cliente.setClienteID(generatedKeys.getInt(1));
        }
      }
    }
  }

  public boolean update(Cliente cliente) throws SQLException {
    String sql = "UPDATE Cliente SET Nombre = ?, Apellido = ?, Email = ?, Telefono = ? WHERE ClienteID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, cliente.getNombre());
      stmt.setString(2, cliente.getApellido());
      stmt.setString(3, cliente.getEmail());
      stmt.setString(4, cliente.getTelefono());
      stmt.setInt(5, cliente.getClienteID());
      return stmt.executeUpdate() > 0;
    }
  }

  public boolean delete(int clienteId) throws SQLException {
    String sql = "DELETE FROM Cliente WHERE ClienteID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, clienteId);
      return stmt.executeUpdate() > 0;
    }
  }

  public Cliente findById(int clienteId) throws SQLException {
    String sql = "SELECT ClienteID, Nombre, Apellido, Email, Telefono FROM Cliente WHERE ClienteID = ?";
    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, clienteId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return new Cliente(
              rs.getInt("ClienteID"),
              rs.getString("Nombre"),
              rs.getString("Apellido"),
              rs.getString("Email"),
              rs.getString("Telefono"));
        }
      }
    }
    return null;
  }

  public List<Cliente> findAll() throws SQLException {
    List<Cliente> clientes = new ArrayList<>();
    String sql = "SELECT ClienteID, Nombre, Apellido, Email, Telefono FROM Cliente";
    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      while (rs.next()) {
        clientes.add(new Cliente(
            rs.getInt("ClienteID"),
            rs.getString("Nombre"),
            rs.getString("Apellido"),
            rs.getString("Email"),
            rs.getString("Telefono")));
      }
    }
    return clientes;
  }
}
