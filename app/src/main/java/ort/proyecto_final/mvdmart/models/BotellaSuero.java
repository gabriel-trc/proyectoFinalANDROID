package ort.proyecto_final.mvdmart.models;

import org.json.JSONException;
import org.json.JSONObject;

public class BotellaSuero {
    private String codigo;
    private int cantidadOcupada;
    private int cantidadDisponible;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCantidadOcupada() {
        return cantidadOcupada;
    }

    public void setCantidadOcupada(int cantidadOcupada) {
        this.cantidadOcupada = cantidadOcupada;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public BotellaSuero(String codigo, int cantidadOcupada) {
        this.codigo = codigo;
        this.cantidadOcupada = cantidadOcupada;
        this.cantidadDisponible = 500 - cantidadOcupada;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Codigo", this.codigo);
            jsonBody.put("Cantidad", this.cantidadOcupada);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.cantidadOcupada + " ml";
    }
}
