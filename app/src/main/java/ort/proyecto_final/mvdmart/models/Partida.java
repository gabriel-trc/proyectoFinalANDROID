package ort.proyecto_final.mvdmart.models;

import android.text.Editable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Partida {

    private int localId, numeroOperario, idFrigorifico, cantConservadoras, temperatura, peso, posFrigorifico;
    private String fechaHora, numCote;

    //region Helper Getter's and Setter's

    public int getLocalId() { return localId; }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getNumeroOperario() {
        return numeroOperario;
    }

    public void setNumeroOperario(int numeroOperario) {
        this.numeroOperario = numeroOperario;
    }

    public int getIdFrigorifico() {
        return idFrigorifico;
    }

    public void setIdFrigorifico(int idFrigorifico) {
        this.idFrigorifico = idFrigorifico;
    }

    public int getCantConservadoras() {
        return cantConservadoras;
    }

    public void setCantConservadoras(int cantConservadoras) { this.cantConservadoras = cantConservadoras; }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getNumCote() {
        return numCote;
    }

    public void setNumCote(String numCote) {
        this.numCote = numCote;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public int getPosFrigorifico() {
        return posFrigorifico;
    }

    public void setPosFrigorifico(int posFrigorifico) {
        this.posFrigorifico = posFrigorifico;
    }
    //endregion

    public Partida() {

    }

    public Partida(int idFrigorifico, int cantConservadoras, int peso, int temperatura, String fechaHora, String numCote, int posFrigorifico, int numeroOperario) {
        this.idFrigorifico = idFrigorifico;
        this.cantConservadoras = cantConservadoras;
        this.peso = peso;
        this.temperatura = temperatura;
        this.fechaHora = fechaHora;
        this.numCote = numCote;
        this.posFrigorifico = posFrigorifico;
        this.numeroOperario = numeroOperario;
    }

    public static boolean validar(int cantConservadoras, int temperatura, int pesoTotal, String numeroCote, int posFrigorifico) {
        if (cantConservadoras > 0 && cantConservadoras < 100 && temperatura > -100 && temperatura < 100 && numeroCote.length() < 50 &&
        pesoTotal > 0 && pesoTotal < 2000 && posFrigorifico >= 1)
            return true;
        else
            return false;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Fecha", this.fechaHora);
            jsonBody.put("CodigoFrigorifico", this.idFrigorifico);
            jsonBody.put("NumeroDeCote", this.numCote);
            jsonBody.put("Temperatura", this.temperatura);
            jsonBody.put("PesoTotal", this.peso);
            jsonBody.put("CantidadDeConservadoras", this.cantConservadoras);
            jsonBody.put("OperarioId", this.numeroOperario);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

}
