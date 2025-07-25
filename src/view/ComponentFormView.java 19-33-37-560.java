package view;

import model.entity.*; // Importa todas las entidades de Componente
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ComponentFormView extends JDialog {
  private JTextField txtNumeroSerie;
  private JTextField txtPrecio;
  private JTextField txtCantidadDisponible;
  private JComboBox<String> cmbTipoComponente;
  private JButton btnGuardar;
  private JButton btnCancelar;

  // Paneles para campos específicos de cada tipo de componente
  private JPanel specificFieldsPanel;
  private CardLayout cardLayout;

  // Mapas para almacenar los campos de texto específicos por tipo de componente
  private Map<String, JTextField> specificTextFields;

  // Campos comunes para casi todos los subtipos (se inicializan una vez en los
  // métodos createXPanel)
  private JTextField currentMarcaField; // Usado para el campo de marca actualmente visible
  private JTextField currentModeloField; // Usado para el campo de modelo actualmente visible

  // Campos específicos para TarjetaMadre
  private JTextField txtSocketMicroTM;
  private JTextField txtTipoMemoriaRamTM;
  private JTextField txtConexionesDiscoTM;

  // Campos específicos para Microprocesador
  private JTextField txtSocketMP;
  private JTextField txtVelocidadMP;

  // Campos específicos para MemoriaRAM
  private JTextField txtCapacidadMR;
  private JTextField txtTipoMemoriaMR;

  // Campos específicos para DiscoDuro
  private JTextField txtCapacidadDD;
  private JTextField txtTipoConexionDD;

  public ComponentFormView(JFrame parent) {
    super(parent, "Gestión de Componente", true); // true para modal
    setSize(550, 450); // Tamaño ajustado para más campos
    setLocationRelativeTo(parent);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Cerrar solo este diálogo

    setLayout(new BorderLayout(10, 10)); // Layout principal

    // Panel superior para campos comunes y selector de tipo
    JPanel commonPanel = new JPanel(new GridLayout(5, 2, 10, 5));
    commonPanel.setBorder(BorderFactory.createTitledBorder("Información General"));

    commonPanel.add(new JLabel("Número de Serie:"));
    txtNumeroSerie = new JTextField();
    commonPanel.add(txtNumeroSerie);

    commonPanel.add(new JLabel("Precio:"));
    txtPrecio = new JTextField();
    commonPanel.add(txtPrecio);

    commonPanel.add(new JLabel("Cantidad Disponible:"));
    txtCantidadDisponible = new JTextField();
    commonPanel.add(txtCantidadDisponible);

    commonPanel.add(new JLabel("Tipo de Componente:"));
    cmbTipoComponente = new JComboBox<>(
        new String[] { "Seleccione", "TarjetaMadre", "Microprocesador", "MemoriaRAM", "DiscoDuro" });
    commonPanel.add(cmbTipoComponente);

    add(commonPanel, BorderLayout.NORTH);

    // Panel central con CardLayout para campos específicos
    cardLayout = new CardLayout();
    specificFieldsPanel = new JPanel(cardLayout);
    specificFieldsPanel.setBorder(BorderFactory.createTitledBorder("Detalles Específicos"));
    add(specificFieldsPanel, BorderLayout.CENTER);

    // Inicializar los mapas de campos específicos
    specificTextFields = new HashMap<>();

    // --- Crear paneles para cada tipo de componente ---
    createTarjetaMadrePanel();
    createMicroprocesadorPanel();
    createMemoriaRAMPanel();
    createDiscoDuroPanel();

    // Panel de botones
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    btnGuardar = new JButton("Guardar");
    btnCancelar = new JButton("Cancelar");
    buttonPanel.add(btnGuardar);
    buttonPanel.add(btnCancelar);
    add(buttonPanel, BorderLayout.SOUTH);

    // Listener para el ComboBox para cambiar los paneles de campos específicos
    cmbTipoComponente.addActionListener(e -> showSpecificFieldsPanel((String) cmbTipoComponente.getSelectedItem()));

    // Mostrar el panel por defecto (vacío)
    showSpecificFieldsPanel("Seleccione");
  }

  // --- Métodos para crear los paneles específicos ---
  private void createTarjetaMadrePanel() {
    JPanel panel = new JPanel(new GridLayout(5, 2, 10, 5));
    JTextField marcaField = new JTextField();
    JTextField modeloField = new JTextField();
    txtSocketMicroTM = new JTextField();
    txtTipoMemoriaRamTM = new JTextField();
    txtConexionesDiscoTM = new JTextField();

    panel.add(new JLabel("Marca:"));
    panel.add(marcaField);
    panel.add(new JLabel("Modelo:"));
    panel.add(modeloField);
    panel.add(new JLabel("Socket Micro:"));
    panel.add(txtSocketMicroTM);
    panel.add(new JLabel("Tipo Memoria RAM:"));
    panel.add(txtTipoMemoriaRamTM);
    panel.add(new JLabel("Conexiones Disco:"));
    panel.add(txtConexionesDiscoTM);

    specificFieldsPanel.add(panel, "TarjetaMadre");
    specificTextFields.put("TarjetaMadre_Marca", marcaField);
    specificTextFields.put("TarjetaMadre_Modelo", modeloField);
    specificTextFields.put("TarjetaMadre_SocketMicro", txtSocketMicroTM);
    specificTextFields.put("TarjetaMadre_TipoMemoriaRam", txtTipoMemoriaRamTM);
    specificTextFields.put("TarjetaMadre_ConexionesDisco", txtConexionesDiscoTM);
  }

  private void createMicroprocesadorPanel() {
    JPanel panel = new JPanel(new GridLayout(4, 2, 10, 5));
    JTextField marcaField = new JTextField();
    JTextField modeloField = new JTextField();
    txtSocketMP = new JTextField();
    txtVelocidadMP = new JTextField();

    panel.add(new JLabel("Marca:"));
    panel.add(marcaField);
    panel.add(new JLabel("Modelo:"));
    panel.add(modeloField);
    panel.add(new JLabel("Socket:"));
    panel.add(txtSocketMP);
    panel.add(new JLabel("Velocidad:"));
    panel.add(txtVelocidadMP);

    specificFieldsPanel.add(panel, "Microprocesador");
    specificTextFields.put("Microprocesador_Marca", marcaField);
    specificTextFields.put("Microprocesador_Modelo", modeloField);
    specificTextFields.put("Microprocesador_Socket", txtSocketMP);
    specificTextFields.put("Microprocesador_Velocidad", txtVelocidadMP);
  }

  private void createMemoriaRAMPanel() {
    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 5));
    JTextField marcaField = new JTextField();
    txtCapacidadMR = new JTextField();
    txtTipoMemoriaMR = new JTextField();

    panel.add(new JLabel("Marca:"));
    panel.add(marcaField);
    panel.add(new JLabel("Capacidad:"));
    panel.add(txtCapacidadMR);
    panel.add(new JLabel("Tipo Memoria:"));
    panel.add(txtTipoMemoriaMR);

    specificFieldsPanel.add(panel, "MemoriaRAM");
    specificTextFields.put("MemoriaRAM_Marca", marcaField);
    specificTextFields.put("MemoriaRAM_Capacidad", txtCapacidadMR);
    specificTextFields.put("MemoriaRAM_TipoMemoria", txtTipoMemoriaMR);
  }

  private void createDiscoDuroPanel() {
    JPanel panel = new JPanel(new GridLayout(4, 2, 10, 5));
    JTextField marcaField = new JTextField();
    JTextField modeloField = new JTextField();
    txtCapacidadDD = new JTextField();
    txtTipoConexionDD = new JTextField();

    panel.add(new JLabel("Marca:"));
    panel.add(marcaField);
    panel.add(new JLabel("Modelo:"));
    panel.add(modeloField);
    panel.add(new JLabel("Capacidad:"));
    panel.add(txtCapacidadDD);
    panel.add(new JLabel("Tipo Conexión:"));
    panel.add(txtTipoConexionDD);

    specificFieldsPanel.add(panel, "DiscoDuro");
    specificTextFields.put("DiscoDuro_Marca", marcaField);
    specificTextFields.put("DiscoDuro_Modelo", modeloField);
    specificTextFields.put("DiscoDuro_Capacidad", txtCapacidadDD);
    specificTextFields.put("DiscoDuro_TipoConexion", txtTipoConexionDD);
  }

  private void showSpecificFieldsPanel(String type) {
    if ("Seleccione".equals(type)) {
      JPanel emptyPanel = new JPanel();
      emptyPanel.add(new JLabel("Seleccione un tipo de componente."));
      specificFieldsPanel.add(emptyPanel, "Empty");
      cardLayout.show(specificFieldsPanel, "Empty");
    } else {
      cardLayout.show(specificFieldsPanel, type);
    }
    clearAllSpecificFields(); // Limpia los campos cuando cambias de tipo
  }

  // --- Getters para los campos comunes (JTextFields) ---
  public JTextField getTxtNumeroSerie() {
    return txtNumeroSerie;
  }

  public JTextField getTxtPrecio() {
    return txtPrecio;
  }

  public JTextField getTxtCantidadDisponible() {
    return txtCantidadDisponible;
  }

  public JComboBox<String> getCmbTipoComponente() {
    return cmbTipoComponente;
  }

  // --- Getters para los botones ---
  public JButton getBtnGuardar() {
    return btnGuardar;
  }

  public JButton getBtnCancelar() {
    return btnCancelar;
  }

  // --- Método para obtener el mapa de campos específicos (para el controlador)
  // ---
  public Map<String, JTextField> getSpecificTextFields() {
    return specificTextFields;
  }

  // --- Métodos para añadir listeners a los botones ---
  public void addGuardarButtonListener(ActionListener listener) {
    btnGuardar.addActionListener(listener);
  }

  public void addCancelarButtonListener(ActionListener listener) {
    btnCancelar.addActionListener(listener);
  }

  // Método para limpiar todos los campos (útil para "Crear")
  public void clearForm() {
    txtNumeroSerie.setText("");
    txtPrecio.setText("");
    txtCantidadDisponible.setText("");
    cmbTipoComponente.setSelectedItem("Seleccione");
    clearAllSpecificFields();
  }

  private void clearAllSpecificFields() {
    for (JTextField field : specificTextFields.values()) {
      if (field != null) {
        field.setText("");
      }
    }
  }

  // Método para establecer los datos de un componente (útil para "Editar")
  public void setComponenteData(Componente componente) {
    clearForm(); // Limpiar antes de cargar nuevos datos
    if (componente == null)
      return;

    txtNumeroSerie.setText(componente.getNumeroSerie());
    txtPrecio.setText(componente.getPrecio().toString());
    txtCantidadDisponible.setText(String.valueOf(componente.getCantidadDisponible()));
    cmbTipoComponente.setSelectedItem(componente.getTipoComponente());

    // Cargar datos específicos
    String tipo = componente.getTipoComponente();
    switch (tipo) {
      case "TarjetaMadre":
        TarjetaMadre tm = (TarjetaMadre) componente;
        specificTextFields.get("TarjetaMadre_Marca").setText(tm.getMarca());
        specificTextFields.get("TarjetaMadre_Modelo").setText(tm.getModelo());
        specificTextFields.get("TarjetaMadre_SocketMicro").setText(tm.getSocketMicro());
        specificTextFields.get("TarjetaMadre_TipoMemoriaRam").setText(tm.getTipoMemoriaRam());
        specificTextFields.get("TarjetaMadre_ConexionesDisco").setText(tm.getConexionesDisco());
        break;
      case "Microprocesador":
        Microprocesador mp = (Microprocesador) componente;
        specificTextFields.get("Microprocesador_Marca").setText(mp.getMarca());
        specificTextFields.get("Microprocesador_Modelo").setText(mp.getModelo());
        specificTextFields.get("Microprocesador_Socket").setText(mp.getSocket());
        specificTextFields.get("Microprocesador_Velocidad").setText(mp.getVelocidad());
        break;
      case "MemoriaRAM":
        MemoriaRAM mr = (MemoriaRAM) componente;
        specificTextFields.get("MemoriaRAM_Marca").setText(mr.getMarca());
        specificTextFields.get("MemoriaRAM_Capacidad").setText(mr.getCapacidad());
        specificTextFields.get("MemoriaRAM_TipoMemoria").setText(mr.getTipoMemoria());
        break;
      case "DiscoDuro":
        DiscoDuro dd = (DiscoDuro) componente;
        specificTextFields.get("DiscoDuro_Marca").setText(dd.getMarca());
        specificTextFields.get("DiscoDuro_Modelo").setText(dd.getModelo());
        specificTextFields.get("DiscoDuro_Capacidad").setText(dd.getCapacidad());
        specificTextFields.get("DiscoDuro_TipoConexion").setText(dd.getTipoConexion());
        break;
    }
    showSpecificFieldsPanel(tipo); // Asegurarse de que el panel correcto esté visible
  }

  // Método para obtener un objeto Componente del formulario
  public Componente getComponenteFromForm(int id) {
    String numeroSerie = txtNumeroSerie.getText();
    String tipoComponente = (String) cmbTipoComponente.getSelectedItem();
    BigDecimal precio;
    int cantidadDisponible;

    try {
      precio = new BigDecimal(txtPrecio.getText());
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error de Entrada",
          JOptionPane.ERROR_MESSAGE);
      return null;
    }

    try {
      cantidadDisponible = Integer.parseInt(txtCantidadDisponible.getText());
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "La cantidad disponible debe ser un número entero válido.",
          "Error de Entrada", JOptionPane.ERROR_MESSAGE);
      return null;
    }

    Componente componente = null;
    switch (tipoComponente) {
      case "TarjetaMadre":
        String marcaTM = specificTextFields.get("TarjetaMadre_Marca").getText();
        String modeloTM = specificTextFields.get("TarjetaMadre_Modelo").getText();
        String socketMicroTM = specificTextFields.get("TarjetaMadre_SocketMicro").getText();
        String tipoMemoriaRamTM = specificTextFields.get("TarjetaMadre_TipoMemoriaRam").getText();
        String conexionesDiscoTM = specificTextFields.get("TarjetaMadre_ConexionesDisco").getText();
        if (id == 0) { // Crear nuevo
          componente = new TarjetaMadre(numeroSerie, precio, cantidadDisponible,
              marcaTM, modeloTM, socketMicroTM, tipoMemoriaRamTM, conexionesDiscoTM);
        } else { // Editar existente
          componente = new TarjetaMadre(id, numeroSerie, precio, cantidadDisponible,
              marcaTM, modeloTM, socketMicroTM, tipoMemoriaRamTM, conexionesDiscoTM);
        }
        break;
      case "Microprocesador":
        String marcaMP = specificTextFields.get("Microprocesador_Marca").getText();
        String modeloMP = specificTextFields.get("Microprocesador_Modelo").getText();
        String socketMP = specificTextFields.get("Microprocesador_Socket").getText();
        String velocidadMP = specificTextFields.get("Microprocesador_Velocidad").getText();
        if (id == 0) {
          componente = new Microprocesador(numeroSerie, precio, cantidadDisponible,
              marcaMP, modeloMP, socketMP, velocidadMP);
        } else {
          componente = new Microprocesador(id, numeroSerie, precio, cantidadDisponible,
              marcaMP, modeloMP, socketMP, velocidadMP);
        }
        break;
      case "MemoriaRAM":
        String marcaMR = specificTextFields.get("MemoriaRAM_Marca").getText();
        String capacidadMR = specificTextFields.get("MemoriaRAM_Capacidad").getText();
        String tipoMemoriaMR = specificTextFields.get("MemoriaRAM_TipoMemoria").getText();
        if (id == 0) {
          componente = new MemoriaRAM(numeroSerie, precio, cantidadDisponible,
              marcaMR, capacidadMR, tipoMemoriaMR);
        } else {
          componente = new MemoriaRAM(id, numeroSerie, precio, cantidadDisponible,
              marcaMR, capacidadMR, tipoMemoriaMR);
        }
        break;
      case "DiscoDuro":
        String marcaDD = specificTextFields.get("DiscoDuro_Marca").getText();
        String modeloDD = specificTextFields.get("DiscoDuro_Modelo").getText();
        String capacidadDD = specificTextFields.get("DiscoDuro_Capacidad").getText();
        String tipoConexionDD = specificTextFields.get("DiscoDuro_TipoConexion").getText();
        if (id == 0) {
          componente = new DiscoDuro(numeroSerie, precio, cantidadDisponible,
              marcaDD, modeloDD, capacidadDD, tipoConexionDD);
        } else {
          componente = new DiscoDuro(id, numeroSerie, precio, cantidadDisponible,
              marcaDD, modeloDD, capacidadDD, tipoConexionDD);
        }
        break;
      default:
        JOptionPane.showMessageDialog(this, "Seleccione un tipo de componente válido.", "Error de Selección",
            JOptionPane.ERROR_MESSAGE);
        return null;
    }
    return componente;
  }

  public String getSelectedComponentType() {
    return (String) cmbTipoComponente.getSelectedItem();
  }

  // Nuevo método showMessage para mostrar mensajes al usuario
  public void showMessage(String message, String title, int messageType) {
    JOptionPane.showMessageDialog(this, message, title, messageType);
  }
}
