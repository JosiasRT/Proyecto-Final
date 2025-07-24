package model;

public class TarjetaMadre extends Componente {
    private String modelo;
    private String tipoConector;
    private String tipoMemoriaRam;

    public TarjetaMadre(String numeroSerie, int id, float precio, int cantidadDisponible, String marca, String modelo,
            String tipoConector, String tipoMemoriaRam) {
        super(numeroSerie, id, precio, cantidadDisponible, marca);
        this.modelo = modelo;
        this.tipoConector = tipoConector;
        this.tipoMemoriaRam = tipoMemoriaRam;
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

    public String getTipoMemoriaRam() {
        return tipoMemoriaRam;
    }

    public void setTipoMemoriaRam(String tipoMemoriaRam) {
        this.tipoMemoriaRam = tipoMemoriaRam;
    }


}
