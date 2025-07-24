package controller;

import view.ClienteView;
import view.HomeView;
import view.InventoryView; // Importa la vista de inventario

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HomeController implements ActionListener {
  private HomeView homeView;
  private ClienteView clienteView;
  private ClienteController clienteController;

  private InventoryView inventoryView; // Nueva referencia a la vista de inventario
  private InventoryController inventoryController; // Nueva referencia al controlador de inventario

  public HomeController(HomeView homeView) {
    this.homeView = homeView;

    // Asignar listeners a los botones de la vista de inicio
    this.homeView.addClientesButtonListener(this);
    this.homeView.addInventarioButtonListener(this); // Nuevo listener para el botón de Inventario
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == homeView.getBtnClientes()) {
      openClientesModule();
    } else if (e.getSource() == homeView.getBtnInventario()) { // Nuevo caso para el botón de Inventario
      openInventoryModule();
    }
    // El botón "Volver" de ClienteView sigue siendo manejado aquí
    else if (clienteView != null && e.getSource() == clienteView.getBtnVolver()) {
      returnToHomeFromClient();
    }
    // Aquí podrías añadir lógica para otros botones (Ventas, etc.)
  }

  private void openClientesModule() {
    homeView.hideView();

    if (clienteView == null) {
      clienteView = new ClienteView();
      clienteController = new ClienteController(clienteView);
      clienteView.addVolverButtonListener(this); // El botón "Volver" de ClienteView
      clienteView.addWindowCloseListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          homeView.showView(); // Mostrar HomeView cuando ClienteView se cierra
        }
      });
    }
    clienteView.showView();
  }

  private void openInventoryModule() {
    homeView.hideView(); // Ocultar la ventana de inicio

    if (inventoryView == null) {
      inventoryView = new InventoryView();
      // Pasar la referencia de HomeController al InventoryController
      inventoryController = new InventoryController(inventoryView, this);

      // Asignar WindowListener a InventoryView para cuando se cierra
      inventoryView.addWindowCloseListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          homeView.showView(); // Mostrar HomeView cuando InventoryView se cierra
        }
      });
    }
    inventoryView.showView(); // Hacer visible la ventana de inventario
  }

  // Método para que ClienteView llame al volver al Home
  private void returnToHomeFromClient() {
    if (clienteView != null) {
      clienteView.hideView();
    }
    homeView.showView();
  }

  // Nuevo método para que InventoryController pueda llamar al volver al Home
  public void showHomeView() {
    homeView.showView();
  }
}
