package model.entity;

import java.math.BigDecimal;

public class MemoriaRAM extends Componente {
    private String marca;
    private String capacidad;
    private String tipoMemoria;

    public MemoriaRAM(int componenteID, String numeroSerie, BigDecimal precio, int cantidadDisponible,
            String marca, String capacidad, String tipoMemoria) {
        super(componenteID, numeroSerie, "MemoriaRAM", precio, cantidadDisponible);
        this.marca = marca;
        this.capacidad = capacidad;
        this.tipoMemoria = tipoMemoria;
    }

    public MemoriaRAM(String numeroSerie, BigDecimal precio, int cantidadDisponible,
            String marca, String capacidad, String tipoMemoria) {
        super(numeroSerie, "MemoriaRAM", precio, cantidadDisponible);
        this.marca = marca;
        this.capacidad = capacidad;
        this.tipoMemoria = tipoMemoria;
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
        return "";
    } // RAM no suele tener "modelo" como CPU/MB, devuelve vacío

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipoMemoria() {
        return tipoMemoria;
    }

    public void setTipoMemoria(String tipoMemoria) {
        this.tipoMemoria = tipoMemoria;
    }

    // ¡NUEVO! Sobrescribir toString()
    @Override
    public String toString() {
        return "RAM: " + marca + " " + capacidad + " " + tipoMemoria + " - $"
                + getPrecio().setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
