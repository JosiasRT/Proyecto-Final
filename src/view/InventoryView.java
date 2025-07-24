package view; // Asegúrate de que este paquete coincida con la ubicación de tus vistas

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;

public class InventoryView extends JFrame {
  private JTable componenteTable;
  private DefaultTableModel tableModel;
  private JComboBox<String> filterComboBox;
  private JButton btnCrear;
  private JButton btnEditar;
  private JButton btnEliminar;
  private JButton btnVolver; // Botón para volver al Home

  // Columnas para la tabla de componentes
  private final String[] COLUMN_NAMES = {
      "ID", "Número Serie", "Tipo", "Marca", "Modelo", "Precio", "Cantidad", "Detalles Específicos"
  };

  public InventoryView() {
    setTitle("Gestión de Inventario - Componentes");
    setSize(1000, 700); // Tamaño más grande para más columnas
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana
    setLocationRelativeTo(null); // Centrar la ventana

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // --- Panel Superior: Filtro y Botón Crear ---
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    topPanel.setBorder(BorderFactory.createTitledBorder("Filtro y Acciones"));

    // ComboBox para filtrar por tipo de componente
    String[] tiposComponente = { "Todos", "TarjetaMadre", "Microprocesador", "MemoriaRAM", "DiscoDuro" };
    filterComboBox = new JComboBox<>(tiposComponente);
    topPanel.add(new JLabel("Filtrar por Tipo:"));
    topPanel.add(filterComboBox);

    btnCrear = new JButton("Crear Nuevo Componente");
    topPanel.add(btnCrear);

    mainPanel.add(topPanel, BorderLayout.NORTH);

    // --- Panel Central: Tabla de Componentes ---
    tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // Hacer que las celdas de la tabla no sean editables
      }
    };
    componenteTable = new JTable(tableModel);
    componenteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionable
    JScrollPane scrollPane = new JScrollPane(componenteTable);
    mainPanel.add(scrollPane, BorderLayout.CENTER);

    // Listener para seleccionar fila y (futuramente) cargar datos para edición
    componenteTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // Lógica para seleccionar fila (el controlador la usará para editar/eliminar)
        // Por ahora, solo asegura que la fila esté seleccionada
      }
    });

    // --- Panel Inferior: Botones de Edición, Eliminación y Volver ---
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    btnEditar = new JButton("Editar Componente");
    btnEliminar = new JButton("Eliminar Componente");
    btnVolver = new JButton("Volver al Inicio");

    bottomPanel.add(btnEditar);
    bottomPanel.add(btnEliminar);
    bottomPanel.add(btnVolver);

    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    add(mainPanel);
  }

  // --- Métodos para que el Controlador interactúe con la Vista ---

  /**
   * Establece los datos en la tabla de componentes.
   *
   * @param data Matriz de objetos con los datos de los componentes.
   */
  public void setTableData(Object[][] data) {
    tableModel.setRowCount(0); // Limpiar tabla
    for (Object[] row : data) {
      tableModel.addRow(row);
    }
  }

  /**
   * Obtiene el ID del componente seleccionado en la tabla.
   *
   * @return El ID del componente seleccionado, o -1 si no hay ninguno.
   */
  public int getSelectedComponenteId() {
    int selectedRow = componenteTable.getSelectedRow();
    if (selectedRow != -1) {
      return (int) tableModel.getValueAt(selectedRow, 0); // La columna ID es la primera (índice 0)
    }
    return -1;
  }

  /**
   * Obtiene el tipo del componente seleccionado en la tabla.
   *
   * @return El tipo del componente seleccionado, o null si no hay ninguno.
   */
  public String getSelectedComponenteType() {
    int selectedRow = componenteTable.getSelectedRow();
    if (selectedRow != -1) {
      return (String) tableModel.getValueAt(selectedRow, 2); // La columna Tipo es la tercera (índice 2)
    }
    return null;
  }

  /**
   * Obtiene el tipo de componente seleccionado en el JComboBox de filtro.
   *
   * @return El tipo de componente seleccionado como String.
   */
  public String getSelectedFilterType() {
    return (String) filterComboBox.getSelectedItem();
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

  public void addFilterComboBoxListener(ActionListener listener) {
    filterComboBox.addActionListener(listener);
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

  public JComboBox<String> getFilterComboBox() {
    return filterComboBox;
  }

  // Métodos para mostrar/ocultar la vista (consistente con HomeView y
  // ClienteView)
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
