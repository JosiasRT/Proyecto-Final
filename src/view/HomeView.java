package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HomeView extends JFrame {
  private JButton btnClientes;
  private JButton btnInventario;
  private JButton btnVentas;
  private JButton btnCombos;

  public HomeView() {
    setTitle("Sistema de Gestión - Inicio");
    setSize(400, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

    btnClientes = new JButton("Gestión de Clientes");
    btnInventario = new JButton("Gestión de Inventario");
    btnVentas = new JButton("Gestión de Ventas");
    btnCombos = new JButton("Gestión de Combos");

    Dimension buttonSize = new Dimension(200, 40);

    btnClientes.setMaximumSize(buttonSize);
    btnClientes.setPreferredSize(buttonSize);
    btnClientes.setAlignmentX(Component.CENTER_ALIGNMENT);

    btnInventario.setMaximumSize(buttonSize);
    btnInventario.setPreferredSize(buttonSize);
    btnInventario.setAlignmentX(Component.CENTER_ALIGNMENT);

    btnVentas.setMaximumSize(buttonSize);
    btnVentas.setPreferredSize(buttonSize);
    btnVentas.setAlignmentX(Component.CENTER_ALIGNMENT);

    btnCombos.setMaximumSize(buttonSize);
    btnCombos.setPreferredSize(buttonSize);
    btnCombos.setAlignmentX(Component.CENTER_ALIGNMENT);

    mainPanel.add(btnClientes);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    mainPanel.add(btnInventario);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    mainPanel.add(btnVentas);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    mainPanel.add(btnCombos);

    add(mainPanel);
  }

  public void addClientesButtonListener(ActionListener listener) {
    btnClientes.addActionListener(listener);
  }

  public void addInventarioButtonListener(ActionListener listener) {
    btnInventario.addActionListener(listener);
  }

  public void addCombosButtonListener(ActionListener listener) {
    btnCombos.addActionListener(listener);
  }

  // Nuevo método para añadir listener al botón de Ventas
  public void addVentasButtonListener(ActionListener listener) {
    btnVentas.addActionListener(listener);
  }

  public JButton getBtnClientes() {
    return btnClientes;
  }

  public JButton getBtnInventario() {
    return btnInventario;
  }

  public JButton getBtnCombos() {
    return btnCombos;
  }

  // Nuevo getter para el botón de Ventas
  public JButton getBtnVentas() {
    return btnVentas;
  }

  public void showView() {
    setVisible(true);
  }

  public void hideView() {
    setVisible(false);
  }
}
