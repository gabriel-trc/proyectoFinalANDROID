package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.helpers.StringWithTag;
import ort.proyecto_final.mvdmart.models.Partida;
import ort.proyecto_final.mvdmart.server_calls.RegistroMateriasPrimasServerCall;

public class RegistroMateriasPrimasActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Partida partidaToModify = null;
    private TextView tpHora;
    private EditText txtCantConservadoras, txtPeso, txtTemperatura, txtNCote;
    private Button btnAgregar, btnFinalizar;
    private int idFrigorifico, idCondicion, posFrigorifico, posCondicion, numOperario;
    private String condicion;
    private ArrayList<Partida> partidas = new ArrayList<>();
    private TableLayout tablaPartidas;
    private ArrayAdapter<StringWithTag> frigorificosArray, condicionesArray;
    private Spinner dropDownFrigorificos, dropDownCondicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_materias_primas);
        numOperario = Integer.parseInt(Config.getNumeroOperario(this));
        inicializarVistas();
    }

    private void inicializarVistas() {
        JSONArray obj = null;
        List<StringWithTag> frigorificos = null;
        tablaPartidas = (TableLayout) findViewById(R.id.tablaPartidas);
        dropDownFrigorificos = (Spinner) findViewById(R.id.ddFrigorificos);
        //esto hay que revisar, no se asegura que la llamada al servidor este completa
        try {
            obj = new JSONArray(Config.getFrigorificos(this));
            frigorificos = StringWithTag.convertJSONArrayToAarrayFrigorificos(obj);
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + Config.getFrigorificos(this) + "\"");
        }
        frigorificosArray = new ArrayAdapter<StringWithTag>(this, android.R.layout.simple_spinner_item, frigorificos);
        frigorificosArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownFrigorificos.setAdapter(frigorificosArray);
        dropDownFrigorificos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                idFrigorifico = s.tag;
                posFrigorifico = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropDownCondicion = (Spinner) findViewById(R.id.ddCondicion);
        condicionesArray = new ArrayAdapter<StringWithTag>(this, android.R.layout.simple_spinner_item, StringWithTag.arrayCondicionEnRegistroMaterias());
        condicionesArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownCondicion.setAdapter(condicionesArray);
        dropDownCondicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                idCondicion = s.tag;
                condicion = s.string;
                posCondicion = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AppCompatButton) v).getText().equals("Agregar"))
                    addPartida();
                else
                    modifyPartida();
            }
        });

        btnFinalizar = (Button) findViewById(R.id.btnFinalizar);
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!partidas.isEmpty()){
                    sendPartidas();
                }
            }
        });
/*
        El elemento tpHora no se encuentra mas en el layout
        tpHora = (TextView) findViewById(R.id.tpHora);
        tpHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
*/
        txtCantConservadoras = (EditText) findViewById(R.id.cantConservadoras);
        txtPeso = (EditText) findViewById(R.id.peso);
        txtTemperatura = (EditText) findViewById(R.id.temperatura);
        txtNCote = (EditText) findViewById(R.id.nroCote);
    }

    private void sendPartidas() {
        JSONObject send = new JSONObject();
        for(int i = 0; i < partidas.size(); i++ ){

            try {
                send.put(i+" ",partidas.get(i).toJSONObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new RegistroMateriasPrimasServerCall(this,send);
    }

    private void addPartida() {
        String fechaHora = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date());
        if (HelpersFunctions.isIntegerParseInt(txtCantConservadoras.getText().toString()) && HelpersFunctions.isIntegerParseInt(txtPeso.getText().toString()) &&
                HelpersFunctions.isIntegerParseInt(txtTemperatura.getText().toString())) {
            int bolsas = Integer.parseInt(txtCantConservadoras.getText().toString());
            int peso = Integer.parseInt(txtPeso.getText().toString());
            int temp = Integer.parseInt(txtTemperatura.getText().toString());
            String nCote = txtNCote.getText().toString();
            // if (Partida.validar(bolsas, temp)) {
            if (true) {
                Partida partida = new Partida(idFrigorifico, bolsas, peso, temp, fechaHora, condicion, nCote, posFrigorifico, numOperario);
                partidas.add(partida);
                cleanFields();
                createRows();
                JSONObject send = partida.toJSONObject();

                new RegistroMateriasPrimasServerCall(this,send);
            } else {
                Toast errorToast = Toast.makeText(this.getApplicationContext(), "Atención: Hay campos incorrectos.", Toast.LENGTH_LONG);
                errorToast.show();
            }
        } else {
            Toast errorToast = Toast.makeText(this.getApplicationContext(), "Atención: Debe completar todos los campos.", Toast.LENGTH_LONG);
            errorToast.show();
        }
    }

    private void modifyPartida() {
        if (partidaToModify != null) {
            int indexPartida = partidas.indexOf(partidaToModify);
            partidas.get(indexPartida).setCantConservadoras(Integer.parseInt(txtCantConservadoras.getText().toString()));
            partidas.get(indexPartida).setPeso(Integer.parseInt(txtPeso.getText().toString()));
            partidas.get(indexPartida).setTemperatura(Integer.parseInt(txtTemperatura.getText().toString()));
            partidas.get(indexPartida).setNumCote(txtNCote.getText().toString());
            partidas.get(indexPartida).setIdFrigorifico(idFrigorifico);
            partidas.get(indexPartida).setPosFrigorifico(posFrigorifico);
            partidas.get(indexPartida).setCondicion(condicion);
            partidaToModify = null;
            btnAgregar.setText("Agregar");
            createRows();
        }
    }

    private void createRows() {
        tablaPartidas.removeAllViews();
        for (Partida partida : partidas) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setId(partida.getLocalId());
            final TextView rowFrigorifico = new TextView(this.getApplicationContext());
            rowFrigorifico.setText(frigorificosArray.getItem(partida.getPosFrigorifico()).string);
            rowFrigorifico.setTextColor(0xFF000000);
            rowFrigorifico.setBackgroundColor(Color.parseColor("#f7f7f7"));
            row.addView(rowFrigorifico);
            final TextView rowCantidadConservadoras = new TextView(this.getApplicationContext());
            rowCantidadConservadoras.setText(partida.getCantConservadoras() + "");
            rowCantidadConservadoras.setTextColor(0xFF000000);
            row.addView(rowCantidadConservadoras);
            final TextView rowPeso = new TextView(this.getApplicationContext());
            rowPeso.setText(partida.getPeso() + "");
            rowPeso.setTextColor(0xFF000000);
            row.addView(rowPeso);
            final TextView rowTemperatura = new TextView(this.getApplicationContext());
            rowTemperatura.setText(partida.getTemperatura() + "");
            rowTemperatura.setTextColor(0xFF000000);
            row.addView(rowTemperatura);
            final TextView rowCondicion = new TextView(this.getApplicationContext());
            rowCondicion.setText(partida.getCondicion() + " ");
            rowCondicion.setTextColor(0xFF000000);
            row.addView(rowCondicion);
            final TextView rowCote = new TextView(this.getApplicationContext());
            rowCote.setText(partida.getNumCote() + "");
            rowCote.setTextColor(0xFF000000);
            row.addView(rowCote);
            final Button rowEditBtn = new Button(this);
            rowEditBtn.setBackgroundResource(R.drawable.ic_edit_row);
            rowEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    questionEditPartida(((TableRow) v.getParent()).getId());
                }
            });
            row.addView(rowEditBtn);
            final Button rowDeleteBtn = new Button(this);
            rowDeleteBtn.setBackgroundResource(R.drawable.ic_delete_row);
            rowDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePartidaById(((TableRow) v.getParent()).getId());
                }
            });
            row.addView(rowDeleteBtn);
            tablaPartidas.addView(row);

            final TableRow trSep = new TableRow(this);
            TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trParamsSep.setMargins(0, 0, 0, 0);
            trSep.setLayoutParams(trParamsSep);
            TextView tvSep = new TextView(this);
            TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tvSepLay.span = 8;
            tvSep.setLayoutParams(tvSepLay);
            tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
            tvSep.setHeight(1);
            trSep.addView(tvSep);
            tablaPartidas.addView(trSep, trParamsSep);
        }
    }

    private void cleanFields() {
        txtCantConservadoras.setText("");
        txtPeso.setText("");
        txtTemperatura.setText("");
        txtNCote.setText("");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        tpHora.setText(hourOfDay + ":" + minute);
    }

    private Partida getPartidaById(int idPartida) {
        Partida ret = null;
        for (Partida partida : partidas) {
            if (partida.getLocalId() == idPartida) {
                ret = partida;
            }
        }
        return ret;
    }

    private void removePartidaById(int id) {
//investigar para crear el alert con un helperfunctino
        final int partidaId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroMateriasPrimasActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("¿Quiere borrar ese registro?");
        //   builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                partidas.remove(getPartidaById(partidaId));
                createRows();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void questionEditPartida(int partidaId) {
        partidaToModify = getPartidaById(partidaId);
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroMateriasPrimasActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("¿Quiere editar ese registro?");
        //builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                btnAgregar.setText("Modificar");
                setPartida(partidaToModify);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setPartida(Partida partida) {
        txtCantConservadoras.setText(partida.getCantConservadoras() + "");
        txtPeso.setText(partida.getPeso() + "");
        txtTemperatura.setText(partida.getTemperatura() + "");
        txtNCote.setText(partida.getNumCote() + "");
        dropDownFrigorificos.setSelection(posFrigorifico);
        dropDownCondicion.setSelection(posCondicion);
    }
}

