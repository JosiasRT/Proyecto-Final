package model.entity; // Asegúrate de que este paquete coincida con tu estructura

public class ComboDetalle {
  private int comboID;
  private int componenteID;
  private int cantidad;

  // Opcional: Referencia al objeto Componente para facilitar el acceso a sus
  // detalles
  private Componente componente;

  public ComboDetalle(int comboID, int componenteID, int cantidad) {
    this.comboID = comboID;
    this.componenteID = componenteID;
    this.cantidad = cantidad;
  }

  // Constructor con objeto Componente (útil al recuperar de la DB)
  public ComboDetalle(int comboID, int componenteID, int cantidad, Componente componente) {
    this.comboID = comboID;
    this.componenteID = componenteID;
    this.cantidad = cantidad;
    this.componente = componente;
  }

  // Constructor vacío
  public ComboDetalle() {}

  // Getters
  public int getComboID() {
    return comboID;
  }

  public int getComponenteID() {
    return componenteID;
  }

  public int getCantidad() {
    return cantidad;
  }

  public Componente getComponente() {
    return componente;
  }

  // Setters
  public void setComboID(int comboID) {
    this.comboID = comboID;
  }

  public void setComponenteID(int componenteID) {
    this.componenteID = componenteID;
  }

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }

  public void setComponente(Componente componente) {
    this.componente = componente;
  }
}
