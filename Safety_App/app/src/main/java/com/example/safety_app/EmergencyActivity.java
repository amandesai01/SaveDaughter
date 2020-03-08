package com.example.safety_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

public class EmergencyActivity extends AppCompatActivity {

    String[] phones = new String[5];
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int REQUEST_CALL = 1;
    String lat="";
    String lon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        Bundle extras1 = getIntent().getExtras();
        if (extras1 != null) {
            lat = extras1.getString("lat");
            lon = extras1.getString("lon");
        }
        SharedPreferences sp = getSharedPreferences("MySharedPreferences1", MODE_PRIVATE);
        phones[0] = sp.getString("id1", null);
        phones[1] = sp.getString("id2", null);
        phones[2] = sp.getString("id3", null);
        phones[3] = sp.getString("id4", null);
        phones[4] = sp.getString("id5", null);
        if (isNetworkAvailable()) {
            sendMessage();
        } else {
            makeCall();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void sendMessage() {

        String message = "I need help, \nTrace me here\n https://www.google.com/maps/search/?api=1&query=19.1154,72.8397314";

        if (ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EmergencyActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phones[0], null, message, null, null);
            smsManager.sendTextMessage(phones[1], null, message, null, null);
            smsManager.sendTextMessage(phones[2], null, message, null, null);
            smsManager.sendTextMessage(phones[3], null, message, null, null);
            smsManager.sendTextMessage(phones[4], null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

        }
    }

    private void makeCall() {
        String n = "100";
        if (ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EmergencyActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + n)));
        }
    }

}
