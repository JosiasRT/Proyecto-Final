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
}
