package view; // Asegúrate de que este paquete coincida con la ubicación de tu App.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class HomeView extends JFrame {
  private JButton btnClientes;
  private JButton btnInventario; // Placeholder para futuros botones
  private JButton btnVentas; // Placeholder para futuros botones

  public HomeView() {
    setTitle("Sistema de Gestión - Inicio");
    setSize(400, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // Centrar la ventana en la pantalla

    // Panel principal con un BoxLayout para organizar los botones verticalmente
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Padding

    // Crear botones
    btnClientes = new JButton("Gestión de Clientes");
    btnInventario = new JButton("Gestión de Inventario"); // Botón de ejemplo
    btnVentas = new JButton("Gestión de Ventas"); // Botón de ejemplo

    // Configurar tamaño y alineación de los botones
    Dimension buttonSize = new Dimension(200, 40); // Ancho fijo, alto fijo

    btnClientes.setMaximumSize(buttonSize);
    btnClientes.setPreferredSize(buttonSize);
    btnClientes.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar horizontalmente

    btnInventario.setMaximumSize(buttonSize);
    btnInventario.setPreferredSize(buttonSize);
    btnInventario.setAlignmentX(Component.CENTER_ALIGNMENT);

    btnVentas.setMaximumSize(buttonSize);
    btnVentas.setPreferredSize(buttonSize);
    btnVentas.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Añadir los botones al panel con espacio entre ellos
    mainPanel.add(btnClientes);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio vertical
    mainPanel.add(btnInventario);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    mainPanel.add(btnVentas);

    add(mainPanel);
  }

  // Método para que el controlador pueda añadir un listener al botón de clientes
  public void addClientesButtonListener(ActionListener listener) {
    btnClientes.addActionListener(listener);
  }

  // Puedes añadir métodos similares para otros botones si los implementas
  public JButton getBtnClientes() {
    return btnClientes;
  }
}
