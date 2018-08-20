package ort.proyecto_final.mvdmart.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import java.util.List;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.helpers.StringWithTag;
import ort.proyecto_final.mvdmart.models.Partida;
import ort.proyecto_final.mvdmart.server_calls.RegistroMateriasPrimasServerCall;

public class RegistroMateriasPrimasActivity extends AppCompatActivity {

    private Partida partidaToModify = null;
    private TextView txtFecha, txtHora;
    private EditText txtCantConservadoras, txtPeso, txtTemperatura, txtNCote;
    private Button btnAgregar, btnFinalizar;
    private int idFrigorifico, posFrigorifico, numeroOperario;
    private ArrayList<Partida> partidas = new ArrayList<>();
    private TableLayout tablaRegistroPartidas;
    private ArrayAdapter<StringWithTag> frigorificosArray;
    private Spinner dropDownFrigorificos;
    public ConstraintLayout spinnerLoader;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_materias_primas);
        numeroOperario = Integer.parseInt(Config.getNumeroOperario(this));
        inicializarVistas();
    }

    private void inicializarVistas() {
        JSONArray obj = null;
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
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AppCompatButton) v).getText().equals(getResources().getString(R.string.btnAgregar)))
                    agregarPartida();
                else
                    modificarPartida();
                HelpersFunctions.esconderTecado(RegistroMateriasPrimasActivity.this);
            }
        });
        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!partidas.isEmpty())
                    enviarRegistrosDePartidas();
                else
                    Toast.makeText(RegistroMateriasPrimasActivity.this, "Atención: No tienes ninguna partida.", Toast.LENGTH_LONG).show();
                HelpersFunctions.esconderTecado(RegistroMateriasPrimasActivity.this);
            }
        });
        txtHora = findViewById(R.id.hora);
        txtHora.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        txtHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(RegistroMateriasPrimasActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtHora.setText(horaEnFormato(hourOfDay,minute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
        txtFecha = findViewById(R.id.fecha);
        txtFecha.setText(fechaEnFormato(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistroMateriasPrimasActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtFecha.setText(fechaEnFormato(year, month, dayOfMonth));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        txtCantConservadoras = findViewById(R.id.cantConservadoras);
        txtCantConservadoras.setTransformationMethod(null);
        txtPeso = findViewById(R.id.peso);
        txtPeso.setTransformationMethod(null);
        txtTemperatura = findViewById(R.id.temperatura);
        txtTemperatura.setTransformationMethod(null);
        txtNCote = findViewById(R.id.nroCote);
        txtNCote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    HelpersFunctions.esconderTecado(RegistroMateriasPrimasActivity.this);
                }
                return false;
            }
        });
    }

    private void agregarPartida() {
        if (chequearCamposCompletosYTiposDatos()) {
            int cantConservadoras = Integer.parseInt(txtCantConservadoras.getText().toString());
            int pesoTotal = Integer.parseInt(txtPeso.getText().toString());
            int temperatura = Integer.parseInt(txtTemperatura.getText().toString());
            String numeroCote = txtNCote.getText().toString().replaceAll("\\s+", "");
            String fechaHora = txtFecha.getText().toString() + txtHora.getText();
            try {
                String esValida = Partida.validar(cantConservadoras, temperatura, pesoTotal, numeroCote, idFrigorifico);
                if (esValida == "Ok") {
                    Partida partida = new Partida(idFrigorifico, posFrigorifico, cantConservadoras, pesoTotal, temperatura, fechaHora, numeroCote, numeroOperario);
                    partidas.add(partida);
                    limpiarCampos();
                    crearTablaRegistroParttidas();
                } else {
                    HelpersFunctions.alertDatosInvalidos(RegistroMateriasPrimasActivity.this,esValida).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this.getApplicationContext(), "Atención: Campos mal formateados. Si perciste, comunicarlo.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void enviarRegistrosDePartidas() {
        if (btnAgregar.getText().equals("Agregar")) {
            JSONArray send = new JSONArray();
            for (int i = 0; i < partidas.size(); i++) {
                send.put(partidas.get(i).toJSONObject());
            }
            iniciarLoader();
            new RegistroMateriasPrimasServerCall(this, send);
        } else {
            Toast.makeText(this.getApplicationContext(), "Atención: Debes terminar de modificar la partida.", Toast.LENGTH_LONG).show();
        }

    }

    private void modificarPartida() {
        if (partidaToModify != null && chequearCamposCompletosYTiposDatos()) {
            try {
                String esValida = Partida.validar(Integer.parseInt(txtCantConservadoras.getText().toString()), Integer.parseInt(txtTemperatura.getText().toString()), Integer.parseInt(txtPeso.getText().toString()), txtNCote.getText().toString(), idFrigorifico);
                if (esValida == "Ok") {
                    int indexPartida = partidas.indexOf(partidaToModify);
                    partidas.get(indexPartida).setCantConservadoras(Integer.parseInt(txtCantConservadoras.getText().toString()));
                    partidas.get(indexPartida).setPeso(Integer.parseInt(txtPeso.getText().toString()));
                    partidas.get(indexPartida).setTemperatura(Integer.parseInt(txtTemperatura.getText().toString()));
                    partidas.get(indexPartida).setNumCote(txtNCote.getText().toString());
                    partidas.get(indexPartida).setIdFrigorifico(idFrigorifico);
                    partidas.get(indexPartida).setPosFrigorifico(posFrigorifico);
                    partidas.get(indexPartida).setFecha( txtFecha.getText().toString() + txtHora.getText());
                    partidaToModify = null;
                    limpiarCampos();
                    btnAgregar.setText(R.string.btnAgregar);
                    btnAgregar.setBackgroundResource(android.R.color.holo_green_dark);
                    crearTablaRegistroParttidas();
                } else {
                    HelpersFunctions.alertDatosInvalidos(RegistroMateriasPrimasActivity.this,esValida).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this.getApplicationContext(), "Atención: Campos mal formateados. Si perciste, comunicarlo.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean chequearCamposCompletosYTiposDatos() {
        //TODO se podria refactoriar, y dejarlo en una sola funcion. Nno hay tiempo ahora mismo.
        boolean camposCompletos = true;
        String camposIncompletos = "Atención, debe completar los siguientes campos:";
        int largoStringCampos = camposIncompletos.length();
        if (idFrigorifico == -1)
            camposIncompletos += " frigorífico,";
        if (!HelpersFunctions.isIntegerParseInt(txtCantConservadoras.getText().toString()))
            camposIncompletos += " cantidad de conservadoras,";
        if (!HelpersFunctions.isIntegerParseInt(txtPeso.getText().toString()))
            camposIncompletos += " peso,";
        if (!HelpersFunctions.isIntegerParseInt(txtTemperatura.getText().toString()))
            camposIncompletos += " temperatura.";
        if (camposIncompletos.length() != largoStringCampos) {
            camposCompletos = false;
            Toast.makeText(this.getApplicationContext(), camposIncompletos, Toast.LENGTH_LONG).show();
        }
        return camposCompletos;
    }

    private String fechaEnFormato(int año, int mes, int dia) {
        int mesReal = mes + 1;
        String sMes = (mesReal < 10) ? "0" + mesReal : mesReal + "";
        String sDia = (dia < 10) ? "0" + dia : dia + "";
        return (sDia + "-" + sMes + "-" + año);
    }

    private String horaEnFormato(int hora, int minutos) {
        String sHora = (hora < 10) ? "0" + hora : hora + "";
        String sMinutos = (minutos < 10) ? "0" + minutos : minutos + "";
        return (sHora + ":" + sMinutos);
    }









    public void limpiarTabla() {
        tablaRegistroPartidas.removeAllViews();
    }

    private void crearTablaRegistroParttidas() {
        tablaRegistroPartidas.removeAllViews();
        for (int i = 0; i < partidas.size(); i++) {
            partidas.get(i).setLocalId(i);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setId(i/*partidas.get(i).getLocalId()*/);
            final TextView rowFrigorifico = new TextView(this.getApplicationContext());
            rowFrigorifico.setText(frigorificosArray.getItem(partidas.get(i).getPosFrigorifico()).string);
            rowFrigorifico.setTextColor(0xFF000000);
            rowFrigorifico.setBackgroundColor(Color.parseColor("#f7f7f7"));
            row.addView(rowFrigorifico);
            final TextView rowCantidadConservadoras = new TextView(this.getApplicationContext());
            rowCantidadConservadoras.setText(partidas.get(i).getCantConservadoras() + "");
            rowCantidadConservadoras.setTextColor(0xFF000000);
            row.addView(rowCantidadConservadoras);
            final TextView rowPeso = new TextView(this.getApplicationContext());
            rowPeso.setText(partidas.get(i).getPeso() + "");
            rowPeso.setTextColor(0xFF000000);
            row.addView(rowPeso);
            final TextView rowTemperatura = new TextView(this.getApplicationContext());
            rowTemperatura.setText(partidas.get(i).getTemperatura() + "");
            rowTemperatura.setTextColor(0xFF000000);
            row.addView(rowTemperatura);
            final TextView rowCote = new TextView(this.getApplicationContext());
            rowCote.setText(partidas.get(i).getNumCote() + "");
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
            tablaRegistroPartidas.addView(row);

            final TableRow trSep = new TableRow(this);
            TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trParamsSep.setMargins(0, 0, 0, 0);
            trSep.setLayoutParams(trParamsSep);
            TextView tvSep = new TextView(this);
            TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tvSepLay.span = 7;
            tvSep.setLayoutParams(tvSepLay);
            tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
            tvSep.setHeight(1);
            trSep.addView(tvSep);
            tablaRegistroPartidas.addView(trSep, trParamsSep);
        }
    }

    private void limpiarCampos() {
        txtCantConservadoras.setText("");
        txtPeso.setText("");
        txtTemperatura.setText("");
        txtNCote.setText("");
        dropDownFrigorificos.setSelection(0);
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
                crearTablaRegistroParttidas();
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
            @SuppressLint("ResourceAsColor")
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                btnAgregar.setText(R.string.btnModificar);
                btnAgregar.setBackgroundResource(android.R.color.holo_orange_dark);
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

    public void onResponseErrorPartida(int partidaId) {
        partidaToModify = getPartidaById(partidaId);
        btnAgregar.setText("Modificar");
        setPartida(partidaToModify);
    }

    private void setPartida(Partida partida) {
        txtCantConservadoras.setText(partida.getCantConservadoras() + "");
        txtPeso.setText(partida.getPeso() + "");
        txtTemperatura.setText(partida.getTemperatura() + "");
        txtNCote.setText(partida.getNumCote() + "");
        dropDownFrigorificos.setSelection(partida.getPosFrigorifico());
        txtFecha.setText(partida.getFecha().substring(0,10));
        txtHora.setText(partida.getFecha().substring(10,15));
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