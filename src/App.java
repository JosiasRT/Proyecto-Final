import javax.swing.SwingUtilities;

import controller.ClienteController;
import view.ClienteView;

public class App {
    public static void main(String[] args) { // Asegurarse de que la GUI se ejecute en el Event Dispatch Thread (EDT)
        // Esto es crucial para la seguridad de hilos en Swing.
        SwingUtilities.invokeLater(() -> {
            ClienteView clienteView = new ClienteView();
            // El controlador se inicializa con la vista, y a su vez, el controlador
            // inicializa el servicio y este el DAO.
            ClienteController clienteController = new ClienteController(clienteView);
            clienteView.setVisible(true);
        });
    }
}
