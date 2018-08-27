package ort.proyecto_final.mvdmart.models;

import ort.proyecto_final.mvdmart.models.Item;

public class ExtraccionSueroDeBolsa extends Item {

    private String codigoBotellaDeSuero;
    private int cantidad;


    public String getCodigoBotellaDeSuero() {
        return codigoBotellaDeSuero;
    }

    public void setCodigoBotellaDeSuero(String codigoBotellaDeSuero) {
        this.codigoBotellaDeSuero = codigoBotellaDeSuero;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public ExtraccionSueroDeBolsa(String codigo, int tipo, String codigoBotellaDeSuero, int cantidad) {
        super(codigo, tipo);
        this.codigoBotellaDeSuero = codigoBotellaDeSuero;
        this.cantidad = cantidad;
    }
}
