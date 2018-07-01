package com.example.toto.testapp;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.TimeUnit;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

@SuppressWarnings("ALL")
public class HomeActivity2 extends AppCompatActivity {
    TextView day, date, time, signs, signedIn, signedOut, logout, support;
    Button signIn, signOut;
    int signInTrials = 0;
    int signOutTrials = 0;
    private BeaconManager beaconManager;
    private BeaconRegion region, region2;


    JSONObject jsonResponse;
    Boolean shown;

    String submit;
    String[] array = new String[0];
    String numOfSignIN;
    String numOfSignOut;
    Integer major, minor;
    String uuid;
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private static final Map<String, List<String>> PLACES_BY_BEACONS;

    int k;

    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("18906:7793", new ArrayList<String>() {{
            add("Heavenly Sandwiches");
            // read as: "Heavenly Sandwiches" is closest
            // to the beacon with major 22504 and minor 48827

            // "Mini Panini" is the furthest away
        }});
        placesByBeacons.put("41747:20110", new ArrayList<String>() {{
            add("Mini Panini");
            add("Green & Green Salads");
            add("Heavenly Sandwiches");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        day = findViewById(R.id.home_day);
        date = findViewById(R.id.home_date);
        time = findViewById(R.id.home_time);
        signs = findViewById(R.id.home_sign_number);
        signedIn = findViewById(R.id.home_signin_number);
        signedOut = findViewById(R.id.home_signout_number);
        signIn = findViewById(R.id.home_button_signin);
        signOut = findViewById(R.id.home_button_signout);
        logout = findViewById(R.id.logout);
        support = findViewById(R.id.support);
        final Drawable back2 = getResources().getDrawable(R.drawable.circle_button);
        final Drawable back1 = getResources().getDrawable(R.drawable.circle_button2);


        // check if the user try signin or signout
        SystemRequirementsChecker.checkWithDefaultDialogs(this);


        int rc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(HomeActivity2.this);
                mypreference.edit().putBoolean("loggedIn", false).apply();
                Intent intent = new Intent(HomeActivity2.this, LoginActivity.class);
                HomeActivity2.this.startActivity(intent);
                HomeActivity2.this.finish();
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity2.this, SupportActivity.class);
                HomeActivity2.this.startActivity(intent);
            }
        });


        final SharedPreferences editor = getSharedPreferences("token", MODE_PRIVATE);

        getDate();

        SharedPreferences e = getSharedPreferences("submit", MODE_PRIVATE);

        submit = e.getString("submit", "no");
        if (submit.equals("done")) {
            Toast.makeText(HomeActivity2.this, "Wait the admin reply", Toast.LENGTH_LONG);
            // listen the notification
            String url2 = "http://196.202.112.95:8000/BYODService.asmx/OperationNotification?token=" + editor.getString("token", "");

            JsonObjectRequest noti = new JsonObjectRequest(url2, null,
                    new Response.Listener<JSONObject>() {
                        @Override

                        public void onResponse(JSONObject response) {
                            SharedPreferences.Editor em = getSharedPreferences("submit", MODE_PRIVATE).edit();
                            em.putString("submit", "none");
                            em.apply();
                            try {
                                if (response.getString("Succes").equals("1")) {
                                    Toast.makeText(HomeActivity2.this, "your submit is successfuly done ", Toast.LENGTH_LONG).show();
                                } else {

                                    Toast.makeText(HomeActivity2.this, "your submit is refused, you can sign in ", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {


                            }
                        }
                    }, new Response.ErrorListener()

            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity2.this, "Connection Failed", Toast.LENGTH_LONG).show();
                }

            });

            com.example.toto.testapp.AppController.getInstance().addToRequestQueue(noti);

        }
        final Dialog dialog0 = new Dialog(HomeActivity2.this);
        dialog0.setContentView(R.layout.dialog_check_location);
        dialog0.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog0.setTitle(null);
        final Dialog dialog2 = new Dialog(HomeActivity2.this);
        dialog2.setContentView(R.layout.dialog_no_coverage);
        dialog2.setTitle(null);

        WindowManager.LayoutParams l = new WindowManager.LayoutParams();
        Window windo = dialog2.getWindow();
        assert windo != null;
        l.copyFrom(windo.getAttributes());
//This makes the dialog take up the full width
        l.width = WindowManager.LayoutParams.FILL_PARENT;
        l.height = WindowManager.LayoutParams.WRAP_CONTENT;


        signIn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog0.show();
                k = 0;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (k == 0) {
                            dialog0.dismiss();
                            dialog2.show();
                            beaconManager.disconnect();


                        }
                    }
                }, 17000);
                beaconManager = new BeaconManager(HomeActivity2.this);

                beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
                    @Override
                    public void onBeaconsDiscovered(final BeaconRegion beaconRegion, final List<Beacon> beacons) {

                        if (!beacons.isEmpty()) {
                            Log.d("Hi", "Found");
                            Log.d("beacons", beacons.toString());
                            String url2 = "http://196.202.112.95:8000/BYODService.asmx/GetBeaconInfo";
                            JsonArrayRequest stringRequest2 = new JsonArrayRequest(url2,
                                    new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(final JSONArray response) {
                                            Log.d("response", response.toString());
                                            for (int f = 0; f < beacons.size(); f++) {
                                                Beacon nearestBeacon = beacons.get(f);
                                                Log.d("beacon1", nearestBeacon.toString());
                                                for (int i = 0; i < response.length(); i++) {
                                                    try {
                                                        jsonResponse = response.getJSONObject(i);
                                                        uuid = jsonResponse.get("UUID").toString();
                                                        Log.i("response", uuid);
                                                        major = parseInt(jsonResponse.get("Major").toString());
                                                        Log.i("response", major.toString());
                                                        minor = parseInt(jsonResponse.get("Minor").toString());
                                                        Log.i("response", minor.toString());
                                                    } catch (JSONException e) {
                                                        Log.d("Error", e.toString());
                                                    }
                                                    if (nearestBeacon.getMajor() == major & nearestBeacon.getMinor() == minor) {
                                                        k = 1;
                                                        SharedPreferences.Editor edi = getSharedPreferences("Sign", MODE_PRIVATE).edit();
                                                        edi.putString("type", "in");
                                                        edi.apply();

                                                        Intent intent = new Intent(HomeActivity2.this, CameraActivity.class);
                                                        intent.putExtra("auth", "0");
                                                        HomeActivity2.this.startActivity(intent);
                                                        beaconManager.disconnect();

                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(HomeActivity2.this, "Connection Failed", Toast.LENGTH_LONG).show();
                                    dialog0.dismiss();
                                }
                            });
                            com.example.toto.testapp.AppController.getInstance().addToRequestQueue(stringRequest2);
                        }
                    }
                });
                region = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
                beaconManager.setForegroundScanPeriod(10000, 1000);
                beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        beaconManager.startRanging(region);
                    }
                });
            }
        });
        signOut.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {


                dialog0.show();
                k = 0;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (k == 0) {
                            dialog0.dismiss();
                            dialog2.show();
                            beaconManager.disconnect();


                        }
                    }
                }, 17000);
                beaconManager = new BeaconManager(HomeActivity2.this);

                beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
                    @Override
                    public void onBeaconsDiscovered(final BeaconRegion beaconRegion, final List<Beacon> beacons) {

                        if (!beacons.isEmpty()) {
                            Log.d("Hi", "Found");
                            Log.d("beacons", beacons.toString());
                            String url2 = "http://196.202.112.95:8000/BYODService.asmx/GetBeaconInfo";
                            JsonArrayRequest stringRequest2 = new JsonArrayRequest(url2,
                                    new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(final JSONArray response) {
                                            Log.d("response", response.toString());
                                            for (int f = 0; f < beacons.size(); f++) {
                                                Beacon nearestBeacon = beacons.get(f);
                                                Log.d("beacon1", nearestBeacon.toString());
                                                for (int i = 0; i < response.length(); i++) {
                                                    try {
                                                        jsonResponse = response.getJSONObject(i);
                                                        uuid = jsonResponse.get("UUID").toString();
                                                        Log.i("response", uuid);
                                                        major = parseInt(jsonResponse.get("Major").toString());
                                                        Log.i("response", major.toString());
                                                        minor = parseInt(jsonResponse.get("Minor").toString());
                                                        Log.i("response", minor.toString());
                                                    } catch (JSONException e) {
                                                        Log.d("Error", e.toString());
                                                    }
                                                    if (nearestBeacon.getMajor() == major & nearestBeacon.getMinor() == minor) {
                                                        k = 1;
                                                        SharedPreferences.Editor editor = getSharedPreferences("Sign", MODE_PRIVATE).edit();
                                                        editor.putString("type", "out");
                                                        editor.apply();
                                                        Intent intent = new Intent(HomeActivity2.this, CameraActivity.class);
                                                        intent.putExtra("auth", "0");
                                                        HomeActivity2.this.startActivity(intent);
                                                        beaconManager.disconnect();

                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(HomeActivity2.this, "Connection Failed", Toast.LENGTH_LONG).show();
                                    dialog0.dismiss();
                                }
                            });
                            com.example.toto.testapp.AppController.getInstance().addToRequestQueue(stringRequest2);
                        }
                    }
                });
                region = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
                beaconManager.setForegroundScanPeriod(10000, 1000);
                beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        beaconManager.startRanging(region);
                    }
                });
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);

    }

    private void requestCameraPermission() {

        final String[] permissions = new String[]{android.Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };


    }


    public void getDate() {


        final Drawable back2 = getResources().getDrawable(R.drawable.circle_button);
        final Drawable back1 = getResources().getDrawable(R.drawable.circle_button2);

        final SharedPreferences editor = getSharedPreferences("token", MODE_PRIVATE);
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function every min

                String url = "http://196.202.112.95:8000/BYODService.asmx/GetEmployeeAnalytics?token=" + editor.getString("token", "");
                JsonObjectRequest stringRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.i("response", response.toString());

                                Pattern pattern = Pattern.compile(" ");


                                try {
                                    array = pattern.split(response.getString("DateTime"));
                                    numOfSignIN = response.getString("TotalSignIn");
                                    numOfSignOut = response.getString("TotalSignOut");
                                } catch (JSONException e) {

                                }

                                Date now = new Date();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
                                day.setText(simpleDateformat.format(now));
                                Integer x = parseInt(numOfSignIN);
                                Integer z = parseInt(numOfSignOut);
                                Integer u = x + z;
                                String ctime = array[1] + " " + array[2];
                                String cdate = array[0];
                                date.setText(cdate);
                                time.setText(ctime);


                                signedIn.setText(numOfSignIN);
                                signedOut.setText(numOfSignOut);
                                signs.setText(u.toString());
                                SharedPreferences day = getSharedPreferences("date", MODE_PRIVATE);
                                SharedPreferences st = getSharedPreferences("Signed", MODE_PRIVATE);

                                if (day.getString("time", "0").equals(cdate)) {
                                    Log.i(" enable sign out", "sign out enabeled");
                                    if (st.getString("done", "no").equals("in")) {
                                        signIn.setEnabled(false);
                                        signIn.setBackground(back2);
                                        signOut.setEnabled(true);
                                        signOut.setBackground(back1);
                                    } else if (st.getString("done", "no").equals("out")) {
                                        signIn.setEnabled(false);
                                        signOut.setEnabled(false);
                                        signOut.setBackground(back2);
                                        signIn.setBackground(back2);

                                    } else {
                                        signIn.setBackground(back1);
                                        signIn.setEnabled(true);
                                        signOut.setEnabled(false);
                                    }

                                } else {
                                    Log.i(" enable sign out", "sign out disabled");
                                    SharedPreferences.Editor edi = getSharedPreferences("numberSigns", MODE_PRIVATE).edit();
                                    edi.putInt("number", 0);
                                    edi.apply();
                                    signOut.setEnabled(false);
                                }
                                SharedPreferences.Editor editor = getSharedPreferences("date", MODE_PRIVATE).edit();
                                editor.putString("time", cdate);
                                editor.apply();
                                SharedPreferences.Editor editor2 = getSharedPreferences("time", MODE_PRIVATE).edit();
                                editor2.putString("tim", ctime);
                                editor2.apply();


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity2.this, "Connection Failed", Toast.LENGTH_LONG).show();
                    }
                });

                com.example.toto.testapp.AppController.getInstance().addToRequestQueue(stringRequest);


                ha.postDelayed(this, 60*1000);
            }
        }, 500);


    }

}
