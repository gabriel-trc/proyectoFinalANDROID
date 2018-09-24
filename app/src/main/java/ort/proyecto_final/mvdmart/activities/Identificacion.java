package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

public class Identificacion extends ActivityMadre {

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
    private HashMap<String, String> hashMapCustomAlertFunction = new HashMap<>();


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
                    String[] fecha = partida.getString("fecha").substring(0, 10).split("-");
                    txtNombrePartida.setText("PARTIDA DE " + partida.getJSONObject("frigorifico").getString("nombre").toUpperCase() + " (" + fecha[2] + "-" + fecha[1] + "-" + fecha[0] + ")");
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
        btnAgregar = findViewById(R.id.btn_ib_AgregarBolsa);
        btnAgregar.setOnClickListener(this);
        btnEnviarBolsas = findViewById(R.id.btn_ib_AgregarBolsas);
        btnEnviarBolsas.setOnClickListener(this);
        btnFinalizar = findViewById(R.id.btn_ib_Finalizar);
        btnFinalizar.setOnClickListener(this);
        btnCancelar = findViewById(R.id.btn_ib_Cancelar);
        btnCancelar.setOnClickListener(this);
        tablaBolsas = findViewById(R.id.tablaBolsas);
    }

    private void agregarBolsa() {
        if (HelpersFunctions.isDoubleParseDouble(pesoGramos.getText().toString())) {
            Double peso = Double.parseDouble(pesoGramos.getText().toString());
            // if (true) {
            if (Bolsa.validar(peso)) {
                Bolsa bolsa = new Bolsa(peso, condicion);
                bolsas.add(bolsa);
                nroUltimaBolsa++;
                bolsa.generarCodigo(codigoBolsa, nroUltimaBolsa);
                limpiarCampos();
                crearTablasBolsas();
            } else {
                alert(Identificacion.this, new String[]{"DATOS INVÁLIDOS", "El peso debe de estar entre 1 y 2000."}, null);
            }
        } else {
            alert(Identificacion.this, new String[]{"ATENCIÓN", "No se ha ingresado el peso de la bolsa."}, null);
        }
    }

    @Override
    public void limpiarCampos() {
        pesoGramos.setText("");
        spinnerCondicion.setSelection(0);
    }

    @Override
    public void customServerModelError(Object object) {

    }

    public void limpiarTabla() {
        bolsas = new ArrayList<>();
        tablaBolsas.removeAllViews();
    }

    private void crearTablasBolsas() {
        final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        tablaBolsas.removeAllViews();
        if (bolsas.size() > 0) {
            Collections.sort(bolsas);
            for (int i = -1; i < bolsas.size(); i++) {
                TableRow fila = new TableRow(this);
                fila.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 15.5f));
                if (i < 0)
                    fila.setBackgroundColor(getResources().getColor(R.color.colorTituloTabla));
                else {
                    bolsas.get(i).setLocalId(i);
                    fila.setId(i/*partidas.get(i).getLocalId()*/);
                    fila.setBackgroundColor((i % 2 == 0) ? getResources().getColor(R.color.colorFilaImpar) : getResources().getColor(R.color.colorFilaPar));
                }

                TextView columnaCodigo = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaCodigo.setText("Código");
                else
                    columnaCodigo.setText(bolsas.get(i).getCodigoBolsa());
                columnaCodigo.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCodigo.setGravity(Gravity.CENTER);
                columnaCodigo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaCodigo, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.5f));

                TextView columnaPeso = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaPeso.setText("Peso");
                else
                    columnaPeso.setText(bolsas.get(i).getPeso() + "");
                columnaPeso.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaPeso.setGravity(Gravity.CENTER);
                columnaPeso.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaPeso, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

                TextView columnaCondicion = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaCondicion.setText("Condición");
                else
                    columnaCondicion.setText(spinnerCondicion.getItemAtPosition(bolsas.get(i).getCondicion()).toString());
                columnaCondicion.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCondicion.setGravity(Gravity.CENTER);
                columnaCondicion.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaCondicion, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));

                if (i < 0) {
                    TextView columnaDescartar = new TextView(this.getApplicationContext());
                    columnaDescartar.setText("Descartar");
                    columnaDescartar.setTextColor(getResources().getColor(R.color.colorBlanco));
                    columnaDescartar.setGravity(Gravity.CENTER);
                    columnaDescartar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    fila.addView(columnaDescartar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2.5f));
                } else {
                    Button columnaDescartar = new Button(this);
                    columnaDescartar.setText((bolsas.get(i).getRazonDescarte().equals("")) ? "DESCARTAR" : "DESCARTADA");
                    columnaDescartar.setId(Integer.parseInt((bolsas.get(i).getCodigoBolsa()).substring(6)));
                    columnaDescartar.setBackgroundResource((bolsas.get(i).getRazonDescarte().equals("")) ? android.R.color.holo_blue_dark : android.R.color.holo_red_light);
                    columnaDescartar.setTextColor(getResources().getColor(R.color.colorBlanco));
                    columnaDescartar.setGravity(Gravity.CENTER);
                    columnaDescartar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    columnaDescartar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            v.startAnimation(scale);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (btnAgregar.getText().equals(getResources().getString(R.string.btnAgregar))) {
                                        alertDescarte(((TableRow) v.getParent()).getId(), v.getId());
                                    } else
                                        alert(Identificacion.this, new String[]{"ATENCIÓN", "Para continuar primero debes terminar de modificar el registro."}, null);
                                }
                            }, 300);
                        }
                    });
                    fila.addView(columnaDescartar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2.5f));
                }

                if (i < 0) {
                    TextView columnaModificar = new TextView(this.getApplicationContext());
                    columnaModificar.setText("Modificar");
                    columnaModificar.setTextColor(getResources().getColor(R.color.colorBlanco));
                    columnaModificar.setGravity(Gravity.CENTER);
                    columnaModificar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    fila.addView(columnaModificar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
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
                            hashMapCustomAlertFunction.put("id", (((TableRow) v.getParent()).getId()) + "");
                            hashMapCustomAlertFunction.put("funcion", "0");
                            v.startAnimation(scale);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDosBotones(Identificacion.this, new String[]{"Atención", "¿Quiere editar este registro?"}, hashMapCustomAlertFunction);
                                }
                            }, 300);
                        }
                    });
                    fila.addView(columnaModificar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
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
                            hashMapCustomAlertFunction.put("id", (((TableRow) v.getParent()).getId()) + "");
                            hashMapCustomAlertFunction.put("funcion", "1");
                            v.startAnimation(scale);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDosBotones(Identificacion.this, new String[]{"Atención", "¿Quiere borrar este registro?"}, hashMapCustomAlertFunction);
                                }
                            }, 300);
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
                tvSep.setBackgroundColor(Color.BLACK);
                tvSep.setHeight(1);
                trSep.addView(tvSep);
                tablaBolsas.addView(trSep, trParamsSep);
            }
        }
    }

    private void alertDescarte(int bolsaId, final int idBtn) {
        bolsa = getBolsaById(bolsaId);
        final Button btnDescartar = findViewById(idBtn);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Atención: Registrando Descarte");
        alert.setIcon(R.drawable.ic_alert);
        alert.setMessage("¿Porque se esta descartando la bolsa de sangre?");
        final EditText razonDescarte = new EditText(this);
        android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 60);
        razonDescarte.setLayoutParams(lp);
        alert.setView(razonDescarte);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = razonDescarte.getText().toString();
                if (value.length() > 500) {
                    alert(Identificacion.this, new String[]{"ATENCIÓN", "El informe de descarte supero el largo permitido de 500 caracteres."}, null);
                } else {
                    if (value.length() == 0)
                        value = "No especificada.";
                    bolsa.setRazonDescarte(value);
                    btnDescartar.setBackgroundResource(android.R.color.holo_red_light);
                    btnDescartar.setText("DESCARTADA");
                }
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
            bolsa.setPeso(Double.parseDouble(pesoGramos.getText().toString()));
            bolsa.setCondicion(condicion);
            btnAgregar.setText(R.string.btnAgregar);
            btnAgregar.setBackgroundResource(android.R.color.holo_green_dark);
            bolsa = null;
            limpiarCampos();
            crearTablasBolsas();
        }
    }

    private void enviarBolsas(boolean finalizar) {
        if (btnAgregar.getText().equals(getResources().getString(R.string.btnAgregar))) {
            JSONArray jsonBolsas = new JSONArray();
            for (int i = 0; i < bolsas.size(); i++) {
                jsonBolsas.put(bolsas.get(i).toJSONObject());
            }
            if (finalizar)
                new RegistroBolsasServerCall(this, jsonBolsas, idPartidaSeleccionada, finalizar);
            else {
                if (!bolsas.isEmpty()) {
                    new RegistroBolsasServerCall(this, jsonBolsas, idPartidaSeleccionada, finalizar);
                } else {
                    alert(Identificacion.this, new String[]{"ATENCIÓN", "No tienes ninguna bolsa identificada."}, null);
                }
            }
        } else {
            alert(Identificacion.this, new String[]{"ATENCIÓN", "Para continuar debes terminar de modificar el regitro."}, null);
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

    @Override
    public void customAlertFunction(Object object) {
        HashMap<String, String> hashMap = (HashMap<String, String>) object;
        switch (hashMap.get("funcion")) {
            case "-1":
                backButtonFunction();
                break;
            case "0":
                if (btnAgregar.getText().equals(getResources().getString(R.string.btnAgregar))) {
                    bolsa = getBolsaById(Integer.parseInt(hashMap.get("id")));
                    btnAgregar.setText(R.string.btnModificar);
                    btnAgregar.setBackgroundResource(android.R.color.holo_orange_dark);
                    setearBolsa(bolsa);
                } else
                    alert(Identificacion.this, new String[]{"ATENCIÓN", "Ya estas modificando un registro, primero debes terminarlo."}, null);
                break;
            case "1":
                if (btnAgregar.getText().equals(getResources().getString(R.string.btnAgregar))) {
                    bolsa = getBolsaById(Integer.parseInt(hashMap.get("id")));
                    btnAgregar.setText(R.string.btnModificar);
                    btnAgregar.setBackgroundResource(android.R.color.holo_orange_dark);
                    setearBolsa(bolsa);
                    bolsas.remove(getBolsaById(Integer.parseInt(hashMap.get("id"))));
                    crearTablasBolsas();
                } else
                    alert(Identificacion.this, new String[]{"ATENCIÓN", "Para continuar primero debes terminar de modificar el registro."}, null);
                break;
        }
    }

    @Override
    public void backButtonFunction() {
        new CancelarIdentificacionPartidaServerCall(Identificacion.this, idPartidaSeleccionada);
    }

    @Override
    public void onClick(final View v) {
        final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        v.startAnimation(scale);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.btn_ib_AgregarBolsa:
                        esconderTecado(Identificacion.this);
                        if (btnAgregar.getText().equals(getResources().getString(R.string.btnAgregar)))
                            agregarBolsa();
                        else
                            modificarBolsa();
                        break;
                    case R.id.btn_ib_AgregarBolsas:
                        enviarBolsas(false);
                        break;
                    case R.id.btn_ib_Cancelar:
                        onBackPressed();
                        break;
                    case R.id.btn_ib_Finalizar:
                        enviarBolsas(true);
                        break;
                }
            }
        }, 300);
    }
}