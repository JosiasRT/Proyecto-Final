package logic;

public class Ram extends Componente{
    private String cantidadMemoria;
    private String tipoMemoria;
    public Ram(String numeroSerie, int id, float precio, int cantidadDisponible, String marca, String cantidadMemoria,
            String tipoMemoria) {
        super(numeroSerie, id, precio, cantidadDisponible, marca);
        this.cantidadMemoria = cantidadMemoria;
        this.tipoMemoria = tipoMemoria;
    }
    public String getCantidadMemoria() {
        return cantidadMemoria;
    }
    public void setCantidadMemoria(String cantidadMemoria) {
        this.cantidadMemoria = cantidadMemoria;
    }
    public String getTipoMemoria() {
        return tipoMemoria;
    }
    public void setTipoMemoria(String tipoMemoria) {
        this.tipoMemoria = tipoMemoria;
    }

    
}
