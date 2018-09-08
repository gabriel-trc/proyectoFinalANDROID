package ort.proyecto_final.mvdmart.models;

import org.json.JSONException;
import org.json.JSONObject;

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

    public JSONObject toJSON() {
        JSONObject jsonBody = new JSONObject();
        JSONObject item = new JSONObject();
        try {
            item.put("codigo", this.getCodigo());
            item.put("tipo", this.getTipo());
            jsonBody.put("item",item);
            jsonBody.put("itemFinalizado",this.isFinalizado());
            jsonBody.put("codigoBotellaDeSuero",this.codigoBotellaDeSuero);
            jsonBody.put("cantidad",this.cantidad);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj == null)
            return false;
        else
            return (this.getCodigo().equals(((ExtraccionSueroDeBolsa) obj).getCodigo()) && this.getCodigoBotellaDeSuero().equals(((ExtraccionSueroDeBolsa) obj).getCodigoBotellaDeSuero()));
    }


}
