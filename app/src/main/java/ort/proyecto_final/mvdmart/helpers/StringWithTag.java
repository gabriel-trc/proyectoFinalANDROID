package ort.proyecto_final.mvdmart.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StringWithTag {

    public String string;
    public int tag;

    public StringWithTag(String stringPart, int tagPart) {
        string = stringPart;
        tag = tagPart;
    }

    public static List<StringWithTag> arrayCondicion() {
        List<StringWithTag> ret = new ArrayList<StringWithTag>();
        ret.add(new StringWithTag("ACEPTABLE", 1));
        ret.add(new StringWithTag("DAÑO MENOR", 2));
        ret.add(new StringWithTag("DAÑO MAYOR", 3));
        ret.add(new StringWithTag("COLOR INACEPTABLE", 4));
        return ret;
    }

    public static List<StringWithTag> convertJSONArrayToAarrayFrigorificos(JSONArray jsonArray) throws JSONException {
        List<StringWithTag> ret = new ArrayList<StringWithTag>();
        ret.add(new StringWithTag("Seleccione uno",  -1));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            ret.add(new StringWithTag(object.getString("Nombre"),  Integer.parseInt(object.getString("Codigo"))));
        }
        return ret;
    }

    public static List<StringWithTag> convertJSONArrayToAarrayPartidasPendientes(JSONArray jsonArray) throws JSONException {
        List<StringWithTag> ret = new ArrayList<StringWithTag>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String stringList = object.getJSONObject("frigorifico").getString("nombre");
            String[] fecha = object.getString("fecha").substring(0,10).split("-");
            stringList += ". Fecha: " + fecha[2] + "-" + fecha[1] + "-" + fecha[0];
            ret.add(new StringWithTag(stringList,  object.getInt("id")));
        }
        return ret;
    }


    @Override
    public String toString() {
        return string;
    }

}
