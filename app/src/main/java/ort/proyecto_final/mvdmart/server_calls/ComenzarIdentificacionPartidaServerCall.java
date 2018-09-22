package ort.proyecto_final.mvdmart.server_calls;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import ort.proyecto_final.mvdmart.activities.Identificacion;
import ort.proyecto_final.mvdmart.activities.SeleccionArea;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.config.Constants;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;

public class ComenzarIdentificacionPartidaServerCall {
    private SeleccionArea activity;
    private Context context;

    public ComenzarIdentificacionPartidaServerCall(final SeleccionArea activity, final int idPartida) {
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
                                Intent goToNextActivity = new Intent(context, Identificacion.class);
                                goToNextActivity.putExtra("idPartida", idPartida);
                                goToNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(goToNextActivity);
                            } else {
                                JSONArray errorArray = response.getJSONArray("mensajes");
                                activity.alert(activity, HelpersFunctions.errores(errorArray), null);
                            }
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.finalizarLoader();
                        String errorMensaje[] = new String[2];
                        errorMensaje[0] = "Error de conexión";
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMensaje[1] = "No se pudo conectar con el servidor";
                        } else if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 500:
                                    errorMensaje[1] = "Código 500 – Internal Server Error\n";
                                    break;
                                case 502:
                                    errorMensaje[1] = "Código 502 – Bad Gateway\n";
                                    break;
                                default:
                                    errorMensaje[1] = "Error en el servidor";
                                    break;
                            }
                        } else {
                            errorMensaje[1] = "Verifique su conexion a internet.\n";
                        }
                        activity.alert(activity, errorMensaje, null);
                    }
                });
        jsonObjectRequest.setRetryPolicy(Constants.mRetryPolicy);
        VolleyRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
