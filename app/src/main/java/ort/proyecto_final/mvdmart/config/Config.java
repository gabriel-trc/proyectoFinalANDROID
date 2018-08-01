package ort.proyecto_final.mvdmart.config;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    private static void assignSharedPreferences(Activity activity, String key, String value){
        SharedPreferences sharedpreferences = activity.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }



    public static void setToken(Activity activity, String token){
        assignSharedPreferences(activity,"token",token);
    }

    public static String getToken (Activity activity){
        return activity.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE).getString("token","");
    }

    public static void setNumeroOperario (Activity activity, String pNumeroOperario){
        assignSharedPreferences(activity,"numeroOperario", pNumeroOperario);
    }

    public static String getNumeroOperario (Activity activity){
        return activity.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE).getString("numeroOperario","");
    }

    public static void setFrigorificos (Activity activity, String pFrigorificos){
        assignSharedPreferences(activity,"frigorificos", pFrigorificos);
    }

    public static String getFrigorificos (Activity activity){
        return activity.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE).getString("frigorificos","");
    }

}
