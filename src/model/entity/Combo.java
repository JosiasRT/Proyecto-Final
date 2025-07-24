package model.entity;

import java.util.ArrayList;

public class Combo {
    private int idCombo;
    private float descuento;
    private ArrayList<Componente> componentesCombo;

    public Combo(int idCombo, float descuento) {
        this.idCombo = idCombo;
        this.descuento = descuento;
    }

    public int getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(int idCombo) {
        this.idCombo = idCombo;
    }

    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }

    public ArrayList<Componente> getComponentesCombo() {
        return componentesCombo;
    }

    public void setComponentesCombo(ArrayList<Componente> componentesCombo) {
        this.componentesCombo = componentesCombo;
    }


}
