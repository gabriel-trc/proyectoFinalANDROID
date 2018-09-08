package ort.proyecto_final.mvdmart.server_calls;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import ort.proyecto_final.mvdmart.activities.MainActivity;
import ort.proyecto_final.mvdmart.activities.SelectAreaActivity;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.config.Constants;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;

public class LoginUsuarioServerCall {
    private MainActivity activity;
    private Context context;

    public LoginUsuarioServerCall(final MainActivity activity, final String numeroOperario) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        String url = Constants.DOMAIN + "/api/operario/login/" + numeroOperario;
        activity.iniciarLoader();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        activity.finalizarLoader();
                        try {
                            if (response.getBoolean("suceso")) {
                                Config.setNumeroOperario(activity, numeroOperario + "");
                                Intent goToNextActivity = new Intent(context, SelectAreaActivity.class);
                                goToNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//TODO leer que hace este parche
                                context.startActivity(goToNextActivity);
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
                        errorMensaje[0] = "Error en el servidor";
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
                            }
                        } else {
                            errorMensaje[1] = "Error en servidor\n";
                        }
                        activity.alert(activity, errorMensaje, null);
                    }
                });
        jsonObjectRequest.setRetryPolicy(Constants.mRetryPolicy);
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}