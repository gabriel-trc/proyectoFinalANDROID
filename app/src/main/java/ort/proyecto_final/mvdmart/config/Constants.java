package ort.proyecto_final.mvdmart.config;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

public class Constants {
    public static final String DOMAIN = "http://192.168.1.44:45455/";//"https://appweb.conveyor.cloud/";

    //Para evitar bug volley, hace llamadas dos veces.
    public static RetryPolicy mRetryPolicy = new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    public static String MyPREFERENCES="MyPreferences";

    public static final String AppName = "MVDMART S.A.";

}
