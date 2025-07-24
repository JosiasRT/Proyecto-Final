package model;

public class DiscoDuro extends Componente{
    private String modelo;
    private String cantidadAlmacenamiento;
    private String tipoConexion;
    public DiscoDuro(String numeroSerie, int id, float precio, int cantidadDisponible, String marca, String modelo,
            String cantidadAlmacenamiento, String tipoConexion) {
        super(numeroSerie, id, precio, cantidadDisponible, marca);
        this.modelo = modelo;
        this.cantidadAlmacenamiento = cantidadAlmacenamiento;
        this.tipoConexion = tipoConexion;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public String getCantidadAlmacenamiento() {
        return cantidadAlmacenamiento;
    }
    public void setCantidadAlmacenamiento(String cantidadAlmacenamiento) {
        this.cantidadAlmacenamiento = cantidadAlmacenamiento;
    }
    public String getTipoConexion() {
        return tipoConexion;
    }
    public void setTipoConexion(String tipoConexion) {
        this.tipoConexion = tipoConexion;
    }


}
