package ort.proyecto_final.mvdmart.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;

import ort.proyecto_final.mvdmart.R;

public abstract class ActivityMadre extends AppCompatActivity implements View.OnClickListener {
    public ConstraintLayout spinnerLoader;
    public Handler handler = new Handler();

    public ActivityMadre() {
    }

    public abstract void inicializarVistas();

    public abstract void limpiarCampos();

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

    public void alert(Context contexto, String[] mensaje, final Object object) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(contexto);
        builder.setTitle(Html.fromHtml("<font color='#FF0000'>" + mensaje[0].toUpperCase() + "</font>"));
        builder.setMessage(mensaje[1]);
        builder.setIcon(R.drawable.ic_alert);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (object != null)
                    customServerModelError(object);
                dialog.dismiss();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void alertCheck(Context contexto, String[] mensaje, final boolean finalizar) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(contexto);
//        builder.setTitle(Html.fromHtml("<font color='#00FF00'>" + mensaje[0].toUpperCase() + "</font>"));
//        builder.setMessage(mensaje[1]);
        builder.setTitle(Html.fromHtml("<font color='#00FF00'>Registros guardados</font>"));
        builder.setMessage("Se han guardado todos los registros correctamente.");
        builder.setIcon(R.drawable.ic_check);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if(finalizar)
                    finish();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void alertDosBotones(Context contexto, String[] mensaje, final Object object) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(contexto);
        builder.setTitle(Html.fromHtml(mensaje[0].toUpperCase()));
        builder.setMessage(mensaje[1]);
        builder.setIcon(R.drawable.ic_alert);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (object != null)
                    customAlertFunction(object);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public abstract void customServerModelError(Object object);

    public abstract void customAlertFunction(Object object);

    public void iniciarLoader() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinnerLoader.setVisibility(View.VISIBLE);
    }

    public void finalizarLoader() {
        spinnerLoader.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public abstract void backButtonFunction();

    @Override
    public void onBackPressed() {
        HashMap<String, String> obj = new HashMap<>();
        obj.put("funcion", "-1");
        alertDosBotones(ActivityMadre.this, new String[]{"Atención: esta saliendo de la actividad.", "Si los hay, perderá los registros que no haya finalizado"}, obj);
    }

}
