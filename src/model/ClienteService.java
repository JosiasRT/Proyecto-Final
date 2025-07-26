package model;

import java.sql.SQLException;
import java.util.List;

public class ClienteService {
  private ClienteDAO clienteDAO;

  public ClienteService() {
    this.clienteDAO = new ClienteDAO();
  }

  public void addCliente(Cliente cliente) throws SQLException, IllegalArgumentException {

    if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
    }
    if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
      throw new IllegalArgumentException("El apellido del cliente no puede estar vacío.");
    }
    clienteDAO.insert(cliente);
  }

  public boolean updateCliente(Cliente cliente) throws SQLException, IllegalArgumentException {
    if (cliente.getClienteID() <= 0) {
      throw new IllegalArgumentException("ID de cliente inválido para actualización.");
    }
    if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
    }
    if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
      throw new IllegalArgumentException("El apellido del cliente no puede estar vacío.");
    }
    return clienteDAO.update(cliente);
  }

  public boolean deleteCliente(int clienteId) throws SQLException {
    if (clienteId <= 0) {
      throw new IllegalArgumentException("ID de cliente inválido para eliminación.");
    }
    return clienteDAO.delete(clienteId);
  }

  public Cliente getClienteById(int clienteId) throws SQLException {
    return clienteDAO.findById(clienteId);
  }

  public List<Cliente> getAllClientes() throws SQLException {
    return clienteDAO.findAll();
  }
}
