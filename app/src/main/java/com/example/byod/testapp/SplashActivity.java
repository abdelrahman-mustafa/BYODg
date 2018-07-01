package com.example.toto.testapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences mypreference = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);

                if (mypreference.getBoolean("loggedIn", false)) {
                    Log.i("Log", "User is logged in");
                    if (android.os.Build.VERSION.SDK_INT > 22) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        SplashActivity.this.startActivity(intent);
                        SplashActivity.this.finish();

                    } else {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity2.class);
                        SplashActivity.this.startActivity(intent);
                        SplashActivity.this.finish();

                    }

                    // to skip login page
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
