package model.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List; // Necesario si Combo.java usa List

public abstract class Componente {
    protected int componenteID;
    protected String numeroSerie;
    protected String tipoComponente;
    protected BigDecimal precio;
    protected int cantidadDisponible;

    public Componente(int componenteID, String numeroSerie, String tipoComponente, BigDecimal precio,
            int cantidadDisponible) {
        this.componenteID = componenteID;
        this.numeroSerie = numeroSerie;
        this.tipoComponente = tipoComponente;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
    }

    public Componente(String numeroSerie, String tipoComponente, BigDecimal precio, int cantidadDisponible) {
        this.numeroSerie = numeroSerie;
        this.tipoComponente = tipoComponente;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
    }

    // Getters y Setters (los mismos que tenías)
    public int getComponenteID() {
        return componenteID;
    }

    public void setComponenteID(int componenteID) {
        this.componenteID = componenteID;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getTipoComponente() {
        return tipoComponente;
    }

    public void setTipoComponente(String tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public abstract String getMarca();

    public abstract String getModelo();

    // ¡NUEVO! Sobrescribir toString() para una visualización significativa en
    // JComboBox
    @Override
    public String toString() {
        return tipoComponente + " - Serie: " + numeroSerie + " ($" + precio.setScale(2, BigDecimal.ROUND_HALF_UP) + ")";
    }
}
