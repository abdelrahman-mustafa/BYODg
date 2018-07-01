package com.example.toto.testapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";


    private CameraSource mCameraSource = null;


    JSONObject jsonObject;
    int num;
    Button capture;
    JSONObject ll;
    private com.example.toto.testapp.CameraSourcePreview mPreview;
    private com.example.toto.testapp.GraphicOverlay mGraphicOverlay;
    private Context context = CameraActivity.this;


    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    String photo = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (android.os.Build.VERSION.SDK_INT > 22) {

                    Intent intent = new Intent(CameraActivity.this, HomeActivity.class);
                    CameraActivity.this.startActivity(intent);
                } else {

                    Intent intent = new Intent(CameraActivity.this, HomeActivity2.class);
                    CameraActivity.this.startActivity(intent);
                }


            }
        });

        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        GraphicOverlay.setContext(getApplicationContext());


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();

        } else {
            requestCameraPermission();
        }
        SharedPreferences date0 = getSharedPreferences("date", MODE_PRIVATE);
        final String date = date0.getString("time", null);
        SharedPreferences time0 = getSharedPreferences("time", MODE_PRIVATE);
        final String time = time0.getString("tim", null);

        SharedPreferences prefs = getSharedPreferences("token", MODE_PRIVATE);
        final String m = prefs.getString("token", "123");

        final String x = getIntent().getStringExtra("auth");


        capture = findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {


                    @Override
                    public void onPictureTaken(byte[] bytes) {

                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image = getResizedBitmap(image, 150, 150);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        photo = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        SharedPreferences st = getSharedPreferences("Sign", MODE_PRIVATE);
                        if (st.getString("type", "no").equals("in")) {
                            num = 1;

                        } else if (st.getString("type", "no").equals("out")) {
                            num = 0;

                        }

                        Log.i("me", photo);
                        try {
                            String jsonString = new JSONObject()
                                    .put("Token", m.toString())
                                    .put("Date", date.toString())
                                    .put("Time", time.toString())
                                    .put("AuthenticationType", x.toString())
                                    .put("OperationType", String.valueOf(num))
                                    .put("CapturedImage", photo.toString()).toString();
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
                                        + " \"OperationType\":" + "\"" + 2 + "\"" + ","
                                        + " \"CapturedImage\":" + "\"" + photo + "\""
                                        + " }");
                        try {
                            jsonObject = new JSONObject(params_Date);

                            Log.i("request", jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String url = "http://196.202.112.95:8000/BYODService.asmx/Authenticate";
/*

                Intent intent = new Intent(CameraActivity.this, SignFailActivity.class);
                intent.putExtra("auth", x);
                intent.putExtra("photo", photo);
                CameraActivity.this.startActivity(intent);
*/
                        JsonObjectRequest stringRequest = new JsonObjectRequest(url, ll,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("responseOfAuthenticate", String.valueOf(response));

                                        try {

                                            if (response.getInt("Status") == 0) {
                                                SharedPreferences st = getSharedPreferences("Sign", MODE_PRIVATE);
                                                if (st.getString("type", "no").equals("in")) {
                                                    Intent intent = new Intent(CameraActivity.this, SignFailActivity.class);
                                                    intent.putExtra("auth", x);
                                                    intent.putExtra("photo", photo);
                                                    CameraActivity.this.startActivity(intent);
                                                    CameraActivity.this.finish();

                                                } else if (st.getString("type", "no").equals("out")) {
                                                    Intent intent = new Intent(CameraActivity.this, SignFailOutActivity.class);
                                                    intent.putExtra("auth", x);
                                                    intent.putExtra("photo", photo);
                                                    CameraActivity.this.startActivity(intent);
                                                    CameraActivity.this.finish();

                                                }

                                            } else if (response.getInt("Status") == 1) {

                                                Intent intent = new Intent(CameraActivity.this, SignSucessActivity.class);
                                                intent.putExtra("data", response.toString());
                                                CameraActivity.this.startActivity(intent);
                                                CameraActivity.this.finish();

                                            }
                                        } catch (JSONException e) {

                                        }


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                SharedPreferences st = getSharedPreferences("Sign", MODE_PRIVATE);
                                if (st.getString("type", "no").equals("in")) {
                                    Intent intent = new Intent(CameraActivity.this, SignFailActivity.class);
                                    intent.putExtra("auth", x);
                                    intent.putExtra("photo", photo);
                                    CameraActivity.this.startActivity(intent);
                                    CameraActivity.this.finish();

                                } else if (st.getString("type", "no").equals("out")) {
                                    Intent intent = new Intent(CameraActivity.this, SignFailOutActivity.class);
                                    intent.putExtra("auth", x);
                                    intent.putExtra("photo", photo);
                                    CameraActivity.this.startActivity(intent);
                                    CameraActivity.this.finish();
                                }
                            }
                        });

                        com.example.toto.testapp.AppController.getInstance().addToRequestQueue(stringRequest);
                    }
                });


            }
        }); // take image function together with later main processing part.


    }


    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

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

    // Face detection creation
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }
// open the camera front ot back

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.f)
                .build();

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }


    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        CameraActivity.this.finish();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));


    }

    // Build Camera source which connect face detector with camera preview
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {

        // take action just detect any face

        @Override
        public Tracker<Face> create(Face face) {
/*
            final CameraSource.PictureCallback callbackPicture = new CameraSource.PictureCallback() {
                public void onPictureTaken(byte[] data) {
                    x = data;
                    Toast.makeText(getApplicationContext(), "Face detected", Toast.LENGTH_LONG).show();
                    mCameraSource.stop();

                }
            };
            mCameraSource.takePicture(null, callbackPicture);*/


            return new GraphicFaceTracker(mGraphicOverlay, context);

        }
    }


    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private com.example.toto.testapp.GraphicOverlay mOverlay;
        private com.example.toto.testapp.FaceGraphic mFaceGraphic;
        Context mContext = getBaseContext();

        GraphicFaceTracker(com.example.toto.testapp.GraphicOverlay overlay, Context context) {
            context.getApplicationContext();
            mOverlay = overlay;
            mFaceGraphic = new com.example.toto.testapp.FaceGraphic(overlay, mContext);

        }


        // Start tracking the detected face instance within the face overlay.
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }


        //Update the position/characteristics of the face within the overlay.
        //Take action on face detected

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }


        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }


        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }


    public static String UploadImage(byte[] imgByteArray, String sessionToken, boolean isImagePublic)
            throws IOException {

        InputStream is = null;
        try {
            URL url = new URL("http://www.agiteq.com/services/BYODService.asmx/Authenticate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // setting some properties on the connection
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);    // means that: i am going to receive data from the web service into the application
            conn.setRequestProperty("sessionToken", sessionToken);
            conn.setRequestProperty("Content-Type", "image/jpeg");
            conn.setRequestProperty("extension", "jpeg");
            conn.setRequestProperty("profile", "1");

            if (isImagePublic) {
                conn.setRequestProperty("privacy", "0");
            } else {
                conn.setRequestProperty("privacy", "1");
            }

            conn.setDoOutput(true);
            conn.connect();
            //--
            conn.getOutputStream().write(imgByteArray);

            conn.getOutputStream().flush();

            conn.getOutputStream().close();


            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Got response code " + responseCode);
            }


            is = conn.getInputStream();
            return readStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return null;
    }

    private static String readStream(InputStream stream) throws IOException {

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        BufferedOutputStream out = null;
        try {
            int length = 0;
            out = new BufferedOutputStream(byteArray);
            while ((length = stream.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            return byteArray.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
