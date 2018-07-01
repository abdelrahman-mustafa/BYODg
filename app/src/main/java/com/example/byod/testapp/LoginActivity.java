package com.example.toto.testapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static java.lang.Integer.parseInt;


public class LoginActivity extends AppCompatActivity {

    EditText userName, password;
    Button login;
    //  JSONObject jsonObject;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = findViewById(R.id.login_userid);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!userName.getText().toString().isEmpty()) {
                    if (!password.getText().toString().isEmpty()) {
                        String user = userName.getText().toString();
                        String pass = password.getText().toString();


                        String url = "http://196.202.112.95:8000/BYODService.asmx/Login?username=" + user + "&&" + "password=" + pass;
                        JsonObjectRequest stringRequest = new JsonObjectRequest(url, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.i("response", response.toString());
                                        try {
                                            token = response.getString("Token");
                                        } catch (JSONException e) {

                                        }

                                        if (!token.equals("")) {
                                            SharedPreferences.Editor tok = getSharedPreferences("token", MODE_PRIVATE).edit();
                                            tok.putString("token", token);
                                            tok.apply();
                                            SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                            mypreference.edit().putBoolean("loggedIn", true).apply();


                                            if (android.os.Build.VERSION.SDK_INT > 22) {
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                LoginActivity.this.startActivity(intent);
                                                LoginActivity.this.finish();
                                            } else {
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity2.class);
                                                LoginActivity.this.startActivity(intent);
                                                LoginActivity.this.finish();
                                            }
                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.v("VolleyError",error.toString());

                                Toast.makeText(LoginActivity.this, "User name or password is not correct", Toast.LENGTH_LONG).show();
                            }
                        });

                        com.example.toto.testapp.AppController.getInstance().addToRequestQueue(stringRequest);

                    } else {
                        Toast.makeText(LoginActivity.this, "Password is not correct", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User name is not correct", Toast.LENGTH_LONG).show();

                }
            }
        });


    }


}
