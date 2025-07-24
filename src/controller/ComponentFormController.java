package controller; // Asegúrate de que este paquete coincida con la ubicación de tus controladores

import model.entity.*; // Importa todas las entidades de Componente
import model.service.ComponenteService; // Importa el servicio de componentes
import view.ComponentFormView; // Importa la vista del formulario de componente

import javax.swing.*;
import javax.swing.border.Border; // Para manejar los bordes de los campos
import javax.swing.border.LineBorder;
import java.awt.*; // Para Color
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ComponentFormController implements ActionListener {
  private ComponentFormView formView;
  private ComponenteService componenteService;
  private InventoryController inventoryController; // Para notificar al listado principal
  private int componenteIdToEdit = 0; // 0 para nuevo, >0 para editar

  // Mapa para almacenar los bordes originales de los JTextFields
  private Map<JTextField, Border> originalBorders;

  public ComponentFormController(ComponentFormView formView, ComponenteService componenteService,
      InventoryController inventoryController) {
    this.formView = formView;
    this.componenteService = componenteService;
    this.inventoryController = inventoryController;

    // Asignar listeners a los botones del formulario
    this.formView.addGuardarButtonListener(this);
    this.formView.addCancelarButtonListener(this);

    // Inicializar el mapa de bordes originales
    originalBorders = new HashMap<>();
    storeOriginalBorders();
  }

  // Constructor para el modo "Crear"
  public ComponentFormController(ComponentFormView formView, ComponenteService componenteService,
      InventoryController inventoryController, boolean isCreateMode) {
    this(formView, componenteService, inventoryController);
    if (isCreateMode) {
      this.componenteIdToEdit = 0; // Asegura que es modo creación
      this.formView.clearForm(); // Limpiar el formulario para un nuevo componente
    }
  }

  // Constructor para el modo "Editar"
  public ComponentFormController(ComponentFormView formView, ComponenteService componenteService,
      InventoryController inventoryController, int componenteIdToEdit) {
    this(formView, componenteService, inventoryController);
    this.componenteIdToEdit = componenteIdToEdit;
    loadComponenteForEdit(componenteIdToEdit); // Cargar datos del componente para editar
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == formView.getBtnGuardar()) {
      saveOrUpdateComponente();
    } else if (e.getSource() == formView.getBtnCancelar()) {
      formView.dispose(); // Cerrar el diálogo sin guardar
    }
  }

  /**
   * Almacena los bordes originales de todos los JTextFields para poder
   * restaurarlos.
   */
  private void storeOriginalBorders() {
    originalBorders.put(formView.getTxtNumeroSerie(), formView.getTxtNumeroSerie().getBorder());
    originalBorders.put(formView.getTxtPrecio(), formView.getTxtPrecio().getBorder());
    originalBorders.put(formView.getTxtCantidadDisponible(), formView.getTxtCantidadDisponible().getBorder());

    // Almacenar bordes de campos específicos (asumiendo que los getters existen en
    // formView)
    // Esto es un poco más complejo ya que los campos específicos se crean
    // dinámicamente.
    // Una forma sería pasar el mapa specificTextFields de la vista al controlador,
    // o acceder a ellos a través de los getters de la vista si los creamos.
    // Por simplicidad en este ejemplo, nos enfocaremos en los campos comunes y los
    // que se acceden directamente.
    // Para los campos específicos, la validación dinámica se hará en validateForm()
  }

  /**
   * Restaura los bordes originales de todos los JTextFields.
   */
  private void resetFieldBorders() {
    for (Map.Entry<JTextField, Border> entry : originalBorders.entrySet()) {
      entry.getKey().setBorder(entry.getValue());
    }
    // También limpiar bordes de campos específicos si se han modificado
    // Esto requeriría un getter para specificTextFields en ComponentFormView
    for (JTextField field : formView.getSpecificTextFields().values()) {
      if (field != null) {
        field.setBorder(UIManager.getBorder("TextField.border")); // Restaurar borde por defecto de Swing
      }
    }
  }

  /**
   * Carga los datos de un componente existente en el formulario para edición.
   *
   * @param id El ID del componente a cargar.
   */
  private void loadComponenteForEdit(int id) {
    try {
      Componente componente = componenteService.getComponenteById(id);
      if (componente != null) {
        formView.setComponenteData(componente); // La vista se encarga de rellenar los campos
      } else {
        formView.showMessage("Componente no encontrado para edición.", "Error", JOptionPane.ERROR_MESSAGE);
        formView.dispose(); // Cerrar el diálogo si no se encuentra
      }
    } catch (SQLException ex) {
      formView.showMessage("Error al cargar componente para edición: " + ex.getMessage(), "Error de BD",
          JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
      formView.dispose();
    }
  }

  /**
   * Valida los campos del formulario y aplica feedback visual.
   *
   * @return true si todos los campos obligatorios y con formato correcto están
   *         llenos, false en caso contrario.
   */
  private boolean validateForm() {
    resetFieldBorders(); // Limpiar bordes anteriores
    boolean isValid = true;
    String errorMessage = "";

    // Validar campos comunes
    if (formView.getTxtNumeroSerie().getText().trim().isEmpty()) {
      formView.getTxtNumeroSerie().setBorder(new LineBorder(Color.RED, 2));
      errorMessage += "- Número de Serie es obligatorio.\n";
      isValid = false;
    }

    try {
      new BigDecimal(formView.getTxtPrecio().getText());
      if (new BigDecimal(formView.getTxtPrecio().getText()).compareTo(BigDecimal.ZERO) <= 0) {
        formView.getTxtPrecio().setBorder(new LineBorder(Color.RED, 2));
        errorMessage += "- El precio debe ser un valor positivo.\n";
        isValid = false;
      }
    } catch (NumberFormatException e) {
      formView.getTxtPrecio().setBorder(new LineBorder(Color.RED, 2));
      errorMessage += "- Precio debe ser un número válido.\n";
      isValid = false;
    }

    try {
      int cantidad = Integer.parseInt(formView.getTxtCantidadDisponible().getText());
      if (cantidad < 0) {
        formView.getTxtCantidadDisponible().setBorder(new LineBorder(Color.RED, 2));
        errorMessage += "- Cantidad Disponible no puede ser negativa.\n";
        isValid = false;
      }
    } catch (NumberFormatException e) {
      formView.getTxtCantidadDisponible().setBorder(new LineBorder(Color.RED, 2));
      errorMessage += "- Cantidad Disponible debe ser un número entero válido.\n";
      isValid = false;
    }

    String tipoComponente = formView.getSelectedComponentType();
    if ("Seleccione".equals(tipoComponente)) {
      // No hay un componente visual directo para el ComboBox, así que solo mensaje
      errorMessage += "- Seleccione un tipo de componente.\n";
      isValid = false;
    } else {
      // Validar campos específicos según el tipo seleccionado
      switch (tipoComponente) {
        case "TarjetaMadre":
          if (formView.getSpecificTextFields().get("TarjetaMadre_Marca").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("TarjetaMadre_Marca").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Marca de Tarjeta Madre es obligatoria.\n";
            isValid = false;
          }
          if (formView.getSpecificTextFields().get("TarjetaMadre_Modelo").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("TarjetaMadre_Modelo").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Modelo de Tarjeta Madre es obligatorio.\n";
            isValid = false;
          }
          if (formView.getSpecificTextFields().get("TarjetaMadre_SocketMicro").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("TarjetaMadre_SocketMicro").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Socket Micro de Tarjeta Madre es obligatorio.\n";
            isValid = false;
          }
          // Add more validations for TarjetaMadre specific fields
          break;
        case "Microprocesador":
          if (formView.getSpecificTextFields().get("Microprocesador_Marca").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("Microprocesador_Marca").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Marca de Microprocesador es obligatoria.\n";
            isValid = false;
          }
          if (formView.getSpecificTextFields().get("Microprocesador_Modelo").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("Microprocesador_Modelo").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Modelo de Microprocesador es obligatorio.\n";
            isValid = false;
          }
          if (formView.getSpecificTextFields().get("Microprocesador_Socket").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("Microprocesador_Socket").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Socket de Microprocesador es obligatorio.\n";
            isValid = false;
          }
          // Add more validations for Microprocesador specific fields
          break;
        case "MemoriaRAM":
          if (formView.getSpecificTextFields().get("MemoriaRAM_Marca").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("MemoriaRAM_Marca").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Marca de Memoria RAM es obligatoria.\n";
            isValid = false;
          }
          if (formView.getSpecificTextFields().get("MemoriaRAM_Capacidad").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("MemoriaRAM_Capacidad").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Capacidad de Memoria RAM es obligatoria.\n";
            isValid = false;
          }
          // Add more validations for MemoriaRAM specific fields
          break;
        case "DiscoDuro":
          if (formView.getSpecificTextFields().get("DiscoDuro_Marca").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("DiscoDuro_Marca").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Marca de Disco Duro es obligatoria.\n";
            isValid = false;
          }
          if (formView.getSpecificTextFields().get("DiscoDuro_Modelo").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("DiscoDuro_Modelo").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Modelo de Disco Duro es obligatorio.\n";
            isValid = false;
          }
          if (formView.getSpecificTextFields().get("DiscoDuro_Capacidad").getText().trim().isEmpty()) {
            formView.getSpecificTextFields().get("DiscoDuro_Capacidad").setBorder(new LineBorder(Color.RED, 2));
            errorMessage += "- Capacidad de Disco Duro es obligatoria.\n";
            isValid = false;
          }
          // Add more validations for DiscoDuro specific fields
          break;
      }
    }

    if (!isValid) {
      formView.showMessage("Por favor, corrija los siguientes errores:\n" + errorMessage, "Errores de Validación",
          JOptionPane.WARNING_MESSAGE);
    }
    return isValid;
  }

  /**
   * Guarda o actualiza un componente en la base de datos.
   */
  private void saveOrUpdateComponente() {
    if (!validateForm()) {
      return; // No proceder si la validación falla
    }

    Componente componente = formView.getComponenteFromForm(componenteIdToEdit);
    if (componente == null) {
      // getComponenteFromForm ya muestra un mensaje de error si falla la conversión
      return;
    }

    try {
      if (componenteIdToEdit == 0) { // Modo Crear
        componenteService.addComponente(componente);
        formView.showMessage("Componente agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
      } else { // Modo Editar
        componenteService.updateComponente(componente);
        formView.showMessage("Componente actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
      }
      formView.dispose(); // Cerrar el formulario
      inventoryController.loadComponentesIntoTable(); // Notificar al controlador de inventario para recargar la tabla
    } catch (IllegalArgumentException ex) {
      formView.showMessage(ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
    } catch (SQLException ex) {
      formView.showMessage("Error de base de datos al guardar componente: " + ex.getMessage(), "Error de BD",
          JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
}
