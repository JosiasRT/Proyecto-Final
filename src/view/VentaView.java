package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public class VentaView extends JFrame {
    private JComboBox<String> clienteComboBox;
    private JComboBox<String> comboComboBox;
    private JList<String> componentesList;
    private JButton btnRegistrarVenta;
    private JLabel lblTotal;
    private JButton btnVolver;
    private JButton btnNuevoCliente;
    private DefaultListModel<String> seleccionadosModel;
    private JList<String> seleccionadosList;
    private JButton btnAgregarComponente;

    public VentaView() {
        setTitle("Gestión de Ventas");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicialización de componentes
        clienteComboBox = new JComboBox<>();
        comboComboBox = new JComboBox<>();
        componentesList = new JList<>();
        btnRegistrarVenta = new JButton("Registrar Venta");
        lblTotal = new JLabel("Total: $0.00");
        btnVolver = new JButton("Volver");
        btnNuevoCliente = new JButton("Nuevo Cliente");
        seleccionadosModel = new DefaultListModel<>();
        seleccionadosList = new JList<>(seleccionadosModel);
        btnAgregarComponente = new JButton("Agregar a venta");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        int row = 0;

        // Cliente
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel clientePanel = new JPanel(new BorderLayout(10, 0));
        clientePanel.add(clienteComboBox, BorderLayout.CENTER);
        clientePanel.add(btnNuevoCliente, BorderLayout.EAST);
        panel.add(clientePanel, gbc);
        row++;

        // Combo
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel("Combo (opcional):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(comboComboBox, gbc);
        row++;

        // Componentes individuales
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel("Componentes individuales:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(new JScrollPane(componentesList), gbc);
        row++;

        // Botón agregar componente
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(btnAgregarComponente, gbc);
        row++;

        // Componentes seleccionados
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel("Componentes seleccionados:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(new JScrollPane(seleccionadosList), gbc);
        row++;

        // Total
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(lblTotal, gbc);
        row++;

        // Botón registrar venta
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(btnRegistrarVenta, gbc);
        gbc.gridx = 2; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(btnVolver, gbc);

        add(panel);
    }

    public void addRegistrarVentaListener(ActionListener listener) {
        btnRegistrarVenta.addActionListener(listener);
    }

    public void addVolverButtonListener(ActionListener listener) {
        btnVolver.addActionListener(listener);
    }

    public void addWindowCloseListener(WindowListener listener) {
        this.addWindowListener(listener);
    }

    public void addNuevoClienteListener(ActionListener listener) {
        btnNuevoCliente.addActionListener(listener);
    }

    public void addAgregarComponenteListener(ActionListener listener) {
        btnAgregarComponente.addActionListener(listener);
    }

    public JComboBox<String> getClienteComboBox() {
        return clienteComboBox;
    }

    public JComboBox<String> getComboComboBox() {
        return comboComboBox;
    }

    public JList<String> getComponentesList() {
        return componentesList;
    }

    public JLabel getLblTotal() {
        return lblTotal;
    }

    public JButton getBtnRegistrarVenta() {
        return btnRegistrarVenta;
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }

    public JButton getBtnNuevoCliente() {
        return btnNuevoCliente;
    }

    public JList<String> getSeleccionadosList() {
        return seleccionadosList;
    }

    public DefaultListModel<String> getSeleccionadosModel() {
        return seleccionadosModel;
    }

    public JButton getBtnAgregarComponente() {
        return btnAgregarComponente;
    }

    public void showView() {
        setVisible(true);
    }

    public void hideView() {
        setVisible(false);
    }

    // Método para mostrar un diálogo de registro rápido de cliente
    public String[] showNuevoClienteDialog() {
        JTextField nombre = new JTextField();
        JTextField apellido = new JTextField();
        JTextField email = new JTextField();
        JTextField telefono = new JTextField();
        Object[] message = {
            "Nombre:", nombre,
            "Apellido:", apellido,
            "Email:", email,
            "Teléfono:", telefono
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Nuevo Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            return new String[]{nombre.getText(), apellido.getText(), email.getText(), telefono.getText()};
        } else {
            return null;
        }
    }
}