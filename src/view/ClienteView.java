package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener; // Importar WindowListener

public class ClienteView extends JFrame {
  private JTextField txtId;
  private JTextField txtNombre;
  private JTextField txtApellido;
  private JTextField txtEmail;
  private JTextField txtTelefono;
  private JButton btnAgregar;
  private JButton btnActualizar;
  private JButton btnEliminar;
  private JButton btnLimpiar;
  private JButton btnVolver; // Nuevo botón para volver
  private JTable clienteTable;
  private DefaultTableModel tableModel;

  public ClienteView() {
    setTitle("Gestión de Clientes - MVC");
    setSize(800, 600);
    // CAMBIO CLAVE: No salir de la aplicación al cerrar esta ventana
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana
    setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
    inputPanel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

    txtId = new JTextField();
    txtId.setEditable(false);
    inputPanel.add(new JLabel("ID Cliente:"));
    inputPanel.add(txtId);

    txtNombre = new JTextField();
    inputPanel.add(new JLabel("Nombre:"));
    inputPanel.add(txtNombre);

    txtApellido = new JTextField();
    inputPanel.add(new JLabel("Apellido:"));
    inputPanel.add(txtApellido);

    txtEmail = new JTextField();
    inputPanel.add(new JLabel("Email:"));
    inputPanel.add(txtEmail);

    txtTelefono = new JTextField();
    inputPanel.add(new JLabel("Teléfono:"));
    inputPanel.add(txtTelefono);

    mainPanel.add(inputPanel, BorderLayout.NORTH);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    btnAgregar = new JButton("Agregar");
    btnActualizar = new JButton("Actualizar");
    btnEliminar = new JButton("Eliminar");
    btnLimpiar = new JButton("Limpiar Campos");
    btnVolver = new JButton("Volver al Inicio"); // Inicializar el nuevo botón

    buttonPanel.add(btnAgregar);
    buttonPanel.add(btnActualizar);
    buttonPanel.add(btnEliminar);
    buttonPanel.add(btnLimpiar);
    buttonPanel.add(btnVolver); // Añadir el nuevo botón

    String[] columnNames = { "ID", "Nombre", "Apellido", "Email", "Teléfono" };
    tableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    clienteTable = new JTable(tableModel);
    clienteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(clienteTable);

    clienteTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int selectedRow = clienteTable.getSelectedRow();
        if (selectedRow != -1) {
          txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
          txtNombre.setText(tableModel.getValueAt(selectedRow, 1).toString());
          txtApellido.setText(tableModel.getValueAt(selectedRow, 2).toString());
          txtEmail.setText(tableModel.getValueAt(selectedRow, 3).toString());
          txtTelefono.setText(tableModel.getValueAt(selectedRow, 4).toString());
        }
      }
    });

    JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
    centerPanel.add(buttonPanel, BorderLayout.NORTH);
    centerPanel.add(scrollPane, BorderLayout.CENTER);

    mainPanel.add(centerPanel, BorderLayout.CENTER);

    add(mainPanel);
  }

  // --- Métodos para obtener datos de la Vista ---
  public int getClienteIdInput() {
    try {
      return Integer.parseInt(txtId.getText());
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  public String getNombreInput() {
    return txtNombre.getText().trim();
  }

  public String getApellidoInput() {
    return txtApellido.getText().trim();
  }

  public String getEmailInput() {
    return txtEmail.getText().trim();
  }

  public String getTelefonoInput() {
    return txtTelefono.getText().trim();
  }

  // --- Métodos para que el Controlador actualice la Vista ---
  public void setTableData(Object[][] data) {
    tableModel.setRowCount(0);
    for (Object[] row : data) {
      tableModel.addRow(row);
    }
  }

  public void clearFields() {
    txtId.setText("");
    txtNombre.setText("");
    txtApellido.setText("");
    txtEmail.setText("");
    txtTelefono.setText("");
  }

  public void showMessage(String message, String title, int messageType) {
    JOptionPane.showMessageDialog(this, message, title, messageType);
  }

  // --- Métodos para que el Controlador añada ActionListeners ---
  public void addAgregarListener(ActionListener listener) {
    btnAgregar.addActionListener(listener);
  }

  public void addActualizarListener(ActionListener listener) {
    btnActualizar.addActionListener(listener);
  }

  public void addEliminarListener(ActionListener listener) {
    btnEliminar.addActionListener(listener);
  }

  public void addLimpiarListener(ActionListener listener) {
    btnLimpiar.addActionListener(listener);
  }

  // Nuevo método para añadir listener al botón de volver
  public void addVolverButtonListener(ActionListener listener) {
    btnVolver.addActionListener(listener);
  }

  // Nuevo método para añadir un WindowListener
  public void addWindowCloseListener(WindowListener listener) {
    this.addWindowListener(listener);
  }

  // Getters para los botones
  public JButton getBtnAgregar() {
    return btnAgregar;
  }

  public JButton getBtnActualizar() {
    return btnActualizar;
  }

  public JButton getBtnEliminar() {
    return btnEliminar;
  }

  public JButton getBtnLimpiar() {
    return btnLimpiar;
  }

  public JButton getBtnVolver() { // Getter para el nuevo botón
    return btnVolver;
  }

  // Nuevo método para mostrar la vista
  public void showView() {
    setVisible(true);
  }

  // Nuevo método para ocultar la vista
  public void hideView() {
    setVisible(false);
  }
}
