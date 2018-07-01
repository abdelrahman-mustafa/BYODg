package com.example.toto.testapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignFailOutActivity extends AppCompatActivity {

    int nu;
    int num = 0;
    TextView support, logout, submit;
    Button signIn;
    ImageButton back;
    JSONObject  jsonObject, ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_fail_out);

        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (android.os.Build.VERSION.SDK_INT > 22) {

                    Intent intent = new Intent(SignFailOutActivity.this, HomeActivity.class);
                    SignFailOutActivity.this.startActivity(intent);
                } else {

                    Intent intent = new Intent(SignFailOutActivity.this, HomeActivity2.class);
                    SignFailOutActivity.this.startActivity(intent);
                }


            }
        });

        submit = findViewById(R.id.fail_submit);
        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(SignFailOutActivity.this);
                        dialog.setContentView(R.layout.dialog_submit);
                        dialog.setTitle(null);
                        dialog.show();
                        String x = getIntent().getStringExtra("auth");
                        String photo = getIntent().getStringExtra("photo");
                        SharedPreferences date0 = getSharedPreferences("date", MODE_PRIVATE);
                        final String date = date0.getString("time", null);
                        SharedPreferences time0 = getSharedPreferences("time", MODE_PRIVATE);
                        final String time = time0.getString("tim", null);

                        SharedPreferences prefs = getSharedPreferences("token", MODE_PRIVATE);
                        String m = prefs.getString("token", null);

                        String params_Date =
                                ("{"
                                        + " \"Token\":" + "\"" + m + "\"" + ","
                                        + " \"Date\":" + "\"" + date + "\"" + ","
                                        + " \"Time\":" + "\"" + time + "\"" + ","
                                        + " \"AuthenticationType\":" + "\"" + x + "\"" + ","
                                        + " \"OperationType\":" + "\"" + "2" + "\"" + ","
                                        + " \"CapturedImage\":" + "\"" + photo + "\""
                                        + "}");
                        try {
                            jsonObject = new JSONObject(params_Date);

                            Log.i("request", jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        String url = "http://196.202.112.95:8000/BYODService.asmx/SubmitToAdmin";
                        JsonObjectRequest stringRequest = new JsonObjectRequest(url, ll,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("responseOfSubmit",response.toString());
                                        try {
                                            if (response.get("status").equals(String.valueOf(-1))){
                                                SharedPreferences.Editor e = getSharedPreferences("submit", MODE_PRIVATE).edit();
                                                e.putString("submit", "null" );
                                                e.apply();
                                                if (android.os.Build.VERSION.SDK_INT > 22) {

                                                    Intent intent = new Intent(SignFailOutActivity.this, HomeActivity.class);
                                                    SignFailOutActivity.this.startActivity(intent);
                                                } else {

                                                    Intent intent = new Intent(SignFailOutActivity.this, HomeActivity2.class);

                                                    SignFailOutActivity.this.startActivity(intent);
                                                }
                                            }else{
                                                 // if failed check go back to home to reture sign in
                                                /*
                                                if (android.os.Build.VERSION.SDK_INT > 22) {

                                                    Intent intent = new Intent(SignFailOutActivity.this, HomeActivity.class);
                                                    SignFailOutActivity.this.startActivity(intent);
                                                } else {

                                                    Intent intent = new Intent(SignFailOutActivity.this, HomeActivity2.class);

                                                    SignFailOutActivity.this.startActivity(intent);
                                                }
   */                                         }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(SignFailOutActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                            }
                        });

                        AppController.getInstance().addToRequestQueue(stringRequest);



                    }
                }
        );
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(SignFailOutActivity.this);
                        mypreference.edit().putBoolean("loggedIn", false).apply();

                        Intent intent = new Intent(SignFailOutActivity.this, LoginActivity.class);
                        SignFailOutActivity.this.startActivity(intent);
                        SignFailOutActivity.this.finish();

                    }
                }
        );
        support = findViewById(R.id.support);
        support.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent = new Intent(SignFailOutActivity.this, SupportActivity.class);
                        SignFailOutActivity.this.startActivity(intent);

                    }
                }
        );
        signIn = findViewById(R.id.fail_button_signin);
        signIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences ed = getSharedPreferences("numberSigns", MODE_PRIVATE);
                        num = ed.getInt("number", 0)+1 ;
                        SharedPreferences.Editor edi = getSharedPreferences("numberSigns", MODE_PRIVATE).edit();
                        edi.putInt("number", num );
                        edi.apply();
                        final Dialog dialog2 = new Dialog(SignFailOutActivity.this);
                        dialog2.setContentView(R.layout.dialog_exceed);
                        dialog2.setTitle(null);

                        WindowManager.LayoutParams l = new WindowManager.LayoutParams();
                        Window windo = dialog2.getWindow();
                        assert windo != null;
                        l.copyFrom(windo.getAttributes());
//This makes the dialog take up the full width
                        l.width = WindowManager.LayoutParams.FILL_PARENT;
                        l.height = WindowManager.LayoutParams.WRAP_CONTENT;


                        if (num >= 3){
                            dialog2.show();
                            Button submit  = (Button) findViewById(R.id.exceed_submit);
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    final Dialog dialog = new Dialog(SignFailOutActivity.this);
                                    dialog.setContentView(R.layout.dialog_submit);
                                    dialog.setTitle(null);
                                    dialog.show();
                                    String x = getIntent().getStringExtra("auth");
                                    String photo = getIntent().getStringExtra("photo");
                                    SharedPreferences date0 = getSharedPreferences("date", MODE_PRIVATE);
                                    final String date = date0.getString("time", null);
                                    SharedPreferences time0 = getSharedPreferences("time", MODE_PRIVATE);
                                    final String time = time0.getString("tim", null);
                                    SharedPreferences prefs = getSharedPreferences("token", MODE_PRIVATE);
                                    String m = prefs.getString("token", null);
                                    SharedPreferences stu = getSharedPreferences("Sign", MODE_PRIVATE);
                                    if (stu.getString("type", "no").equals("in")) {
                                        nu = 1;

                                    } else if (stu.getString("type", "no").equals("out")) {
                                        nu = 0;

                                    }
                                    String jsonString = null;
                                    try {
                                        jsonString = new JSONObject()
                                                .put("Token", m.toString())
                                                .put("Date", date.toString())
                                                .put("Time",time.toString())
                                                .put("AuthenticationType",x.toString())
                                                .put("OperationType",String.valueOf(nu))
                                                .put("CapturedImage",photo.toString()).toString();
                                        ll = new JSONObject(jsonString);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String params_Date =
                                            ("{"
                                                    + " \"Token\":" + "\"" + m + "\"" + ","
                                                    + " \"Date\":" + "\"" + date + "\"" + ","
                                                    + " \"Time\":" + "\"" + time + "\"" + ","
                                                    + " \"AuthenticationType\":" + "\"" + x + "\"" + ","
                                                    + " \"OperationType\":" + "\"" + "2" + "\"" + ","
                                                    + " \"CapturedImage\":" + "\"" + photo + "\""
                                                    + "}");
                                    try {
                                        jsonObject = new JSONObject(params_Date);

                                        Log.i("request", jsonObject.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    String url = "http://196.202.112.95:8000/BYODService.asmx/SubmitToAdmin";
                                    JsonObjectRequest stringRequest = new JsonObjectRequest(url, ll,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.i("responseOfSubmit",response.toString());
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(SignFailOutActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    AppController.getInstance().addToRequestQueue(stringRequest);

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

                                    if (android.os.Build.VERSION.SDK_INT > 22) {

                                        Intent intent = new Intent(SignFailOutActivity.this, HomeActivity.class);
                                        intent.putExtra("submit", "false");
                                        SignFailOutActivity.this.startActivity(intent);
                                    } else {

                                        Intent intent = new Intent(SignFailOutActivity.this, HomeActivity2.class);
                                        intent.putExtra("submit", "false");
                                        SignFailOutActivity.this.startActivity(intent);
                                    }


                                }
                            });
                        }else {
                            if (android.os.Build.VERSION.SDK_INT > 22) {

                                Intent intent = new Intent(SignFailOutActivity.this, HomeActivity.class);
                                SignFailOutActivity.this.startActivity(intent);
                                SignFailOutActivity.this.finish();

                            } else {

                                Intent intent = new Intent(SignFailOutActivity.this, HomeActivity2.class);
                                SignFailOutActivity.this.startActivity(intent);
                                SignFailOutActivity.this.finish();
                            }
                        }
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signed_support, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(SignFailOutActivity.this, HomeActivity.class);
        SignFailOutActivity.this.startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (android.os.Build.VERSION.SDK_INT > 22) {

            Intent intent = new Intent(SignFailOutActivity.this, HomeActivity.class);
            SignFailOutActivity.this.startActivity(intent);
            SignFailOutActivity.this.finish();

        } else {

            Intent intent = new Intent(SignFailOutActivity.this, HomeActivity2.class);
            SignFailOutActivity.this.startActivity(intent);
            SignFailOutActivity.this.finish();
        }
    }
}
