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

    public static List<StringWithTag> arrayCondicionEnRegistroMaterias() {
        List<StringWithTag> ret = new ArrayList<StringWithTag>();
        ret.add(new StringWithTag("Seleccione una condición",  -1));
        ret.add(new StringWithTag("ACEPTABLE", 1));
        ret.add(new StringWithTag("DAÑO MENOR", 2));
        ret.add(new StringWithTag("DAÑO MAYOR", 3));
        ret.add(new StringWithTag("COLOR INACEPTABLE", 4));
        return ret;
    }

    public static List<StringWithTag> convertJSONArrayToAarrayFrigorificos(JSONArray jsonArray) throws JSONException {
        List<StringWithTag> ret = new ArrayList<StringWithTag>();
        ret.add(new StringWithTag("Seleccione un frigorifico",  -1));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            ret.add(new StringWithTag(object.getString("Nombre"),  Integer.parseInt(object.getString("Codigo"))));
        }
        return ret;
    }
    
    public static List<StringWithTag> arrayFrigorificos() {
        List<StringWithTag> ret = new ArrayList<StringWithTag>();
        ret.add(new StringWithTag("COLONIA S.A.", 1));
        ret.add(new StringWithTag("LAS PIEDRAS S.A.", 2));
        ret.add(new StringWithTag("CANELONES S.A.", 3));
        ret.add(new StringWithTag("MAT. CARRASCO S.A.", 4));
        ret.add(new StringWithTag("SCHNECK", 5));
        ret.add(new StringWithTag("MAT. PANDO", 6));
        ret.add(new StringWithTag("DURAZNO", 7));
        ret.add(new StringWithTag("LA CABALLADA", 8));
        ret.add(new StringWithTag("CATTIVELLI HNOS.", 9));
        ret.add(new StringWithTag("SAN JACINTO", 10));
        ret.add(new StringWithTag("LAS MORAS", 11));
        ret.add(new StringWithTag("SARUBBI", 12));
        return ret;
    }

    @Override
    public String toString() {
        return string;
    }

}
