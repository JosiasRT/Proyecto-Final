package view; // Asegúrate de que este paquete coincida con la ubicación de tus vistas

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;

public class ComboView extends JFrame {
  private JTable comboTable;
  private DefaultTableModel tableModel;
  private JButton btnCrear;
  private JButton btnEditar;
  private JButton btnEliminar;
  private JButton btnVolver; // Botón para volver al Home

  // Columnas para la tabla de combos
  private final String[] COLUMN_NAMES = {
      "ID Combo", "Descuento (%)", "Componentes Incluidos", "Precio Total Estimado"
  };

  public ComboView() {
    setTitle("Gestión de Combos");
    setSize(900, 600); // Tamaño ajustado para la información de combos
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana
    setLocationRelativeTo(null); // Centrar la ventana

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // --- Panel Superior: Botones de Acción ---
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    topPanel.setBorder(BorderFactory.createTitledBorder("Acciones de Combo"));

    btnCrear = new JButton("Crear Nuevo Combo");
    topPanel.add(btnCrear);

    btnEditar = new JButton("Editar Combo");
    topPanel.add(btnEditar);

    btnEliminar = new JButton("Eliminar Combo");
    topPanel.add(btnEliminar);

    mainPanel.add(topPanel, BorderLayout.NORTH);

    // --- Panel Central: Tabla de Combos ---
    tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // Hacer que las celdas de la tabla no sean editables
      }
    };
    comboTable = new JTable(tableModel);
    comboTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionable
    JScrollPane scrollPane = new JScrollPane(comboTable);
    mainPanel.add(scrollPane, BorderLayout.CENTER);

    // Listener para seleccionar fila (el controlador la usará para editar/eliminar)
    comboTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // Lógica para seleccionar fila (el controlador la usará para editar/eliminar)
        // Por ahora, solo asegura que la fila esté seleccionada
      }
    });

    // --- Panel Inferior: Botón Volver ---
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    btnVolver = new JButton("Volver al Inicio");
    bottomPanel.add(btnVolver);

    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    add(mainPanel);
  }

  // --- Métodos para que el Controlador interactúe con la Vista ---

  /**
   * Establece los datos en la tabla de combos.
   *
   * @param data Matriz de objetos con los datos de los combos.
   */
  public void setTableData(Object[][] data) {
    tableModel.setRowCount(0); // Limpiar tabla
    for (Object[] row : data) {
      tableModel.addRow(row);
    }
  }

  /**
   * Obtiene el ID del combo seleccionado en la tabla.
   *
   * @return El ID del combo seleccionado, o -1 si no hay ninguno.
   */
  public int getSelectedComboId() {
    int selectedRow = comboTable.getSelectedRow();
    if (selectedRow != -1) {
      return (int) tableModel.getValueAt(selectedRow, 0); // La columna ID es la primera (índice 0)
    }
    return -1;
  }

  /**
   * Muestra un mensaje al usuario.
   *
   * @param message     Mensaje a mostrar.
   * @param title       Título del cuadro de diálogo.
   * @param messageType Tipo de mensaje (JOptionPane.INFORMATION_MESSAGE,
   *                    ERROR_MESSAGE, etc.).
   */
  public void showMessage(String message, String title, int messageType) {
    JOptionPane.showMessageDialog(this, message, title, messageType);
  }

  // --- Métodos para que el Controlador añada ActionListeners ---
  public void addCrearButtonListener(ActionListener listener) {
    btnCrear.addActionListener(listener);
  }

  public void addEditarButtonListener(ActionListener listener) {
    btnEditar.addActionListener(listener);
  }

  public void addEliminarButtonListener(ActionListener listener) {
    btnEliminar.addActionListener(listener);
  }

  public void addVolverButtonListener(ActionListener listener) {
    btnVolver.addActionListener(listener);
  }

  // --- Getters para los botones (para que el controlador los identifique) ---
  public JButton getBtnCrear() {
    return btnCrear;
  }

  public JButton getBtnEditar() {
    return btnEditar;
  }

  public JButton getBtnEliminar() {
    return btnEliminar;
  }

  public JButton getBtnVolver() {
    return btnVolver;
  }

  // Métodos para mostrar/ocultar la vista (consistente con HomeView, ClienteView,
  // InventoryView)
  public void showView() {
    setVisible(true);
  }

  public void hideView() {
    setVisible(false);
  }

  // Método para añadir un WindowListener (para volver al Home al cerrar)
  public void addWindowCloseListener(WindowListener listener) {
    this.addWindowListener(listener);
  }
}
