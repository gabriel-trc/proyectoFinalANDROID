package ort.proyecto_final.mvdmart.server_calls;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ort.proyecto_final.mvdmart.activities.LoadingActivity;
import ort.proyecto_final.mvdmart.activities.RegistroMateriasPrimasActivity;
import ort.proyecto_final.mvdmart.config.Config;

public class GetAllFrigorificosServerCall {

    private Activity activity;
    private Context context;


    public GetAllFrigorificosServerCall(final Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        String url = "http://192.168.1.44:45455/api/frigorifico/todos";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            Log.d("My App", obj.toString());
                           Config.setFrigorificos(activity,obj.get("retorno").toString());
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}