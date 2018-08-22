package ort.proyecto_final.mvdmart.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import ort.proyecto_final.mvdmart.R;

public abstract class ActivityMadre extends AppCompatActivity {
    public ConstraintLayout spinnerLoader;

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
                    customOnErrorResponseVolley(object);
                dialog.dismiss();
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

    public abstract void customOnErrorResponseVolley(Object object);

    public abstract void customAlertFunction(Object object);

    //region Manejo loader
    public void iniciarLoader() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinnerLoader.setVisibility(View.VISIBLE);
    }

    public void finalizarLoader() {
        spinnerLoader.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    //endregion
}
