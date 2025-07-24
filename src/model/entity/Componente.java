package model.entity;

public abstract class Componente {
    protected String numeroSerie;
    protected int id;
    protected float precio;
    protected int cantidadDisponible;
    protected String marca;

    public Componente(String numeroSerie, int id, float precio, int cantidadDisponible, String marca) {
        this.numeroSerie = numeroSerie;
        this.id = id;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
        this.marca = marca;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }
    public int getId() {
        return id;
    }
    public float getPrecio() {
        return precio;
    }
    public void setPrecio(float precio) {
        this.precio = precio;
    }
    public int getCantidadDisponible() {
        return cantidadDisponible;
    }
    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }
    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }


}
