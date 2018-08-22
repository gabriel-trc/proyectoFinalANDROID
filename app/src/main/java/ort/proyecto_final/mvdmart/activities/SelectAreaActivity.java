package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.helpers.StringWithTag;
import ort.proyecto_final.mvdmart.server_calls.ComenzarIdentificacionPartidaServerCall;
import ort.proyecto_final.mvdmart.server_calls.GetAllFrigorificosServerCall;
import ort.proyecto_final.mvdmart.server_calls.GetAllPartidasPendientesServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerItemsIdentificadosServerCall;

public class SelectAreaActivity extends AppCompatActivity {

    private Button btnLogout,  btnRegistroSeparacion, btnIdentificacionBolsa, btnRegistroMaterias;
    private TextView txtOperario;
    public ConstraintLayout spinnerLoader;
    private int idPartida = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area);
        spinnerLoader = findViewById(R.id.spinner_loader);
        iniciarLoader();
        new GetAllFrigorificosServerCall(this);
        inicializarVistas();
    }

    private void inicializarVistas() {
        txtOperario = findViewById(R.id.txtOperario);
        txtOperario.setText("Número de operario: " + Config.getNumeroOperario(SelectAreaActivity.this));
        txtOperario.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToNextActivity);
                finish();
            }
        });
        btnRegistroMaterias = findViewById(R.id.btnRegistroMaterias);
        btnRegistroMaterias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToNextActivity = new Intent(getApplicationContext(), RegistroMateriasPrimasActivity.class);
                startActivity(goToNextActivity);
            }
        });

        btnIdentificacionBolsa = findViewById(R.id.btnIdentificacionBolsa);
        btnIdentificacionBolsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLoader();
                new GetAllPartidasPendientesServerCall(SelectAreaActivity.this);
            }
        });

        btnRegistroSeparacion= findViewById(R.id.btnRegistroSeparacion);
        btnRegistroSeparacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToNextActivity = new Intent(getApplicationContext(), SeparacionItemsActivity.class);
                startActivity(goToNextActivity);


            }
        });
    }

    public void alertSelectPartida() {
        List<StringWithTag> partidas = null;
        try {
            JSONArray obj = new JSONArray(Config.getPartidasPendientes(this));
            partidas = StringWithTag.convertJSONArrayToAarrayPartidasPendientes(obj);
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + Config.getFrigorificos(this) + "\"");
        }
        final ListAdapter adaptador = new ArrayAdapter<StringWithTag>(this, android.R.layout.select_dialog_singlechoice, partidas);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SelectAreaActivity.this);
        mBuilder.setTitle("Seleccione una partida");
        if(adaptador.getCount() > 0){
            mBuilder.setSingleChoiceItems(adaptador, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StringWithTag partidaSeleccionada = (StringWithTag) ((AlertDialog) dialog).getListView().getItemAtPosition(which);
                    idPartida = partidaSeleccionada.tag;
                }
            });
        }else{
            mBuilder.setMessage("No hay partidas pendientes para identificar.");
        }
        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (adaptador.getCount() != 0 && idPartida != -1) {
                    new ComenzarIdentificacionPartidaServerCall(SelectAreaActivity.this, idPartida);
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


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
