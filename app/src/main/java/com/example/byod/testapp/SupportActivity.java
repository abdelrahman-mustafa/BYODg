package com.example.toto.testapp;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class SupportActivity extends AppCompatActivity {

    ImageButton back;
    TextView send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);


        send = findViewById(R.id.send_submit);
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (android.os.Build.VERSION.SDK_INT > 22) {

                    Intent intent = new Intent(SupportActivity.this, HomeActivity.class);
                    SupportActivity.this.startActivity(intent);
                    SupportActivity.this.finish();

                } else {

                    Intent intent = new Intent(SupportActivity.this, HomeActivity2.class);
                    SupportActivity.this.startActivity(intent);
                    SupportActivity.this.finish();
                }


            }
        });
    }

}
