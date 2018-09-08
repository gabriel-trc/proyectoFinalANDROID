package ort.proyecto_final.mvdmart.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Item {
    private String codigo;
    private int tipo;
    private int localId;
    private boolean finalizado;

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

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
        this.finalizado = false;
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

    public static HashMap<String, List<Item>> itemsParaSeparar(JSONObject itemsIdentificados) throws JSONException {
        HashMap<String, List<Item>> itemsParaIdentificar = new HashMap<>();
        JSONArray partidasConBolsas = itemsIdentificados.getJSONArray("partidasConBolsas");
        JSONArray botellas = itemsIdentificados.getJSONArray("botellasDeMezcla");
        for (int i = 0; i < partidasConBolsas.length(); i++) {
            JSONObject partida = partidasConBolsas.getJSONObject(i);
            JSONArray bolsas = partida.getJSONArray("bolsaDeSangre");
            ArrayList<Item> bolsasDeSangre = new ArrayList<>();
            for (int j = 0; j < bolsas.length(); j++) {
                bolsasDeSangre.add(new Item(bolsas.getJSONObject(j).getString("codigo"), 0));
            }
            String[] fecha = partida.getString("fechaCompleta").substring(0, 10).split("-");
            String llavePartida = partida.getJSONObject("frigorifico").getString("nombre") + " - " + fecha[2] + "-" + fecha[1] + "-" + fecha[0];
            itemsParaIdentificar.put(llavePartida, bolsasDeSangre);
        }
        ArrayList<Item> botellasDeMezcla = new ArrayList<>();
        if (botellas.length() > 0) {
            for (int i = 0; i < botellas.length(); i++) {
                JSONObject botellaDeMezcla = botellas.getJSONObject(i);
                botellasDeMezcla.add(new Item(botellaDeMezcla.getString("codigo"), 1));
            }
            String llaveBotellas = "Botellas de mezcla";
            itemsParaIdentificar.put(llaveBotellas, botellasDeMezcla);
        }
        return itemsParaIdentificar;
    }

    @Override
    public String toString() {
        return (this.tipo == 0) ? "Bolsa de sangre | " + this.codigo : "Botella de mezcla | " + this.codigo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj == null)
            return false;
        else
            return this.codigo.equals(((Item) obj).getCodigo());
    }

}
