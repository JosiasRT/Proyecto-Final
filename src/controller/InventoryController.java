package controller;

import model.entity.Componente;
import model.entity.TarjetaMadre;
import model.entity.Microprocesador;
import model.entity.MemoriaRAM;
import model.entity.DiscoDuro;
import model.service.ComponenteService;
import view.InventoryView;
import view.ComponentFormView; // Importar la nueva vista del formulario

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class InventoryController implements ActionListener {
  private InventoryView inventoryView;
  private ComponenteService componenteService;
  private HomeController homeController;

  // Referencia al formulario de componente (puede ser null si no está abierto)
  private ComponentFormView componentFormView;
  private ComponentFormController componentFormController; // Su controlador

  public InventoryController(InventoryView inventoryView, HomeController homeController) {
    this.inventoryView = inventoryView;
    this.componenteService = new ComponenteService();
    this.homeController = homeController;

    this.inventoryView.addCrearButtonListener(this);
    this.inventoryView.addEditarButtonListener(this);
    this.inventoryView.addEliminarButtonListener(this);
    this.inventoryView.addVolverButtonListener(this);
    this.inventoryView.addFilterComboBoxListener(this);

    loadComponentesIntoTable();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == inventoryView.getBtnCrear()) {
      openCreateComponentForm();
    } else if (e.getSource() == inventoryView.getBtnEditar()) {
      openEditComponentForm();
    } else if (e.getSource() == inventoryView.getBtnEliminar()) {
      deleteComponente();
    } else if (e.getSource() == inventoryView.getBtnVolver()) {
      returnToHome();
    } else if (e.getSource() == inventoryView.getFilterComboBox()) {
      filterComponents();
    }
  }

  /**
   * Carga todos los componentes (o filtrados) de la base de datos
   * y los muestra en la tabla de la vista. Este método ahora es público
   * para ser llamado desde otros controladores para refrescar la tabla.
   */
  public void loadComponentesIntoTable() { // CAMBIO: Ahora es público
    try {
      List<Componente> componentes;
      String selectedFilter = inventoryView.getSelectedFilterType();

      if ("Todos".equals(selectedFilter)) {
        componentes = componenteService.getAllComponentes();
      } else {
        componentes = componenteService.getComponentesByType(selectedFilter);
      }

      Object[][] data = new Object[componentes.size()][8];

      for (int i = 0; i < componentes.size(); i++) {
        Componente comp = componentes.get(i);
        data[i][0] = comp.getComponenteID();
        data[i][1] = comp.getNumeroSerie();
        data[i][2] = comp.getTipoComponente();
        data[i][3] = comp.getMarca();
        data[i][4] = comp.getModelo() != null ? comp.getModelo() : ""; // Manejar null si no tienen modelo
        data[i][5] = comp.getPrecio();
        data[i][6] = comp.getCantidadDisponible();
        data[i][7] = getSpecificDetailsString(comp);
      }
      inventoryView.setTableData(data);
    }
    // ... (resto del try-catch para errores de carga)
    catch (SQLException ex) {
      inventoryView.showMessage("Error al cargar componentes: " + ex.getMessage(), "Error de Base de Datos",
          JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    } catch (IllegalArgumentException ex) {
      inventoryView.showMessage(ex.getMessage(), "Error de Filtro", JOptionPane.WARNING_MESSAGE);
      ex.printStackTrace();
    }
  }

  private String getSpecificDetailsString(Componente comp) {
    switch (comp.getTipoComponente()) {
      case "TarjetaMadre":
        TarjetaMadre tm = (TarjetaMadre) comp;
        return "Socket: " + tm.getSocketMicro() + ", RAM: " + tm.getTipoMemoriaRam() + ", Discos: "
            + tm.getConexionesDisco();
      case "Microprocesador":
        Microprocesador mp = (Microprocesador) comp;
        return "Socket: " + mp.getSocket() + ", Velocidad: " + mp.getVelocidad();
      case "MemoriaRAM":
        MemoriaRAM mr = (MemoriaRAM) comp;
        return "Capacidad: " + mr.getCapacidad() + ", Tipo: " + mr.getTipoMemoria();
      case "DiscoDuro":
        DiscoDuro dd = (DiscoDuro) comp;
        return "Capacidad: " + dd.getCapacidad() + ", Conexión: " + dd.getTipoConexion();
      default:
        return "N/A";
    }
  }

  private void openCreateComponentForm() {
    componentFormView = new ComponentFormView(inventoryView);
    componentFormController = new ComponentFormController(componentFormView, componenteService, this, true);
    componentFormView.setVisible(true);
  }

  private void openEditComponentForm() {
    int selectedId = inventoryView.getSelectedComponenteId();
    if (selectedId == -1) {
      inventoryView.showMessage("Seleccione un componente de la tabla para editar.", "Advertencia",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    componentFormView = new ComponentFormView(inventoryView);
    componentFormController = new ComponentFormController(componentFormView, componenteService, this, selectedId);
    componentFormView.setVisible(true);
  }

  private void deleteComponente() {
    int selectedId = inventoryView.getSelectedComponenteId();
    if (selectedId == -1) {
      inventoryView.showMessage("Seleccione un componente de la tabla para eliminar.", "Advertencia",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(inventoryView,
        "¿Está seguro de que desea eliminar el componente con ID " + selectedId + "?",
        "Confirmar Eliminación",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      try {
        componenteService.deleteComponente(selectedId);
        inventoryView.showMessage("Componente eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        loadComponentesIntoTable();
      } catch (IllegalArgumentException ex) {
        inventoryView.showMessage(ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
      } catch (SQLException ex) {
        inventoryView.showMessage("Error al eliminar componente: " + ex.getMessage(), "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
      }
    }
  }

  private void filterComponents() {
    loadComponentesIntoTable();
  }

  private void returnToHome() {
    inventoryView.hideView();
    homeController.showHomeView();
  }

  public void handleWindowClosed() {
    homeController.showHomeView();
  }

  // Método para que el ComponentFormController notifique a InventoryController
  // que recargue la tabla
  public void refreshTable() { // Este método ya existía, pero ahora loadComponentesIntoTable es público
    loadComponentesIntoTable();
  }
}
