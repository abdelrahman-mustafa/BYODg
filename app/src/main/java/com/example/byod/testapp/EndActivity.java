package com.example.toto.testapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class EndActivity extends AppCompatActivity {
    ImageView imageView;
    JSONObject jsonObj;
    byte[] data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);


        byte[] data = getIntent().getByteArrayExtra("image");

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        imageView = (ImageView) findViewById(R.id.image2);
        imageView.setImageBitmap(bitmap);

        final String s1 = Arrays.toString(data);

        String url = "http://www.agiteq.com/Services/IOService.asmx/Upload";

        JsonObjectRequest req = new JsonObjectRequest(url, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            jsonObj = new JSONObject("{\"image\":\"" + s1 + "\"}");
                            if (response.getJSONObject("IsUploaded").toString().equalsIgnoreCase("true")) {
                                Toast.makeText(EndActivity.this, "Image is uploaded", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        com.example.toto.testapp.AppController.getInstance().addToRequestQueue(req);

    }
}



