package com.example.toto.testapp;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignSucessActivity extends AppCompatActivity {

    TextView userName, userName2, time, time2, job, phone, email;
    Button signOut;
    ImageButton back;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_sucess);

        SharedPreferences.Editor ed = getSharedPreferences("numberSigns", MODE_PRIVATE).edit();
        ed.putInt("number", 0 );
        ed.apply();
        SharedPreferences.Editor edi = getSharedPreferences("Signed", MODE_PRIVATE).edit();
        SharedPreferences st = getSharedPreferences("Sign", MODE_PRIVATE);
        if (st.getString("type","no").equals("in")) {
            edi.putString("done", "in");
            edi.apply();

        }else if (st.getString("type","no").equals("out")) {
            edi.putString("done", "out");
            edi.apply();

        }
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (android.os.Build.VERSION.SDK_INT > 22) {

                    Intent intent = new Intent(SignSucessActivity.this, HomeActivity.class);
                    SignSucessActivity.this.startActivity(intent);
                } else {

                    Intent intent = new Intent(SignSucessActivity.this, HomeActivity2.class);
                    SignSucessActivity.this.startActivity(intent);
                }


            }
        });
        userName = findViewById(R.id.signin_sucess_name);
        userName2 = findViewById(R.id.signin_sucess_user);
        time = findViewById(R.id.signin_sucess_time);
        time2 = findViewById(R.id.signin_sucess_time2);
        job = findViewById(R.id.signin_sucess_job);
        phone = findViewById(R.id.signin_sucess_phone);
        email = findViewById(R.id.signin_sucess_email);
        signOut = findViewById(R.id.signin_sucess_button_signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT > 22) {
                    Intent intent = new Intent(SignSucessActivity.this, HomeActivity.class);
                    SignSucessActivity.this.startActivity(intent);

                } else {
                    Intent intent = new Intent(SignSucessActivity.this, HomeActivity2.class);
                    intent.putExtra("submit","true");
                    SignSucessActivity.this.startActivity(intent);


                }
            }
        });

        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("data"));

            userName.setText(jsonObject.getString("Name"));
            userName2.setText(jsonObject.getString("Name"));
            job.setText(jsonObject.getString("Job"));
            phone.setText(jsonObject.getString("Phone"));
            email.setText(jsonObject.getString("Email"));
            SharedPreferences prefs = getSharedPreferences("date", MODE_PRIVATE);
            time.setText(prefs.getString("time", "0"));
            time2.setText(prefs.getString("time", "0"));

        } catch (JSONException e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signed_support, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        return true;
    }



}
