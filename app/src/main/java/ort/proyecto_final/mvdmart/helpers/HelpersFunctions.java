package ort.proyecto_final.mvdmart.helpers;

import org.json.JSONArray;
import org.json.JSONException;

public class HelpersFunctions {

    public HelpersFunctions() {
    }

    public static boolean isIntegerParseInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {

        }
        return false;
    }

    public static String[] errores(JSONArray errores) throws JSONException {
        String titulo = "";
        String mensaje = "";

        int largoErrorArray = errores.length();
        if (largoErrorArray == 0) {
            titulo = "Error";
            mensaje = "Error desconocido";
        } else if(largoErrorArray == 1){
            titulo = "Error";
            mensaje = errores.getString(0);
        } else{
            titulo =  errores.getString(0);
            for (int i = 1; i < largoErrorArray; i++){
                mensaje += errores.getString(i);
                if(i + 1 < largoErrorArray){
                    mensaje += "\n";
                }
            }
        }
        return new String[]{titulo, mensaje};

    }


}
