package controller; // O el paquete donde tengas tus controladores

import view.ClienteView; // Importa tu vista de clientes
import view.HomeView; // Importa la nueva vista de inicio

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeController implements ActionListener {
  private HomeView homeView;
  private ClienteView clienteView; // Necesitamos una referencia a la vista de clientes
  private ClienteController clienteController; // Y a su controlador

  public HomeController(HomeView homeView) {
    this.homeView = homeView;

    // Asignar el listener al botón de clientes en la vista de inicio
    this.homeView.addClientesButtonListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // Identificar qué botón se presionó en la HomeView
    if (e.getSource() == homeView.getBtnClientes()) {
      openClientesModule();
    }
    // Aquí podrías añadir lógica para otros botones (Inventario, Ventas, etc.)
  }

  private void openClientesModule() {
    // Ocultar la ventana de inicio
    homeView.setVisible(false);

    // Crear e inicializar la vista y el controlador de clientes si no existen
    // Esto asegura que solo se cree una instancia si no está ya abierta
    if (clienteView == null || !clienteView.isVisible()) {
      clienteView = new ClienteView(); // Crea una nueva instancia de tu ClienteView
      clienteController = new ClienteController(clienteView); // Pasa la vista al controlador
    }

    // Hacer visible la ventana de clientes
    clienteView.setVisible(true);
  }
}
