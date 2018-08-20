package ort.proyecto_final.mvdmart.models;

import ort.proyecto_final.mvdmart.models.Item;

public class ExtraccionSueroDeBolsa extends Item {

    private String codigoBotellaDeSuero;
    private Double cantidad;
    private int localId;

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public String getCodigoBotellaDeSuero() {
        return codigoBotellaDeSuero;
    }

    public void setCodigoBotellaDeSuero(String codigoBotellaDeSuero) {
        this.codigoBotellaDeSuero = codigoBotellaDeSuero;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public ExtraccionSueroDeBolsa(String codigo, int tipo, String codigoBotellaDeSuero, Double cantidad) {
        super(codigo, tipo);
        this.codigoBotellaDeSuero = codigoBotellaDeSuero;
        this.cantidad = cantidad;
    }
}
