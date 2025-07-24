import controller.HomeController; // Importa el nuevo controlador de inicio
import view.HomeView;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Asegurarse de que la GUI se ejecute en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // 1. Crear la vista de inicio
            HomeView homeView = new HomeView();

            // 2. Crear el controlador de inicio y pasarle la vista
            HomeController homeController = new HomeController(homeView);

            // 3. Hacer visible la ventana de inicio
            homeView.setVisible(true);
        });
    }
}
