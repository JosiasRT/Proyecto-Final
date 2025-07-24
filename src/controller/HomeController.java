package controller;

import view.ClienteView;
import view.HomeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter; // Para simplificar WindowListener
import java.awt.event.WindowEvent;

public class HomeController implements ActionListener {
  private HomeView homeView;
  private ClienteView clienteView; // Referencia a la instancia de ClienteView
  private ClienteController clienteController; // Referencia a la instancia de ClienteController

  public HomeController(HomeView homeView) {
    this.homeView = homeView;

    // Asignar el listener al botón de clientes en la vista de inicio
    this.homeView.addClientesButtonListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == homeView.getBtnClientes()) {
      openClientesModule();
    } else if (e.getSource() == clienteView.getBtnVolver()) { // Si el evento viene del botón "Volver"
      returnToHome();
    }
    // Aquí podrías añadir lógica para otros botones (Inventario, Ventas, etc.)
  }

  private void openClientesModule() {
    homeView.hideView(); // Ocultar la ventana de inicio

    // Crear e inicializar la vista y el controlador de clientes si no existen
    if (clienteView == null) {
      clienteView = new ClienteView();
      clienteController = new ClienteController(clienteView);

      // CAMBIO CLAVE: Asignar listener al botón "Volver" de ClienteView
      clienteView.addVolverButtonListener(this);

      // CAMBIO CLAVE: Asignar WindowListener a ClienteView para cuando se cierra
      clienteView.addWindowCloseListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          // Cuando ClienteView se cierra (DISPOSE_ON_CLOSE), mostrar HomeView
          homeView.showView();
        }
      });
    }

    // Hacer visible la ventana de clientes
    clienteView.showView();
  }

  private void returnToHome() {
    if (clienteView != null) {
      clienteView.hideView(); // Ocultar la ventana de clientes
    }
    homeView.showView(); // Mostrar la ventana de inicio
  }
}
