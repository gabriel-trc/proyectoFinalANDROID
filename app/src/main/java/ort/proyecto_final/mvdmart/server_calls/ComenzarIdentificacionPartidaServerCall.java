package ort.proyecto_final.mvdmart.server_calls;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import ort.proyecto_final.mvdmart.activities.IdentificacionBolsasActivity;
import ort.proyecto_final.mvdmart.activities.SelectAreaActivity;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.config.Constants;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;

public class ComenzarIdentificacionPartidaServerCall {
    private SelectAreaActivity activity;
    private Context context;

    public ComenzarIdentificacionPartidaServerCall(final SelectAreaActivity activity, final int idPartida) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        String url = Constants.DOMAIN + "/api/partida/comenzaridentificacion/" + idPartida + "/" + Config.getNumeroOperario(activity);
        activity.iniciarLoader();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        activity.finalizarLoader();
                        try {
                            if (response.getBoolean("suceso")) {
                                Intent goToNextActivity = new Intent(context, IdentificacionBolsasActivity.class);
                                goToNextActivity.putExtra("idPartida", idPartida);
                                goToNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//TODO leer que hace este parche
                                context.startActivity(goToNextActivity);
                            } else {
                                JSONArray errorArray = response.getJSONArray("mensajes");
                                activity.alert(context, HelpersFunctions.errores(errorArray), null);
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
