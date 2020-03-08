package com.example.safety_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

//public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//    TextView t1, t2;
//    Button b1, b2, b3;
//    Location loc;
//    GoogleApiClient gac;
//    String uid = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//        t1 = findViewById(R.id.textView);
//        t2 = findViewById(R.id.textView2);
//        b1 = findViewById(R.id.button5);
//        b2 = findViewById(R.id.button6);
//        b3 = findViewById(R.id.button7);
//        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(getApplicationContext());
//        builder.addApi(LocationServices.API);
//        builder.addConnectionCallbacks(HomeActivity.this);
//        gac = builder.build();
//        onClicker();
//    }
//
//    private void onClicker() {
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), EmergencyActivity.class));
//            }
//        });
//        b3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GoogleApiClient.Builder builder = new GoogleApiClient.Builder(getApplicationContext());
//                builder.addApi(LocationServices.API);
//                builder.addConnectionCallbacks(HomeActivity.this);
//                gac = builder.build();
//            }
//        });
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        loc = LocationServices.FusedLocationApi.getLastLocation(gac);
//        System.out.println(loc);
//        if (loc != null) {
//            Toast.makeText(this, "Lat not null", Toast.LENGTH_SHORT).show();
//            double lat = loc.getLatitude();
//            double lon = loc.getLongitude();
//            String uid = getSharedPreferences("MySharedPreferences", MODE_PRIVATE).getString("id", null);
//            RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
//            String url = "https://8e6780ca.ngrok.io/generatealert?id=" + uid + "&latitude=" + lat + "&longitude=" + lon;
//            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+lat+" "+lon);
//            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    System.out.println(error);
//                }
//            });
//            queue.add(stringRequest);
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//    }
//}


public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public double currentLatitude;
    public double currentLongitude;
    TextView t1, t2, t3;
    Button b3;
    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        t3 = findViewById(R.id.textView8);
        b3 = findViewById(R.id.button7);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        onClicker();

        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        String uid = getSharedPreferences("MySharedPreferences", MODE_PRIVATE).getString("id", null);
        String url = "https://8e6780ca.ngrok.io/generatealert?" + "latitude=" + currentLatitude + "&longitude=" + currentLongitude;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("values");
                                t3.setText(jsonArray.getJSONObject(0).toString());
                                System.out.println("))))))))))"+jsonArray.getJSONObject(0).toString());
                            }
                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        queue.add(stringRequest);

    }

    private void onClicker() {
        b3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                String uid = getSharedPreferences("MySharedPreferences", MODE_PRIVATE).getString("id", null);
                String url = "https://8e6780ca.ngrok.io/generatealert?id=" + uid + "&latitude=" + currentLatitude + "&longitude=" + currentLongitude;
//                Toast.makeText(HomeActivity.this, url, Toast.LENGTH_SHORT).show();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println(response);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
                queue.add(stringRequest);
                try {
//                    Toast.makeText(getApplicationContext(), "D1", Toast.LENGTH_SHORT).show();
                    recordAudio();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(HomeActivity.this, EmergencyActivity.class);
                intent.putExtra("lat", currentLatitude);
                intent.putExtra("lon", currentLongitude);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    /**
     * If connected get lat and long
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

//        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void recordAudio() throws IOException, InterruptedException {
        Toast.makeText(getApplicationContext(), "D2", Toast.LENGTH_SHORT).show();
        File output = new File(getApplicationContext().getExternalFilesDir(null), "andcorder.3gp");
        Objects.requireNonNull(output.getParentFile()).mkdirs();
        int StoppingTime = 15;
        int loop = 1;
        long StartTime = System.currentTimeMillis() / 1000;

        MediaRecorder mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(output);

        mediaRecorder.prepare();
        mediaRecorder.start();
//        for (int i = 0; i < loop; i++) {
//            Toast.makeText(getApplicationContext(), "D", Toast.LENGTH_SHORT).show();
//            loop++;
//            if (((System.currentTimeMillis() / 1000) / StartTime) > StoppingTime) {
//                loop = 0;
//            }
//        }
        Thread.sleep(15000);

        mediaRecorder.stop();
        mediaRecorder.release();
    }
}