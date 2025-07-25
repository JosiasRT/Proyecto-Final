package controller; // Asegúrate de que este paquete coincida con la ubicación de tus controladores

import model.entity.Combo;
import model.entity.ComboDetalle;
import model.entity.Componente; // Asegúrate de importar Componente
import model.service.ComboService;
import model.service.ComponenteService; // Necesario para ComboFormController
import view.ComboView;
import view.ComboFormView; // Importar la nueva vista del formulario

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional; // Para cuadros de diálogo de confirmación
import java.math.BigDecimal; // Para BigDecimal

public class ComboController implements ActionListener {
  private ComboView comboView;
  private ComboService comboService;
  private ComponenteService componenteService; // Instancia de ComponenteService para pasar al form controller

  private HomeController homeController;

  // Referencias al formulario de combo y su controlador
  private ComboFormView comboFormView;
  private ComboFormController comboFormController;

  public ComboController(ComboView comboView, HomeController homeController) {
    this.comboView = comboView;
    this.comboService = new ComboService();
    this.componenteService = new ComponenteService(); // Inicializar ComponenteService
    this.homeController = homeController;

    this.comboView.addCrearButtonListener(this);
    this.comboView.addEditarButtonListener(this);
    this.comboView.addEliminarButtonListener(this);
    this.comboView.addVolverButtonListener(this);

    loadCombosIntoTable();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == comboView.getBtnCrear()) {
      openCreateComboForm();
    } else if (e.getSource() == comboView.getBtnEditar()) {
      openEditComboForm();
    } else if (e.getSource() == comboView.getBtnEliminar()) {
      deleteCombo();
    } else if (e.getSource() == comboView.getBtnVolver()) {
      returnToHome();
    }
  }

  public void loadCombosIntoTable() {
    try {
      List<Combo> combos = comboService.getAllCombos();
      Object[][] data = new Object[combos.size()][4];

      for (int i = 0; i < combos.size(); i++) {
        Combo combo = combos.get(i);
        data[i][0] = combo.getComboID();
        data[i][1] = combo.getDescuentoPorciento().toPlainString() + "%";

        StringBuilder componentesStr = new StringBuilder();
        BigDecimal precioTotalEstimado = BigDecimal.ZERO;

        if (combo.getDetalles() != null && !combo.getDetalles().isEmpty()) {
          for (ComboDetalle detalle : combo.getDetalles()) {
            if (detalle.getComponente() != null) {
              componentesStr.append(detalle.getComponente().getTipoComponente())
                  .append(" (")
                  .append(detalle.getCantidad())
                  .append("x), ");
              precioTotalEstimado = precioTotalEstimado.add(
                  detalle.getComponente().getPrecio().multiply(new BigDecimal(detalle.getCantidad())));
            }
          }
          if (componentesStr.length() > 0) {
            componentesStr.setLength(componentesStr.length() - 2);
          }
        } else {
          componentesStr.append("Sin componentes");
        }
        data[i][2] = componentesStr.toString();

        data[i][3] = combo.calculateDiscountedPrice(precioTotalEstimado).setScale(2, BigDecimal.ROUND_HALF_UP)
            .toPlainString();
      }
      comboView.setTableData(data);
    } catch (SQLException ex) {
      comboView.showMessage("Error al cargar combos: " + ex.getMessage(), "Error de Base de Datos",
          JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    } catch (IllegalArgumentException ex) {
      comboView.showMessage(ex.getMessage(), "Error al cargar", JOptionPane.WARNING_MESSAGE);
      ex.printStackTrace();
    }
  }

  /**
   * Abre el formulario para crear un nuevo combo.
   */
  private void openCreateComboForm() {
    comboFormView = new ComboFormView(comboView); // Crear el diálogo modal
    comboFormController = new ComboFormController(comboFormView, comboService, componenteService, this); // Modo crear
    comboFormView.setVisible(true); // Mostrar el diálogo
  }

  /**
   * Abre el formulario para editar un combo existente.
   */
  private void openEditComboForm() {
    int selectedId = comboView.getSelectedComboId();
    if (selectedId == -1) {
      comboView.showMessage("Seleccione un combo de la tabla para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
      return;
    }
    comboFormView = new ComboFormView(comboView); // Crear el diálogo modal
    comboFormController = new ComboFormController(comboFormView, comboService, componenteService, this, selectedId); // Modo editar
    comboFormView.setVisible(true); // Mostrar el diálogo
  }

  private void deleteCombo() {
    int selectedId = comboView.getSelectedComboId();
    if (selectedId == -1) {
      comboView.showMessage("Seleccione un combo de la tabla para eliminar.", "Advertencia",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(comboView,
        "¿Está seguro de que desea eliminar el combo con ID " + selectedId + "?",
        "Confirmar Eliminación",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      try {
        comboService.deleteCombo(selectedId);
        comboView.showMessage("Combo eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        loadCombosIntoTable();
      } catch (IllegalArgumentException ex) {
        comboView.showMessage(ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
      } catch (SQLException ex) {
        comboView.showMessage("Error al eliminar combo: " + ex.getMessage(), "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
      }
    }
  }

  private void returnToHome() {
    comboView.hideView();
    homeController.showHomeView();
  }

  public void handleWindowClosed() {
    homeController.showHomeView();
  }

  public void refreshTable() {
    loadCombosIntoTable();
  }
}
