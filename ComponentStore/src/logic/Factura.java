package logic;

import java.sql.Date;
import java.util.ArrayList;

public class Factura {
    private String idFactura;
    private ArrayList<Componente> componentes;
    private Cliente cliente;
    private float precioTotal;
    private Combo comboVendido;
    private Date fechaPedido;
    public Factura(String idFactura, Cliente cliente, float precioTotal, Combo comboVendido, Date fechaPedido) {
        this.idFactura = idFactura;
        this.cliente = cliente;
        this.precioTotal = precioTotal;
        this.comboVendido = comboVendido;
        this.fechaPedido = fechaPedido;
    }
    public String getIdFactura() {
        return idFactura;
    }
    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }
    public ArrayList<Componente> getComponentes() {
        return componentes;
    }
    public void setComponentes(ArrayList<Componente> componentes) {
        this.componentes = componentes;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public float getPrecioTotal() {
        return precioTotal;
    }
    public void setPrecioTotal(float precioTotal) {
        this.precioTotal = precioTotal;
    }
    public Combo getComboVendido() {
        return comboVendido;
    }
    public void setComboVendido(Combo comboVendido) {
        this.comboVendido = comboVendido;
    }
    public Date getFechaPedido() {
        return fechaPedido;
    }
    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    
}
