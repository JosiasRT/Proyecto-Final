package model.entity;

import java.math.BigDecimal;

public class TarjetaMadre extends Componente {
    private String marca;
    private String modelo;
    private String socketMicro;
    private String tipoMemoriaRam;
    private String conexionesDisco;

    public TarjetaMadre(int componenteID, String numeroSerie, BigDecimal precio, int cantidadDisponible,
            String marca, String modelo, String socketMicro, String tipoMemoriaRam, String conexionesDisco) {
        super(componenteID, numeroSerie, "TarjetaMadre", precio, cantidadDisponible);
        this.marca = marca;
        this.modelo = modelo;
        this.socketMicro = socketMicro;
        this.tipoMemoriaRam = tipoMemoriaRam;
        this.conexionesDisco = conexionesDisco;
    }

    public TarjetaMadre(String numeroSerie, BigDecimal precio, int cantidadDisponible,
            String marca, String modelo, String socketMicro, String tipoMemoriaRam, String conexionesDisco) {
        super(numeroSerie, "TarjetaMadre", precio, cantidadDisponible);
        this.marca = marca;
        this.modelo = modelo;
        this.socketMicro = socketMicro;
        this.tipoMemoriaRam = tipoMemoriaRam;
        this.conexionesDisco = conexionesDisco;
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

    public String getSocketMicro() {
        return socketMicro;
    }

    public void setSocketMicro(String socketMicro) {
        this.socketMicro = socketMicro;
    }

    public String getTipoMemoriaRam() {
        return tipoMemoriaRam;
    }

    public void setTipoMemoriaRam(String tipoMemoriaRam) {
        this.tipoMemoriaRam = tipoMemoriaRam;
    }

    public String getConexionesDisco() {
        return conexionesDisco;
    }

    public void setConexionesDisco(String conexionesDisco) {
        this.conexionesDisco = conexionesDisco;
    }

    // ¡NUEVO! Sobrescribir toString()
    @Override
    public String toString() {
        return "TM: " + marca + " " + modelo + " (Socket: " + socketMicro + ", RAM: " + tipoMemoriaRam + ") - $"
                + getPrecio().setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
