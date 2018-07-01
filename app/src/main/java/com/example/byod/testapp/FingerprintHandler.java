package com.example.toto.testapp;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.example.toto.testapp.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class  FingerprintHandler extends FingerprintManager.AuthenticationCallback {


private Context context;


// Constructor
public FingerprintHandler(Context mContext) {
        context = mContext;
        }


public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
        return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }


@Override
public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Fingerprint Authentication error\n" + errString, false);
        }


@Override
public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Fingerprint Authentication help\n" + helpString, false);
        }


@Override
public void onAuthenticationFailed() {
        this.update("Fingerprint Authentication failed.", false);
        }


@Override
public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
     ///   this.update("Fingerprint Authentication succeeded.", true);
        final Intent intent = new Intent(this.context, CameraActivity.class);
        intent.putExtra("auth","2");

        Thread thread0 = new Thread(){
                @Override
                public void run() {
                        try {
                                Thread.sleep(500); // As I am using LENGTH_LONG in Toast
                                context.startActivity(intent);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
        };

        thread0.start();

        }


public void update(String e, Boolean success){

        if(success){

        }
        }
        }
