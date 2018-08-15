package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.helpers.StringWithTag;
import ort.proyecto_final.mvdmart.models.Bolsa;
import ort.proyecto_final.mvdmart.models.Partida;
import ort.proyecto_final.mvdmart.server_calls.CancelarIdentificacionPartidaServerCall;
import ort.proyecto_final.mvdmart.server_calls.GetAllPartidasPendientesServerCall;
import ort.proyecto_final.mvdmart.server_calls.RegistroBolsasServerCall;

public class IdentificacionBolsasActivity extends AppCompatActivity {

    private JSONObject partida;
    private TextView txtPartida;
    private EditText pesoGramos, razonDescarte;
    private Button btnFinalizar, btnCancelar, btnEnviarBolsas;
    private FloatingActionButton btnAgregar;
    private int condicion, idPartida, bolsaId;
    private static int nroUltimaBolsa, nroUltimaBolsaRecibido;
    private String codigoBolsa;
    private ArrayList<Bolsa> bolsas = new ArrayList<>();
    private TableLayout tablaBolsas;
    private Spinner spinnerCondicion;
    public ConstraintLayout spinnerLoader;
    private Bolsa bolsa;
    private boolean modoEdicion = false;
    private IdentificacionBolsasActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identificacion_bolsas);
        Intent intent = getIntent();
        idPartida = intent.getIntExtra("idPartida", -1);
        inizializeViews();
    }

    private void inizializeViews() {
        txtPartida = findViewById(R.id.txtPartida);
        try {
            JSONArray partidas = new JSONArray(Config.getPartidasPendientes(this));
            for (int i = 0; i < partidas.length(); i++) {
                if (partidas.getJSONObject(i).getInt("id") == idPartida) {
                    partida = partidas.getJSONObject(i);
                    txtPartida.setText("Identificación de bolsas, partida de " + partida.getJSONObject("frigorifico").getString("nombre"));
                    codigoBolsa = partida.getString("codigoUltimaBolsa");
                    nroUltimaBolsa = partida.getInt("nroUltimaBolsa");
                    nroUltimaBolsaRecibido = nroUltimaBolsa;
                    break;
                }
            }
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + Config.getFrigorificos(this) + "\"");
        }
        spinnerLoader = findViewById(R.id.spinner_loader);
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
        pesoGramos = findViewById(R.id.pesoGramos);
        btnAgregar = findViewById(R.id.btnAgregarbolsa);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!modoEdicion)
                    agregarBolsa();
                else
                    modificarBolsa();
            }
        });
        btnEnviarBolsas = findViewById(R.id.btnAgregarBolsas);
        btnEnviarBolsas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLoader();
                enviarBolsas(false);
            }
        });
        btnFinalizar = findViewById(R.id.btnFinalizarIdentificacion);
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLoader();
                enviarBolsas(true);
            }
        });
        btnCancelar = findViewById(R.id.btnCancelarIdentificacion);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLoader();
                new CancelarIdentificacionPartidaServerCall(activity, idPartida);
            }
        });
        tablaBolsas = findViewById(R.id.tablaBolsas);
    }

    private void agregarBolsa() {
        if (HelpersFunctions.isIntegerParseInt(pesoGramos.getText().toString())) {
            int peso = Integer.parseInt(pesoGramos.getText().toString());
            // if (true) {
            if (Bolsa.validar(peso, condicion)) {
                Bolsa bolsa = new Bolsa(peso, condicion);
                bolsas.add(bolsa);
                nroUltimaBolsa++;
                bolsa.generarCodigo(codigoBolsa, nroUltimaBolsa);
                cleanFields();
                createRows();
            } else {
                Toast errorToast = Toast.makeText(this.getApplicationContext(), "Atención: Hay campos incorrectos.", Toast.LENGTH_LONG);
                errorToast.show();
            }
        } else {
            Toast errorToast = Toast.makeText(this.getApplicationContext(), "Atención: Debe completar todos los campos.", Toast.LENGTH_LONG);
            errorToast.show();
        }
    }

//    private void finalizarIdentificacion() {
//        JSONArray send = new JSONArray();
//        for (int i = 0; i < bolsas.size(); i++) {
//            send.put(bolsas.get(i).toJSONObject());
//        }
//        new RegistroBolsasServerCall(this, send, idPartida);
//    }

    public void iniciarLoader() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinnerLoader.setVisibility(View.VISIBLE);
    }

    public void finalizarLoader() {
        spinnerLoader.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void limpiarTabla() {
        tablaBolsas.removeAllViews();
    }

    public void vaciarArrayBolsas() {
        bolsas = new ArrayList<>();
        tablaBolsas.removeAllViews();
    }

    private void createRows() {
        tablaBolsas.removeAllViews();
        Collections.sort(bolsas);
        for (int i = 0; i < bolsas.size(); i++) {
            bolsas.get(i).setLocalId(i);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setId(i);
            final TextView columnCodigo = new TextView(this.getApplicationContext());
            columnCodigo.setText(bolsas.get(i).getCodigoBolsa());
            columnCodigo.setTextColor(0xFF000000);
            columnCodigo.setBackgroundColor(Color.parseColor("#f7f7f7"));
            row.addView(columnCodigo);
            final TextView columnPeso = new TextView(this.getApplicationContext());
            columnPeso.setText(bolsas.get(i).getPeso() + "");
            columnPeso.setTextColor(0xFF000000);
            columnPeso.setBackgroundColor(Color.parseColor("#f7f7f7"));
            row.addView(columnPeso);
            final TextView columnCondicion = new TextView(this.getApplicationContext());
            columnCondicion.setText(spinnerCondicion.getItemAtPosition(bolsas.get(i).getCondicion()) + "");
            columnCondicion.setTextColor(0xFF000000);
            row.addView(columnCondicion);
            final Button columnEditBtn = new Button(this);
            columnEditBtn.setBackgroundResource(R.drawable.ic_edit_row);
            columnEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    questionEditBolsa(((TableRow) v.getParent()).getId());
                }
            });
            row.addView(columnEditBtn);
            final Button columnDeleteBtn = new Button(this);
            columnDeleteBtn.setBackgroundResource(R.drawable.ic_delete_row);
            columnDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarBolsa(((TableRow) v.getParent()).getId());
                }
            });
            row.addView(columnDeleteBtn);
            final Button columnDescartarBtn = new Button(this);
            columnDescartarBtn.setBackgroundResource(R.color.colorAccent);
            columnDescartarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    descartarBolsa(((TableRow) v.getParent()).getId());
                }
            });
            row.addView(columnDescartarBtn);
            tablaBolsas.addView(row);

            final TableRow trSep = new TableRow(this);
            TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trParamsSep.setMargins(0, 0, 0, 0);
            trSep.setLayoutParams(trParamsSep);
            TextView tvSep = new TextView(this);
            TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tvSepLay.span = 6;
            tvSep.setLayoutParams(tvSepLay);
            tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
            tvSep.setHeight(1);
            trSep.addView(tvSep);
            tablaBolsas.addView(trSep, trParamsSep);
        }
    }

    private void descartarBolsa(int bolsaId) {
        bolsa = getBolsaById(bolsaId);
//        AlertDialog.Builder builder = new AlertDialog.Builder(IdentificacionBolsasActivity.this);
//        builder.setTitle(R.string.app_name);
//        builder.setMessage("¿Quiere descartar esta bolsa?");
//        //builder.setIcon(R.drawable.ic_launcher_foreground);
//        razonDescarte = new EditText(this);
//        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//               bolsa.setRazonDescarte(razonDescarte.getText().toString());
//                dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Descarte Bolsa");
        alert.setMessage("Deje un mensaje:");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                bolsa.setRazonDescarte(value);
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        return;
                    }
                });
        alert.show();
    }

    private void eliminarBolsa(int id) {
        //investigar para crear el alert con un helperfunctino
        bolsaId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(IdentificacionBolsasActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("¿Quiere borrar ese registro?");
        //   builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                bolsas.remove(getBolsaById(bolsaId));
                regenerarCodigos();
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
        nroUltimaBolsa--;

    }

    private void modificarBolsa() {
        if (bolsa != null) {
            int indexPartida = bolsas.indexOf(bolsa);
            bolsa.setPeso(Integer.parseInt(pesoGramos.getText().toString()));
            bolsa.setCondicion(condicion);
            bolsa = null;
            modoEdicion = false;
            cleanFields();
            createRows();
        }
    }

    private void cleanFields() {
        pesoGramos.setText("");
    }

    private void enviarBolsas(boolean finalizar) {
        JSONArray send = new JSONArray();
        for (int i = 0; i < bolsas.size(); i++) {
            send.put(bolsas.get(i).toJSONObject());
        }
        new RegistroBolsasServerCall(this, send, idPartida, finalizar);
    }

    private void questionEditBolsa(int bolsaId) {
        bolsa = getBolsaById(bolsaId);
        AlertDialog.Builder builder = new AlertDialog.Builder(IdentificacionBolsasActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("¿Quiere editar ese registro?");
        //builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                modoEdicion = true;
                btnAgregar.setBackgroundColor(6);
                setBolsa(bolsa);
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

    private Bolsa getBolsaById(int idPartida) {
        Bolsa ret = null;
        for (Bolsa bolsa : bolsas) {
            if (bolsa.getLocalId() == idPartida) {
                ret = bolsa;
            }
        }
        return ret;
    }

    private void setBolsa(Bolsa bolsa) {
        pesoGramos.setText(bolsa.getPeso() + "");
        spinnerCondicion.setSelection(bolsa.getCondicion());
    }

    private void regenerarCodigos(){
        int num = nroUltimaBolsaRecibido + 1;
        for (int i = 0; i < bolsas.size(); i++) {
            bolsas.get(i).generarCodigo(codigoBolsa,num++);
        }
    }

}
