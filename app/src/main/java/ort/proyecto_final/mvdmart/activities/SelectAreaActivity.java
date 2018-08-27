package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
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
import ort.proyecto_final.mvdmart.server_calls.TraerTodosLosFrigorificosServerCall;
import ort.proyecto_final.mvdmart.server_calls.TraerTodasLasPartidasPendientesServerCall;

public class SelectAreaActivity extends ActivityMadre {

    private Button btnLogout,  btnRegistroSeparacion, btnIdentificacionBolsa, btnRegistroMaterias;
    private TextView txtOperario;
    private int idPartidaSelecionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area);
        spinnerLoader = findViewById(R.id.spinner_loader);
        iniciarLoader();
        new TraerTodosLosFrigorificosServerCall(this);
        inicializarVistas();
    }

    public void inicializarVistas() {
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
                new TraerTodasLasPartidasPendientesServerCall(SelectAreaActivity.this);
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

    //TODO en caso de reusar el sialogo con single chice, refactoring del metodo para poder rutilizarlo
    public void alertSelectPartida() {
        List<StringWithTag> partidas = null;
        try {
            JSONArray obj = new JSONArray(Config.getPartidasPendientes(this));
            partidas = StringWithTag.convertJSONArrayToAarrayPartidasPendientes(obj);
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + Config.getPartidasPendientes(this) + "\"");
        }
        final ListAdapter adaptador = new ArrayAdapter<StringWithTag>(this, android.R.layout.select_dialog_singlechoice, partidas);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SelectAreaActivity.this);
        mBuilder.setTitle("Seleccione una partida");
        if(adaptador.getCount() > 0){
            mBuilder.setSingleChoiceItems(adaptador, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StringWithTag partidaSeleccionada = (StringWithTag) ((AlertDialog) dialog).getListView().getItemAtPosition(which);
                    idPartidaSelecionada = partidaSeleccionada.tag;
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
                if (adaptador.getCount() != 0 && idPartidaSelecionada != -1) {
                    new ComenzarIdentificacionPartidaServerCall(SelectAreaActivity.this, idPartidaSelecionada);
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }


    @Override
    public void limpiarCampos() {

    }

    @Override
    public void customOnErrorResponseVolley(Object object) {

    }

    @Override
    public void customAlertFunction(Object object) {

    }

}
