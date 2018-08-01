package ort.proyecto_final.mvdmart.helpers;

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


}
