package model.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Combo {
    private int comboID;
    private BigDecimal descuentoPorciento;
    private List<ComboDetalle> detalles; // Nueva lista para los detalles del combo
    private String nombre;

    // Constructor para crear un nuevo Combo (ID será generado por la DB)
    public Combo(BigDecimal descuentoPorciento) {
        this.descuentoPorciento = descuentoPorciento;
        this.detalles = new ArrayList<>(); // Inicializar la lista
    }

    // Constructor para recuperar un Combo existente de la DB
    public Combo(int comboID, BigDecimal descuentoPorciento) {
        this.comboID = comboID;
        this.descuentoPorciento = descuentoPorciento;
        this.detalles = new ArrayList<>(); // Inicializar la lista
    }

    // Constructor vacío
    public Combo() {
        this.detalles = new ArrayList<>();
    }

    // Getters
    public int getComboID() {
        return comboID;
    }

    public BigDecimal getDescuentoPorciento() {
        return descuentoPorciento;
    }

    public List<ComboDetalle> getDetalles() { // Getter para los detalles
        return detalles;
    }

    // Nuevo getter y setter para nombre
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Setters
    public void setComboID(int comboID) {
        this.comboID = comboID;
    }

    public void setDescuentoPorciento(BigDecimal descuentoPorciento) {
        this.descuentoPorciento = descuentoPorciento;
    }

    public void setDetalles(List<ComboDetalle> detalles) { // Setter para la lista completa de detalles
        this.detalles = detalles;
    }

    // Método para añadir un detalle individual (útil al construir desde el DAO)
    public void addDetalle(ComboDetalle detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
    }

    // Opcional: Método para calcular el precio total del combo (sin componentes
    // aquí, solo la lógica)
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        if (originalPrice == null || descuentoPorciento == null) {
            return originalPrice;
        }
        BigDecimal discountFactor = BigDecimal.ONE
                .subtract(descuentoPorciento.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP));
        return originalPrice.multiply(discountFactor);
    }
}
