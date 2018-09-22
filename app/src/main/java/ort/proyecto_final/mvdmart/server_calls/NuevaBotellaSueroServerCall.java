package ort.proyecto_final.mvdmart.server_calls;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import ort.proyecto_final.mvdmart.activities.Separacion;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.config.Constants;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.models.BotellaSuero;

public class NuevaBotellaSueroServerCall {
    private Separacion activity;
    private Context context;

    public NuevaBotellaSueroServerCall(final Separacion activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        String url = Constants.DOMAIN + "/api/botelladesuero/nueva/" + Config.getNumeroOperario(activity);
        activity.iniciarLoader();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        activity.finalizarLoader();
                        try {
                            if (response.getBoolean("suceso")) {
                                BotellaSuero nueva = new BotellaSuero(response.getString("retorno"), 0);
                                activity.setNuevaBotellaDeSueroSeleccionada(nueva);
                                if (activity.getBotellaSueroSeleccionada() != null && activity.getHashMapReferenciasEnVista().containsKey(activity.getBotellaSueroSeleccionada().getCodigo()) && activity.getHashMapReferenciasEnVista().get(activity.getBotellaSueroSeleccionada().getCodigo()) == 1)
                                    new CambiarBotellaSueroSeleccionadaServerCall(activity, activity.getBotellaSueroSeleccionada().getCodigo(), nueva.getCodigo());
                                else {
                                    activity.botellaDeSueroSeleccionada();
                                }
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
                })
//        {
//            /**
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                //headers.put("Content-Type", "application/json");
//                headers.put("idOperario", Config.getNumeroOperario(activity));
//                return headers;
//            }
//        }
                ;
        jsonObjectRequest.setRetryPolicy(Constants.mRetryPolicy);
        VolleyRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
