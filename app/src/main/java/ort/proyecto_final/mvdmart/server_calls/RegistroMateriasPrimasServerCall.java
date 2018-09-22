package ort.proyecto_final.mvdmart.server_calls;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ort.proyecto_final.mvdmart.activities.RegistrosMateriaPrima;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.config.Constants;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;

public class RegistroMateriasPrimasServerCall  {

    private RegistrosMateriaPrima activity;
    private Context context;
    private HashMap<String, String> params;

    public RegistroMateriasPrimasServerCall(final RegistrosMateriaPrima activity, final JSONArray partidas) {
        this.activity = activity;
        this.context = activity.getApplicationContext();

        String url = Constants.DOMAIN + "/api/partida/registro";
        JSONObject sendObject = new JSONObject();
        try {
            sendObject.put("partidas", partidas);
            sendObject.put("codigoOperario", Config.getNumeroOperario(activity));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        activity.iniciarLoader();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, sendObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            activity.finalizarLoader();
                            if (response.getBoolean("suceso")) {
                                activity.alertCheck(activity,null, true);
                            } else {
                                Object partidaId = response.getInt("retorno");
                                activity.alert(activity,HelpersFunctions.errores(response.getJSONArray("mensajes")),partidaId);
                            }
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
//TODO mostrar alert generico si entra al catch
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
                        activity.alert(activity,errorMensaje,null);
                    }
                });
        jsonObjectRequest.setRetryPolicy(Constants.mRetryPolicy);
        VolleyRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}