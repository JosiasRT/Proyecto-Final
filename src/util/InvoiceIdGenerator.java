package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating unique invoice IDs with validation
 */
public class InvoiceIdGenerator {

  private static final String ID_PREFIX = "INV";
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final int MAX_RETRY_ATTEMPTS = 10;

  /**
   * Generates a unique invoice ID with format: INV-YYYYMMDD-HHMMSS-RRR
   * Where RRR is a random 3-digit number for uniqueness
   *
   * @return A unique invoice ID
   * @throws SQLException if unable to validate uniqueness after max attempts
   */
  public static String generateUniqueInvoiceId() throws SQLException {
    for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
      String invoiceId = generateInvoiceId();
      if (!invoiceIdExists(invoiceId)) {
        return invoiceId;
      }
    }
    throw new SQLException("Unable to generate unique invoice ID after " + MAX_RETRY_ATTEMPTS + " attempts");
  }

  /**
   * Generates an invoice ID with current timestamp and random suffix
   *
   * @return Generated invoice ID
   */
  private static String generateInvoiceId() {
    LocalDateTime now = LocalDateTime.now();
    String datePart = now.format(DATE_FORMAT);
    String timePart = String.format("%02d%02d%02d",
        now.getHour(), now.getMinute(), now.getSecond());
    int randomSuffix = ThreadLocalRandom.current().nextInt(100, 1000);

    return String.format("%s-%s-%s-%03d", ID_PREFIX, datePart, timePart, randomSuffix);
  }

  /**
   * Validates that an invoice ID is properly formatted
   *
   * @param invoiceId The invoice ID to validate
   * @return true if the format is valid
   */
  public static boolean isValidInvoiceIdFormat(String invoiceId) {
    if (invoiceId == null || invoiceId.trim().isEmpty()) {
      return false;
    }

    // Check format: INV-YYYYMMDD-HHMMSS-RRR
    String pattern = "^INV-\\d{8}-\\d{6}-\\d{3}$";
    return invoiceId.matches(pattern);
  }

  /**
   * Checks if an invoice ID already exists in the database
   *
   * @param invoiceId The invoice ID to check
   * @return true if the invoice ID exists
   * @throws SQLException if database error occurs
   */
  private static boolean invoiceIdExists(String invoiceId) throws SQLException {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      conn = DatabaseConnection.getConnection();
      String sql = "SELECT COUNT(*) FROM Factura WHERE facturaid = ?";
      pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, invoiceId);
      rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getInt(1) > 0;
      }
      return false;

    } catch (SQLException e) {
      System.err.println("ERROR: Unable to check invoice ID existence: " + e.getMessage());
      throw new SQLException("Database error while validating invoice ID uniqueness: " + e.getMessage(), e);
    } finally {
      if (rs != null)
        rs.close();
      if (pstmt != null)
        pstmt.close();
      if (conn != null)
        conn.close();
    }
  }

  /**
   * Validates that an invoice ID doesn't already exist in the database
   *
   * @param invoiceId The invoice ID to validate
   * @throws SQLException if the invoice ID already exists or database error
   *                      occurs
   */
  public static void validateInvoiceIdUniqueness(String invoiceId) throws SQLException {
    if (invoiceIdExists(invoiceId)) {
      throw new SQLException("Invoice ID already exists: " + invoiceId);
    }
  }
}