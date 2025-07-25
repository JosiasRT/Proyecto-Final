package model.entity;

import java.math.BigDecimal;

public class DiscoDuro extends Componente {
    private String marca;
    private String modelo;
    private String capacidad;
    private String tipoConexion;

    public DiscoDuro(int componenteID, String numeroSerie, BigDecimal precio, int cantidadDisponible,
            String marca, String modelo, String capacidad, String tipoConexion) {
        super(componenteID, numeroSerie, "DiscoDuro", precio, cantidadDisponible);
        this.marca = marca;
        this.modelo = modelo;
        this.capacidad = capacidad;
        this.tipoConexion = tipoConexion;
    }

    public DiscoDuro(String numeroSerie, BigDecimal precio, int cantidadDisponible,
            String marca, String modelo, String capacidad, String tipoConexion) {
        super(numeroSerie, "DiscoDuro", precio, cantidadDisponible);
        this.marca = marca;
        this.modelo = modelo;
        this.capacidad = capacidad;
        this.tipoConexion = tipoConexion;
    }

    @Override
    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    @Override
    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipoConexion() {
        return tipoConexion;
    }

    public void setTipoConexion(String tipoConexion) {
        this.tipoConexion = tipoConexion;
    }

    // ¡NUEVO! Sobrescribir toString()
    @Override
    public String toString() {
        return "HDD/SSD: " + marca + " " + modelo + " (" + capacidad + ", " + tipoConexion + ") - $"
                + getPrecio().setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
