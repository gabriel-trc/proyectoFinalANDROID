package ort.proyecto_final.mvdmart.models;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Bolsa implements Comparable<Bolsa> {

    private int localId, condicion, peso;
    private String razonDescarte, codigoBolsa;

    //region Helper Getter's and Setter's

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getCondicion() {
        return condicion;
    }

    public void setCondicion(int condicion) {
        this.condicion = condicion;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public String getRazonDescarte() {
        return razonDescarte;
    }

    public void setRazonDescarte(String razonDescarte) {
        this.razonDescarte = razonDescarte;
    }

    public String getCodigoBolsa() {
        return codigoBolsa;
    }

    public void setCodigoBolsa(String codigoBolsa) {
        this.codigoBolsa = codigoBolsa;
    }
    //endregion

    public Bolsa(String pCodigo) {
        this.codigoBolsa = pCodigo;
    }

    public Bolsa(int pPeso, int pCondicion) {
        this.peso = pPeso;
        this.condicion = pCondicion;
    }

    public static boolean validar(int pPeso, int pCondicion) {
        if (pCondicion >= 0 && pCondicion <= 3 && pPeso >= 0 && pPeso <= 2000)
            return true;
        else
            return false;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Peso", this.peso);
            jsonBody.put("Condicion", this.condicion);
            jsonBody.put("RazonDescarte", this.razonDescarte);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    public void generarCodigo(String codigoBolsa, int nroUltimaBolsa) {
        int largoNroUltima = (nroUltimaBolsa + "").length();
        String codigo = codigoBolsa.substring(0, codigoBolsa.length() - largoNroUltima);
        setCodigoBolsa(codigo + nroUltimaBolsa);
    }

    @Override
    public int compareTo(@NonNull Bolsa b) {
        return this.codigoBolsa.compareTo(b.codigoBolsa);
    }

    @Override
    public String toString() {
        return this.codigoBolsa;
    }
}
