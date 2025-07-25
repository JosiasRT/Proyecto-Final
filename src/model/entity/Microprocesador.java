package model.entity;

import java.math.BigDecimal;

public class Microprocesador extends Componente {
    private String marca;
    private String modelo;
    private String socket;
    private String velocidad;

    public Microprocesador(int componenteID, String numeroSerie, BigDecimal precio, int cantidadDisponible,
            String marca, String modelo, String socket, String velocidad) {
        super(componenteID, numeroSerie, "Microprocesador", precio, cantidadDisponible);
        this.marca = marca;
        this.modelo = modelo;
        this.socket = socket;
        this.velocidad = velocidad;
    }

    public Microprocesador(String numeroSerie, BigDecimal precio, int cantidadDisponible,
            String marca, String modelo, String socket, String velocidad) {
        super(numeroSerie, "Microprocesador", precio, cantidadDisponible);
        this.marca = marca;
        this.modelo = modelo;
        this.socket = socket;
        this.velocidad = velocidad;
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

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(String velocidad) {
        this.velocidad = velocidad;
    }

    // ¡NUEVO! Sobrescribir toString()
    @Override
    public String toString() {
        return "CPU: " + marca + " " + modelo + " (" + velocidad + ", Socket: " + socket + ") - $"
                + getPrecio().setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
