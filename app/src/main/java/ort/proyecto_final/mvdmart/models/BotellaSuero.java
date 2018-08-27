package ort.proyecto_final.mvdmart.models;

import org.json.JSONException;
import org.json.JSONObject;

public class BotellaSuero {
    private String codigo;
    private int cantidad;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BotellaSuero(String codigo, int cantidad) {
        this.codigo = codigo;
        this.cantidad = cantidad;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Codigo", this.codigo);
            jsonBody.put("Cantidad", this.cantidad);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.cantidad + " ml";
    }
}
