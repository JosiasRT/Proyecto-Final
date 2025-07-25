package model.dao; // Asegúrate de que este paquete coincida con tu estructura

import model.entity.Combo;
import model.entity.ComboDetalle;
import model.entity.Componente; // Necesario para construir ComboDetalle con Componente
import util.DatabaseConnection; // Tu utilidad de conexión a la base de datos

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public class ComboDAO {

  private ComponenteDAO componenteDAO; // Para recuperar detalles de componentes si es necesario

  public ComboDAO() {
    this.componenteDAO = new ComponenteDAO(); // Inicializar el DAO de componentes
  }

  /**
   * Inserta un nuevo combo y sus detalles asociados en la base de datos.
   * Utiliza una transacción para asegurar la atomicidad de la operación.
   *
   * @param combo El objeto Combo a insertar (con sus detalles de componentes).
   * @return El objeto Combo insertado con su ID generado.
   * @throws SQLException Si ocurre un error de acceso a la base de datos.
   */
  public Combo insertCombo(Combo combo) throws SQLException {
    String sqlCombo = "INSERT INTO Combo (DescuentoPorciento, nombre) VALUES (?, ?) RETURNING ComboID;";
    String sqlComboDetalle = "INSERT INTO Combo_Detalle (ComboID, ComponenteID, Cantidad) VALUES (?, ?, ?);";

    Connection conn = null;
    PreparedStatement pstmtCombo = null;
    PreparedStatement pstmtDetalle = null;
    ResultSet rs = null;

    try {
      conn = DatabaseConnection.getConnection();
      conn.setAutoCommit(false); // Iniciar transacción

      // 1. Insertar en la tabla Combo
      pstmtCombo = conn.prepareStatement(sqlCombo);
      pstmtCombo.setBigDecimal(1, combo.getDescuentoPorciento());
      pstmtCombo.setString(2, combo.getNombre());
      rs = pstmtCombo.executeQuery(); // Usar executeQuery para RETURNING

      int comboID = -1;
      if (rs.next()) {
        comboID = rs.getInt(1);
        combo.setComboID(comboID); // Asignar el ID generado al objeto Combo
      } else {
        throw new SQLException("Error al obtener el ID del combo insertado.");
      }

      // 2. Insertar en la tabla Combo_Detalle para cada componente del combo
      // Asumimos que el objeto Combo tendrá una lista de ComboDetalle
      // Para esto, necesitarás añadir una lista de ComboDetalle a tu clase Combo.
      // Por ahora, el DAO esperará una lista de ComboDetalle como parámetro adicional
      // o que el Combo tenga un getter para ella.

      // Nota: Para que esta parte funcione, la clase Combo necesitará una
      // List<ComboDetalle>
      // y un getter/setter para ella. Si no la tienes, necesitarás añadirla.
      // Por simplicidad, este DAO asumirá que el Combo tiene un método getDetalles()
      // que devuelve List<ComboDetalle>.

      // Si tu clase Combo no tiene una lista de detalles, necesitarás pasarla aquí:
      // public Combo insertCombo(Combo combo, List<ComboDetalle> detalles) throws
      // SQLException {
      // ...
      // for (ComboDetalle detalle : detalles) { ... }
      // ...

      // Por ahora, asumiremos que los detalles se pasan como una lista separada
      // o que se obtienen de alguna manera. Para el ejemplo, usaremos una lista
      // vacía.
      // La lógica real de los detalles se manejará en el Service y Controller.
      // Aquí, solo se muestra cómo se insertaría si se recibieran.
      List<ComboDetalle> detalles = (List<ComboDetalle>) combo.getDetalles(); // Asumiendo que Combo tiene getDetalles()

      if (detalles != null && !detalles.isEmpty()) {
        pstmtDetalle = conn.prepareStatement(sqlComboDetalle);
        for (ComboDetalle detalle : detalles) {
          pstmtDetalle.setInt(1, comboID);
          pstmtDetalle.setInt(2, detalle.getComponenteID());
          pstmtDetalle.setInt(3, detalle.getCantidad());
          pstmtDetalle.addBatch(); // Añadir al lote para inserción eficiente
        }
        pstmtDetalle.executeBatch(); // Ejecutar todas las inserciones por lotes
      }

      conn.commit(); // Confirmar la transacción
      return combo;
    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback(); // Deshacer en caso de error
      }
      throw e;
    } finally {
      if (rs != null)
        rs.close();
      if (pstmtCombo != null)
        pstmtCombo.close();
      if (pstmtDetalle != null)
        pstmtDetalle.close();
      if (conn != null)
        conn.setAutoCommit(true); // Restaurar autocommit
      DatabaseConnection.closeConnection(conn);
    }
  }

  /**
   * Actualiza un combo existente y sus detalles.
   * Esto implica eliminar los detalles antiguos y reinsertar los nuevos.
   *
   * @param combo          El objeto Combo a actualizar.
   * @param nuevosDetalles La nueva lista de ComboDetalle para el combo.
   * @throws SQLException Si ocurre un error de acceso a la base de datos.
   */
  public void updateCombo(Combo combo, List<ComboDetalle> nuevosDetalles) throws SQLException {
    String sqlUpdateCombo = "UPDATE Combo SET DescuentoPorciento = ?, nombre = ? WHERE ComboID = ?;";
    String sqlDeleteDetalles = "DELETE FROM Combo_Detalle WHERE ComboID = ?;";
    String sqlInsertDetalle = "INSERT INTO Combo_Detalle (ComboID, ComponenteID, Cantidad) VALUES (?, ?, ?);";

    Connection conn = null;
    PreparedStatement pstmtUpdateCombo = null;
    PreparedStatement pstmtDeleteDetalles = null;
    PreparedStatement pstmtInsertDetalle = null;

    try {
      conn = DatabaseConnection.getConnection();
      conn.setAutoCommit(false); // Iniciar transacción

      // 1. Actualizar la tabla Combo
      pstmtUpdateCombo = conn.prepareStatement(sqlUpdateCombo);
      pstmtUpdateCombo.setBigDecimal(1, combo.getDescuentoPorciento());
      pstmtUpdateCombo.setString(2, combo.getNombre());
      pstmtUpdateCombo.setInt(3, combo.getComboID());
      pstmtUpdateCombo.executeUpdate();

      // 2. Eliminar los detalles existentes del combo
      pstmtDeleteDetalles = conn.prepareStatement(sqlDeleteDetalles);
      pstmtDeleteDetalles.setInt(1, combo.getComboID());
      pstmtDeleteDetalles.executeUpdate();

      // 3. Insertar los nuevos detalles del combo
      if (nuevosDetalles != null && !nuevosDetalles.isEmpty()) {
        pstmtInsertDetalle = conn.prepareStatement(sqlInsertDetalle);
        for (ComboDetalle detalle : nuevosDetalles) {
          pstmtInsertDetalle.setInt(1, combo.getComboID());
          pstmtInsertDetalle.setInt(2, detalle.getComponenteID());
          pstmtInsertDetalle.setInt(3, detalle.getCantidad());
          pstmtInsertDetalle.addBatch();
        }
        pstmtInsertDetalle.executeBatch();
      }

      conn.commit(); // Confirmar la transacción
    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback(); // Deshacer en caso de error
      }
      throw e;
    } finally {
      if (pstmtUpdateCombo != null)
        pstmtUpdateCombo.close();
      if (pstmtDeleteDetalles != null)
        pstmtDeleteDetalles.close();
      if (pstmtInsertDetalle != null)
        pstmtInsertDetalle.close();
      if (conn != null)
        conn.setAutoCommit(true); // Restaurar autocommit
      DatabaseConnection.closeConnection(conn);
    }
  }

  /**
   * Elimina un combo y sus detalles asociados de la base de datos.
   *
   * @param comboID El ID del combo a eliminar.
   * @throws SQLException Si ocurre un error de acceso a la base de datos.
   */
  public void deleteCombo(int comboID) throws SQLException {
    // Gracias a ON DELETE CASCADE en Combo_Detalle, solo necesitamos eliminar de
    // Combo.
    String sql = "DELETE FROM Combo WHERE ComboID = ?;";

    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
      conn = DatabaseConnection.getConnection();
      pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, comboID);
      pstmt.executeUpdate();
    } finally {
      if (pstmt != null)
        pstmt.close();
      DatabaseConnection.closeConnection(conn);
    }
  }

  /**
   * Obtiene un combo por su ID, incluyendo sus detalles de componentes.
   *
   * @param comboID El ID del combo a buscar.
   * @return El objeto Combo con sus detalles, o null si no se encuentra.
   * @throws SQLException Si ocurre un error de acceso a la base de datos.
   */
  public Combo getComboById(int comboID) throws SQLException {
    String sqlCombo = "SELECT ComboID, DescuentoPorciento, nombre FROM Combo WHERE ComboID = ?;";
    String sqlDetalles = "SELECT cd.ComponenteID, cd.Cantidad FROM Combo_Detalle cd WHERE cd.ComboID = ?;";

    Connection conn = null;
    PreparedStatement pstmtCombo = null;
    PreparedStatement pstmtDetalles = null;
    ResultSet rsCombo = null;
    ResultSet rsDetalles = null;
    Combo combo = null;
    List<ComboDetalle> detalles = new ArrayList<>();

    try {
      conn = DatabaseConnection.getConnection();

      // 1. Obtener los datos del combo principal
      pstmtCombo = conn.prepareStatement(sqlCombo);
      pstmtCombo.setInt(1, comboID);
      rsCombo = pstmtCombo.executeQuery();

      if (rsCombo.next()) {
        combo = new Combo(
            rsCombo.getInt("ComboID"),
            rsCombo.getBigDecimal("DescuentoPorciento"));
        combo.setNombre(rsCombo.getString("nombre"));

        // 2. Obtener los detalles de componentes para este combo
        pstmtDetalles = conn.prepareStatement(sqlDetalles);
        pstmtDetalles.setInt(1, comboID);
        rsDetalles = pstmtDetalles.executeQuery();

        while (rsDetalles.next()) {
          int componenteID = rsDetalles.getInt("ComponenteID");
          int cantidad = rsDetalles.getInt("Cantidad");

          // Opcional: Recuperar el objeto Componente completo para el detalle
          Componente componente = componenteDAO.getComponenteById(componenteID); // Usar ComponenteDAO
          detalles.add(new ComboDetalle(comboID, componenteID, cantidad, componente));
        }
        // Asignar los detalles al combo. Esto requiere un setter en la clase Combo.
        combo.setDetalles(detalles); // Necesitarás añadir este setter en Combo.java
      }
    } finally {
      if (rsCombo != null)
        rsCombo.close();
      if (rsDetalles != null)
        rsDetalles.close();
      if (pstmtCombo != null)
        pstmtCombo.close();
      if (pstmtDetalles != null)
        pstmtDetalles.close();
      DatabaseConnection.closeConnection(conn);
    }
    return combo;
  }

  /**
   * Obtiene todos los combos, incluyendo sus detalles de componentes.
   *
   * @return Una lista de objetos Combo, cada uno con su lista de ComboDetalle.
   * @throws SQLException Si ocurre un error de acceso a la base de datos.
   */
  public List<Combo> getAllCombos() throws SQLException {
    List<Combo> combos = new ArrayList<>();
    String sqlCombos = "SELECT ComboID, DescuentoPorciento, nombre FROM Combo;";
    String sqlDetalles = "SELECT cd.ComboID, cd.ComponenteID, cd.Cantidad FROM Combo_Detalle cd;";

    Connection conn = null;
    Statement stmtCombos = null;
    PreparedStatement pstmtDetalles = null; // Usar PreparedStatement para detalles
    ResultSet rsCombos = null;
    ResultSet rsDetalles = null;

    try {
      conn = DatabaseConnection.getConnection();
      stmtCombos = conn.createStatement();
      rsCombos = stmtCombos.executeQuery(sqlCombos);

      // Mapa para almacenar combos y sus detalles
      // Map<ComboID, Combo>
      Map<Integer, Combo> comboMap = new HashMap<>();

      while (rsCombos.next()) {
        int comboID = rsCombos.getInt("ComboID");
        BigDecimal descuento = rsCombos.getBigDecimal("DescuentoPorciento");
        Combo combo = new Combo(comboID, descuento);
        combo.setNombre(rsCombos.getString("nombre"));
        comboMap.put(comboID, combo);
        combos.add(combo); // Añadir a la lista final
      }

      // Ahora, obtener todos los detalles y asignarlos a los combos correspondientes
      pstmtDetalles = conn.prepareStatement(sqlDetalles);
      rsDetalles = pstmtDetalles.executeQuery();

      while (rsDetalles.next()) {
        int comboID = rsDetalles.getInt("ComboID");
        int componenteID = rsDetalles.getInt("ComponenteID");
        int cantidad = rsDetalles.getInt("Cantidad");

        Combo combo = comboMap.get(comboID);
        if (combo != null) {
          Componente componente = componenteDAO.getComponenteById(componenteID); // Obtener Componente completo
          ComboDetalle detalle = new ComboDetalle(comboID, componenteID, cantidad, componente);
          // Añadir el detalle al combo. Esto requiere que Combo tenga un método
          // addDetalle(ComboDetalle)
          // o que su setter de detalles permita añadir a una lista existente.
          combo.addDetalle(detalle); // Necesitarás añadir este método en Combo.java
        }
      }

    } finally {
      if (rsCombos != null)
        rsCombos.close();
      if (rsDetalles != null)
        rsDetalles.close();
      if (stmtCombos != null)
        stmtCombos.close();
      if (pstmtDetalles != null)
        pstmtDetalles.close();
      DatabaseConnection.closeConnection(conn);
    }
    return combos;
  }
}
