package model.dao;

import model.entity.Componente;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product (Componente) stock management operations.
 * Handles stock reduction, validation, and level checking.
 */
public class ProductoDAO {

  /**
   * Updates the stock level for a specific product by reducing the quantity.
   *
   * @param componenteID     The ID of the component/product
   * @param quantityToReduce The amount to reduce from current stock
   * @return true if update was successful, false otherwise
   * @throws SQLException if database operation fails
   */
  public boolean updateStock(int componenteID, int quantityToReduce) throws SQLException {
    String sql = "UPDATE Componente SET CantidadDisponible = CantidadDisponible - ? WHERE ComponenteID = ? AND CantidadDisponible >= ?";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, quantityToReduce);
      pstmt.setInt(2, componenteID);
      pstmt.setInt(3, quantityToReduce); // Ensure we don't go negative

      int rowsAffected = pstmt.executeUpdate();
      return rowsAffected > 0;
    }
  }

  /**
   * Gets the current stock level for a specific product.
   *
   * @param componenteID The ID of the component/product
   * @return The current stock level, or -1 if product not found
   * @throws SQLException if database operation fails
   */
  public int getStockLevel(int componenteID) throws SQLException {
    String sql = "SELECT CantidadDisponible FROM Componente WHERE ComponenteID = ?";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, componenteID);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("CantidadDisponible");
        }
      }
    }
    return -1; // Product not found
  }

  /**
   * Validates if sufficient stock is available for a purchase.
   *
   * @param componenteID     The ID of the component/product
   * @param requiredQuantity The quantity needed
   * @return true if sufficient stock is available, false otherwise
   * @throws SQLException if database operation fails
   */
  public boolean isStockAvailable(int componenteID, int requiredQuantity) throws SQLException {
    int currentStock = getStockLevel(componenteID);
    return currentStock >= requiredQuantity && currentStock > 0;
  }

  /**
   * Validates stock availability for multiple products.
   *
   * @param productQuantities List of arrays where each array contains
   *                          [componenteID, requiredQuantity]
   * @return true if all products have sufficient stock, false otherwise
   * @throws SQLException if database operation fails
   */
  public boolean validateMultipleStock(List<int[]> productQuantities) throws SQLException {
    for (int[] productQuantity : productQuantities) {
      int componenteID = productQuantity[0];
      int requiredQuantity = productQuantity[1];

      if (!isStockAvailable(componenteID, requiredQuantity)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Gets all products with their current stock levels.
   *
   * @return List of arrays where each array contains [componenteID, stockLevel]
   * @throws SQLException if database operation fails
   */
  public List<int[]> getAllStockLevels() throws SQLException {
    List<int[]> stockLevels = new ArrayList<>();
    String sql = "SELECT ComponenteID, CantidadDisponible FROM Componente ORDER BY ComponenteID";

    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        int[] stockInfo = new int[2];
        stockInfo[0] = rs.getInt("ComponenteID"); // componenteID
        stockInfo[1] = rs.getInt("CantidadDisponible"); // stock level
        stockLevels.add(stockInfo);
      }
    }
    return stockLevels;
  }

  /**
   * Gets products with low stock (below specified threshold).
   *
   * @param threshold The minimum stock level threshold
   * @return List of arrays where each array contains [componenteID, stockLevel]
   * @throws SQLException if database operation fails
   */
  public List<int[]> getLowStockProducts(int threshold) throws SQLException {
    List<int[]> lowStockProducts = new ArrayList<>();
    String sql = "SELECT ComponenteID, CantidadDisponible FROM Componente WHERE CantidadDisponible <= ? ORDER BY CantidadDisponible ASC";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, threshold);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          int[] stockInfo = new int[2];
          stockInfo[0] = rs.getInt("ComponenteID");
          stockInfo[1] = rs.getInt("CantidadDisponible");
          lowStockProducts.add(stockInfo);
        }
      }
    }
    return lowStockProducts;
  }

  /**
   * Checks if a product is out of stock.
   *
   * @param componenteID The ID of the component/product
   * @return true if product is out of stock (quantity = 0), false otherwise
   * @throws SQLException if database operation fails
   */
  public boolean isOutOfStock(int componenteID) throws SQLException {
    int stockLevel = getStockLevel(componenteID);
    return stockLevel == 0;
  }
}