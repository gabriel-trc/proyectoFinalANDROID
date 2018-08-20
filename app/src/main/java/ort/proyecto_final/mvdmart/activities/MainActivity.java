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

public class MainActivity extends AppCompatActivity {

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
    protected void onRestart(){
        super.onRestart();
        Config.setNumeroOperario(this, null);
    }

    private void inicializarVistas() {

        txtNumeroOperario = findViewById(R.id.txtNumeroOperario);
        txtNumeroOperario.setTransformationMethod(null);
        txtNumeroOperario.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    HelpersFunctions.esconderTecado(MainActivity.this);
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
            Toast toast = Toast.makeText(this.getApplicationContext(), "Ingrese correctamente su número de operario", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Config.setNumeroOperario(this, txtNumeroOperario.getText().toString());
            Intent goToNextActivity = new Intent(getApplicationContext(), SelectAreaActivity.class);
            startActivity(goToNextActivity);
        }
    }

    //Para mostrar solamente el teclado numerico, se usa el inputType numberPassword; luego con esta clase interna se transforma el * en el número ingresado.

}
