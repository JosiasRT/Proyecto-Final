package model.service; // Asegúrate de que este paquete coincida con tu estructura

import model.dao.ComboDAO; // Importa el DAO de combo
import model.dao.ComponenteDAO; // Necesario para la lógica de compatibilidad
import model.entity.Combo;
import model.entity.ComboDetalle;
import model.entity.Componente;
import model.entity.TarjetaMadre;
import model.entity.Microprocesador;
import model.entity.MemoriaRAM;
import model.entity.DiscoDuro;

import java.sql.SQLException;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ComboService {
  private ComboDAO comboDAO;
  private ComponenteDAO componenteDAO;

  public ComboService() {
    this.comboDAO = new ComboDAO();
    this.componenteDAO = new ComponenteDAO();
  }

  public Combo addCombo(Combo combo) throws IllegalArgumentException, SQLException {
    if (combo.getDescuentoPorciento() == null || combo.getDescuentoPorciento().compareTo(BigDecimal.ZERO) < 0
        || combo.getDescuentoPorciento().compareTo(new BigDecimal("100")) > 0) {
      throw new IllegalArgumentException("El porcentaje de descuento debe ser entre 0 y 100.");
    }
    if (combo.getDetalles() == null || combo.getDetalles().isEmpty()) {
      throw new IllegalArgumentException("Un combo debe contener al menos un componente.");
    }

    validateComboCompatibility(combo.getDetalles()); // Llama al método público

    return comboDAO.insertCombo(combo);
  }

  public void updateCombo(Combo combo, List<ComboDetalle> nuevosDetalles)
      throws IllegalArgumentException, SQLException {
    if (combo.getComboID() <= 0) {
      throw new IllegalArgumentException("ID de combo inválido para actualización.");
    }
    if (combo.getDescuentoPorciento() == null || combo.getDescuentoPorciento().compareTo(BigDecimal.ZERO) < 0
        || combo.getDescuentoPorciento().compareTo(new BigDecimal("100")) > 0) {
      throw new IllegalArgumentException("El porcentaje de descuento debe ser entre 0 y 100.");
    }
    if (nuevosDetalles == null || nuevosDetalles.isEmpty()) {
      throw new IllegalArgumentException("Un combo debe contener al menos un componente.");
    }

    validateComboCompatibility(nuevosDetalles); // Llama al método público

    comboDAO.updateCombo(combo, nuevosDetalles);
  }

  public void deleteCombo(int comboID) throws IllegalArgumentException, SQLException {
    if (comboID <= 0) {
      throw new IllegalArgumentException("ID de combo inválido para eliminación.");
    }
    comboDAO.deleteCombo(comboID);
  }

  public Combo getComboById(int comboID) throws SQLException {
    if (comboID <= 0) {
      throw new IllegalArgumentException("ID de combo inválido para búsqueda.");
    }
    return comboDAO.getComboById(comboID);
  }

  public List<Combo> getAllCombos() throws SQLException {
    return comboDAO.getAllCombos();
  }

  /**
   * Valida la compatibilidad de un conjunto de componentes para formar un combo.
   * Lanza una IllegalArgumentException si se encuentran incompatibilidades.
   * Este método ahora es PUBLIC para ser accesible desde ComboFormController.
   *
   * @param detallesCombo La lista de ComboDetalle que representa los componentes
   *                      propuestos para el combo.
   * @throws IllegalArgumentException Si los componentes son incompatibles.
   * @throws SQLException             Si ocurre un error al acceder a la base de
   *                                  datos para obtener detalles de componentes.
   */
  public void validateComboCompatibility(List<ComboDetalle> detallesCombo)
      throws IllegalArgumentException, SQLException { // CAMBIO: Ahora es PUBLIC
    TarjetaMadre tarjetaMadre = null;
    Microprocesador microprocesador = null;
    MemoriaRAM memoriaRAM = null;
    // DiscoDuro discoDuro = null; // No se inicializa aquí para permitir múltiples

    // Extraer los componentes principales del combo para la validación
    for (ComboDetalle detalle : detallesCombo) {
      Componente comp = componenteDAO.getComponenteById(detalle.getComponenteID());
      if (comp == null) {
        throw new IllegalArgumentException(
            "Componente con ID " + detalle.getComponenteID() + " no encontrado en el inventario.");
      }
      // Asignar el componente completo al detalle para futuras referencias
      detalle.setComponente(comp);

      switch (comp.getTipoComponente()) {
        case "TarjetaMadre":
          if (tarjetaMadre != null)
            throw new IllegalArgumentException("Un combo solo puede tener una Tarjeta Madre.");
          tarjetaMadre = (TarjetaMadre) comp;
          break;
        case "Microprocesador":
          if (microprocesador != null)
            throw new IllegalArgumentException("Un combo solo puede tener un Microprocesador.");
          microprocesador = (Microprocesador) comp;
          break;
        case "MemoriaRAM":
          // Se pueden tener varias RAM, pero la compatibilidad se valida con el tipo de
          // la primera
          if (memoriaRAM == null)
            memoriaRAM = (MemoriaRAM) comp;
          break;
        case "DiscoDuro":
          // No necesitamos una variable única para DiscoDuro aquí, ya que la validación
          // es con la TM
          break;
      }
    }

    // Reglas de compatibilidad obligatorias
    if (tarjetaMadre == null) {
      throw new IllegalArgumentException("Un combo de PC completo debe incluir una Tarjeta Madre.");
    }
    if (microprocesador == null) {
      throw new IllegalArgumentException("Un combo de PC completo debe incluir un Microprocesador.");
    }
    if (memoriaRAM == null) {
      throw new IllegalArgumentException("Un combo de PC completo debe incluir al menos una Memoria RAM.");
    }
    // Validar que haya al menos un Disco Duro
    boolean hasDiscoDuro = detallesCombo.stream()
        .anyMatch(d -> d.getComponente() != null && d.getComponente().getTipoComponente().equals("DiscoDuro"));
    if (!hasDiscoDuro) {
      throw new IllegalArgumentException("Un combo de PC completo debe incluir al menos un Disco Duro.");
    }

    // 1. Tarjeta Madre y Microprocesador: Socket
    if (!tarjetaMadre.getSocketMicro().equals(microprocesador.getSocket())) {
      throw new IllegalArgumentException(
          "Incompatibilidad: El Socket de la Tarjeta Madre (" + tarjetaMadre.getSocketMicro() +
              ") no coincide con el Socket del Microprocesador (" + microprocesador.getSocket() + ").");
    }

    // 2. Tarjeta Madre y Memoria RAM: Tipo de Memoria RAM
    // Validar cada RAM seleccionada
    for (ComboDetalle detalle : detallesCombo) {
      if (detalle.getComponente() instanceof MemoriaRAM) {
        MemoriaRAM currentRam = (MemoriaRAM) detalle.getComponente();
        if (!tarjetaMadre.getTipoMemoriaRam().equals(currentRam.getTipoMemoria())) {
          throw new IllegalArgumentException(
              "Incompatibilidad: El Tipo de Memoria RAM de la Tarjeta Madre (" + tarjetaMadre.getTipoMemoriaRam() +
                  ") no coincide con el Tipo de Memoria RAM (" + currentRam.getTipoMemoria() + ") en el combo.");
        }
      }
    }

    // 3. Tarjeta Madre y Disco Duro: Conexiones de Disco
    // Validar cada Disco Duro seleccionado
    for (ComboDetalle detalle : detallesCombo) {
      if (detalle.getComponente() instanceof DiscoDuro) {
        DiscoDuro currentDd = (DiscoDuro) detalle.getComponente();
        if (tarjetaMadre.getConexionesDisco() != null
            && !tarjetaMadre.getConexionesDisco().contains(currentDd.getTipoConexion())) {
          throw new IllegalArgumentException(
              "Incompatibilidad: El Tipo de Conexión del Disco Duro (" + currentDd.getTipoConexion() +
                  ") no es soportado por la Tarjeta Madre (" + tarjetaMadre.getConexionesDisco() + ").");
        }
      }
    }
  }

  public List<Componente> getCompatibleComponents(Componente selectedComponent, String targetType) throws SQLException {
    List<Componente> allTargetComponents = componenteDAO.getComponentesByType(targetType);
    List<Componente> compatibleComponents = new ArrayList<>();

    if (selectedComponent == null) {
      return allTargetComponents;
    }

    for (Componente targetComponent : allTargetComponents) {
      boolean compatible = false;
      if (selectedComponent instanceof TarjetaMadre) {
        TarjetaMadre tm = (TarjetaMadre) selectedComponent;
        if (targetComponent instanceof Microprocesador) {
          Microprocesador mp = (Microprocesador) targetComponent;
          compatible = tm.getSocketMicro().equals(mp.getSocket());
        } else if (targetComponent instanceof MemoriaRAM) {
          MemoriaRAM mr = (MemoriaRAM) targetComponent;
          compatible = tm.getTipoMemoriaRam().equals(mr.getTipoMemoria());
        } else if (targetComponent instanceof DiscoDuro) {
          DiscoDuro dd = (DiscoDuro) targetComponent;
          compatible = tm.getConexionesDisco().contains(dd.getTipoConexion());
        }
      } else if (selectedComponent instanceof Microprocesador) {
        Microprocesador mp = (Microprocesador) selectedComponent;
        if (targetComponent instanceof TarjetaMadre) {
          TarjetaMadre tm = (TarjetaMadre) targetComponent;
          compatible = mp.getSocket().equals(tm.getSocketMicro());
        }
      } else if (selectedComponent instanceof MemoriaRAM) {
        MemoriaRAM mr = (MemoriaRAM) selectedComponent;
        if (targetComponent instanceof TarjetaMadre) {
          TarjetaMadre tm = (TarjetaMadre) targetComponent;
          compatible = mr.getTipoMemoria().equals(tm.getTipoMemoriaRam());
        }
      } else if (selectedComponent instanceof DiscoDuro) {
        DiscoDuro dd = (DiscoDuro) selectedComponent;
        if (targetComponent instanceof TarjetaMadre) {
          TarjetaMadre tm = (TarjetaMadre) targetComponent;
          compatible = tm.getConexionesDisco().contains(dd.getTipoConexion());
        }
      }
      if (compatible) {
        compatibleComponents.add(targetComponent);
      }
    }
    return compatibleComponents;
  }

  // Permite crear un combo desde el controlador reescrito
  public void crearCombo(BigDecimal descuento, List<ComboDetalle> detalles) throws SQLException {
    Combo combo = new Combo();
    combo.setDescuentoPorciento(descuento);
    combo.setDetalles(detalles);
    addCombo(combo);
  }

  // Permite actualizar un combo desde el controlador reescrito
  public void actualizarCombo(int comboId, BigDecimal descuento, List<ComboDetalle> detalles) throws SQLException {
    Combo combo = getComboById(comboId);
    if (combo == null) throw new IllegalArgumentException("Combo no encontrado para actualizar");
    combo.setDescuentoPorciento(descuento);
    combo.setDetalles(detalles);
    updateCombo(combo, detalles);
  }
}
