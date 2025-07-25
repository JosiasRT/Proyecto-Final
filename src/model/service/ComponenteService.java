package model.service;

import model.dao.ComponenteDAO;
import model.entity.Componente;
import java.sql.SQLException;
import java.util.List;

// Importar clases específicas de componentes si ComponenteDAO las devuelve directamente.
// Por ejemplo:
import model.entity.TarjetaMadre;
import model.entity.Microprocesador;
import model.entity.MemoriaRAM;
import model.entity.DiscoDuro;

public class ComponenteService {
  private ComponenteDAO componenteDAO;

  public ComponenteService() {
    this.componenteDAO = new ComponenteDAO();
    System.out.println("DEBUG: ComponenteService - Instanciado ComponenteDAO.");
  }

  public List<Componente> getAllComponentes() throws SQLException {
    System.out.println("DEBUG: ComponenteService - Llamando a getAllComponentes().");
    return componenteDAO.getAllComponentes();
  }

  public List<Componente> getComponentesByType(String tipoComponente) throws SQLException {
    System.out.println("DEBUG: ComponenteService - Llamando a getComponentesByType() para tipo: " + tipoComponente);
    try {
      return componenteDAO.getComponentesByType(tipoComponente);
    } catch (SQLException e) {
      System.err.println("ERROR: ComponenteService - SQLException en getComponentesByType para " + tipoComponente + ": "
          + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  public Componente getComponenteById(int id) throws SQLException {
    System.out.println("DEBUG: ComponenteService - Llamando a getComponenteById() para ID: " + id);
    try {
      return componenteDAO.getComponenteById(id);
    } catch (SQLException e) {
      System.err.println(
          "ERROR: ComponenteService - SQLException en getComponenteById para ID " + id + ": " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  // --- MÉTODOS QUE PUEDEN ESTAR FALTANDO O MAL DEFINIDOS ---
  // Añade o verifica que estos métodos existan en tu ComponenteService
  public void addComponente(Componente componente) throws SQLException {
    System.out.println("DEBUG: ComponenteService - Llamando a addComponente().");
    componenteDAO.addComponente(componente);
  }

  public void updateComponente(Componente componente) throws SQLException {
    System.out.println("DEBUG: ComponenteService - Llamando a updateComponente().");
    componenteDAO.updateComponente(componente);
  }

  public void deleteComponente(int id) throws SQLException {
    System.out.println("DEBUG: ComponenteService - Llamando a deleteComponente().");
    componenteDAO.deleteComponente(id);
  }

  // Asegúrate de que los constructores de tus entidades Componente y sus
  // subclases sean públicos
  // y que los imports para TarjetaMadre, Microprocesador, MemoriaRAM, DiscoDuro
  // estén en los archivos donde se usan (ComponentFormController,
  // InventoryController, etc.)
}
