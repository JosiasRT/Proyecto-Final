package controller;

import model.Cliente;
import model.ClienteService;
import view.ClienteView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ClienteController implements ActionListener {
  private ClienteService clienteService;
  private ClienteView clienteView;

  public ClienteController(ClienteView view) {
    this.clienteService = new ClienteService();
    this.clienteView = view;

    // Asignar listeners a los botones de la vista
    this.clienteView.addAgregarListener(this);
    this.clienteView.addActualizarListener(this);
    this.clienteView.addEliminarListener(this);
    this.clienteView.addLimpiarListener(this);

    // Cargar los clientes en la tabla al iniciar la aplicación
    loadClientesIntoTable();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // Identificar el botón que disparó el evento
    if (e.getSource() == clienteView.getBtnAgregar()) {
      addCliente();
    } else if (e.getSource() == clienteView.getBtnActualizar()) {
      updateCliente();
    } else if (e.getSource() == clienteView.getBtnEliminar()) {
      deleteCliente();
    } else if (e.getSource() == clienteView.getBtnLimpiar()) {
      clienteView.clearFields();
    }
  }

  /**
   * Carga todos los clientes de la base de datos y los muestra en la tabla de la
   * vista.
   */
  private void loadClientesIntoTable() {
    try {
      List<Cliente> clientes = clienteService.getAllClientes();
      Object[][] data = new Object[clientes.size()][5]; // 5 columnas: ID, Nombre, Apellido, Email, Telefono

      for (int i = 0; i < clientes.size(); i++) {
        Cliente cliente = clientes.get(i);
        data[i][0] = cliente.getClienteID();
        data[i][1] = cliente.getNombre();
        data[i][2] = cliente.getApellido();
        data[i][3] = cliente.getEmail();
        data[i][4] = cliente.getTelefono();
      }
      clienteView.setTableData(data);
    } catch (SQLException ex) {
      clienteView.showMessage("Error al cargar clientes: " + ex.getMessage(), "Error de Base de Datos",
          JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace(); // Imprimir el stack trace para depuración
    }
  }

  /**
   * Agrega un nuevo cliente utilizando los datos de la vista.
   */
  private void addCliente() {
    String nombre = clienteView.getNombreInput();
    String apellido = clienteView.getApellidoInput();
    String email = clienteView.getEmailInput();
    String telefono = clienteView.getTelefonoInput();

    // Validaciones básicas del controlador
    if (nombre.isEmpty() || apellido.isEmpty()) {
      clienteView.showMessage("Nombre y Apellido son campos obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
      return;
    }

    Cliente nuevoCliente = new Cliente(nombre, apellido, email, telefono);
    try {
      clienteService.addCliente(nuevoCliente); // El controlador llama al servicio
      clienteView.showMessage("Cliente agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
      clienteView.clearFields(); // Limpiar campos después de agregar
      loadClientesIntoTable(); // Recargar la tabla
    } catch (IllegalArgumentException ex) {
      clienteView.showMessage(ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
    } catch (SQLException ex) {
      clienteView.showMessage("Error al agregar cliente: " + ex.getMessage(), "Error de Base de Datos",
          JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }

  /**
   * Actualiza un cliente existente utilizando los datos de la vista.
   */
  private void updateCliente() {
    int id = clienteView.getClienteIdInput();
    String nombre = clienteView.getNombreInput();
    String apellido = clienteView.getApellidoInput();
    String email = clienteView.getEmailInput();
    String telefono = clienteView.getTelefonoInput();

    // Validaciones
    if (id == -1) {
      clienteView.showMessage("Seleccione un cliente de la tabla para actualizar.", "Advertencia",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    if (nombre.isEmpty() || apellido.isEmpty()) {
      clienteView.showMessage("Nombre y Apellido son campos obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
      return;
    }

    Cliente clienteActualizado = new Cliente(id, nombre, apellido, email, telefono);
    try {
      boolean updated = clienteService.updateCliente(clienteActualizado); // El controlador llama al servicio
      if (updated) {
        clienteView.showMessage("Cliente actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        clienteView.clearFields();
        loadClientesIntoTable();
      } else {
        clienteView.showMessage("No se encontró el cliente con ID " + id + " para actualizar.", "Advertencia",
            JOptionPane.WARNING_MESSAGE);
      }
    } catch (IllegalArgumentException ex) {
      clienteView.showMessage(ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
    } catch (SQLException ex) {
      clienteView.showMessage("Error al actualizar cliente: " + ex.getMessage(), "Error de Base de Datos",
          JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }

  /**
   * Elimina un cliente utilizando el ID de la vista.
   */
  private void deleteCliente() {
    int id = clienteView.getClienteIdInput();

    if (id == -1) {
      clienteView.showMessage("Seleccione un cliente de la tabla para eliminar.", "Advertencia",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(clienteView,
        "¿Está seguro de que desea eliminar el cliente con ID " + id + "?",
        "Confirmar Eliminación",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      try {
        boolean deleted = clienteService.deleteCliente(id); // El controlador llama al servicio
        if (deleted) {
          clienteView.showMessage("Cliente eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
          clienteView.clearFields();
          loadClientesIntoTable();
        } else {
          clienteView.showMessage("No se encontró el cliente con ID " + id + " para eliminar.", "Advertencia",
              JOptionPane.WARNING_MESSAGE);
        }
      } catch (IllegalArgumentException ex) {
        clienteView.showMessage(ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
      } catch (SQLException ex) {
        clienteView.showMessage("Error al eliminar cliente: " + ex.getMessage(), "Error de Base de Datos",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
      }
    }
  }
}
