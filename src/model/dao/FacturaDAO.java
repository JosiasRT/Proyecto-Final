package model.dao;

import model.entity.Factura;
import model.entity.Cliente;
import model.entity.Combo;
import model.entity.Componente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DatabaseConnection;

public class FacturaDAO {
    public void insertFactura(Factura factura) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Factura (facturaid, clienteid, fechapedido, comboid, preciototal) VALUES (?, ?, ?, ?, ?);";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, factura.getIdFactura());
            pstmt.setInt(2, factura.getCliente().getClienteID());
            pstmt.setDate(3, factura.getFechaPedido());
            pstmt.setInt(4, factura.getComboVendido() != null ? factura.getComboVendido().getComboID() : null);
            pstmt.setFloat(5, factura.getPrecioTotal());
            pstmt.executeUpdate();
            // Aquí podrías insertar los componentes vendidos en una tabla intermedia si
            // aplica
        } catch (SQLException e) {
            // Handle schema mismatch errors with meaningful messages
            if (e.getMessage().contains("column") || e.getMessage().contains("table")) {
                System.err.println(
                        "ERROR: Database schema mismatch in insertFactura. Expected columns: facturaid, clienteid, fechapedido, comboid, preciototal");
                System.err.println("SQL Error: " + e.getMessage());
                throw new SQLException(
                        "Database schema error: The Factura table structure does not match expected format. " +
                                "Expected columns: facturaid, clienteid, fechapedido, comboid, preciototal. Error: "
                                + e.getMessage(),
                        e);
            } else if (e.getMessage().contains("constraint") || e.getMessage().contains("foreign key")) {
                System.err.println("ERROR: Data integrity constraint violation in insertFactura: " + e.getMessage());
                throw new SQLException("Data integrity error: Unable to insert factura due to constraint violation. " +
                        "Please verify that cliente and combo references are valid. Error: " + e.getMessage(), e);
            } else {
                System.err.println("ERROR: Unexpected database error in insertFactura: " + e.getMessage());
                throw new SQLException("Database operation failed: Unable to insert factura. Error: " + e.getMessage(),
                        e);
            }
        } finally {
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                conn.close();
        }
    }

    public List<Factura> getAllFacturas() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM Factura;";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                try {
                    // Aquí deberías mapear los datos de la factura y sus relaciones
                    Factura factura = new Factura(
                            rs.getString("facturaid"),
                            null, // Cliente real pendiente
                            rs.getFloat("preciototal"),
                            null, // Combo real pendiente
                            rs.getDate("fechapedido"));
                    facturas.add(factura);
                } catch (SQLException columnError) {
                    // Handle missing column errors gracefully
                    System.err.println(
                            "WARNING: Error reading factura row, skipping. Expected columns: facturaid, clienteid, fechapedido, comboid, preciototal");
                    System.err.println("Column error: " + columnError.getMessage());
                    // Continue processing other rows instead of failing completely
                }
            }
        } catch (SQLException e) {
            // Handle schema mismatch errors with meaningful messages
            if (e.getMessage().contains("table") && e.getMessage().contains("exist")) {
                System.err.println("ERROR: Factura table does not exist in database");
                throw new SQLException(
                        "Database schema error: Factura table not found. Please ensure the database is properly initialized.",
                        e);
            } else if (e.getMessage().contains("column")) {
                System.err.println(
                        "ERROR: Database schema mismatch in getAllFacturas. Expected columns: facturaid, clienteid, fechapedido, comboid, preciototal");
                System.err.println("SQL Error: " + e.getMessage());
                throw new SQLException(
                        "Database schema error: The Factura table structure does not match expected format. " +
                                "Expected columns: facturaid, clienteid, fechapedido, comboid, preciototal. Error: "
                                + e.getMessage(),
                        e);
            } else {
                System.err.println("ERROR: Unexpected database error in getAllFacturas: " + e.getMessage());
                throw new SQLException(
                        "Database operation failed: Unable to retrieve facturas. Error: " + e.getMessage(), e);
            }
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        }
        return facturas;
    }

    public boolean facturaExists(String facturaId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM Factura WHERE facturaid = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, facturaId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to check if factura exists: " + e.getMessage());
            throw new SQLException(
                    "Database operation failed: Unable to verify factura existence. Error: " + e.getMessage(), e);
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                conn.close();
        }
        return false;
    }
}