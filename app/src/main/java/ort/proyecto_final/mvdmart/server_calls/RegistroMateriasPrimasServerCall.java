package ort.proyecto_final.mvdmart.server_calls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ort.proyecto_final.mvdmart.activities.RegistroMateriasPrimasActivity;
import ort.proyecto_final.mvdmart.config.Constants;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;

public class RegistroMateriasPrimasServerCall {

    private RegistroMateriasPrimasActivity activity;
    private Context context;
    private HashMap<String, String> params;

    public RegistroMateriasPrimasServerCall(final RegistroMateriasPrimasActivity activity, final JSONArray partidas) {
        this.activity = activity;
        this.context = activity.getApplicationContext();

        String url = Constants.DOMAIN + "/api/partida/registro";
        JSONObject sendObject = new JSONObject();
        try {
            sendObject.put("partidas", partidas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, sendObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            activity.finalizarLoader();
                            if (response.getBoolean("suceso")) {
                                Toast.makeText(context, "Registros guardados", Toast.LENGTH_LONG).show();
                                activity.limpiarTabla();
                            } else {
                                String[] errores = HelpersFunctions.errores(response.getJSONArray("mensajes"));
                                final int partidaId = response.getInt("retorno");
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setTitle(errores[0]);
                                builder.setMessage(errores[1]);
                                //builder.setIcon(R.drawable.ic_launcher_foreground);
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        activity.onResponseErrorPartida(partidaId);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.finalizarLoader();
                        if (error.getClass().equals(TimeoutError.class)) {
                            Toast.makeText(context, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show();
                        } else if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 502:
                                    Toast.makeText(context, "Error servidor 502", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        } else {
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        jsonObjectRequest.setRetryPolicy(Constants.mRetryPolicy);
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}