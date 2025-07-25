package view;

import model.entity.Componente;
import model.entity.ComboDetalle;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComboFormView extends JDialog {
    private JTextField txtDescuentoPorciento;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private JComboBox<Componente> cmbTarjetaMadre;
    private JComboBox<Componente> cmbMicroprocesador;
    private JComboBox<Componente> cmbMemoriaRAM;
    private JComboBox<Componente> cmbDiscoDuro;

    private JSpinner spCantidadTM;
    private JSpinner spCantidadMP;
    private JSpinner spCantidadMR;
    private JSpinner spCantidadDD;

    private Map<String, JComboBox<Componente>> componentComboBoxes;
    private Map<String, JSpinner> componentQuantities;

    private JLabel loadingLabel;
    private JPanel contentPanel;

    public ComboFormView(JFrame parent) {
        super(parent, "Crear Combo", true);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        discountPanel.setBorder(BorderFactory.createTitledBorder("Descuento del Combo"));
        discountPanel.add(new JLabel("Descuento (%):"));
        txtDescuentoPorciento = new JTextField(5);
        discountPanel.add(txtDescuentoPorciento);
        contentPanel.add(discountPanel, BorderLayout.NORTH);

        JPanel componentsPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        componentsPanel.setBorder(BorderFactory.createTitledBorder("Selección de Componentes"));

        cmbTarjetaMadre = new JComboBox<>();
        cmbMicroprocesador = new JComboBox<>();
        cmbMemoriaRAM = new JComboBox<>();
        cmbDiscoDuro = new JComboBox<>();

        spCantidadTM = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        spCantidadTM.setEnabled(false);
        spCantidadMP = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        spCantidadMP.setEnabled(false);
        spCantidadMR = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        spCantidadMR.setEnabled(false);
        spCantidadDD = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        spCantidadDD.setEnabled(false);

        componentComboBoxes = new HashMap<>();
        componentComboBoxes.put("TarjetaMadre", cmbTarjetaMadre);
        componentComboBoxes.put("Microprocesador", cmbMicroprocesador);
        componentComboBoxes.put("MemoriaRAM", cmbMemoriaRAM);
        componentComboBoxes.put("DiscoDuro", cmbDiscoDuro);

        componentQuantities = new HashMap<>();
        componentQuantities.put("TarjetaMadre", spCantidadTM);
        componentQuantities.put("Microprocesador", spCantidadMP);
        componentQuantities.put("MemoriaRAM", spCantidadMR);
        componentQuantities.put("DiscoDuro", spCantidadDD);

        componentsPanel.add(new JLabel("Tarjeta Madre:"));
        componentsPanel.add(cmbTarjetaMadre);
        componentsPanel.add(spCantidadTM);

        componentsPanel.add(new JLabel("Microprocesador:"));
        componentsPanel.add(cmbMicroprocesador);
        componentsPanel.add(spCantidadMP);

        componentsPanel.add(new JLabel("Memoria RAM:"));
        componentsPanel.add(cmbMemoriaRAM);
        componentsPanel.add(spCantidadMR);

        componentsPanel.add(new JLabel("Disco Duro:"));
        componentsPanel.add(cmbDiscoDuro);
        componentsPanel.add(spCantidadDD);

        contentPanel.add(componentsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnGuardar = new JButton("Guardar Combo");
        btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        loadingLabel = new JLabel("Cargando componentes, por favor espere...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loadingLabel.setForeground(Color.BLUE);
        loadingLabel.setVisible(false);
        add(loadingLabel, BorderLayout.NORTH);
    }

    public JTextField getTxtDescuentoPorciento() { return txtDescuentoPorciento; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JComboBox<Componente> getCmbTarjetaMadre() { return cmbTarjetaMadre; }
    public JComboBox<Componente> getCmbMicroprocesador() { return cmbMicroprocesador; }
    public JComboBox<Componente> getCmbMemoriaRAM() { return cmbMemoriaRAM; }
    public JComboBox<Componente> getCmbDiscoDuro() { return cmbDiscoDuro; }
    public JSpinner getSpCantidadTM() { return spCantidadTM; }
    public JSpinner getSpCantidadMP() { return spCantidadMP; }
    public JSpinner getSpCantidadMR() { return spCantidadMR; }
    public JSpinner getSpCantidadDD() { return spCantidadDD; }
    public Map<String, JComboBox<Componente>> getComponentComboBoxes() { return componentComboBoxes; }
    public Map<String, JSpinner> getComponentQuantities() { return componentQuantities; }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void clearForm() {
        txtDescuentoPorciento.setText("");
        cmbTarjetaMadre.setSelectedItem(null);
        cmbMicroprocesador.setSelectedItem(null);
        cmbMemoriaRAM.setSelectedItem(null);
        cmbDiscoDuro.setSelectedItem(null);
        spCantidadTM.setValue(1);
        spCantidadMP.setValue(1);
        spCantidadMR.setValue(1);
        spCantidadDD.setValue(1);
    }

    public void populateComboBox(List<Componente> components, String type) {
        JComboBox<Componente> comboBox = componentComboBoxes.get(type);
        if (comboBox != null) {
            comboBox.removeAllItems();
            comboBox.addItem(null);
            if (components != null) {
                for (Componente comp : components) {
                    comboBox.addItem(comp);
                }
            }
        }
    }

    public void setComboData(BigDecimal descuentoPorciento, List<ComboDetalle> detalles) {
        clearForm();
        txtDescuentoPorciento.setText(descuentoPorciento != null ? descuentoPorciento.toPlainString() : "");
        if (detalles != null) {
            for (ComboDetalle detalle : detalles) {
                Componente comp = detalle.getComponente();
                if (comp != null) {
                    switch (comp.getTipoComponente()) {
                        case "TarjetaMadre":
                            cmbTarjetaMadre.setSelectedItem(comp);
                            spCantidadTM.setValue(detalle.getCantidad());
                            break;
                        case "Microprocesador":
                            cmbMicroprocesador.setSelectedItem(comp);
                            spCantidadMP.setValue(detalle.getCantidad());
                            break;
                        case "MemoriaRAM":
                            cmbMemoriaRAM.setSelectedItem(comp);
                            spCantidadMR.setValue(detalle.getCantidad());
                            break;
                        case "DiscoDuro":
                            cmbDiscoDuro.setSelectedItem(comp);
                            spCantidadDD.setValue(detalle.getCantidad());
                            break;
                    }
                }
            }
        }
    }

    public void showLoadingIndicator() {
        contentPanel.setVisible(false);
        loadingLabel.setVisible(true);
    }
    public void hideLoadingIndicator() {
        loadingLabel.setVisible(false);
        contentPanel.setVisible(true);
    }
}
