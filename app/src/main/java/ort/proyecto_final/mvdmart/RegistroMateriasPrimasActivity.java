package ort.proyecto_final.mvdmart;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ort.proyecto_final.mvdmart.helpers.StringWithTag;
import ort.proyecto_final.mvdmart.helpers.TimePickerFragment;
import ort.proyecto_final.mvdmart.server_calls.RegistroMateriasPrimas;

public class RegistroMateriasPrimasActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    TextView tpHora;
    EditText txtCantBolsas, txtPeso, txtTemperatura, txtNCote;
    Button btnAgregar;
    int bolsas, idFrigorifico, peso, temp, idCondicion, nCote;
    String hora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_materias_primas);


        Spinner dropDownFrigorificos = (Spinner) findViewById(R.id.ddFrigorificos);
        ArrayAdapter<StringWithTag> adap = new ArrayAdapter<StringWithTag>(this, android.R.layout.simple_spinner_item, StringWithTag.arrayFrigorificos());
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownFrigorificos.setAdapter(adap);
        dropDownFrigorificos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                idFrigorifico = s.tag;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "nada seleccinoado", Toast.LENGTH_SHORT).show();
            }
        });

        Spinner dropDownCondicion = (Spinner) findViewById(R.id.ddCondicion);
        ArrayAdapter<StringWithTag> adap2 = new ArrayAdapter<StringWithTag>(this, android.R.layout.simple_spinner_item, StringWithTag.arrayCondicionEnRegistroMaterias());
        adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownCondicion.setAdapter(adap2);
        dropDownCondicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                idCondicion = s.tag;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "nada seleccinoado", Toast.LENGTH_SHORT).show();
            }
        });

        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerDatos();
            }
        });

        tpHora = (TextView) findViewById(R.id.tpHora);
        tpHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        txtCantBolsas = (EditText) findViewById(R.id.cantBolsas);
        txtPeso = (EditText) findViewById(R.id.peso);
        txtTemperatura = (EditText) findViewById(R.id.temperatura);
        txtNCote = (EditText) findViewById(R.id.nroCote);
    }

    private void obtenerDatos() {
        bolsas = Integer.parseInt(txtCantBolsas.getText().toString());
        peso = Integer.parseInt(txtPeso.getText().toString());
        temp = Integer.parseInt(txtTemperatura.getText().toString());
        nCote = Integer.parseInt(txtNCote.getText().toString());
        hora = tpHora.getText().toString();


        //validar que esten todos
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("frigorifico", idFrigorifico);
            jsonBody.put("bolsas", bolsas);
            jsonBody.put("peso", peso);
            jsonBody.put("temperatura", temp);
            jsonBody.put("hora", hora);
            jsonBody.put("condicion", idCondicion);
            jsonBody.put("nCote", nCote);
            RegistroMateriasPrimas registroMateriasPrimas = new RegistroMateriasPrimas(this, jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        tpHora.setText(hourOfDay + ":" + minute);
    }
}