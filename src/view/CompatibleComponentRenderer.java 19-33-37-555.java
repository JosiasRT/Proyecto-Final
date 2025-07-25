// En un nuevo archivo: view/CompatibleComponentRenderer.java

package view;

import model.entity.*;
import javax.swing.*;
import java.awt.*;

public class CompatibleComponentRenderer extends DefaultListCellRenderer {
  private Componente selectedTM;
  private Componente selectedMP;
  private Componente selectedMR;

  public void setCompatibilitySources(Componente tm, Componente mp, Componente mr) {
    this.selectedTM = tm;
    this.selectedMP = mp;
    this.selectedMR = mr;
  }

  @Override
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
      boolean cellHasFocus) {
    // Llama al método padre para obtener el componente base (un JLabel)
    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    if (value instanceof Componente) {
      Componente currentComponent = (Componente) value;
      boolean isCompatible = checkCompatibility(currentComponent);

      if (!isCompatible) {
        c.setForeground(Color.LIGHT_GRAY);
        c.setEnabled(false);
      } else {
        c.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        c.setEnabled(true);
      }
    } else if (value == null) {
      setText(" (Ninguno) ");
    }

    return c;
  }

  private boolean checkCompatibility(Componente target) {
    // Si no hay nada seleccionado, todo es compatible
    if (selectedTM == null && selectedMP == null && selectedMR == null) {
      return true;
    }

    // Caso 1: Verificando una Tarjeta Madre
    if (target instanceof TarjetaMadre) {
      TarjetaMadre tm = (TarjetaMadre) target;
      if (selectedMP != null && !tm.getSocketMicro().equals(((Microprocesador) selectedMP).getSocket()))
        return false;
      if (selectedMR != null && !tm.getTipoMemoriaRam().equals(((MemoriaRAM) selectedMR).getTipoMemoria()))
        return false;
    }
    // Caso 2: Verificando un Microprocesador
    else if (target instanceof Microprocesador) {
      Microprocesador mp = (Microprocesador) target;
      if (selectedTM != null && !((TarjetaMadre) selectedTM).getSocketMicro().equals(mp.getSocket()))
        return false;
    }
    // Caso 3: Verificando una Memoria RAM
    else if (target instanceof MemoriaRAM) {
      MemoriaRAM mr = (MemoriaRAM) target;
      if (selectedTM != null && !((TarjetaMadre) selectedTM).getTipoMemoriaRam().equals(mr.getTipoMemoria()))
        return false;
    }
    // Caso 4: Verificando un Disco Duro
    else if (target instanceof DiscoDuro) {
      DiscoDuro dd = (DiscoDuro) target;
      if (selectedTM != null && !((TarjetaMadre) selectedTM).getConexionesDisco().contains(dd.getTipoConexion()))
        return false;
    }

    return true;
  }
}