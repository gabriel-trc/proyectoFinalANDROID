package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.models.BotellaSuero;
import ort.proyecto_final.mvdmart.models.ExtraccionSueroDeBolsa;
import ort.proyecto_final.mvdmart.models.Item;
import ort.proyecto_final.mvdmart.models.Partida;
import ort.proyecto_final.mvdmart.server_calls.CambiarItemIdentificadoServerCall;
import ort.proyecto_final.mvdmart.server_calls.NuevaBotellaSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerBotellasDeSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerItemsIdentificadosServerCall;
import ort.proyecto_final.mvdmart.server_calls.SeleccionarItemServerCall;


public class SeparacionItemsActivity extends AppCompatActivity {
    private BotellaSuero botellaSueroSeleccionada;
    private Button btnSeleccionarItem, btnSeleccionarBotellaSuero, btnAgregarExtraaccionSuero;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<Item>> expandableListDetail;
    private ConstraintLayout spinnerLoader;
    private Item itemSeleccionado, nuevoItemSeleccionado;
    private TextView alertTitle, txtItemPreSeleccionado, txtBotellaSuero, txtDisponibleBotellaSuero;
    private HashMap<String, Integer> objetosEnVista = new HashMap<>();
    private TableLayout tablaExtraccionesSuero;
    ArrayList<ExtraccionSueroDeBolsa> extraccionSueroDeBolsas = new ArrayList<>();
    private EditText txtCantidadExtracionSuero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_separacion_items);
        spinnerLoader = findViewById(R.id.spinner_loader);
        iniciarVistas();
    }

    private void iniciarVistas() {
        txtCantidadExtracionSuero = findViewById(R.id.txtCantidadExtracionSuero);
        tablaExtraccionesSuero = findViewById(R.id.tablaExtraccionSuero);
        txtItemPreSeleccionado = findViewById(R.id.txtItemPreSeleccionado);
        txtBotellaSuero = findViewById(R.id.txtBotellaSuero);
        txtDisponibleBotellaSuero = findViewById(R.id.txtDisponibleBotellaSuero);
        btnSeleccionarItem = findViewById(R.id.btnSeleccionarItem);
        btnSeleccionarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLoader();
                new ObtenerItemsIdentificadosServerCall(SeparacionItemsActivity.this);
            }
        });
        btnSeleccionarBotellaSuero = findViewById(R.id.btnSeleccionarBotellaSuero);
        btnSeleccionarBotellaSuero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLoader();
                new ObtenerBotellasDeSueroServerCall(SeparacionItemsActivity.this);
            }
        });

        btnAgregarExtraaccionSuero = findViewById(R.id.btnAgregarExtraaccionSuero);
        btnAgregarExtraaccionSuero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(botellaSueroSeleccionada != null && itemSeleccionado != null){
                    agregarNuevaExtraccion();
                }
            }
        });
    }

    private void agregarNuevaExtraccion() {
        extraccionSueroDeBolsas.add(new ExtraccionSueroDeBolsa(itemSeleccionado.getCodigo(),itemSeleccionado.getTipo(), botellaSueroSeleccionada.getCodigo(), Double.parseDouble(txtCantidadExtracionSuero.getText().toString())));
        createRows();
        txtDisponibleBotellaSuero.setText("Disponible: " + (500 - Double.parseDouble(txtCantidadExtracionSuero.getText().toString())));

    }

    public void alertSeleccionItem(JSONObject itemsParaSeparar) {
        try {
            expandableListDetail = Partida.partidasParaSeparar(itemsParaSeparar.getJSONArray("partidasConBolsas"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        alertTitle = new TextView(SeparacionItemsActivity.this);
        alertTitle.setText("Selección de item");
        alertTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        alertTitle.setGravity(Gravity.CENTER);
        builder.setCustomTitle(alertTitle);
        builder.setMessage("");
        expandableListView = new ExpandableListView(this);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new ort.proyecto_final.mvdmart.helpers.ExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        builder.setView(expandableListView);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //      Toast.makeText(getApplicationContext(),expandableListTitle.get(groupPosition) + " List Expanded.",Toast.LENGTH_SHORT).show();
            }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),expandableListTitle.get(groupPosition) + " List Collapsed.",Toast.LENGTH_SHORT).show();
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                nuevoItemSeleccionado = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
                alertTitle.setText("Ha seleccionado: " + nuevoItemSeleccionado.toString());
                txtItemPreSeleccionado.setText(nuevoItemSeleccionado.toString());
                return false;
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (nuevoItemSeleccionado != null) {
                    if (itemSeleccionado != null && objetosEnVista.containsKey(itemSeleccionado.getCodigo()) && objetosEnVista.get(itemSeleccionado.getCodigo()) == 1) {
                        new CambiarItemIdentificadoServerCall(SeparacionItemsActivity.this, itemSeleccionado, nuevoItemSeleccionado);
                    } else {
                        new SeleccionarItemServerCall(SeparacionItemsActivity.this, nuevoItemSeleccionado);
                    }
                    dialog.dismiss();
                } else
                    Toast.makeText(getApplicationContext(), "No a preseleccionado ningun item.", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void itemSeleccionado() {
        int valor = 1;
        itemSeleccionado = nuevoItemSeleccionado;
        nuevoItemSeleccionado = null;
        if (objetosEnVista.containsKey(itemSeleccionado.getCodigo()))
            valor += objetosEnVista.get(itemSeleccionado.getCodigo());
        objetosEnVista.put(itemSeleccionado.getCodigo(), valor);
        btnSeleccionarItem.setText("Cambiar");
        btnSeleccionarItem.setBackgroundColor(Color.MAGENTA);
    }

    public void cambiarItem() {//esta se engarga de cambiar el estado en el servidor
        objetosEnVista.remove(itemSeleccionado.getCodigo());
        itemSeleccionado();
    }

    public void alertSeleccionarBotella(JSONArray botellas) {
        ArrayList<BotellaSuero> botellasSuero = new ArrayList<>();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SeparacionItemsActivity.this);
        if (botellas.length() > 0) {
            for (int i = 0; i < botellas.length(); i++) {
                try {
                    botellasSuero.add(new BotellaSuero(botellas.getJSONObject(i).getString("codigo"), botellas.getJSONObject(i).getDouble("cantidad")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mBuilder.setTitle("Seleccione una partida");
            final ListAdapter adaptador = new ArrayAdapter<BotellaSuero>(this, android.R.layout.select_dialog_singlechoice, botellasSuero);
            mBuilder.setSingleChoiceItems(adaptador, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    botellaSueroSeleccionada = (BotellaSuero) ((AlertDialog) dialog).getListView().getItemAtPosition(which);
                }
            });
            mBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//TODO override del listener del positive button para que no cierre el dialogo, si no escojio botella
                    iniciarNuevaBotellaSuero(botellaSueroSeleccionada);
                    dialog.dismiss();
                }
            });
        } else {
            mBuilder.setTitle("No hay botellas de suero disponible");
            mBuilder.setMessage("No hay ninguna botella de suero para completar.\nDebe agregar una nueva.");
            mBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    iniciarLoader();
                    new NuevaBotellaSueroServerCall(SeparacionItemsActivity.this);
                    dialog.dismiss();
                }
            });
        }
        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void iniciarNuevaBotellaSuero(BotellaSuero botella) {
        botellaSueroSeleccionada = botella;
        txtBotellaSuero.setText(botellaSueroSeleccionada.getCodigo().toString());
        txtDisponibleBotellaSuero.setText("Disponible: " + (500 - botellaSueroSeleccionada.getCantidad()));
    }

    private void createRows() {
        tablaExtraccionesSuero.removeAllViews();
        for (int i = 0; i < extraccionSueroDeBolsas.size(); i++) {
            extraccionSueroDeBolsas.get(i).setLocalId(i);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setId(i/*partidas.get(i).getLocalId()*/);
            final TextView columnaCodigoItem = new TextView(this.getApplicationContext());
            columnaCodigoItem.setText(extraccionSueroDeBolsas.get(i).getCodigo());
            columnaCodigoItem.setTextColor(0xFF000000);
            columnaCodigoItem.setBackgroundColor(Color.parseColor("#f7f7f7"));
            row.addView(columnaCodigoItem);
            final TextView columnaCantidadExtraida = new TextView(this.getApplicationContext());
            columnaCantidadExtraida.setText(extraccionSueroDeBolsas.get(i).getCantidad() + "");
            columnaCantidadExtraida.setTextColor(0xFF000000);
            row.addView(columnaCantidadExtraida);
            final TextView columnaCodigoBotellaSuero = new TextView(this.getApplicationContext());
            columnaCodigoBotellaSuero.setText(extraccionSueroDeBolsas.get(i).getCodigoBotellaDeSuero());
            columnaCodigoBotellaSuero.setTextColor(0xFF000000);
            row.addView(columnaCodigoBotellaSuero);

            final Button rowEditBtn = new Button(this);
            rowEditBtn.setBackgroundResource(R.drawable.ic_edit_row);
            rowEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // questionEditPartida(((TableRow) v.getParent()).getId());
                }
            });
            row.addView(rowEditBtn);
            final Button rowDeleteBtn = new Button(this);
            rowDeleteBtn.setBackgroundResource(R.drawable.ic_delete_row);
            rowDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //removePartidaById(((TableRow) v.getParent()).getId());
                }
            });
            row.addView(rowDeleteBtn);
            tablaExtraccionesSuero.addView(row);

            final TableRow trSep = new TableRow(this);
            TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trParamsSep.setMargins(0, 0, 0, 0);
            trSep.setLayoutParams(trParamsSep);
            TextView tvSep = new TextView(this);
            TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tvSepLay.span = 5;
            tvSep.setLayoutParams(tvSepLay);
            tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
            tvSep.setHeight(1);
            trSep.addView(tvSep);
            tablaExtraccionesSuero.addView(trSep, trParamsSep);
        }
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
