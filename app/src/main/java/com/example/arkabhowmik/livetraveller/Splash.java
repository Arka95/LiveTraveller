package com.example.arkabhowmik.livetraveller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.arkabhowmik.livetraveller.app.AppConfig;

public class Splash extends Activity {
    EditText etIP;
    SharedPreferences sharedPreferences;
    public static final String IP = "LastIP";
    public static final String MyPREFERENCES = "LivetravellerPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastIP = sharedPreferences.getString(IP, AppConfig.BASE_URL);

        Button ok=(Button)findViewById(R.id.btIP);
        etIP=(EditText)findViewById(R.id.etIP);

        etIP.setText(lastIP);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saves the dynamic IP in shared Prefs
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(IP,etIP.getText().toString());
                editor.commit();
                Intent cami = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(cami);
                finish();
            }
        });

       /* Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);//2000ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    Intent Login = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(Login);


                }
            }
        };
        timer.start();*/

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
