package model.service; // This package declaration matches your project structure

import model.dao.ComponenteDAO; // Ensure this import path is correct for your ComponenteDAO
import model.entity.Componente; // Import the base Componente class
import model.entity.TarjetaMadre; // Import specific component types for casting if needed
import model.entity.Microprocesador;
import model.entity.MemoriaRAM;
import model.entity.DiscoDuro;

import java.sql.SQLException;
import java.util.List;
import java.math.BigDecimal; // Import BigDecimal for price

public class ComponenteService {
  private ComponenteDAO componenteDAO;

  public ComponenteService() {
    this.componenteDAO = new ComponenteDAO();
  }

  /**
   * Adds a new component to the database.
   * Performs basic validations before calling the DAO.
   *
   * @param componente The Componente object to add.
   * @return The component with the ID assigned by the database.
   * @throws IllegalArgumentException If the component data is invalid.
   * @throws SQLException             If a database error occurs.
   */
  public Componente addComponente(Componente componente) throws IllegalArgumentException, SQLException {
    // Business validations at the service level
    if (componente.getNumeroSerie() == null || componente.getNumeroSerie().trim().isEmpty()) {
      throw new IllegalArgumentException("El número de serie es obligatorio.");
    }
    if (componente.getTipoComponente() == null || componente.getTipoComponente().trim().isEmpty()) {
      throw new IllegalArgumentException("El tipo de componente es obligatorio.");
    }
    // Ensure price is not null before comparison
    if (componente.getPrecio() == null || componente.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El precio debe ser un valor positivo.");
    }
    if (componente.getCantidadDisponible() < 0) {
      throw new IllegalArgumentException("La cantidad disponible no puede ser negativa.");
    }

    // Additional validations for specific component types (example)
    switch (componente.getTipoComponente()) {
      case "TarjetaMadre":
        TarjetaMadre tm = (TarjetaMadre) componente;
        if (tm.getMarca() == null || tm.getMarca().trim().isEmpty())
          throw new IllegalArgumentException("La marca de la Tarjeta Madre es obligatoria.");
        // Add more specific validations here
        break;
      case "Microprocesador":
        Microprocesador mp = (Microprocesador) componente;
        if (mp.getMarca() == null || mp.getMarca().trim().isEmpty())
          throw new IllegalArgumentException("La marca del Microprocesador es obligatoria.");
        // Add more specific validations here
        break;
      case "MemoriaRAM":
        MemoriaRAM mr = (MemoriaRAM) componente;
        if (mr.getMarca() == null || mr.getMarca().trim().isEmpty())
          throw new IllegalArgumentException("La marca de la Memoria RAM es obligatoria.");
        // Add more specific validations here
        break;
      case "DiscoDuro":
        DiscoDuro dd = (DiscoDuro) componente;
        if (dd.getMarca() == null || dd.getMarca().trim().isEmpty())
          throw new IllegalArgumentException("La marca del Disco Duro es obligatoria.");
        // Add more specific validations here
        break;
      default:
        // Handle unknown type or no specific validations
        break;
    }

    return componenteDAO.insertComponente(componente);
  }

  /**
   * Updates an existing component in the database.
   *
   * @param componente The Componente object with updated data.
   * @throws IllegalArgumentException If the component data is invalid or the ID
   *                                  is null.
   * @throws SQLException             If a database error occurs.
   */
  public void updateComponente(Componente componente) throws IllegalArgumentException, SQLException {
    if (componente.getComponenteID() <= 0) {
      throw new IllegalArgumentException("ID de componente inválido para actualización.");
    }
    if (componente.getNumeroSerie() == null || componente.getNumeroSerie().trim().isEmpty()) {
      throw new IllegalArgumentException("El número de serie es obligatorio.");
    }
    if (componente.getPrecio() == null || componente.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El precio debe ser un valor positivo.");
    }
    if (componente.getCantidadDisponible() < 0) {
      throw new IllegalArgumentException("La cantidad disponible no puede ser negativa.");
    }

    // Add specific validations for update if different from add
    // ...

    componenteDAO.updateComponente(componente);
  }

  /**
   * Deletes a component by its ID.
   *
   * @param componenteID The ID of the component to delete.
   * @throws IllegalArgumentException If the ID is invalid.
   * @throws SQLException             If a database error occurs.
   */
  public void deleteComponente(int componenteID) throws IllegalArgumentException, SQLException {
    if (componenteID <= 0) {
      throw new IllegalArgumentException("ID de componente inválido para eliminación.");
    }
    componenteDAO.deleteComponente(componenteID);
  }

  /**
   * Retrieves a component by its ID.
   *
   * @param componenteID The ID of the component.
   * @return The Componente object if found, or null if not.
   * @throws SQLException If a database error occurs.
   */
  public Componente getComponenteById(int componenteID) throws SQLException {
    if (componenteID <= 0) {
      throw new IllegalArgumentException("ID de componente inválido para búsqueda.");
    }
    return componenteDAO.getComponenteById(componenteID);
  }

  /**
   * Retrieves all components from the inventory.
   *
   * @return A list of Componente objects.
   * @throws SQLException If a database error occurs.
   */
  public List<Componente> getAllComponentes() throws SQLException {
    return componenteDAO.getAllComponentes();
  }

  /**
   * Retrieves components filtered by type.
   *
   * @param tipo The type of component to filter by (e.g., "TarjetaMadre",
   *             "Microprocesador").
   * @return A list of Componente objects of the specified type.
   * @throws IllegalArgumentException If the type is null or empty.
   * @throws SQLException             If a database error occurs.
   */
  public List<Componente> getComponentesByType(String tipo) throws IllegalArgumentException, SQLException {
    if (tipo == null || tipo.trim().isEmpty()) {
      throw new IllegalArgumentException("El tipo de componente para filtrar no puede estar vacío.");
    }
    return componenteDAO.getComponentesByType(tipo);
  }
}
