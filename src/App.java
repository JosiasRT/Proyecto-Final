
import controller.HomeController;
import view.HomeView;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomeView homeView = new HomeView();
            HomeController homeController = new HomeController(homeView);
            homeView.showView(); // Mostrar el menú de inicio al arrancar
        });
    }
}
