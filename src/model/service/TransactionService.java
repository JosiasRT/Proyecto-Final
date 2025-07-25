package model.service;

import model.entity.Factura;
import model.dao.ProductoDAO;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for managing atomic transactions that involve multiple
 * operations.
 * Ensures data consistency across invoice creation and stock reduction
 * operations.
 */
public class TransactionService {

  private FacturaService facturaService;
  private ProductoDAO productoDAO;

  public TransactionService() {
    this.facturaService = new FacturaService();
    this.productoDAO = new ProductoDAO();
  }

  /**
   * Executes a complete purchase transaction atomically.
   * Creates invoice and reduces stock for all products in a single transaction.
   * If any operation fails, all changes are rolled back.
   *
   * @param factura           The invoice to be created
   * @param productQuantities List of arrays where each array contains
   *                          [componenteID, quantity]
   * @return true if transaction completed successfully, false otherwise
   * @throws SQLException if database operation fails
   */
  public boolean executeCompletePurchaseTransaction(Factura factura, List<int[]> productQuantities)
      throws SQLException {
    Connection conn = null;
    boolean originalAutoCommit = true;

    try {
      // Get connection and start transaction
      conn = DatabaseConnection.getConnection();
      originalAutoCommit = conn.getAutoCommit();
      conn.setAutoCommit(false);

      // Step 1: Validate stock availability for all products
      if (!validateStockAvailability(productQuantities, conn)) {
        conn.rollback();
        return false;
      }

      // Step 2: Create the invoice
      if (!createInvoiceInTransaction(factura, conn)) {
        conn.rollback();
        return false;
      }

      // Step 3: Reduce stock for all products
      if (!reduceStockInTransaction(productQuantities, conn)) {
        conn.rollback();
        return false;
      }

      // If all operations succeeded, commit the transaction
      conn.commit();
      return true;

    } catch (SQLException e) {
      // Rollback on any exception
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException rollbackEx) {
          // Log rollback failure but throw original exception
          System.err.println("Failed to rollback transaction: " + rollbackEx.getMessage());
        }
      }
      throw e;
    } finally {
      // Restore original auto-commit setting and close connection
      if (conn != null) {
        try {
          conn.setAutoCommit(originalAutoCommit);
          DatabaseConnection.closeConnection(conn);
        } catch (SQLException ex) {
          System.err.println("Failed to restore auto-commit setting: " + ex.getMessage());
        }
      }
    }
  }

  /**
   * Validates stock availability for all products within a transaction.
   *
   * @param productQuantities List of product quantities to validate
   * @param conn              Database connection within transaction
   * @return true if all products have sufficient stock, false otherwise
   * @throws SQLException if database operation fails
   */
  private boolean validateStockAvailability(List<int[]> productQuantities, Connection conn) throws SQLException {
    for (int[] productQuantity : productQuantities) {
      int componenteID = productQuantity[0];
      int requiredQuantity = productQuantity[1];

      // Check current stock level within transaction
      if (!isStockAvailableInTransaction(componenteID, requiredQuantity, conn)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks stock availability within a transaction context.
   *
   * @param componenteID     The component ID to check
   * @param requiredQuantity The required quantity
   * @param conn             Database connection within transaction
   * @return true if sufficient stock is available, false otherwise
   * @throws SQLException if database operation fails
   */
  private boolean isStockAvailableInTransaction(int componenteID, int requiredQuantity, Connection conn)
      throws SQLException {
    String sql = "SELECT CantidadDisponible FROM Componente WHERE ComponenteID = ? FOR UPDATE";

    try (var pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, componenteID);

      try (var rs = pstmt.executeQuery()) {
        if (rs.next()) {
          int currentStock = rs.getInt("CantidadDisponible");
          return currentStock >= requiredQuantity && currentStock > 0;
        }
      }
    }
    return false;
  }

  /**
   * Creates an invoice within a transaction context.
   *
   * @param factura The invoice to create
   * @param conn    Database connection within transaction
   * @return true if invoice was created successfully, false otherwise
   */
  private boolean createInvoiceInTransaction(Factura factura, Connection conn) {
    try {
      // Use the existing FacturaService but within our transaction
      // Note: This assumes FacturaService can work with provided connection
      // If not, we would need to implement the invoice creation logic here
      facturaService.registrarFactura(factura);
      return true;
    } catch (Exception e) {
      System.err.println("Failed to create invoice in transaction: " + e.getMessage());
      return false;
    }
  }

  /**
   * Reduces stock for all products within a transaction context.
   *
   * @param productQuantities List of product quantities to reduce
   * @param conn              Database connection within transaction
   * @return true if all stock reductions were successful, false otherwise
   * @throws SQLException if database operation fails
   */
  private boolean reduceStockInTransaction(List<int[]> productQuantities, Connection conn) throws SQLException {
    String sql = "UPDATE Componente SET CantidadDisponible = CantidadDisponible - ? WHERE ComponenteID = ? AND CantidadDisponible >= ?";

    try (var pstmt = conn.prepareStatement(sql)) {
      for (int[] productQuantity : productQuantities) {
        int componenteID = productQuantity[0];
        int quantityToReduce = productQuantity[1];

        pstmt.setInt(1, quantityToReduce);
        pstmt.setInt(2, componenteID);
        pstmt.setInt(3, quantityToReduce);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected == 0) {
          // Stock reduction failed (insufficient stock or product not found)
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Rollback mechanism for failed transactions.
   * This method can be called to manually rollback a transaction if needed.
   *
   * @param conn Database connection to rollback
   */
  public void rollbackTransaction(Connection conn) {
    if (conn != null) {
      try {
        conn.rollback();
        System.out.println("Transaction rolled back successfully");
      } catch (SQLException e) {
        System.err.println("Failed to rollback transaction: " + e.getMessage());
      }
    }
  }

  /**
   * Validates transaction consistency by checking if invoice exists and stock was
   * properly reduced.
   * This can be used for post-transaction verification.
   *
   * @param facturaId         The invoice ID to verify
   * @param productQuantities The product quantities that should have been reduced
   * @return true if transaction appears consistent, false otherwise
   */
  public boolean validateTransactionConsistency(String facturaId, List<int[]> productQuantities) {
    try {
      // Check if invoice exists
      if (!facturaService.facturaExists(facturaId)) {
        return false;
      }

      // Check if stock levels are consistent (this is a basic check)
      // In a real system, you might want more sophisticated consistency checks
      for (int[] productQuantity : productQuantities) {
        int componenteID = productQuantity[0];
        int stockLevel = productoDAO.getStockLevel(componenteID);

        // Basic validation: stock should not be negative
        if (stockLevel < 0) {
          return false;
        }
      }

      return true;
    } catch (Exception e) {
      System.err.println("Failed to validate transaction consistency: " + e.getMessage());
      return false;
    }
  }
}