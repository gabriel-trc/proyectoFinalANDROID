package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.models.Bolsa;
import ort.proyecto_final.mvdmart.server_calls.CancelarIdentificacionPartidaServerCall;
import ort.proyecto_final.mvdmart.server_calls.RegistroBolsasServerCall;
import ort.proyecto_final.mvdmart.server_calls.RegistroMateriasPrimasServerCall;

public class IdentificacionBolsasActivity extends ActivityMadre {

    private JSONObject partida;
    private TextView txtNombrePartida;
    private EditText pesoGramos;
    private Button btnFinalizar, btnCancelar, btnEnviarBolsas, btnAgregar;
    private int condicion, idPartidaSeleccionada;
    private static int nroUltimaBolsa, nroUltimaBolsaRecibido;
    private String codigoBolsa;
    private ArrayList<Bolsa> bolsas = new ArrayList<>();
    private TableLayout tablaBolsas;
    private Spinner spinnerCondicion;
    private Bolsa bolsa;
    private IdentificacionBolsasActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identificacion_bolsas);
        Intent intent = getIntent();
        idPartidaSeleccionada = intent.getIntExtra("idPartida", -1);
        inicializarVistas();
    }

    @Override
    public void inicializarVistas() {
        spinnerLoader = findViewById(R.id.spinner_loader);
        txtNombrePartida = findViewById(R.id.txtNombrePartida);
        try {
            JSONArray partidas = new JSONArray(Config.getPartidasPendientes(this));
            for (int i = 0; i < partidas.length(); i++) {
                if (partidas.getJSONObject(i).getInt("id") == idPartidaSeleccionada) {
                    partida = partidas.getJSONObject(i);
                    String[] fecha = partida.getString("fecha").substring(0,10).split("-");
                    txtNombrePartida.setText("PARTIDA DE " + partida.getJSONObject("frigorifico").getString("nombre").toUpperCase() + " (" + fecha[2] + "-" + fecha[1] + "-" + fecha[0] + ")" );
                    codigoBolsa = partida.getString("codigoUltimaBolsa");
                    nroUltimaBolsa = partida.getInt("nroUltimaBolsa");
                    nroUltimaBolsaRecibido = nroUltimaBolsa;
                    break;
                }
            }
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + Config.getPartidasPendientes(this) + "\"");
        }

        spinnerCondicion = findViewById(R.id.spinnerCondicion);
        spinnerCondicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                condicion = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //TODO refactor de nombres de id. poner diminutivo de que actividad pertenecen, al cerrar la aplicacio liberar parida
        pesoGramos = findViewById(R.id.pesoGramos);
        pesoGramos.setTransformationMethod(null);
        btnAgregar = findViewById(R.id.btnAgregarBolsa);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AppCompatButton) v).getText().equals(getResources().getString(R.string.btnAgregar)))
                    agregarBolsa();
                else
                    modificarBolsa();
                esconderTecado(IdentificacionBolsasActivity.this);
            }
        });
        btnEnviarBolsas = findViewById(R.id.btnAgregarBolsas);
        btnEnviarBolsas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarBolsas(false);
            }
        });
        btnFinalizar = findViewById(R.id.btnFinalizarIdentificacion);
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarBolsas(true);
            }
        });
        btnCancelar = findViewById(R.id.btnCancelarIdentificacion);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLoader();
                new CancelarIdentificacionPartidaServerCall(activity, idPartidaSeleccionada);
            }
        });
        tablaBolsas = findViewById(R.id.tablaBolsas);
    }

    private void agregarBolsa() {
        if (HelpersFunctions.isIntegerParseInt(pesoGramos.getText().toString())) {
            int peso = Integer.parseInt(pesoGramos.getText().toString());
            // if (true) {
            if (Bolsa.validar(peso)) {
                Bolsa bolsa = new Bolsa(peso, condicion);
                bolsas.add(bolsa);
                nroUltimaBolsa++;
                bolsa.generarCodigo(codigoBolsa, nroUltimaBolsa);
                limpiarCampos();
                crearTablasBolsas();
            } else {
                alert(IdentificacionBolsasActivity.this, new String[]{"DATOS INVÁLIDOS", "El peso debe de estar entre 0 y 2000."}, null);
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "Atención: Debe completar todos los campos.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void limpiarCampos() {
        pesoGramos.setText("");
        spinnerCondicion.setSelection(0);
    }

    @Override
    public void customOnErrorResponseVolley(Object object) {

    }

    @Override
    public void customAlertFunction(Object object) {
        HashMap<String, Integer> hashMap = (HashMap<String, Integer>) object;
        if (hashMap.get("funcion") == 1) {
            bolsa = getBolsaById(hashMap.get("id"));
            btnAgregar.setText(R.string.btnModificar);
            btnAgregar.setBackgroundResource(android.R.color.holo_orange_dark);
            setearBolsa(bolsa);
        } else {
            bolsas.remove(getBolsaById(hashMap.get("id")));
            crearTablasBolsas();
            nroUltimaBolsa--;
        }
    }

    public void limpiarTabla() {
        tablaBolsas.removeAllViews();
    }

    public void vaciarArrayBolsas() {
        bolsas = new ArrayList<>();
        tablaBolsas.removeAllViews();
    }

    private void crearTablasBolsas() {
        tablaBolsas.removeAllViews();
        Collections.sort(bolsas);
        for (int i = -1; i < bolsas.size(); i++) {
            TableRow fila = new TableRow(this);
            fila.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 8f));
            if (i < 0)
                fila.setBackgroundColor(Color.rgb(36, 123, 160));
            else {
                bolsas.get(i).setLocalId(i);
                fila.setId(i/*partidas.get(i).getLocalId()*/);
                fila.setBackgroundColor((i % 2 == 0) ? Color.rgb(112, 193, 179) : Color.rgb(178, 219, 191));
            }

            TextView columnaCodigo = new TextView(this.getApplicationContext());
            if (i < 0)
                columnaCodigo.setText("Código");
            else
                columnaCodigo.setText(bolsas.get(i).getCodigoBolsa());
            columnaCodigo.setTextColor(getResources().getColor(R.color.colorBlanco));
            columnaCodigo.setGravity(Gravity.CENTER);
            columnaCodigo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            fila.addView(columnaCodigo, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

            TextView columnaPeso = new TextView(this.getApplicationContext());
            if (i < 0)
                columnaPeso.setText("Peso");
            else
                columnaPeso.setText(bolsas.get(i).getPeso() + "");
            columnaPeso.setTextColor(getResources().getColor(R.color.colorBlanco));
            columnaPeso.setGravity(Gravity.CENTER);
            columnaPeso.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            fila.addView(columnaPeso, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

            TextView columnaCondicion = new TextView(this.getApplicationContext());
            if (i < 0)
                columnaCondicion.setText("Condición");
            else
                columnaCondicion.setText(spinnerCondicion.getItemAtPosition(bolsas.get(i).getCondicion()).toString());
            columnaCondicion.setTextColor(getResources().getColor(R.color.colorBlanco));
            columnaCondicion.setGravity(Gravity.CENTER);
            columnaCondicion.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            fila.addView(columnaCondicion, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));

            if (i < 0) {
                TextView columnaDescartar = new TextView(this.getApplicationContext());
                columnaDescartar.setText("Descartar");
                columnaDescartar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaDescartar.setGravity(Gravity.CENTER);
                columnaDescartar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaDescartar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.5f));
            } else {
                Button columnaDescartar = new Button(this);
                columnaDescartar.setText("DESCARTAR");
                columnaDescartar.setId(Integer.parseInt((bolsas.get(i).getCodigoBolsa()).substring(6)));
                columnaDescartar.setBackgroundResource(android.R.color.holo_blue_dark);
                columnaDescartar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaDescartar.setGravity(Gravity.CENTER);
                columnaDescartar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                columnaDescartar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDescarte(((TableRow) v.getParent()).getId(), v.getId());
                    }
                });
                fila.addView(columnaDescartar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.5f));
            }

            if (i < 0) {
                TextView columnaModificar = new TextView(this.getApplicationContext());
                columnaModificar.setText("Modificar");
                columnaModificar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaModificar.setGravity(Gravity.CENTER);
                columnaModificar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaModificar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
            } else {
                Button columnaModificar = new Button(this);
                columnaModificar.setText(R.string.btnModificar);
                columnaModificar.setBackgroundResource(android.R.color.holo_orange_dark);
                columnaModificar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaModificar.setGravity(Gravity.CENTER);
                columnaModificar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                columnaModificar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Integer> obj = new HashMap<>();
                        obj.put("funcion", 1);
                        obj.put("id", (((TableRow) v.getParent()).getId()));
                        alertDosBotones(IdentificacionBolsasActivity.this, new String[]{"Atención", "¿Quiere editar este registro?"}, obj);
                    }
                });
                fila.addView(columnaModificar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
            }

            if (i < 0) {
                TextView columnaBorrar = new TextView(this.getApplicationContext());
                columnaBorrar.setText("Borrar");
                columnaBorrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaBorrar.setGravity(Gravity.CENTER);
                columnaBorrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaBorrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.5f));
            } else {
                Button columnaBorrar = new Button(this);
                columnaBorrar.setText("BORRAR");
                columnaBorrar.setBackgroundResource(android.R.color.holo_red_dark);
                columnaBorrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaBorrar.setGravity(Gravity.CENTER);
                columnaBorrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                columnaBorrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Integer> obj = new HashMap<>();
                        obj.put("funcion", 2);
                        obj.put("id", (((TableRow) v.getParent()).getId()));
                        alertDosBotones(IdentificacionBolsasActivity.this, new String[]{"Atención", "¿Quiere borrar este registro?"}, obj);
                    }
                });
                fila.addView(columnaBorrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.5f));
            }
            tablaBolsas.addView(fila);

            final TableRow trSep = new TableRow(this);
            TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trParamsSep.setMargins(0, 0, 0, 0);
            trSep.setLayoutParams(trParamsSep);
            TextView tvSep = new TextView(this);
            TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tvSepLay.span = 6;
            tvSep.setLayoutParams(tvSepLay);
            tvSep.setBackgroundColor(Color.rgb(243, 255, 189));
            tvSep.setHeight(2);
            trSep.addView(tvSep);
            tablaBolsas.addView(trSep, trParamsSep);
        }
    }

    private void alertDescarte(int bolsaId, final int idBtn) {
        bolsa = getBolsaById(bolsaId);
        final Button btnDescartar = findViewById(idBtn);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Atención: Registrando Descarte");
        alert.setIcon(R.drawable.ic_alert);
        alert.setMessage("¿Porque se esta descartando la bolsa de sangre?:");
        final EditText razonDescarte = new EditText(this);
        android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100,60);
        razonDescarte.setLayoutParams(lp);
        alert.setView(razonDescarte);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = razonDescarte.getText().toString();
                if(value.length() == 0)
                    value = "No especificada.";
                bolsa.setRazonDescarte(value);
                btnDescartar.setBackgroundResource(android.R.color.holo_red_light);
                btnDescartar.setText("DESCARTADA");
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        return;
                    }
                });

        if (bolsa.getRazonDescarte() != "") {
            razonDescarte.setText(bolsa.getRazonDescarte());
            alert.setNeutralButton("Cancelar Descarte", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    bolsa.setRazonDescarte("");
                    Button btnDescarte = findViewById(idBtn);
                    btnDescarte.setBackgroundResource(android.R.color.holo_blue_dark);
                    btnDescarte.setText("DESCARTAR");
                    dialog.dismiss();
                }
            });
        }
        alert.show();
    }

    private void modificarBolsa() {
        if (bolsa != null) {
            bolsa.setPeso(Integer.parseInt(pesoGramos.getText().toString()));
            bolsa.setCondicion(condicion);
            btnAgregar.setText(R.string.btnAgregar);
            btnAgregar.setBackgroundResource(android.R.color.holo_green_dark);
            bolsa = null;
            limpiarCampos();
            crearTablasBolsas();
        }
    }

    private void enviarBolsas(boolean finalizar) {
        if (!bolsas.isEmpty()) {
            if (btnAgregar.getText().equals(getResources().getString(R.string.btnAgregar))) {
                JSONArray jsonBolsas = new JSONArray();
                for (int i = 0; i < bolsas.size(); i++) {
                    jsonBolsas.put(bolsas.get(i).toJSONObject());
                }
                iniciarLoader();
                new RegistroBolsasServerCall(this, jsonBolsas, idPartidaSeleccionada, finalizar);
            } else {
                Toast.makeText(IdentificacionBolsasActivity.this, "Atención: Debes terminar de modificar la bolsa.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(IdentificacionBolsasActivity.this, "Atención: No tienes ninguna partida.", Toast.LENGTH_LONG).show();
        }
    }

    private Bolsa getBolsaById(int idPartida) {
        Bolsa ret = null;
        for (Bolsa bolsa : bolsas) {
            if (bolsa.getLocalId() == idPartida) {
                ret = bolsa;
            }
        }
        return ret;
    }

    private void setearBolsa(Bolsa bolsa) {
        pesoGramos.setText(bolsa.getPeso() + "");
        spinnerCondicion.setSelection(bolsa.getCondicion());
    }

    private void regenerarCodigos() {
        int num = nroUltimaBolsaRecibido + 1;
        for (int i = 0; i < bolsas.size(); i++) {
            bolsas.get(i).generarCodigo(codigoBolsa, num++);
        }
    }

}
