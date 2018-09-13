package ort.proyecto_final.mvdmart.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.helpers.StringWithTag;
import ort.proyecto_final.mvdmart.models.Partida;
import ort.proyecto_final.mvdmart.server_calls.RegistroMateriasPrimasServerCall;

public class RegistroMateriasPrimasActivity extends ActivityMadre {

    private Partida partidaParaModificar = null;
    private TextView txtFecha, txtHora;
    private EditText txtCantConservadoras, txtPeso, txtTemperatura, txtNCote;
    private Button btnAgregar, btnFinalizar, btnCancelarRMP;
    private int idFrigorifico, posFrigorifico;
    private ArrayList<Partida> partidas = new ArrayList<>();
    private TableLayout tablaRegistroPartidas;
    private ArrayAdapter<StringWithTag> frigorificosArray;
    private Spinner dropDownFrigorificos;
    private final Calendar calendar = Calendar.getInstance();
    private HashMap<String, String> hashMapCustomAlertFunction = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_materias_primas);
        inicializarVistas();
    }

    @Override
    public void inicializarVistas() {
        JSONArray obj;
        List<StringWithTag> frigorificos = null;
        spinnerLoader = findViewById(R.id.spinner_loader);
        tablaRegistroPartidas = findViewById(R.id.tablaPartidas);
        dropDownFrigorificos = findViewById(R.id.ddFrigorificos);
        //TODO esto hay que revisar, no se asegura que la llamada al servidor este completa, posible parche.preguntar antes de iniciar la actividad si hay frigorificos
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
        btnAgregar = findViewById(R.id.btn_rmp_AgregarPartida);
        btnAgregar.setOnClickListener(this);
        btnFinalizar = findViewById(R.id.btn_rmp_Finalizar);
        btnFinalizar.setOnClickListener(this);
        btnCancelarRMP = findViewById(R.id.btn_rmp_Cancelar);
        btnCancelarRMP.setOnClickListener(this);

        txtHora = findViewById(R.id.hora);
        txtHora.setText(HelpersFunctions.horaEnFormato(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        txtHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(RegistroMateriasPrimasActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtHora.setText(HelpersFunctions.horaEnFormato(hourOfDay, minute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
        txtFecha = findViewById(R.id.fecha);
        txtFecha.setText(HelpersFunctions.fechaEnFormato(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistroMateriasPrimasActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtFecha.setText(HelpersFunctions.fechaEnFormato(year, month, dayOfMonth));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        txtCantConservadoras = findViewById(R.id.cantConservadoras);
        txtCantConservadoras.setTransformationMethod(null);
//        txtPeso = findViewById(R.id.peso);
//        txtPeso.setTransformationMethod(null);
        txtTemperatura = findViewById(R.id.temperatura);
        txtTemperatura.setTransformationMethod(null);
        txtNCote = findViewById(R.id.nroCote);
        txtNCote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    esconderTecado(RegistroMateriasPrimasActivity.this);
                }
                return false;
            }
        });
    }

    private void agregarPartida() {
        if (chequearCamposCompletosYTiposDatos()) {
//            if (true) {
            int cantConservadoras = Integer.parseInt(txtCantConservadoras.getText().toString());
//            int pesoTotal = Integer.parseInt(txtPeso.getText().toString());
            int temperatura = Integer.parseInt(txtTemperatura.getText().toString());
            String numeroCote = txtNCote.getText().toString().replaceAll("\\s+", "");
            String fecha = txtFecha.getText().toString();
            String hora = txtHora.getText().toString();
            try {
                String[] esValida = Partida.validar(cantConservadoras, temperatura, /*pesoTotal,*/ numeroCote, idFrigorifico);
                if (esValida[1] == "Ok") {
//                    if (true) {
                    Partida partida = new Partida(idFrigorifico, posFrigorifico, cantConservadoras, /*pesoTotal,*/ temperatura, fecha, hora, numeroCote);
                    partidas.add(partida);
                    limpiarCampos();
                    crearTablaRegistroPartidas();
                } else {
                    alert(RegistroMateriasPrimasActivity.this, esValida, null);
                }
            } catch (JSONException e) {
                alert(RegistroMateriasPrimasActivity.this, new String[]{"ATENCION", "Campos mal formateados. Si perciste comunicarlo."}, null);
            }
        }
    }

    private void enviarRegistrosDePartidas() {
        if (!partidas.isEmpty()) {
            if (btnAgregar.getText().equals(getResources().getString(R.string.btnAgregar))) {
                JSONArray jsonPartidas = new JSONArray();
                for (int i = 0; i < partidas.size(); i++) {
                    jsonPartidas.put(partidas.get(i).toJSONObject());
                }
                new RegistroMateriasPrimasServerCall(this, jsonPartidas);
            } else {
                alert(RegistroMateriasPrimasActivity.this, new String[]{"ATENCION", "Debes terminar de modificar la partida."}, null);
            }
        } else {
            alert(RegistroMateriasPrimasActivity.this, new String[]{"ATENCION", "No tienes ningun registro cargado."}, null);
        }
    }

    private void modificarPartida() {
        if (partidaParaModificar != null && chequearCamposCompletosYTiposDatos()) {
            try {
                String[] esValida = Partida.validar(Integer.parseInt(txtCantConservadoras.getText().toString()), Integer.parseInt(txtTemperatura.getText().toString()), /*Integer.parseInt(txtPeso.getText().toString()),*/ txtNCote.getText().toString(), idFrigorifico);
                if (esValida[1] == "Ok") {
                    partidaParaModificar.setCantConservadoras(Integer.parseInt(txtCantConservadoras.getText().toString()));
//                    partidaParaModificar.setPeso(Integer.parseInt(txtPeso.getText().toString()));
                    partidaParaModificar.setTemperatura(Integer.parseInt(txtTemperatura.getText().toString()));
                    partidaParaModificar.setNumCote(txtNCote.getText().toString());
                    partidaParaModificar.setIdFrigorifico(idFrigorifico);
                    partidaParaModificar.setPosFrigorifico(posFrigorifico);
                    partidaParaModificar.setFecha(txtFecha.getText().toString());
                    partidaParaModificar.setHora(txtHora.getText().toString());
                    crearTablaRegistroPartidas();
                    limpiarCampos();
                    btnAgregar.setText(R.string.btnAgregar);
                    btnAgregar.setBackgroundResource(android.R.color.holo_green_dark);
                    partidaParaModificar = null;
                } else {
                    alert(RegistroMateriasPrimasActivity.this, esValida, null);
                }
            } catch (JSONException e) {
                alert(RegistroMateriasPrimasActivity.this, new String[]{"ATENCION", "Campos mal formateados. Si perciste comunicarlo."}, null);
            }
        }
    }

    private boolean chequearCamposCompletosYTiposDatos() {
        boolean camposCompletos = true;
        String camposIncompletos = "Debe completar los siguientes campos:";
        int largoStringCampos = camposIncompletos.length();
        if (idFrigorifico == -1)
            camposIncompletos += " frigorífico,";
        if (!HelpersFunctions.isIntegerParseInt(txtCantConservadoras.getText().toString()))
            camposIncompletos += " cantidad de conservadoras,";
//        if (!HelpersFunctions.isIntegerParseInt(txtPeso.getText().toString()))
//            camposIncompletos += " peso,";
        if (!HelpersFunctions.isIntegerParseInt(txtTemperatura.getText().toString()))
            camposIncompletos += " temperatura.";
        if (camposIncompletos.length() != largoStringCampos) {
            camposCompletos = false;
            alert(RegistroMateriasPrimasActivity.this, new String[]{"ATENCION", camposIncompletos}, null);
        }
        return camposCompletos;
    }

    private void crearTablaRegistroPartidas() {
        final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        tablaRegistroPartidas.removeAllViews();
        if (partidas.size() != 0) {
            for (int i = -1; i < partidas.size(); i++) {
                TableRow fila = new TableRow(this);
                fila.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 14f));
                if (i < 0)
                    fila.setBackgroundColor(Color.rgb(36, 123, 160));
                else {
                    partidas.get(i).setLocalId(i);
                    fila.setId(i/*partidas.get(i).getLocalId()*/);
                    fila.setBackgroundColor((i % 2 == 0) ? Color.rgb(112, 193, 179) : Color.rgb(178, 219, 191));
                }

                TextView columnaFrigorifico = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaFrigorifico.setText("Frigorifico");
                else
                    columnaFrigorifico.setText(frigorificosArray.getItem(partidas.get(i).getPosFrigorifico()).string);
                columnaFrigorifico.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaFrigorifico.setGravity(Gravity.CENTER);
                columnaFrigorifico.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaFrigorifico, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2.5f));

                TextView columnaFecha = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaFecha.setText("Fecha");
                else
                    columnaFecha.setText(partidas.get(i).getFecha());
                columnaFecha.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaFecha.setGravity(Gravity.CENTER);
                columnaFecha.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaFecha, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));

                TextView columnaHora = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaHora.setText("Hora");
                else
                    columnaHora.setText(partidas.get(i).getHora());
                columnaHora.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaHora.setGravity(Gravity.CENTER);
                columnaHora.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaHora, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                TextView columnaConservadoras = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaConservadoras.setText("Conservadoras");
                else
                    columnaConservadoras.setText(partidas.get(i).getCantConservadoras() + "");
                columnaConservadoras.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaConservadoras.setGravity(Gravity.CENTER);
                columnaConservadoras.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaConservadoras, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

//            TextView columnaPeso = new TextView(this.getApplicationContext());
//            if (i < 0)
//                columnaPeso.setText("Peso");
//            else
//                columnaPeso.setText(partidas.get(i).getPeso() + "");
//            columnaPeso.setTextColor(getResources().getColor(R.color.colorBlanco));
//            columnaPeso.setGravity(Gravity.CENTER);
//            columnaPeso.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//            fila.addView(columnaPeso, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                TextView columnaTemperatura = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaTemperatura.setText("Temperatura");
                else
                    columnaTemperatura.setText(partidas.get(i).getTemperatura() + "");
                columnaTemperatura.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaTemperatura.setGravity(Gravity.CENTER);
                columnaTemperatura.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaTemperatura, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

                TextView columnaNumeroCote = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaNumeroCote.setText("Número de cote");
                else
                    columnaNumeroCote.setText(partidas.get(i).getNumCote() + "");
                columnaNumeroCote.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaNumeroCote.setGravity(Gravity.CENTER);
                columnaNumeroCote.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaNumeroCote, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

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
                            v.startAnimation(scale);
                            hashMapCustomAlertFunction.put("id", (((TableRow) v.getParent()).getId()) + "");
                            hashMapCustomAlertFunction.put("funcion", "0");
                            v.startAnimation(scale);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDosBotones(RegistroMateriasPrimasActivity.this, new String[]{"ATENCIÓN", "¿Quiere editar este registro?"}, hashMapCustomAlertFunction);
                                }
                            }, 300);
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
                    fila.addView(columnaBorrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
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
                            v.startAnimation(scale);
                            hashMapCustomAlertFunction.put("id", (((TableRow) v.getParent()).getId()) + "");
                            hashMapCustomAlertFunction.put("funcion", "1");
                            v.startAnimation(scale);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDosBotones(RegistroMateriasPrimasActivity.this, new String[]{"ATENCIÓN", "¿Quiere editar este registro?"}, hashMapCustomAlertFunction);
                                }
                            }, 300);
                        }
                    });
                    fila.addView(columnaBorrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
                }

                tablaRegistroPartidas.addView(fila);

                final TableRow trSep = new TableRow(this);
                TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(0, 0, 0, 0);
                trSep.setLayoutParams(trParamsSep);
                TextView tvSep = new TextView(this);
                TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span = 9;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.rgb(243, 255, 189));
                tvSep.setHeight(4);
                trSep.addView(tvSep);
                tablaRegistroPartidas.addView(trSep, trParamsSep);
            }
        }
    }

    public void limpiarTabla() {
        tablaRegistroPartidas.removeAllViews();
    }

    @Override
    public void limpiarCampos() {
        txtCantConservadoras.setText("");
//        txtPeso.setText("");
        txtTemperatura.setText("");
        txtNCote.setText("");
        dropDownFrigorificos.setSelection(0);
        txtHora.setText(HelpersFunctions.horaEnFormato(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        txtFecha.setText(HelpersFunctions.fechaEnFormato(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
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

    @Override
    public void customServerModelError(Object partidaId) {
        partidaParaModificar = getPartidaById((Integer) partidaId);
        btnAgregar.setText(R.string.btnModificar);
        btnAgregar.setBackgroundResource(android.R.color.holo_orange_dark);
        setearPartida(partidaParaModificar);
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
                    partidaParaModificar = getPartidaById(Integer.parseInt(hashMap.get("id")));
                    btnAgregar.setText(R.string.btnModificar);
                    btnAgregar.setBackgroundResource(android.R.color.holo_orange_dark);
                    setearPartida(partidaParaModificar);
                } else {
                    alert(RegistroMateriasPrimasActivity.this, new String[]{"ATENCION", "Ya se encuentra modificando un registro, termine con ese para poder continuar."}, null);
                }
                break;
            case "1":
                if (btnAgregar.getText().equals(getResources().getString(R.string.btnAgregar))) {
                    partidas.remove(getPartidaById(Integer.parseInt(hashMap.get("id"))));
                    crearTablaRegistroPartidas();
                } else {
                    alert(RegistroMateriasPrimasActivity.this, new String[]{"ATENCION", "Se encuentra modificando un registro, debe termianr para poder usar esta funcionalidad."}, null);
                }
                break;
        }
    }

    @Override
    public void backButtonFunction() {
        finish();
    }

    private void setearPartida(Partida partida) {
        txtCantConservadoras.setText(partida.getCantConservadoras() + "");
//        txtPeso.setText(partida.getPeso() + "");
        txtTemperatura.setText(partida.getTemperatura() + "");
        txtNCote.setText(partida.getNumCote() + "");
        dropDownFrigorificos.setSelection(partida.getPosFrigorifico());
        txtFecha.setText(partida.getFecha());
        txtHora.setText(partida.getHora());
    }

    @Override
    public void onClick(final View v) {
        final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        v.startAnimation(scale);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.btn_rmp_AgregarPartida:
                        if (btnAgregar.getText().equals(getResources().getString(R.string.btnAgregar)))
                            agregarPartida();
                        else
                            modificarPartida();
                        esconderTecado(RegistroMateriasPrimasActivity.this);
                        break;
                    case R.id.btn_rmp_Cancelar:
                        onBackPressed();
                        break;
                    case R.id.btn_rmp_Finalizar:
                        enviarRegistrosDePartidas();
                        break;
                }
            }
        }, 300);
    }
}