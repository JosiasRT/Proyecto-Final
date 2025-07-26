package controller;

import view.ClienteView;
import view.HomeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HomeController implements ActionListener {
  private HomeView homeView;
  private ClienteView clienteView;
  private ClienteController clienteController;

  public HomeController(HomeView homeView) {
    this.homeView = homeView;
    this.homeView.addClientesButtonListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == homeView.getBtnClientes()) {
      openClientesModule();
    }
    // El botón "Volver" de ClienteView
    else if (clienteView != null && e.getSource() == clienteView.getBtnVolver()) {
      returnToHomeFromClient();
    }
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

  // Método para que ClienteView llame al volver al Home
  private void returnToHomeFromClient() {
    if (clienteView != null) {
      clienteView.hideView();
    }
    homeView.showView();
  }

  // Método público para que otros controladores puedan solicitar mostrar la
  // HomeView
  public void showHomeView() {
    homeView.showView();
  }
}
