package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.models.BotellaSuero;
import ort.proyecto_final.mvdmart.models.ExtraccionSueroDeBolsa;
import ort.proyecto_final.mvdmart.models.Item;
import ort.proyecto_final.mvdmart.server_calls.CambiarItemIdentificadoServerCall;
import ort.proyecto_final.mvdmart.server_calls.NuevaBotellaSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerBotellasDeSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerItemsIdentificadosServerCall;
import ort.proyecto_final.mvdmart.server_calls.SeleccionarItemServerCall;

public class SeparacionSueroActivity extends ActivityMadre {

    private BotellaSuero botellaSueroSeleccionada;
    private Button btnSeleccionarItem, btnFinalizarItem, btnSeleccionarBotellaSuero, btnAgregarExtraccionSuero;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<Item>> expandableListDetail;
    private Item itemSeleccionado, nuevoItemSeleccionado;
    private TextView alertTitle, txtItemSeleccionado, txtBotellaSueroSeleccionada, txtDisponibleBotellaSuero;
    private HashMap<String, Integer> objetosEnVista = new HashMap<>();
    private TableLayout tablaExtraccionesSuero;
    ArrayList<ExtraccionSueroDeBolsa> extraccionSueroDeBolsas = new ArrayList<>();
    private EditText txtCantidadSueroExtraido;
    private int disponibleBotellaSuero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_separacion_suero);
        spinnerLoader = findViewById(R.id.spinner_loader);
        inicializarVistas();
    }

    @Override
    public void inicializarVistas() {
        txtCantidadSueroExtraido = findViewById(R.id.txtCantidadSueroExtraido);
        txtCantidadSueroExtraido.setTransformationMethod(null);
        tablaExtraccionesSuero = findViewById(R.id.tablaExtraccionesSuero);
        txtItemSeleccionado = findViewById(R.id.txtItemSeleccionado);
        txtBotellaSueroSeleccionada = findViewById(R.id.txtBotellaSueroSeleccionada);
        txtDisponibleBotellaSuero = findViewById(R.id.txtDisponibleBotellaSuero);
        btnSeleccionarItem = findViewById(R.id.btnSeleccionarItem);
        btnSeleccionarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ObtenerItemsIdentificadosServerCall(SeparacionSueroActivity.this);
            }
        });
        btnSeleccionarBotellaSuero = findViewById(R.id.btnSeleccionarBotellaSuero);
        btnSeleccionarBotellaSuero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarLoader();
                new ObtenerBotellasDeSueroServerCall(SeparacionSueroActivity.this);
            }
        });

        btnAgregarExtraccionSuero = findViewById(R.id.btnAgregarExtraccionSuero);
        btnAgregarExtraccionSuero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botellaSueroSeleccionada != null && itemSeleccionado != null) {
                    agregarNuevaExtraccion();
                }
            }
        });
    }

    public void alertSeleccionItem(JSONObject itemsParaSeparar) {
        try {
            expandableListDetail = Item.itemsParaSeparar(itemsParaSeparar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        alertTitle = new TextView(SeparacionSueroActivity.this);
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
                    txtItemSeleccionado.setText(nuevoItemSeleccionado.toString());
                    if (itemSeleccionado != null && objetosEnVista.containsKey(itemSeleccionado.getCodigo()) && objetosEnVista.get(itemSeleccionado.getCodigo()) == 1) {
                        new CambiarItemIdentificadoServerCall(SeparacionSueroActivity.this, itemSeleccionado, nuevoItemSeleccionado);
                    } else {
                        new SeleccionarItemServerCall(SeparacionSueroActivity.this, nuevoItemSeleccionado);
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
        btnSeleccionarItem.setText(R.string.btnCambiar);
        btnSeleccionarItem.setBackgroundColor(getResources().getColor(R.color.colorBtnModificar));
    }

    public void cambiarItem() {//esta se engarga de cambiar el estado en el servidor
        objetosEnVista.remove(itemSeleccionado.getCodigo());
        itemSeleccionado();
    }

    public void alertSeleccionarBotella(JSONArray botellas) {
        ArrayList<BotellaSuero> botellasSuero = new ArrayList<>();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SeparacionSueroActivity.this);
        if (botellas.length() > 0) {
            for (int i = 0; i < botellas.length(); i++) {
                try {
                    botellasSuero.add(new BotellaSuero(botellas.getJSONObject(i).getString("codigo"), botellas.getJSONObject(i).getInt("cantidad")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mBuilder.setTitle("Seleccione una botella de suero o inicie una nueva.");
            final ListAdapter adaptador = new ArrayAdapter<BotellaSuero>(this, android.R.layout.select_dialog_singlechoice, botellasSuero);
            mBuilder.setSingleChoiceItems(adaptador, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    botellaSueroSeleccionada = (BotellaSuero) ((AlertDialog) dialog).getListView().getItemAtPosition(which);
                }
            });
            mBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO override del listener del positive button para que no cierre el dialogo, si no escojio botella
                   if(botellaSueroSeleccionada != null)
                        iniciarNuevaBotellaSuero(botellaSueroSeleccionada);
                    dialog.dismiss();
                }
            });
            mBuilder.setNeutralButton("Nueva botella", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new NuevaBotellaSueroServerCall(SeparacionSueroActivity.this);
                    dialog.dismiss();
                }
            });
        } else {
            mBuilder.setTitle("No hay botellas de suero disponible");
            mBuilder.setMessage("No hay ninguna botella de suero para completar.\nDebe agregar una nueva.");
            mBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new NuevaBotellaSueroServerCall(SeparacionSueroActivity.this);
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
        txtBotellaSueroSeleccionada.setText(botellaSueroSeleccionada.getCodigo().toString());
        txtDisponibleBotellaSuero.setText("Disponible para llenar: " + (500 - botellaSueroSeleccionada.getCantidad()));
        disponibleBotellaSuero = 500 - botellaSueroSeleccionada.getCantidad();
    }

    private boolean chequearCamposCompletosYTiposDatos() {
        boolean camposCompletos = true;
        String camposIncompletos = "Atención, debe completar los siguientes campos:";
        int largoStringCampos = camposIncompletos.length();
        if (itemSeleccionado == null)
            camposIncompletos += " selección de itém,";
        if (botellaSueroSeleccionada == null)
            camposIncompletos += " botella de suero,";
        if (!HelpersFunctions.isIntegerParseInt(txtCantidadSueroExtraido.getText().toString()))
            camposIncompletos += " cantidad ml a extraer.";
        if (camposIncompletos.length() != largoStringCampos) {
            camposCompletos = false;
            Toast.makeText(this.getApplicationContext(), camposIncompletos, Toast.LENGTH_LONG).show();
        }
        return camposCompletos;
    }

    private void agregarNuevaExtraccion() {
        if (chequearCamposCompletosYTiposDatos()) {
            int cantidadSueroExtraida = Integer.parseInt(txtCantidadSueroExtraido.getText().toString());
            if (cantidadSueroExtraida <= disponibleBotellaSuero) {
                extraccionSueroDeBolsas.add(new ExtraccionSueroDeBolsa(itemSeleccionado.getCodigo(), itemSeleccionado.getTipo(), botellaSueroSeleccionada.getCodigo(), Integer.parseInt(txtCantidadSueroExtraido.getText().toString())));
                crearTablaExtraccionesSuero();
                txtDisponibleBotellaSuero.setText("Disponible para llenar: " + (disponibleBotellaSuero - cantidadSueroExtraida));
                limpiarCampos();
            } else {
                //TODO podria pasar?
                alert(SeparacionSueroActivity.this, new String[]{"Atención", "La cantidad para extraer es mayor a la capacidad de la botella."}, null);
            }
        }
    }

    private void crearTablaExtraccionesSuero() {
        tablaExtraccionesSuero.removeAllViews();
        for (int i = -1; i < extraccionSueroDeBolsas.size(); i++) {
            TableRow fila = new TableRow(this);
            fila.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 15f));
            if (i < 0)
                fila.setBackgroundColor(Color.rgb(36, 123, 160));
            else {
                extraccionSueroDeBolsas.get(i).setLocalId(i);
                fila.setId(i);
                fila.setBackgroundColor((i % 2 == 0) ? Color.rgb(112, 193, 179) : Color.rgb(178, 219, 191));
            }

            TextView columnaCodigoItem = new TextView(this.getApplicationContext());
            if (i < 0)
                columnaCodigoItem.setText("Código de itém");
            else
                columnaCodigoItem.setText(extraccionSueroDeBolsas.get(i).toString());
            columnaCodigoItem.setTextColor(getResources().getColor(R.color.colorBlanco));
            columnaCodigoItem.setGravity(Gravity.CENTER);
            columnaCodigoItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            fila.addView(columnaCodigoItem, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

            TextView columnaCantidadExtraida = new TextView(this.getApplicationContext());
            if (i < 0)
                columnaCantidadExtraida.setText("Cantidad suero extraído");
            else
                columnaCantidadExtraida.setText(extraccionSueroDeBolsas.get(i).getCantidad());
            columnaCantidadExtraida.setTextColor(getResources().getColor(R.color.colorBlanco));
            columnaCantidadExtraida.setGravity(Gravity.CENTER);
            columnaCantidadExtraida.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            fila.addView(columnaCantidadExtraida, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

            TextView columnaCodigoBotellaSuero = new TextView(this.getApplicationContext());
            if (i < 0)
                columnaCodigoBotellaSuero.setText("Hora");
            else
                columnaCodigoBotellaSuero.setText(extraccionSueroDeBolsas.get(i).getCodigoBotellaDeSuero());
            columnaCodigoBotellaSuero.setTextColor(getResources().getColor(R.color.colorBlanco));
            columnaCodigoBotellaSuero.setGravity(Gravity.CENTER);
            columnaCodigoBotellaSuero.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            fila.addView(columnaCodigoBotellaSuero, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

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
                        alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere editar este registro?"}, obj);
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
                        // preguntaEliminarPartida(((TableRow) v.getParent()).getId());
                        HashMap<String, Integer> obj = new HashMap<>();
                        obj.put("funcion", 2);
                        obj.put("id", (((TableRow) v.getParent()).getId()));
                        alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere borrar este registro?"}, obj);
                    }
                });
                fila.addView(columnaBorrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.5f));
            }

            if (i < 0) {
                TextView columnaCerrar = new TextView(this.getApplicationContext());
                columnaCerrar.setText("Cerrar");
                columnaCerrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCerrar.setGravity(Gravity.CENTER);
                columnaCerrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaCerrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.5f));
            } else {
                Button columnaCerrar = new Button(this);
                columnaCerrar.setText("CERRAR");
                columnaCerrar.setBackgroundResource(android.R.color.holo_red_dark);
                columnaCerrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCerrar.setGravity(Gravity.CENTER);
                columnaCerrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                columnaCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // preguntaEliminarPartida(((TableRow) v.getParent()).getId());
                        HashMap<String, Integer> obj = new HashMap<>();
                        obj.put("funcion", 2);
                        obj.put("id", (((TableRow) v.getParent()).getId()));
                        alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere cerrar este item?"}, obj);
                    }
                });
                fila.addView(columnaCerrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.5f));
            }

            tablaExtraccionesSuero.addView(fila);

            final TableRow trSep = new TableRow(this);
            TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trParamsSep.setMargins(0, 0, 0, 0);
            trSep.setLayoutParams(trParamsSep);
            TextView tvSep = new TextView(this);
            TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tvSepLay.span = 6;
            tvSep.setLayoutParams(tvSepLay);
            tvSep.setBackgroundColor(Color.rgb(243, 255, 189));
            tvSep.setHeight(4);
            trSep.addView(tvSep);
            tablaExtraccionesSuero.addView(trSep, trParamsSep);
        }
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


    @Override
    public void limpiarCampos() {
        txtCantidadSueroExtraido.setText("");
    }

    @Override
    public void customOnErrorResponseVolley(Object object) {

    }

    @Override
    public void customAlertFunction(Object object) {

    }


}
