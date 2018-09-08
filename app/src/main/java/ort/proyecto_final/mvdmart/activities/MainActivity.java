package ort.proyecto_final.mvdmart.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.server_calls.LoginUsuarioServerCall;
import ort.proyecto_final.mvdmart.server_calls.TraerTodasLasPartidasPendientesServerCall;

public class MainActivity extends ActivityMadre {

    private Button btnIngresar;
    private EditText txtNumeroOperario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Config.setNumeroOperario(this, null);
        inicializarVistas();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Config.setNumeroOperario(this, null);
    }

    public void inicializarVistas() {
        final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        spinnerLoader = findViewById(R.id.spinner_loader);
        txtNumeroOperario = findViewById(R.id.txtNumeroOperario);
        txtNumeroOperario.setTransformationMethod(null);
        txtNumeroOperario.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    esconderTecado(MainActivity.this);
                }
                return false;
            }
        });
        btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
                v.startAnimation(scale);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ingresar();
                    }
                }, 300);
            }
        });
    }

    public void ingresar() {
        if (!HelpersFunctions.isIntegerParseInt(txtNumeroOperario.getText().toString())) {
            Toast.makeText(this.getApplicationContext(), "Ingrese correctamente su número de operario", Toast.LENGTH_LONG).show();
            limpiarCampos();
        } else {
            new LoginUsuarioServerCall(this, txtNumeroOperario.getText().toString());
        }
    }

    @Override
    public void limpiarCampos() {
        txtNumeroOperario.setText("");
    }

    @Override
    public void customServerModelError(Object object) {

    }

    @Override
    public void customAlertFunction(Object object) {
        HashMap<String, String> hashMap = (HashMap<String, String>) object;
        if (hashMap.get("funcion") == "-1") {
            backButtonFunction();
        }
    }

    @Override
    public void backButtonFunction() {
        finish();
    }
}