package model;

public class Microprocesador extends Componente {
    private String modelo;
    private String tipoConector;
    private String velocidadPorcesamiento;
    public Microprocesador(String numeroSerie, int id, float precio, int cantidadDisponible, String marca,
            String modelo, String tipoConector, String velocidadPorcesamiento) {
        super(numeroSerie, id, precio, cantidadDisponible, marca);
        this.modelo = modelo;
        this.tipoConector = tipoConector;
        this.velocidadPorcesamiento = velocidadPorcesamiento;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public String getTipoConector() {
        return tipoConector;
    }
    public void setTipoConector(String tipoConector) {
        this.tipoConector = tipoConector;
    }
    public String getVelocidadPorcesamiento() {
        return velocidadPorcesamiento;
    }
    public void setVelocidadPorcesamiento(String velocidadPorcesamiento) {
        this.velocidadPorcesamiento = velocidadPorcesamiento;
    }


}
