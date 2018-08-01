package ort.proyecto_final.mvdmart.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ort.proyecto_final.mvdmart.R;
import ort.proyecto_final.mvdmart.server_calls.GetAllFrigorificosServerCall;

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
//            GetAllFrigorificosServerCall serverCall = new GetAllFrigorificosServerCall(activity);
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
