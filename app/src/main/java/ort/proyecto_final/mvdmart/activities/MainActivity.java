package ort.proyecto_final.mvdmart.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.server_calls.LoginUsuarioServerCall;

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
                ingresar();
            }
        });
    }

    public void ingresar() {
        if (!HelpersFunctions.isIntegerParseInt(txtNumeroOperario.getText().toString())) {
            Toast.makeText(this.getApplicationContext(), "Ingrese correctamente su número de operario", Toast.LENGTH_LONG).show();
            limpiarCampos();
        } else {
            iniciarLoader();
            new LoginUsuarioServerCall(this, txtNumeroOperario.getText().toString());
        }
    }

    @Override
    public void limpiarCampos() {
        txtNumeroOperario.setText("");
    }

    @Override
    public void customOnErrorResponseVolley(Object object) {

    }

    @Override
    public void customAlertFunction(Object object) {

    }
}
