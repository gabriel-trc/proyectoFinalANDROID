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
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
import ort.proyecto_final.mvdmart.models.Item;
import ort.proyecto_final.mvdmart.models.Partida;
import ort.proyecto_final.mvdmart.server_calls.CambiarItemIdentificadoServerCall;
import ort.proyecto_final.mvdmart.server_calls.NuevaBotellaSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerBotellasDeSueroServerCall;
import ort.proyecto_final.mvdmart.server_calls.ObtenerItemsIdentificadosServerCall;
import ort.proyecto_final.mvdmart.server_calls.SeleccionarItemServerCall;


public class SeparacionItemsActivity extends AppCompatActivity {
    private BotellaSuero botellaSueroSeleccionada;
    private Button btnSeleccionarItem, btnSeleccionarBotellaSuero;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<Item>> expandableListDetail;
    private ConstraintLayout spinnerLoader;
    private Item itemSeleccionado, nuevoItemSeleccionado;
    private TextView alertTitle, txtItemPreSeleccionado, txtBotellaSuero;
    private HashMap<String, Integer> objetosEnVista = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_separacion_items);
        spinnerLoader = findViewById(R.id.spinner_loader);
        iniciarVistas();
    }

    private void iniciarVistas() {
        txtItemPreSeleccionado = findViewById(R.id.txtItemPreSeleccionado);
        txtBotellaSuero = findViewById(R.id.txtBotellaSuero);
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
            //        for(int i=0; i < botellas.length(); i++){
//            botellasSuero.add(new BotellaSuero());

//        }
// mBuilder.setTitle("Seleccione una partida");
            // final ListAdapter adaptador = new ArrayAdapter<StringWithTag>(this, android.R.layout.select_dialog_singlechoice, partidas);
//            mBuilder.setSingleChoiceItems(adaptador, -1, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    StringWithTag partidaSeleccionada = (StringWithTag) ((AlertDialog) dialog).getListView().getItemAtPosition(which);
//                    idPartida = partidaSeleccionada.tag;
//                }
//            });
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


    //region Manejo loader

    public void iniciarLoader() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinnerLoader.setVisibility(View.VISIBLE);
    }

    public void finalizarLoader() {
        spinnerLoader.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void iniciarNuevaBotellaSuero(String retorno) {
        botellaSueroSeleccionada = new BotellaSuero(retorno, 500);
        txtBotellaSuero.setText(botellaSueroSeleccionada.getCodigo().toString());
    }


    //endregion
}
