package controller;

import view.ClienteView;
import view.HomeView;
import view.InventoryView;
import view.ComboView; // Importa la vista de combos
import view.VentaView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HomeController implements ActionListener {
  private HomeView homeView;
  private ClienteView clienteView;
  private ClienteController clienteController;

  private InventoryView inventoryView;
  private InventoryController inventoryController;

  private ComboView comboView; // Nueva referencia a la vista de combos
  private ComboController comboController; // Nueva referencia al controlador de combos (aún no creado)

  private VentaView ventaView;
  private VentaController ventaController;

  public HomeController(HomeView homeView) {
    this.homeView = homeView;

    this.homeView.addClientesButtonListener(this);
    this.homeView.addInventarioButtonListener(this);
    this.homeView.addCombosButtonListener(this); // Nuevo listener para el botón de Combos
    this.homeView.addVentasButtonListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == homeView.getBtnClientes()) {
      openClientesModule();
    } else if (e.getSource() == homeView.getBtnInventario()) {
      openInventoryModule();
    } else if (e.getSource() == homeView.getBtnCombos()) { // Nuevo caso para el botón de Combos
      openCombosModule();
    } else if (e.getSource() == homeView.getBtnVentas()) {
      openVentasModule();
    }
    // El botón "Volver" de ClienteView
    else if (clienteView != null && e.getSource() == clienteView.getBtnVolver()) {
      returnToHomeFromClient();
    }
    // El botón "Volver" de InventoryView
    else if (inventoryView != null && e.getSource() == inventoryView.getBtnVolver()) {
      returnToHomeFromInventory();
    }
    // Aquí podrías añadir lógica para otros botones (Ventas, etc.)
  }

  private void openClientesModule() {
    homeView.hideView();

    if (clienteView == null) {
      clienteView = new ClienteView();
      clienteController = new ClienteController(clienteView);
      clienteView.addVolverButtonListener(this);
      clienteView.addWindowCloseListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          homeView.showView();
        }
      });
    }
    clienteView.showView();
  }

  private void openInventoryModule() {
    homeView.hideView();

    if (inventoryView == null) {
      inventoryView = new InventoryView();
      inventoryController = new InventoryController(inventoryView, this);
      inventoryView.addWindowCloseListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          homeView.showView();
        }
      });
    }
    inventoryView.showView();
  }

  // Nuevo método para abrir el módulo de Combos
  private void openCombosModule() {
    homeView.hideView(); // Ocultar la ventana de inicio

    if (comboView == null) {
      comboView = new ComboView();
      // Pasar la referencia de HomeController al ComboController
      comboController = new ComboController(comboView, this); // ComboController aún no creado
      // Asignar WindowListener a ComboView para cuando se cierra
      comboView.addWindowCloseListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          homeView.showView(); // Mostrar HomeView cuando ComboView se cierra
        }
      });
    }
    comboView.showView(); // Hacer visible la ventana de combos
  }

  private void openVentasModule() {
    homeView.hideView();
    if (ventaView == null) {
      ventaView = new VentaView();
      ventaController = new VentaController(ventaView, this);
      ventaView.addWindowCloseListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          homeView.showView();
        }
      });
    }
    ventaView.showView();
  }

  // Método para que ClienteView llame al volver al Home
  private void returnToHomeFromClient() {
    if (clienteView != null) {
      clienteView.hideView();
    }
    homeView.showView();
  }

  // Método para que InventoryController llame al volver al Home
  private void returnToHomeFromInventory() {
    if (inventoryView != null) {
      inventoryView.hideView();
    }
    homeView.showView();
  }

  // Método público para que otros controladores puedan solicitar mostrar la
  // HomeView
  public void showHomeView() {
    homeView.showView();
  }
}
