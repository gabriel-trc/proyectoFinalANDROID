package ort.proyecto_final.mvdmart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
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
import ort.proyecto_final.mvdmart.server_calls.RegistroMateriasPrimasServerCall;
import ort.proyecto_final.mvdmart.server_calls.SeleccionarBotellaDeMezclaServerCall;
import ort.proyecto_final.mvdmart.server_calls.SeleccionarBotellaDeSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.SeleccionarItemServerCall;

public class SeparacionSueroActivity extends ActivityMadre {

    private BotellaSuero botellaSueroSeleccionada, nuevaBotellaDeSueroSeleccionada;
    private Button btnSeleccionarItem, btnFinalizarItem, btnFinalizarBotellaMezcla, btnFinalizarSeparacion, btnSeleccionarBotellaSuero, btnSeleccionarBotellaMezcla, btnAgregarExtraccionSuero, btnAgregarExtraccionMezcla, btnCancelarSeparacion;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<Item>> expandableListDetail;
    private Item itemSeleccionado, nuevoItemSeleccionado, botellaMezclaSeleccionada, nuevaBotellaDeMezclaSeleccionada;
    private TextView alertTitle, txtItemSeleccionado, txtBotellaSueroSeleccionada, txtDisponibleParaLlenarSuero, txtBotellaMezclaSeleccionada, txtLabelDisponibleSuero;
    private HashMap<String, Integer> objetosEnVista = new HashMap<>();
    private TableLayout tablaExtraccionesSuero, tablaExtraccionesMezcla;
    ArrayList<ExtraccionSueroDeBolsa> extraccionesSueroDeBolsas = new ArrayList<>();
    ArrayList<ExtraccionMezclaDeBolsa> extraccionesMezclaDeBolsas = new ArrayList<>();
    private ExtraccionSueroDeBolsa extraccionSueroDeBolsaModificar;
    private EditText txtCantidadSueroExtraido;
    private Integer cantidadDisponible;

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
        txtLabelDisponibleSuero = findViewById(R.id.txtLabelDisponibleSuero);
        txtCantidadSueroExtraido = findViewById(R.id.txtCantidadSueroExtraido);
        txtCantidadSueroExtraido.setTransformationMethod(null);
        tablaExtraccionesSuero = findViewById(R.id.tablaExtraccionesSuero);
        tablaExtraccionesMezcla = findViewById(R.id.tablaExtraccionesMezcla);
        txtItemSeleccionado = findViewById(R.id.txtItemSeleccionado);
        txtBotellaMezclaSeleccionada = findViewById(R.id.txtBotellaMezclaSeleccionada);
        txtBotellaSueroSeleccionada = findViewById(R.id.txtBotellaSueroSeleccionada);
        txtDisponibleParaLlenarSuero = findViewById(R.id.txtDisponibleParaLlenarSuero);
        btnFinalizarItem = findViewById(R.id.btnFinalizarItem);
        btnFinalizarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO refactor. De cerrarce el item sin hacerle ninguna extraccion, ese caso puede pasar?
                if (itemSeleccionado != null) {
                    if (((AppCompatButton) v).getText().equals(getResources().getString(R.string.btnCerrar))) {
                        finalizarItem(itemSeleccionado.getLocalId(), itemSeleccionado.getCodigo() + "CERRAR", true);
                        btnFinalizarItem.setText(getResources().getString(R.string.btnCerrada));
                        btnFinalizarItem.setBackgroundResource(android.R.color.holo_purple);
                    } else {
                        finalizarItem(itemSeleccionado.getLocalId(), itemSeleccionado.getCodigo() + "CERRAR", false);
                        btnFinalizarItem.setText(getResources().getString(R.string.btnCerrar));
                        btnFinalizarItem.setBackgroundResource(android.R.color.holo_green_light);
                    }
                } else {
                    alert(SeparacionSueroActivity.this, new String[]{"Atención", "No hay tiene ningun itém seleccionado."}, null);
                }
            }
        });
        btnFinalizarBotellaMezcla = findViewById(R.id.btnFinalizarBotellaMezcla);
        btnFinalizarBotellaMezcla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botellaMezclaSeleccionada != null) {
                    HashMap<String, String> obj = new HashMap<>();
                    obj.put("id", (((TableRow) v.getParent()).getId() + ""));
                    obj.put("tagBtn", botellaMezclaSeleccionada.getCodigo() + "CERRAR");
                    if (getExtraccionSueroDeBolsaById(Integer.parseInt(obj.get("id"))).isFinalizado()) {
                        obj.put("funcion", "3");
                        alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere abrir este item?"}, obj);
                    } else {
                        obj.put("funcion", "4");
                        alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere cerrar este item?"}, obj);
                    }
//                    if (((AppCompatButton) v).getText().equals(getResources().getString(R.string.btnCerrar))) {
//                        finalizarItem(itemSeleccionado.getLocalId(), itemSeleccionado.getCodigo() + "CERRAR", true);
//                        btnFinalizarBotellaMezcla.setText(getResources().getString(R.string.btnCerrada));
//                        btnFinalizarBotellaMezcla.setBackgroundResource(android.R.color.holo_purple);
//                    } else {
//                        finalizarItem(itemSeleccionado.getLocalId(), itemSeleccionado.getCodigo() + "CERRAR", false);
//                        btnFinalizarBotellaMezcla.setText(getResources().getString(R.string.btnCerrar));
//                        btnFinalizarBotellaMezcla.setBackgroundResource(android.R.color.holo_green_light);
//                    }
                } else {
                    alert(SeparacionSueroActivity.this, new String[]{"Atención", "No hay tiene ninguna botella seleccionada."}, null);
                }
            }
        });
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
                new ObtenerBotellasDeSueroServerCall(SeparacionSueroActivity.this);
            }
        });
        btnSeleccionarBotellaMezcla = findViewById(R.id.btnSeleccionarBotellaMezcla);
        btnSeleccionarBotellaMezcla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ObtenerBotellasDeMezclaServerCall(SeparacionSueroActivity.this);
            }
        });
        btnFinalizarSeparacion = findViewById(R.id.btnFinalizarSeparacion);
        btnFinalizarSeparacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarSeparacion(false);
            }
        });
        btnAgregarExtraccionSuero = findViewById(R.id.btnAgregarExtraccionSuero);
        btnAgregarExtraccionSuero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AppCompatButton) v).getText().equals(getResources().getString(R.string.btnAgregarSuero)))
                    agregarNuevaExtraccionSuero();
                else
                    modificarExtraccionSuero();
                esconderTecado(SeparacionSueroActivity.this);
            }
        });
        btnAgregarExtraccionMezcla = findViewById(R.id.btnAgregarExtraccionMezcla);
        btnAgregarExtraccionMezcla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AppCompatButton) v).getText().equals(getResources().getString(R.string.btnAgregarMezcla)))
                    agregarNuevaExtraccionMezcla();
                esconderTecado(SeparacionSueroActivity.this);
            }
        });

        btnCancelarSeparacion = findViewById(R.id.btnCancelarSeparacion);
        btnCancelarSeparacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> obj = new HashMap<>();
                obj.put("funcion", "8");
                alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Esta seguro que quiere cancelar todos los resgistros cargados?"}, obj);
            }
        });
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
                    if (itemSeleccionado != null && objetosEnVista.containsKey(itemSeleccionado.getCodigo()) && objetosEnVista.get(itemSeleccionado.getCodigo()) == 1)
                        new CambiarItemIdentificadoServerCall(SeparacionSueroActivity.this, itemSeleccionado, nuevoItemSeleccionado);
                    else
                        new SeleccionarItemServerCall(SeparacionSueroActivity.this, nuevoItemSeleccionado);
                    dialog.dismiss();
                } else
                    Toast.makeText(getApplicationContext(), "No a preseleccionado ningun item.", Toast.LENGTH_SHORT).show();
            }
        });
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
        if (botellaSueroSeleccionada.getCantidadDisponible() != 0) {
            if (chequearCamposCompletosYTiposDatos()) {
                int cantidadSueroExtraida = Integer.parseInt(txtCantidadSueroExtraido.getText().toString());
                if (cantidadSueroExtraida <= botellaSueroSeleccionada.getCantidadDisponible()) {
                    agregarReferenciaDeObjetoEnVista(itemSeleccionado.getCodigo());
                    agregarReferenciaDeObjetoEnVista(botellaSueroSeleccionada.getCodigo());
                    extraccionesSueroDeBolsas.add(new ExtraccionSueroDeBolsa(itemSeleccionado.getCodigo(), itemSeleccionado.getTipo(), botellaSueroSeleccionada.getCodigo(), Integer.parseInt(txtCantidadSueroExtraido.getText().toString())));
                    crearTablaExtraccionesSuero();
                    txtDisponibleParaLlenarSuero.setText((botellaSueroSeleccionada.getCantidadDisponible() - cantidadSueroExtraida) + " mL");
                    cantidadDisponible = botellaSueroSeleccionada.getCantidadDisponible() - cantidadSueroExtraida;
                    botellaSueroSeleccionada.setCantidadDisponible(botellaSueroSeleccionada.getCantidadDisponible() - cantidadSueroExtraida);
                    limpiarCampos();
                } else {
                    //TODO podria pasar?
                    alert(SeparacionSueroActivity.this, new String[]{"Atención", "La cantidad para extraer es mayor a la capacidad de la botella."}, null);
                }
            }
        } else {
            alert(SeparacionSueroActivity.this, new String[]{"Atención", "No queda mas capacidad disponible en la botella de suero; debe de cambiarla."}, null);
        }
        esconderTecado(SeparacionSueroActivity.this);
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
        tablaExtraccionesSuero.removeAllViews();
        for (int i = -1; i < extraccionesSueroDeBolsas.size(); i++) {
            TableRow fila = new TableRow(this);
            fila.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 10.5f));
            if (i < 0)
                fila.setBackgroundColor(Color.rgb(36, 123, 160));
            else {
                extraccionesSueroDeBolsas.get(i).setLocalId(i);
                fila.setId(i);
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
                        HashMap<String, String> obj = new HashMap<>();
                        obj.put("funcion", "1");
                        obj.put("id", (((TableRow) v.getParent()).getId()) + "");
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
                        HashMap<String, String> obj = new HashMap<>();
                        obj.put("funcion", "2");
                        obj.put("id", (((TableRow) v.getParent()).getId()) + "");
                        alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere borrar este registro?"}, obj);
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
                fila.addView(columnaCerrar, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            } else {
                Button columnaCerrar = new Button(this);
                columnaCerrar.setText("CERRAR");
                columnaCerrar.setTag(extraccionesSueroDeBolsas.get(i).getCodigo() + "CERRAR");
                columnaCerrar.setBackgroundResource(android.R.color.holo_green_light);
                columnaCerrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCerrar.setGravity(Gravity.CENTER);
                columnaCerrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                columnaCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> obj = new HashMap<>();
                        obj.put("id", (((TableRow) v.getParent()).getId() + ""));
                        obj.put("tagBtn", (v.getTag().toString()));
                        if (getExtraccionSueroDeBolsaById(Integer.parseInt(obj.get("id"))).isFinalizado()) {
                            obj.put("funcion", "3");
                            alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere abrir este item?"}, obj);
                        } else {
                            obj.put("funcion", "4");
                            alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere cerrar este item?"}, obj);
                        }
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
            if (!extraccionesMezclaDeBolsas.contains(new ExtraccionMezclaDeBolsa(itemSeleccionado.getCodigo(), itemSeleccionado.getTipo(), botellaMezclaSeleccionada.getCodigo()))) {
                agregarReferenciaDeObjetoEnVista(itemSeleccionado.getCodigo());
                agregarReferenciaDeObjetoEnVista(botellaMezclaSeleccionada.getCodigo());
                extraccionesMezclaDeBolsas.add(new ExtraccionMezclaDeBolsa(itemSeleccionado.getCodigo(), itemSeleccionado.getTipo(), botellaMezclaSeleccionada.getCodigo()));
                crearTablaExtraccionesMezcla();
            } else {
                alert(SeparacionSueroActivity.this, new String[]{"Atención", "Ya ingreso un sobrante de mezcla de el item seleccionado a la botella de mezcla."}, null);
            }
        } else {
            alert(SeparacionSueroActivity.this, new String[]{"Atención", "No hay seleccionado un item de donde extraer la mezcla o un recipiente donde depositarla."}, null);
        }
    }

    private void crearTablaExtraccionesMezcla() {
        tablaExtraccionesMezcla.removeAllViews();
        for (int i = -1; i < extraccionesMezclaDeBolsas.size(); i++) {
            TableRow fila = new TableRow(this);
            fila.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 8.5f));
            if (i < 0)
                fila.setBackgroundColor(Color.rgb(36, 123, 160));
            else {
                extraccionesMezclaDeBolsas.get(i).setLocalId(i);
                fila.setId(i);
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
//                        HashMap<String, Integer> obj = new HashMap<>();
//                        obj.put("funcion", 1);
//                        obj.put("id", (((TableRow) v.getParent()).getId()));
//                        alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere editar este registro?"}, obj);
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
                        HashMap<String, String> obj = new HashMap<>();
                        obj.put("funcion", "5");
                        obj.put("id", (((TableRow) v.getParent()).getId()) + "");
                        alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere borrar este registro?"}, obj);
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
                columnaCerrar.setText("CERRAR");
                columnaCerrar.setTag(extraccionesMezclaDeBolsas.get(i).getCodigoBotellaMezcla() + "CERRAR");
                columnaCerrar.setBackgroundResource(android.R.color.holo_blue_light);
                columnaCerrar.setTextColor(getResources().getColor(R.color.colorBlanco));
                columnaCerrar.setGravity(Gravity.CENTER);
                columnaCerrar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                columnaCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> obj = new HashMap<>();
                        obj.put("id", (((TableRow) v.getParent()).getId() + ""));
                        obj.put("tagBtn", (v.getTag().toString()));
                        if (getItemById(Integer.parseInt(obj.get("id"))).isFinalizado()) {
                            obj.put("funcion", "6");
                            alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere abrir este item?"}, obj);
                        } else {
                            obj.put("funcion", "7");
                            alertDosBotones(SeparacionSueroActivity.this, new String[]{"Atención", "¿Quiere cerrar este item?"}, obj);
                        }
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

    @Override
    public void limpiarCampos() {
        txtCantidadSueroExtraido.setText("");
    }

    @Override
    public void customOnErrorResponseVolley(Object object) {

    }

    @Override
    public void customAlertFunction(Object object) {
        HashMap<String, String> hashMap = (HashMap<String, String>) object;
        if (hashMap.get("funcion") == "1") {//modifica extracción de suero
            extraccionSueroDeBolsaModificar = getExtraccionSueroDeBolsaById(Integer.parseInt(hashMap.get("id")));
            btnAgregarExtraccionSuero.setText(R.string.btnModificar);
            btnAgregarExtraccionSuero.setBackgroundResource(android.R.color.holo_orange_dark);
            setearExtraccion(extraccionSueroDeBolsaModificar);
        } else if (hashMap.get("funcion") == "2") {//borra la extracción de suero
            ExtraccionSueroDeBolsa ext = getExtraccionSueroDeBolsaById(Integer.parseInt(hashMap.get("id")));
            extraccionesSueroDeBolsas.remove(ext);
            quitarReferenciaDeObjetoEnVista(ext.getCodigoBotellaDeSuero());
            quitarReferenciaDeObjetoEnVista(ext.getCodigo());
            crearTablaExtraccionesSuero();
        } else if (hashMap.get("funcion") == "3") {//abre el item de la extracción de suero
            finalizarItem(Integer.parseInt(hashMap.get("id")), hashMap.get("tagBtn"), false);
        } else if (hashMap.get("funcion") == "4") {//finaliza el item de la extracción de suero
            finalizarItem(Integer.parseInt(hashMap.get("id")), hashMap.get("tagBtn"), true);
        } else if (hashMap.get("funcion") == "5") {//borra extraccion de mezcla
            ExtraccionMezclaDeBolsa ext = (ExtraccionMezclaDeBolsa) getItemById(Integer.parseInt(hashMap.get("id")));
            extraccionesMezclaDeBolsas.remove(ext);
            quitarReferenciaDeObjetoEnVista(ext.getCodigoBotellaMezcla());
            quitarReferenciaDeObjetoEnVista(ext.getCodigo());
            crearTablaExtraccionesMezcla();
        } else if (hashMap.get("funcion") == "6") {//abre la botella que recolecta mezcla
            finalizarBotellaJuntarMezcla(Integer.parseInt(hashMap.get("id")), hashMap.get("tagBtn"), false);
        } else if (hashMap.get("funcion") == "7") {//finaliza la botella que recolecta mezcla
            finalizarBotellaJuntarMezcla(Integer.parseInt(hashMap.get("id")), hashMap.get("tagBtn"), true);
        } else if (hashMap.get("funcion") == "8") {//finaliza la botella que recolecta mezcla
            finalizarSeparacion(true);
        }
    }

    private void finalizarItem(Integer itemId, String idBtn, boolean finalizado) {
        Item item = getExtraccionSueroDeBolsaById(itemId);
        for (Item i : extraccionesSueroDeBolsas) {
            if (i.getCodigo().equals(item.getCodigo()))
                i.setFinalizado(finalizado);
        }
        for (int i = 2; i < tablaExtraccionesSuero.getChildCount(); i++) {
            if (i % 2 == 0) {
                View view = tablaExtraccionesSuero.getChildAt(i);
                if (view instanceof TableRow) {
                    TableRow row = (TableRow) view;
                    if (row.getChildAt(5).getTag().equals(item.getCodigo() + "CERRAR")) {
                        Button btnCerrar = (Button) row.getChildAt(5);
                        btnCerrar.setText((finalizado) ? R.string.btnCerrada : R.string.btnCerrar);
                        btnCerrar.setBackgroundResource((finalizado) ? android.R.color.holo_purple : android.R.color.holo_blue_light);
                        if (itemSeleccionado.equals(item)) {
                            btnFinalizarItem.setText((finalizado) ? R.string.btnCerrada : R.string.btnCerrar);
                            btnFinalizarItem.setBackgroundResource((finalizado) ? android.R.color.holo_purple : android.R.color.holo_blue_light);
                        }
                    }
                }
            }
        }
    }

    private void finalizarBotellaJuntarMezcla(Integer itemId, String idBtn, boolean finalizado) {
        ExtraccionMezclaDeBolsa ext = getItemById(itemId);
        ext.setFinalizado(finalizado);
        for (int i = 2; i < tablaExtraccionesMezcla.getChildCount(); i++) {
            if (i % 2 == 0) {
                View view = tablaExtraccionesMezcla.getChildAt(i);
                if (view instanceof TableRow) {
                    TableRow row = (TableRow) view;
                    if (row.getChildAt(4).getTag().equals(ext.getCodigoBotellaMezcla() + "CERRAR")) {
                        Button btnCerrar = (Button) row.getChildAt(5);
                        btnCerrar.setText((finalizado) ? R.string.btnCerrada : R.string.btnCerrar);
                        btnCerrar.setBackgroundResource((finalizado) ? android.R.color.holo_purple : android.R.color.holo_blue_light);
                        if (botellaMezclaSeleccionada.getCodigo().equals(ext.getCodigoBotellaMezcla())) {
                            btnFinalizarBotellaMezcla.setText((finalizado) ? R.string.btnCerrada : R.string.btnCerrar);
                            btnFinalizarBotellaMezcla.setBackgroundResource((finalizado) ? android.R.color.holo_purple : android.R.color.holo_blue_light);
                        }
                    }
                }
            }
        }
    }

    private ExtraccionSueroDeBolsa getExtraccionSueroDeBolsaById(int idEx) {
        ExtraccionSueroDeBolsa ret = null;
        for (ExtraccionSueroDeBolsa extraccion : extraccionesSueroDeBolsas) {
            if (extraccion.getLocalId() == idEx) {
                ret = extraccion;
            }
        }
        return ret;
    }

    private ExtraccionMezclaDeBolsa getItemById(int id) {
        ExtraccionMezclaDeBolsa ret = null;
        for (ExtraccionMezclaDeBolsa extraccion : extraccionesMezclaDeBolsas) {
            if (extraccion.getLocalId() == id) {
                ret = extraccion;
            }
        }
        return ret;
    }

    private void setearExtraccion(ExtraccionSueroDeBolsa extraccion) {
        txtDisponibleParaLlenarSuero.setText(cantidadDisponible + extraccion.getCantidad() + " mL");
        cantidadDisponible = cantidadDisponible + extraccion.getCantidad();
        botellaSueroSeleccionada.setCantidadDisponible(cantidadDisponible);
        txtCantidadSueroExtraido.setText(extraccion.getCantidad() + "");
        esconderTecado(SeparacionSueroActivity.this);
    }


}
