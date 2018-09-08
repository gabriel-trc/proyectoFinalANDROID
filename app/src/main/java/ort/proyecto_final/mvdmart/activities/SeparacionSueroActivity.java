package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
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
import ort.proyecto_final.mvdmart.models.ExtraccionMezclaDeBolsa;
import ort.proyecto_final.mvdmart.models.ExtraccionSueroDeBolsa;
import ort.proyecto_final.mvdmart.models.Item;
import ort.proyecto_final.mvdmart.server_calls.CambiarBotellaDeMezclaSeleccionadaServerCall;
import ort.proyecto_final.mvdmart.server_calls.CambiarBotellaSueroSeleccionadaServerCall;
import ort.proyecto_final.mvdmart.server_calls.CambiarItemIdentificadoServerCall;
import ort.proyecto_final.mvdmart.server_calls.CancelarSeparacionServerCall;
import ort.proyecto_final.mvdmart.server_calls.FinalizarSeparacionServerCall;
import ort.proyecto_final.mvdmart.server_calls.NuevaBotellaMezclaServerCall;
import ort.proyecto_final.mvdmart.server_calls.NuevaBotellaSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerBotellasDeMezclaServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerBotellasDeSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerItemsIdentificadosServerCall;
import ort.proyecto_final.mvdmart.server_calls.SeleccionarBotellaDeMezclaServerCall;
import ort.proyecto_final.mvdmart.server_calls.SeleccionarBotellaDeSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.SeleccionarItemServerCall;

public class SeparacionSueroActivity extends ActivityMadre implements View.OnClickListener {

    private BotellaSuero botellaSueroSeleccionada, nuevaBotellaDeSueroSeleccionada;
    private Button btnSeleccionarItem, btnTirarItem, btnCerrarBotellaMezcla, btnFinalizarSeparacion, btnSeleccionarBotellaSuero, btnSeleccionarBotellaMezcla, btnAgregarExtraccionSuero, btnAgregarExtraccionMezcla, btnCancelarSeparacion;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<Item>> expandableListDetail;
    private Item itemSeleccionado, nuevoItemSeleccionado, botellaMezclaSeleccionada, nuevaBotellaDeMezclaSeleccionada;
    private TextView alertTitle, txtItemSeleccionado, txtBotellaSueroSeleccionada, txtDisponibleParaLlenarSuero, txtBotellaMezclaSeleccionada, txtLabelDisponibleSuero;
    private HashMap<String, Integer> objetosEnVista = new HashMap<>();
    private HashMap<String, String> hashMapCustomAlertFunction = new HashMap<>();
    private TableLayout tablaExtraccionesSuero, tablaExtraccionesMezcla;
    ArrayList<ExtraccionSueroDeBolsa> extraccionesSueroDeBolsas = new ArrayList<>();
    ArrayList<ExtraccionMezclaDeBolsa> extraccionesMezclaDeBolsas = new ArrayList<>();
    private ExtraccionSueroDeBolsa extraccionSueroDeBolsaModificar;
    private EditText txtCantidadSueroExtraido;
    private Integer cantidadDisponible;
    private ScrollView scrollView;

    public HashMap<String, Integer> getObjetosEnVista() {
        return objetosEnVista;
    }

    public BotellaSuero getBotellaSueroSeleccionada() {
        return botellaSueroSeleccionada;
    }

    public Item getBotellaMezclaSeleccionada() {
        return botellaMezclaSeleccionada;
    }

    public void setNuevaBotellaDeSueroSeleccionada(BotellaSuero nuevaBotellaDeSueroSeleccionada) {
        this.nuevaBotellaDeSueroSeleccionada = nuevaBotellaDeSueroSeleccionada;
    }

    public void setNuevaBotellaDeMezclaSeleccionada(Item nuevaBotellaDeMezclaSeleccionada) {
        this.nuevaBotellaDeMezclaSeleccionada = nuevaBotellaDeMezclaSeleccionada;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_separacion_suero);
        spinnerLoader = findViewById(R.id.spinner_loader);
        inicializarVistas();
    }

    @Override
    public void inicializarVistas() {
        scrollView = findViewById(R.id.scrollViewPpal);
        txtLabelDisponibleSuero = findViewById(R.id.txtLabelDisponibleSuero);
        txtCantidadSueroExtraido = findViewById(R.id.txtCantidadSueroExtraido);
        txtCantidadSueroExtraido.setTransformationMethod(null);
        txtCantidadSueroExtraido.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    esconderTecado(SeparacionSueroActivity.this);
                }
                return false;
            }
        });
        tablaExtraccionesSuero = findViewById(R.id.tablaExtraccionesSuero);
        tablaExtraccionesMezcla = findViewById(R.id.tablaExtraccionesMezcla);
        txtItemSeleccionado = findViewById(R.id.txtItemSeleccionado);
        txtBotellaMezclaSeleccionada = findViewById(R.id.txtBotellaMezclaSeleccionada);
        txtBotellaSueroSeleccionada = findViewById(R.id.txtBotellaSueroSeleccionada);
        txtDisponibleParaLlenarSuero = findViewById(R.id.txtDisponibleParaLlenarSuero);

        btnSeleccionarItem = findViewById(R.id.btn_rs_SeleccionarItem);
        btnSeleccionarItem.setOnClickListener(this);
        btnSeleccionarBotellaSuero = findViewById(R.id.btn_rs_SeleccionarBotellaSuero);
        btnSeleccionarBotellaSuero.setOnClickListener(this);
        btnSeleccionarBotellaMezcla = findViewById(R.id.btn_rs_SeleccionarBotellaMezcla);
        btnSeleccionarBotellaMezcla.setOnClickListener(this);
        btnTirarItem = findViewById(R.id.btn_rs_TirarItem);
        btnTirarItem.setOnClickListener(this);
        btnCerrarBotellaMezcla = findViewById(R.id.btn_rs_CerrarBotellaMezcla);
        btnCerrarBotellaMezcla.setOnClickListener(this);
        btnFinalizarSeparacion = findViewById(R.id.btn_rs_FinalizarSeparacion);
        btnFinalizarSeparacion.setOnClickListener(this);
        btnAgregarExtraccionSuero = findViewById(R.id.btn_rs_AgregarExtraccionSuero);
        btnAgregarExtraccionSuero.setOnClickListener(this);
        btnAgregarExtraccionMezcla = findViewById(R.id.btn_rs_AgregarExtraccionMezcla);
        btnAgregarExtraccionMezcla.setOnClickListener(this);
        btnCancelarSeparacion = findViewById(R.id.btn_rs_CancelarSeparacion);
        btnCancelarSeparacion.setOnClickListener(this);
    }

    private void finalizarSeparacion(Boolean cancelar) {
        if (!extraccionesSueroDeBolsas.isEmpty()) {
            if (btnAgregarExtraccionSuero.getText().equals(getResources().getString(R.string.btnAgregarSuero))) {
                JSONArray extraccionesSuero = new JSONArray();
                for (int i = 0; i < extraccionesSueroDeBolsas.size(); i++) {
                    extraccionesSuero.put(extraccionesSueroDeBolsas.get(i).toJSON());
                }
                JSONArray extraccionesMezcla = new JSONArray();
                if (!extraccionesMezclaDeBolsas.isEmpty()) {
                    for (int i = 0; i < extraccionesMezclaDeBolsas.size(); i++) {
                        extraccionesMezcla.put(extraccionesMezclaDeBolsas.get(i).toJSON());
                    }
                }
                if (!cancelar) {
                    Item itemParaLlamada = null;
                    String codigoBotellaSueroLlamada = "";
                    String codigoBotellaMezclaLlamada = "";
                    if (itemSeleccionado != null && objetosEnVista.containsKey(itemSeleccionado.getCodigo()) && objetosEnVista.get(itemSeleccionado.getCodigo()) == 1)
                        itemParaLlamada = itemSeleccionado;
                    if (botellaSueroSeleccionada != null && objetosEnVista.containsKey(botellaSueroSeleccionada.getCodigo()) && objetosEnVista.get(botellaSueroSeleccionada.getCodigo()) == 1)
                        codigoBotellaSueroLlamada = botellaSueroSeleccionada.getCodigo();
                    if (botellaMezclaSeleccionada != null && objetosEnVista.containsKey(botellaMezclaSeleccionada.getCodigo()) && objetosEnVista.get(botellaMezclaSeleccionada.getCodigo()) == 1)
                        codigoBotellaMezclaLlamada = botellaMezclaSeleccionada.getCodigo();
                    new FinalizarSeparacionServerCall(this, extraccionesSuero, extraccionesMezcla, itemParaLlamada, codigoBotellaSueroLlamada, codigoBotellaMezclaLlamada);
                } else
                    new CancelarSeparacionServerCall(this, extraccionesSuero, extraccionesMezcla, itemSeleccionado, (botellaSueroSeleccionada != null) ? botellaSueroSeleccionada.getCodigo() : null, (botellaMezclaSeleccionada != null) ? botellaMezclaSeleccionada.getCodigo() : null);
            } else {
                Toast.makeText(SeparacionSueroActivity.this, "Atención: Debes terminar de modificar el registro.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SeparacionSueroActivity.this, "Atención: No tienes ningun registro de separación de suero.", Toast.LENGTH_LONG).show();
        }
    }

    //region Manejo de Item para separar

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
        if (expandableListDetail.size() == 0)
            builder.setMessage("No hay ningun ítem para separar.");
        else {
            builder.setMessage("");
            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (nuevoItemSeleccionado != null) {
                        if (itemSeleccionado != null && objetosEnVista.containsKey(itemSeleccionado.getCodigo()) && objetosEnVista.get(itemSeleccionado.getCodigo()) == 1)
                            new CambiarItemIdentificadoServerCall(SeparacionSueroActivity.this, itemSeleccionado, nuevoItemSeleccionado);
                        else
                            new SeleccionarItemServerCall(SeparacionSueroActivity.this, nuevoItemSeleccionado);
                        dialog.dismiss();
                    } else
                        Toast.makeText(getApplicationContext(), "No a preseleccionado ningun item.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void itemSeleccionado() {
        itemSeleccionado = nuevoItemSeleccionado;
        nuevoItemSeleccionado = null;
        agregarReferenciaDeObjetoEnVista(itemSeleccionado.getCodigo());
        txtItemSeleccionado.setText(itemSeleccionado.toString());
        btnSeleccionarItem.setText(R.string.btnCambiar);
        btnSeleccionarItem.setBackgroundColor(getResources().getColor(R.color.colorBtnModificar));
        btnTirarItem.setText(R.string.btnTirar);
        btnTirarItem.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
    }

    public void cambiarItem() {//esta se engarga de cambiar el estado en el servidor
        quitarReferenciaDeObjetoEnVista(itemSeleccionado.getCodigo());
        itemSeleccionado();
    }

    //endregion

    //region Manejo de Botella de Suero para separar

    public void alertSeleccionarBotellaSuero(JSONArray botellas) {
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
                    nuevaBotellaDeSueroSeleccionada = (BotellaSuero) ((AlertDialog) dialog).getListView().getItemAtPosition(which);
                }
            });
            mBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO override del listener del positive button para que no cierre el dialogo, si no escojio botella
                    if (nuevaBotellaDeSueroSeleccionada != null) {
                        if (botellaSueroSeleccionada != null && objetosEnVista.containsKey(botellaSueroSeleccionada.getCodigo()) && objetosEnVista.get(botellaSueroSeleccionada.getCodigo()) == 1) {
                            new CambiarBotellaSueroSeleccionadaServerCall(SeparacionSueroActivity.this, botellaSueroSeleccionada, nuevaBotellaDeSueroSeleccionada, false);
                        } else {
                            new SeleccionarBotellaDeSueroServerCall(SeparacionSueroActivity.this, nuevaBotellaDeSueroSeleccionada);
                        }
                        dialog.dismiss();
                    } else
                        Toast.makeText(getApplicationContext(), "No a preseleccionado ningun item.", Toast.LENGTH_SHORT).show();
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

    public void botellaDeSueroSeleccionada() {
        botellaSueroSeleccionada = nuevaBotellaDeSueroSeleccionada;
        nuevaBotellaDeSueroSeleccionada = null;
        agregarReferenciaDeObjetoEnVista(botellaSueroSeleccionada.getCodigo());
        txtLabelDisponibleSuero.setVisibility(View.VISIBLE);
        txtBotellaSueroSeleccionada.setText(botellaSueroSeleccionada.getCodigo());
        txtDisponibleParaLlenarSuero.setText((500 - botellaSueroSeleccionada.getCantidadOcupada()) + " mL");
        cantidadDisponible = 500 - botellaSueroSeleccionada.getCantidadDisponible();
        btnSeleccionarBotellaSuero.setText(R.string.btnCambiar);
        btnSeleccionarBotellaSuero.setBackgroundColor(getResources().getColor(R.color.colorBtnModificar));
    }

    public void cambiarBotellaDeSuero() {//esta se engarga de cambiar el estado en el servidor, ya que al ir bajando la referencia, cuando la misma es uno. En el cambiar va a al metodo que cambia
        quitarReferenciaDeObjetoEnVista(botellaSueroSeleccionada.getCodigo());
        botellaDeSueroSeleccionada();
    }

    //endregion

    //region Manejo de Botella de Mezcla para juntar

    public void alertSeleccionarBotellaMezcla(JSONArray botellas) {
        ArrayList<Item> botellasMezcla = new ArrayList<>();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SeparacionSueroActivity.this);
        if (botellas.length() > 0) {
            for (int i = 0; i < botellas.length(); i++) {
                try {
                    botellasMezcla.add(new Item(botellas.getJSONObject(i).getString("codigo"), 1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mBuilder.setTitle("Seleccione una botella de mezcla o inicie una nueva.");
            final ListAdapter adaptador = new ArrayAdapter<Item>(this, android.R.layout.select_dialog_singlechoice, botellasMezcla);
            mBuilder.setSingleChoiceItems(adaptador, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nuevaBotellaDeMezclaSeleccionada = (Item) ((AlertDialog) dialog).getListView().getItemAtPosition(which);
                }
            });
            mBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO override del listener del positive button para que no cierre el dialogo, si no escojio botella
                    if (nuevaBotellaDeMezclaSeleccionada != null) {
                        if (botellaMezclaSeleccionada != null && objetosEnVista.containsKey(botellaMezclaSeleccionada.getCodigo()) && objetosEnVista.get(botellaMezclaSeleccionada.getCodigo()) == 1) {
                            new CambiarBotellaDeMezclaSeleccionadaServerCall(SeparacionSueroActivity.this, botellaMezclaSeleccionada, nuevaBotellaDeMezclaSeleccionada, false);
                        } else {
                            new SeleccionarBotellaDeMezclaServerCall(SeparacionSueroActivity.this, nuevaBotellaDeMezclaSeleccionada);
                        }
                        dialog.dismiss();
                    } else
                        Toast.makeText(getApplicationContext(), "No a preseleccionado ningun item.", Toast.LENGTH_SHORT).show();
                }
            });
            mBuilder.setNeutralButton("Nueva botella", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new NuevaBotellaMezclaServerCall(SeparacionSueroActivity.this);
                    dialog.dismiss();
                }
            });
        } else {
            mBuilder.setTitle("No hay botellas de mezcla disponible");
            mBuilder.setMessage("No hay ninguna botella de mezca para completar.\nDebe agregar una nueva.");
            mBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new NuevaBotellaMezclaServerCall(SeparacionSueroActivity.this);
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

    public void botellaDeMezclaSeleccionada() {
        botellaMezclaSeleccionada = nuevaBotellaDeMezclaSeleccionada;
        nuevaBotellaDeMezclaSeleccionada = null;
        agregarReferenciaDeObjetoEnVista(botellaMezclaSeleccionada.getCodigo());
        txtBotellaMezclaSeleccionada.setText(botellaMezclaSeleccionada.getCodigo());
        btnSeleccionarBotellaMezcla.setText(R.string.btnCambiar);
        btnSeleccionarBotellaMezcla.setBackgroundColor(getResources().getColor(R.color.colorBtnModificar));
    }

    public void cambiarBotellaDeMezcla() {//esta se engarga de cambiar el estado en el servidor, ya que al ir bajando la referencia, cuando la misma es uno. En el cambiar va a al metodo que cambia
        quitarReferenciaDeObjetoEnVista(botellaMezclaSeleccionada.getCodigo());
        botellaDeMezclaSeleccionada();
    }

    //endregion

    public void agregarReferenciaDeObjetoEnVista(String codigoObjeto) {
        int valor = 1;
        if (objetosEnVista.containsKey(codigoObjeto))
            valor += objetosEnVista.get(codigoObjeto);
        objetosEnVista.put(codigoObjeto, valor);
    }

    public void quitarReferenciaDeObjetoEnVista(String codigoObjeto) {
        objetosEnVista.remove(codigoObjeto);
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

    private void agregarNuevaExtraccionSuero() {
        //TODO demacidos if... dividir
        if (itemSeleccionado != null && botellaSueroSeleccionada != null) {
            if (!itemSeleccionado.isFinalizado()) {
                if (botellaSueroSeleccionada.getCantidadDisponible() != 0) {
                    if (chequearCamposCompletosYTiposDatos()) {
                        int cantidadSueroExtraida = Integer.parseInt(txtCantidadSueroExtraido.getText().toString());
                        ExtraccionSueroDeBolsa nueva = new ExtraccionSueroDeBolsa(itemSeleccionado.getCodigo(), itemSeleccionado.getTipo(), botellaSueroSeleccionada.getCodigo(), Integer.parseInt(txtCantidadSueroExtraido.getText().toString()));
                        if (cantidadSueroExtraida <= botellaSueroSeleccionada.getCantidadDisponible()) {
                            if (!extraccionesSueroDeBolsas.contains(nueva)) {
                                agregarReferenciaDeObjetoEnVista(itemSeleccionado.getCodigo());
                                agregarReferenciaDeObjetoEnVista(botellaSueroSeleccionada.getCodigo());
                                extraccionesSueroDeBolsas.add(nueva);
                            } else {
                                extraccionSueroDeBolsaModificar = extraccionesSueroDeBolsas.get(extraccionesSueroDeBolsas.indexOf(nueva));
                                extraccionSueroDeBolsaModificar.setCantidad(extraccionSueroDeBolsaModificar.getCantidad() + cantidadSueroExtraida);
                                extraccionSueroDeBolsaModificar = null;
                            }
                            txtDisponibleParaLlenarSuero.setText((botellaSueroSeleccionada.getCantidadDisponible() - cantidadSueroExtraida) + " mL");
                            cantidadDisponible = botellaSueroSeleccionada.getCantidadDisponible() - cantidadSueroExtraida;
                            botellaSueroSeleccionada.setCantidadDisponible(botellaSueroSeleccionada.getCantidadDisponible() - cantidadSueroExtraida);
                            limpiarCampos();
                            crearTablaExtraccionesSuero();
                        } else {
                            alert(SeparacionSueroActivity.this, new String[]{"Atención", "La cantidad para extraer es mayor a la capacidad de la botella."}, null);
                        }
                    }
                } else {
                    alert(SeparacionSueroActivity.this, new String[]{"Atención", "No queda mas capacidad disponible en la botella de suero; debe de cambiarla."}, null);
                }
            } else {
                alert(SeparacionSueroActivity.this, new String[]{"Atención", "El ítem seleccionado fue descartado; debe recuperarlo para poder realizar una extraccón."}, null);
            }
        } else {
            alert(SeparacionSueroActivity.this, new String[]{"Atención", "No tiene seleccionado al menos uno de los siguientes: ítem o botella de suero."}, null);
        }
    }

    private void modificarExtraccionSuero() {
        if (chequearCamposCompletosYTiposDatos()) {
            int cantidadSueroExtraida = Integer.parseInt(txtCantidadSueroExtraido.getText().toString());
            if (cantidadSueroExtraida <= botellaSueroSeleccionada.getCantidadDisponible()) {
                extraccionSueroDeBolsaModificar.setCantidad(cantidadSueroExtraida);
                txtDisponibleParaLlenarSuero.setText((botellaSueroSeleccionada.getCantidadDisponible() - cantidadSueroExtraida) + " mL");
                cantidadDisponible = botellaSueroSeleccionada.getCantidadDisponible() - cantidadSueroExtraida;
                botellaSueroSeleccionada.setCantidadDisponible(botellaSueroSeleccionada.getCantidadDisponible() - cantidadSueroExtraida);
                crearTablaExtraccionesSuero();
                limpiarCampos();
                btnAgregarExtraccionSuero.setText(R.string.btnAgregarSuero);
                btnAgregarExtraccionSuero.setBackgroundResource(android.R.color.holo_green_dark);
                extraccionSueroDeBolsaModificar = null;
            } else {
                //TODO podria pasar?
                alert(SeparacionSueroActivity.this, new String[]{"Atención", "La cantidad para extraer es mayor a la capacidad de la botella."}, null);
            }
        }
    }

    private void crearTablaExtraccionesSuero() {
        final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        tablaExtraccionesSuero.removeAllViews();
        for (int i = -1; i < extraccionesSueroDeBolsas.size(); i++) {
            TableRow fila = new TableRow(this);
            fila.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 10.5f));
            if (i < 0)
                fila.setBackgroundColor(Color.rgb(36, 123, 160));
            else {
                extraccionesSueroDeBolsas.get(i).setLocalId(i);
                fila.setId(i);
                fila.setTag(extraccionesSueroDeBolsas.get(i).getCodigo());
                fila.setBackgroundColor((i % 2 == 0) ? Color.rgb(112, 193, 179) : Color.rgb(178, 219, 191));
            }

            TextView columnaCodigoItem = new TextView(this.getApplicationContext());
            if (i < 0)
                columnaCodigoItem.setText("Item");
            else
                columnaCodigoItem.setText(extraccionesSueroDeBolsas.get(i).getCodigo());
            columnaCodigoItem.setTextColor(getResources().getColor(R.color.colorBlanco));
            columnaCodigoItem.setGravity(Gravity.CENTER);
            columnaCodigoItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            fila.addView(columnaCodigoItem, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

            TextView columnaCantidadExtraida = new TextView(this.getApplicationContext());
            if (i < 0)
                columnaCantidadExtraida.setText("Cantidad de suero");
            else
                columnaCantidadExtraida.setText(extraccionesSueroDeBolsas.get(i).getCantidad() + "");
            columnaCantidadExtraida.setTextColor(getResources().getColor(R.color.colorBlanco));
            columnaCantidadExtraida.setGravity(Gravity.CENTER);
            columnaCantidadExtraida.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            fila.addView(columnaCantidadExtraida, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

            TextView columnaCodigoBotellaSuero = new TextView(this.getApplicationContext());
            if (i < 0)
                columnaCodigoBotellaSuero.setText("Botella de suero");
            else
                columnaCodigoBotellaSuero.setText(extraccionesSueroDeBolsas.get(i).getCodigoBotellaDeSuero());
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
                        hashMapCustomAlertFunction.put("funcion", "1");
                        hashMapCustomAlertFunction.put("id", (((TableRow) v.getParent()).getId() + ""));
                        v.startAnimation(scale);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere editar este registro?"}, hashMapCustomAlertFunction);
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
                        hashMapCustomAlertFunction.put("funcion", "2");
                        hashMapCustomAlertFunction.put("id", (((TableRow) v.getParent()).getId() + ""));
                        v.startAnimation(scale);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere borrar este registro?"}, hashMapCustomAlertFunction);
                            }
                        }, 300);


                    }
                });
                fila.addView(columnaBorrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
            }

            if (i < 0) {
                TextView columnaCerrar = new TextView(this.getApplicationContext());
                columnaCerrar.setText("Tirar");
                columnaCerrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCerrar.setGravity(Gravity.CENTER);
                columnaCerrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaCerrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            } else {
                Button columnaCerrar = new Button(this);
                columnaCerrar.setText((extraccionesSueroDeBolsas.get(i).isFinalizado()) ? R.string.btnTirada : R.string.btnTirar);
                columnaCerrar.setTag(extraccionesSueroDeBolsas.get(i).getCodigo() + "TIRAR");
                columnaCerrar.setBackgroundResource(((extraccionesSueroDeBolsas.get(i).isFinalizado())) ? android.R.color.holo_purple : android.R.color.holo_blue_light);
                columnaCerrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCerrar.setGravity(Gravity.CENTER);
                columnaCerrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                columnaCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item i = buscarItemEnExtraccionesDeSuero(((TableRow) v.getParent()).getTag().toString());
                        hashMapCustomAlertFunction.put("id", (((TableRow) v.getParent()).getTag() + ""));
                        hashMapCustomAlertFunction.put("funcion", "3");
                        final String msg = (i.isFinalizado()) ? "¿Quiere recuperar este item?" : "¿Quiere tirar este item?";
                        v.startAnimation(scale);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", msg}, hashMapCustomAlertFunction);
                            }
                        }, 300);
                    }
                });
                fila.addView(columnaCerrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));
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
            tvSep.setHeight(3);
            trSep.addView(tvSep);
            tablaExtraccionesSuero.addView(trSep, trParamsSep);
        }
    }

    private void agregarNuevaExtraccionMezcla() {
        if (botellaMezclaSeleccionada != null && itemSeleccionado != null) {
            if (!botellaMezclaSeleccionada.isFinalizado()) {
                if (!extraccionesMezclaDeBolsas.contains(new ExtraccionMezclaDeBolsa(itemSeleccionado.getCodigo(), itemSeleccionado.getTipo(), botellaMezclaSeleccionada.getCodigo()))) {
                    agregarReferenciaDeObjetoEnVista(itemSeleccionado.getCodigo());
                    agregarReferenciaDeObjetoEnVista(botellaMezclaSeleccionada.getCodigo());
                    extraccionesMezclaDeBolsas.add(new ExtraccionMezclaDeBolsa(itemSeleccionado.getCodigo(), itemSeleccionado.getTipo(), botellaMezclaSeleccionada.getCodigo()));
                    crearTablaExtraccionesMezcla();
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                } else {
                    alert(SeparacionSueroActivity.this, new String[]{"Atención", "Ya ingreso un sobrante de mezcla de el item seleccionado a la botella de mezcla."}, null);
                }
            } else {
                alert(SeparacionSueroActivity.this, new String[]{"Atención", "La botella de mezcla seleccionada fue cerrada; debe abrirla para poder realizar depositar mezcla."}, null);
            }
        } else {
            alert(SeparacionSueroActivity.this, new String[]{"Atención", "No hay seleccionado un item de donde extraer la mezcla o un recipiente donde depositarla."}, null);
        }

    }

    private void crearTablaExtraccionesMezcla() {
        final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        tablaExtraccionesMezcla.removeAllViews();
        if (extraccionesMezclaDeBolsas.size() > 0) {
            for (int i = -1; i < extraccionesMezclaDeBolsas.size(); i++) {
                TableRow fila = new TableRow(this);
                fila.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 8.5f));
                if (i < 0)
                    fila.setBackgroundColor(Color.rgb(36, 123, 160));
                else {
                    extraccionesMezclaDeBolsas.get(i).setLocalId(i);
                    fila.setId(i);
                    fila.setTag(extraccionesMezclaDeBolsas.get(i).getCodigoBotellaMezcla());
                    fila.setBackgroundColor((i % 2 == 0) ? Color.rgb(112, 193, 179) : Color.rgb(178, 219, 191));
                }
                TextView columnaCodigoItem = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaCodigoItem.setText("Item");
                else
                    columnaCodigoItem.setText(extraccionesMezclaDeBolsas.get(i).getCodigo());
                columnaCodigoItem.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCodigoItem.setGravity(Gravity.CENTER);
                columnaCodigoItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaCodigoItem, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

                TextView columnaCodigoBotellaMezcla = new TextView(this.getApplicationContext());
                if (i < 0)
                    columnaCodigoBotellaMezcla.setText("Botella de mezcla");
                else
                    columnaCodigoBotellaMezcla.setText(extraccionesMezclaDeBolsas.get(i).getCodigoBotellaMezcla());
                columnaCodigoBotellaMezcla.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCodigoBotellaMezcla.setGravity(Gravity.CENTER);
                columnaCodigoBotellaMezcla.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                fila.addView(columnaCodigoBotellaMezcla, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

//            if (i < 0) {
//                TextView columnaModificar = new TextView(this.getApplicationContext());
//                columnaModificar.setText("Modificar");
//                columnaModificar.setTextColor(getResources().getColor(R.color.colorBlanco));
//                columnaModificar.setGravity(Gravity.CENTER);
//                columnaModificar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//                fila.addView(columnaModificar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
//            } else {
//                Button columnaModificar = new Button(this);
//                columnaModificar.setText(R.string.btnModificar);
//                columnaModificar.setBackgroundResource(android.R.color.holo_orange_dark);
//                columnaModificar.setTextColor(getResources().getColor(R.color.colorBlanco));
//                columnaModificar.setGravity(Gravity.CENTER);
//                columnaModificar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//                columnaModificar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        HashMap<String, Integer> hashMap = new HashMap<>();
//                        hashMap.put("funcion", 1);
//                        hashMap.put("id", (((TableRow) v.getParent()).getId()));
//                        alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere editar este registro?"}, hashMap);
//                    }
//                });
//                fila.addView(columnaModificar, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.5f));
//            }

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
                            hashMapCustomAlertFunction.put("funcion", "5");
                            hashMapCustomAlertFunction.put("id", (((TableRow) v.getParent()).getId()) + "");
                            alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere borrar este registro de extracción de mezcla?"}, hashMapCustomAlertFunction);
                        }
                    });
                    fila.addView(columnaBorrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
                }

                if (i < 0) {
                    TextView columnaCerrar = new TextView(this.getApplicationContext());
                    columnaCerrar.setText("Cerrar");
                    columnaCerrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                    columnaCerrar.setGravity(Gravity.CENTER);
                    columnaCerrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    fila.addView(columnaCerrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
                } else {
                    Button columnaCerrar = new Button(this);
                    columnaCerrar.setText((extraccionesMezclaDeBolsas.get(i).isFinalizado()) ? R.string.btnCerrada : R.string.btnCerrar);
                    columnaCerrar.setBackgroundResource(((extraccionesMezclaDeBolsas.get(i).isFinalizado())) ? android.R.color.holo_purple : android.R.color.holo_blue_light);
                    columnaCerrar.setTag(extraccionesMezclaDeBolsas.get(i).getCodigoBotellaMezcla() + "CERRAR");
                    columnaCerrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                    columnaCerrar.setGravity(Gravity.CENTER);
                    columnaCerrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    columnaCerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Item i = buscarBotellaDeMezclaEnExtraccionesDeMezcla(((TableRow) v.getParent()).getTag().toString());
                            hashMapCustomAlertFunction.put("id", (((TableRow) v.getParent()).getTag() + ""));
                            hashMapCustomAlertFunction.put("funcion", "4");
                            final String msg = (i.isFinalizado()) ? "¿Quiere abrir esta botella de mezcla?" : "¿Quiere cerrar esta botella de mezcla?";
                            v.startAnimation(scale);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", msg}, hashMapCustomAlertFunction);
                                }
                            }, 300);
                        }
                    });
                    fila.addView(columnaCerrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
                }
                tablaExtraccionesMezcla.addView(fila);
            }

            final TableRow trSep = new TableRow(this);
            TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            trParamsSep.setMargins(0, 0, 0, 0);
            trSep.setLayoutParams(trParamsSep);
            TextView tvSep = new TextView(this);
            TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tvSepLay.span = 5;
            tvSep.setLayoutParams(tvSepLay);
            tvSep.setBackgroundColor(Color.rgb(243, 255, 189));
            tvSep.setHeight(3);
            trSep.addView(tvSep);
            tablaExtraccionesMezcla.addView(trSep, trParamsSep);
        }
    }

    @Override
    public void limpiarCampos() {
        txtCantidadSueroExtraido.setText("");
    }

    @Override
    public void customServerModelError(Object object) {

    }

    @Override
    public void customAlertFunction(Object object) {
        HashMap<String, String> hashMap = (HashMap<String, String>) object;
        switch (hashMap.get("funcion")) {
            case "-1":
                onBackPressed();
                break;
            case "0":
                Toast.makeText(SeparacionSueroActivity.this, "Atención: Debes terminar de modificar el registro.", Toast.LENGTH_LONG).show();
                break;
            case "1":
                extraccionSueroDeBolsaModificar = buscarExtraccionSueroDeBolsaPorLocalId(Integer.parseInt(hashMap.get("id")));
                btnAgregarExtraccionSuero.setText(R.string.btnModificar);
                btnAgregarExtraccionSuero.setBackgroundResource(android.R.color.holo_orange_dark);
                setearExtraccion(extraccionSueroDeBolsaModificar);
                break;
            case "2":
                borrarRegistroExtraccionSuero(Integer.parseInt(hashMap.get("id")));
                break;
            case "3":
                tirarRecuperarItem(hashMap.get("id"));
                break;
            case "4":
                cerrarAbrirBotellaDeMezcla(hashMap.get("id"));
                break;
            case "5":
                borrarRegistroExtraccionMezcla(Integer.parseInt(hashMap.get("id")));
                break;
        }
    }

    private void borrarRegistroExtraccionSuero(int localId) {
        ExtraccionSueroDeBolsa ext = buscarExtraccionSueroDeBolsaPorLocalId(localId);
        extraccionesSueroDeBolsas.remove(ext);
        quitarReferenciaDeObjetoEnVista(ext.getCodigoBotellaDeSuero());
        quitarReferenciaDeObjetoEnVista(ext.getCodigo());
        crearTablaExtraccionesSuero();
        if (botellaSueroSeleccionada.getCodigo().equals(ext.getCodigoBotellaDeSuero())) {
            txtDisponibleParaLlenarSuero.setText((botellaSueroSeleccionada.getCantidadDisponible() + ext.getCantidad()) + " mL");
            cantidadDisponible = botellaSueroSeleccionada.getCantidadDisponible() + ext.getCantidad();
            botellaSueroSeleccionada.setCantidadDisponible(botellaSueroSeleccionada.getCantidadDisponible() + ext.getCantidad());
        }
    }

    private void borrarRegistroExtraccionMezcla(int localId) {
        ExtraccionMezclaDeBolsa ext = buscarExtraccionMezclaPorLocalId(localId);
        extraccionesMezclaDeBolsas.remove(ext);
        quitarReferenciaDeObjetoEnVista(ext.getCodigoBotellaMezcla());
        quitarReferenciaDeObjetoEnVista(ext.getCodigo());
        crearTablaExtraccionesMezcla();
    }

    @Override
    public void backButtonFunction() {
//new CancelarSeparacionServerCall(SeparacionSueroActivity.this);
    }

    private void tirarRecuperarItem(String codigo) {
        for (ExtraccionSueroDeBolsa ext : extraccionesSueroDeBolsas) {
            if (ext.getCodigo().equals(codigo))
                ext.setFinalizado((ext.isFinalizado()) ? false : true);
        }
        if (itemSeleccionado != null && itemSeleccionado.getCodigo().equals(codigo)) {
            itemSeleccionado.setFinalizado((itemSeleccionado.isFinalizado()) ? false : true);
            btnTirarItem.setText((itemSeleccionado.isFinalizado()) ? R.string.btnTirada : R.string.btnTirar);
            btnTirarItem.setBackgroundResource((itemSeleccionado.isFinalizado()) ? android.R.color.holo_purple : android.R.color.holo_blue_light);
        }
        crearTablaExtraccionesSuero();
    }

    private void cerrarAbrirBotellaDeMezcla(String codigoBotella) {
        for (ExtraccionMezclaDeBolsa ext : extraccionesMezclaDeBolsas) {
            if (ext.getCodigoBotellaMezcla().equals(codigoBotella))
                ext.setFinalizado((ext.isFinalizado()) ? false : true);
        }
        if (botellaMezclaSeleccionada != null && botellaMezclaSeleccionada.getCodigo().equals(codigoBotella)) {
            botellaMezclaSeleccionada.setFinalizado((botellaMezclaSeleccionada.isFinalizado()) ? false : true);
            btnCerrarBotellaMezcla.setText((botellaMezclaSeleccionada.isFinalizado()) ? R.string.btnCerrada : R.string.btnCerrar);
            btnCerrarBotellaMezcla.setBackgroundResource((botellaMezclaSeleccionada.isFinalizado()) ? android.R.color.holo_purple : android.R.color.holo_blue_light);
        }
        crearTablaExtraccionesMezcla();
    }

    private ExtraccionSueroDeBolsa buscarExtraccionSueroDeBolsaPorLocalId(int idLocal) {
        ExtraccionSueroDeBolsa ret = null;
        for (ExtraccionSueroDeBolsa extraccion : extraccionesSueroDeBolsas) {
            if (extraccion.getLocalId() == idLocal) {
                ret = extraccion;
            }
        }
        return ret;
    }

    private ExtraccionMezclaDeBolsa buscarExtraccionMezclaPorLocalId(int idLocal) {
        ExtraccionMezclaDeBolsa ret = null;
        for (ExtraccionMezclaDeBolsa extraccion : extraccionesMezclaDeBolsas) {
            if (extraccion.getLocalId() == idLocal) {
                ret = extraccion;
            }
        }
        return ret;
    }

    private Item buscarItemEnExtraccionesDeSuero(String c) {
        Item ret = null;
        for (Item extraccion : extraccionesSueroDeBolsas) {
            if (extraccion.getCodigo().equals(c)) {
                ret = extraccion;
                break;
            }
        }
        //Por dos motivos e item puede no estar dentro de las extracciones, primero porque no haya extracciones donde buscar y segundo porque podria no haber ninguna extraccion
        // del item a buscar. En cualquier caso el item a buscar seria el propio seleccionado
        if (ret == null)
            ret = itemSeleccionado;
        return ret;
    }

    private Item buscarBotellaDeMezclaEnExtraccionesDeMezcla(String c) {
        //Se compara con el codigo de la botella de mezcla
        Item ret = null;
        for (ExtraccionMezclaDeBolsa extraccion : extraccionesMezclaDeBolsas) {
            if (extraccion.getCodigoBotellaMezcla().equals(c)) {
                ret = extraccion;
                break;
            }
        }
        //Por dos motivos la botella puede no estar dentro de las extracciones, primero porque no haya extracciones donde buscar y segundo porque podria no haber ninguna extraccion
        // hacia la botella a buscar. En cualquier caso la botella a buscar seria la propia botella de mezcla seleccionada.
        if (ret == null)
            ret = botellaMezclaSeleccionada;
        return ret;
    }

    private void setearExtraccion(ExtraccionSueroDeBolsa extraccion) {
        txtDisponibleParaLlenarSuero.setText(cantidadDisponible + extraccion.getCantidad() + " mL");
        cantidadDisponible = cantidadDisponible + extraccion.getCantidad();
        botellaSueroSeleccionada.setCantidadDisponible(cantidadDisponible);
        txtCantidadSueroExtraido.setText(extraccion.getCantidad() + "");
        esconderTecado(SeparacionSueroActivity.this);
    }

    @Override
    public void onClick(final View v) {
        final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        v.startAnimation(scale);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.btn_rs_SeleccionarItem:
                        new ObtenerItemsIdentificadosServerCall(SeparacionSueroActivity.this);
                        break;
                    case R.id.btn_rs_SeleccionarBotellaSuero:
                        new ObtenerBotellasDeSueroServerCall(SeparacionSueroActivity.this);
                        break;
                    case R.id.btn_rs_SeleccionarBotellaMezcla:
                        new ObtenerBotellasDeMezclaServerCall(SeparacionSueroActivity.this);
                        break;
                    case R.id.btn_rs_TirarItem:
                        if (itemSeleccionado != null) {
                            hashMapCustomAlertFunction.put("id", itemSeleccionado.getCodigo());
                            hashMapCustomAlertFunction.put("funcion", "3");
                            String msg = (itemSeleccionado.isFinalizado()) ? "¿Quiere recuperar este item?" : "¿Quiere tirar este item?";
                            alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", msg}, hashMapCustomAlertFunction);
                        } else {
                            alert(SeparacionSueroActivity.this, new String[]{"Atención", "No hay tiene ningun itém seleccionado."}, null);
                        }
                        break;
                    case R.id.btn_rs_CerrarBotellaMezcla:
                        if (botellaMezclaSeleccionada != null) {
                            hashMapCustomAlertFunction.put("id", botellaMezclaSeleccionada.getCodigo());
                            hashMapCustomAlertFunction.put("funcion", "4");
                            String msg = (botellaMezclaSeleccionada.isFinalizado()) ? "¿Quiere abrir esta botella de mezcla?" : "¿Quiere cerrar esta botella de mezcla?";
                            alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", msg}, hashMapCustomAlertFunction);
                        } else {
                            alert(SeparacionSueroActivity.this, new String[]{"Atención", "No hay ninguna botella de mezcla seleccionada."}, null);
                        }
                        break;
                    case R.id.btn_rs_AgregarExtraccionSuero:
                        esconderTecado(SeparacionSueroActivity.this);
                        if (btnAgregarExtraccionSuero.getText().equals(getResources().getString(R.string.btnAgregarSuero)))
                            agregarNuevaExtraccionSuero();
                        else
                            modificarExtraccionSuero();
                        break;
                    case R.id.btn_rs_AgregarExtraccionMezcla:
                        esconderTecado(SeparacionSueroActivity.this);
                        agregarNuevaExtraccionMezcla();
                        break;
                    case R.id.btn_rs_FinalizarSeparacion:
                        esconderTecado(SeparacionSueroActivity.this);
                        agregarNuevaExtraccionMezcla();
                        break;
                    case R.id.btn_rs_CancelarSeparacion:
                        esconderTecado(SeparacionSueroActivity.this);
                        agregarNuevaExtraccionMezcla();
                        break;
                }
            }
        }, 300);
    }
}


