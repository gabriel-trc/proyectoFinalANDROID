package ort.proyecto_final.mvdmart.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Item {
    private String codigo;
    private int tipo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public Item(String codigo, int tipo) {
        this.codigo = codigo;
        this.tipo = tipo;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Codigo", this.codigo);
            jsonBody.put("Tipo", this.tipo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    @Override
    public String toString() {
        return (this.tipo == 0)?"Bolsa de sangre | "+this.codigo:"Botella de mezcla |"+this.codigo;
    }
}
