package ort.proyecto_final.mvdmart.helpers;

import org.json.JSONArray;
import org.json.JSONException;

public class HelpersFunctions {

    public static boolean isIntegerParseInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {

        }
        return false;
    }

    public static boolean isDoubleParseDouble(String str) {
        try {
            Double.parseDouble(str);
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
        } else if (largoErrorArray == 1) {
            titulo = "Error";
            mensaje = errores.getString(0);
        } else {
            titulo = errores.getString(0);
            for (int i = 1; i < largoErrorArray; i++) {
                mensaje += errores.getString(i);
                if (i + 1 < largoErrorArray) {
                    mensaje += "\n";
                }
            }
        }
        return new String[]{titulo, mensaje};
    }

    public static String fechaEnFormato(int año, int mes, int dia) {
        int mesReal = mes + 1;
        String sMes = (mesReal < 10) ? "0" + mesReal : mesReal + "";
        String sDia = (dia < 10) ? "0" + dia : dia + "";
        return (sDia + "-" + sMes + "-" + año);
    }

    public static String horaEnFormato(int hora, int minutos) {
        String sHora = (hora < 10) ? "0" + hora : hora + "";
        String sMinutos = (minutos < 10) ? "0" + minutos : minutos + "";
        return (sHora + ":" + sMinutos);
    }


}
