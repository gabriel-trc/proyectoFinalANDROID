package ort.proyecto_final.mvdmart.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ExtraccionMezcla extends Item {

    private String codigoBotellaMezcla;

    public String getCodigoBotellaMezcla() {
        return codigoBotellaMezcla;
    }

    public void setCodigoBotellaMezcla(String codigoBotellaMezcla) {
        this.codigoBotellaMezcla = codigoBotellaMezcla;
    }

    public ExtraccionMezcla(String codigo, int tipo, String codigoBotellaMezcla) {
        super(codigo, tipo);
        this.codigoBotellaMezcla = codigoBotellaMezcla;
    }

    public JSONObject toJSON() {
        JSONObject jsonBody = new JSONObject();
        JSONObject item = new JSONObject();
        try {
            item.put("codigo", this.getCodigo());
            item.put("tipo", this.getTipo());
            jsonBody.put("item",item);
            jsonBody.put("codigoBotellaDeMezcla",this.codigoBotellaMezcla);
            jsonBody.put("botellaDeMezclaCompletada",this.isFinalizado());
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
        else if (getClass() != obj.getClass())
            return false;
        else
            return (this.getCodigoBotellaMezcla().equals(((ExtraccionMezcla) obj).getCodigoBotellaMezcla()) && this.getCodigo().equals(((ExtraccionMezcla) obj).getCodigo()));
    }
}
