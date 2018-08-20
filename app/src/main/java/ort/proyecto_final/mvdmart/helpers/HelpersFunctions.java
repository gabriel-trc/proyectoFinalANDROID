package ort.proyecto_final.mvdmart.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;

import ort.proyecto_final.mvdmart.R;

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

    public static void esconderTecado(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static android.app.AlertDialog alertDatosInvalidos(Context contexto, String mensaje) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(contexto);
        builder.setTitle(Html.fromHtml("<font color='#FF0000'>DATOS INVÁLIDOS</font>"));
        builder.setMessage(mensaje);
        builder.setIcon(R.drawable.ic_alert);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        android.app.AlertDialog alert = builder.create();
        return alert;
    }


}
