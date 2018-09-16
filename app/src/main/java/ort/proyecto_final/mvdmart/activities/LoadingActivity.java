package ort.proyecto_final.mvdmart.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ort.proyecto_final.mvdmart.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

       // new GetAllFrigorificosCall(this).execute();



    }

//
//
//
//
//
//
//
//
//    JSONObject jObj = null;
//
//
//
//    public class GetAllFrigorificosCall extends AsyncTask<Void,Void,String> {
//        private Activity activity = null;
//
//        public GetAllFrigorificosCall(Activity pActivity) {
//            activity = pActivity;
//        }
//
//
//        protected String doInBackground(Void... urls){
//            ObtenerLosFrigorificosServerCall serverCall = new ObtenerLosFrigorificosServerCall(activity);
//
//        }
//
//        protected void onPostExecute(String result) {
//            try {
//                jObj = new JSONObject(result);
//            } catch (JSONException e1) {
//                e1.printStackTrace();
//            }
//
//            try {
//                if(jObj.get("status").toString().equals("success"))
//                {
//                    Toast.makeText(getBaseContext(),"Registration Succesful",Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(Class1.this,Success.class);
//                    Class1.this.startActivity(intent);
//
//                }
//                else
//                {
//                    Toast.makeText(getBaseContext(),jObj.get("error").toString(),Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(Class1.this,Error.class);
//                    Class1.this.startActivity(intent);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }


}
