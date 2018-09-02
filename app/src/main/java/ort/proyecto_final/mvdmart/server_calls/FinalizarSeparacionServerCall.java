package ort.proyecto_final.mvdmart.server_calls;

import android.content.Context;
import android.util.Log;
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
import ort.proyecto_final.mvdmart.activities.SeparacionSueroActivity;
import ort.proyecto_final.mvdmart.config.Config;
import ort.proyecto_final.mvdmart.config.Constants;
import ort.proyecto_final.mvdmart.helpers.HelpersFunctions;
import ort.proyecto_final.mvdmart.models.Item;

public class FinalizarSeparacionServerCall {

    private SeparacionSueroActivity activity;
    private Context context;
    private HashMap<String, String> params;

    public FinalizarSeparacionServerCall(final SeparacionSueroActivity activity, final JSONArray extraccionesSuero, final JSONArray extraccionesMezcla, Item itemSeleccionado, String codigoBotellaDeSueroSeleccionada, String codigoBotellaDeMezclaSeleccionada) {
        this.activity = activity;
        this.context = activity.getApplicationContext();

        String url = Constants.DOMAIN + "/api/separacion/finalizar";
        JSONObject sendObject = new JSONObject();
        try {
            sendObject.put("extraccionesDeSuero", extraccionesSuero);
            sendObject.put("extraccionesDeMezcla", extraccionesMezcla);
            sendObject.put("codigoOperario", Config.getNumeroOperario(activity));
            sendObject.put("itemSeleccionado", itemSeleccionado.toJSONObject());
            sendObject.put("codigoBotellaDeMezclaSeleccionada", codigoBotellaDeMezclaSeleccionada);
            sendObject.put("codigoBotellaDeSueroSeleccionada", codigoBotellaDeSueroSeleccionada);
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
                                Toast.makeText(context, "Registros guardados", Toast.LENGTH_LONG).show();
                                activity.recreate();
                            } else {
                                activity.alert(activity, HelpersFunctions.errores(response.getJSONArray("mensajes")), null);
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
                        activity.alert(activity, errorMensaje, null);
                    }
                });
        jsonObjectRequest.setRetryPolicy(Constants.mRetryPolicy);
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}