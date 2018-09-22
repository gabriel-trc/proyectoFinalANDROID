package ort.proyecto_final.mvdmart.server_calls;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import ort.proyecto_final.mvdmart.activities.SeleccionArea;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.config.Constants;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;

public class LogoutUsuarioServerCall {
    private SeleccionArea activity;
    private Context context;

    public LogoutUsuarioServerCall(final SeleccionArea activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        String url = Constants.DOMAIN + "/api/operario/logout/" + Config.getNumeroOperario(activity);
        activity.iniciarLoader();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        activity.finalizarLoader();
                        try {
                            if (response.getBoolean("suceso")) {
                                Config.setNumeroOperario(activity,"");
                                Config.setNombreOperario(activity, "");
                                activity.finish();
                            } else {
                                activity.alert(activity, HelpersFunctions.errores(response.getJSONArray("mensajes")), null);
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
                        errorMensaje[0] = "Ups!!";
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMensaje[1] = "No se pudo conectar con el servidor, time out error";
                        } else if (error.networkResponse != null) {
                            int codigoError = error.networkResponse.statusCode;
                            if (codigoError >= 500) {
                                errorMensaje[1] = "Error en el lado del servidor, código " + codigoError + "\nPor favor comuníquelo a su superior.";
                            } else if (codigoError >= 400) {
                                errorMensaje[1] = "Error en el lado del cliente, código " + codigoError + "\nPor favor comuníquelo a su superior.";
                            } else {
                                errorMensaje[1] = "Error, código " + codigoError + "\nPor favor comuníquelo  a su superior.";
                            }
                        } else {
                            errorMensaje[1] = "No hay conexión a internet, compruebe que el dispositivo esté conectado a una red.";
                        }
                        activity.alert(activity, errorMensaje, null);
                    }
                });
        jsonObjectRequest.setRetryPolicy(Constants.mRetryPolicy);
        VolleyRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
