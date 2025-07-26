package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HomeView extends JFrame {
  private JButton btnClientes;

  public HomeView() {
    setTitle("Sistema de Gestión de Clientes");
    setSize(400, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

    btnClientes = new JButton("Gestión de Clientes");

    Dimension buttonSize = new Dimension(200, 40);

    btnClientes.setMaximumSize(buttonSize);
    btnClientes.setPreferredSize(buttonSize);
    btnClientes.setAlignmentX(Component.CENTER_ALIGNMENT);

    mainPanel.add(Box.createVerticalGlue());
    mainPanel.add(btnClientes);
    mainPanel.add(Box.createVerticalGlue());

    add(mainPanel);
  }

  public void addClientesButtonListener(ActionListener listener) {
    btnClientes.addActionListener(listener);
  }

  public JButton getBtnClientes() {
    return btnClientes;
  }

  public void showView() {
    setVisible(true);
  }

  public void hideView() {
    setVisible(false);
  }
}
