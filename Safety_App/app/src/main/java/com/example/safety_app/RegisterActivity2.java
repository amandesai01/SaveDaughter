package com.example.safety_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterActivity2 extends AppCompatActivity {
    EditText e1, e2, e3, e4, e5;
    Button b1;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        e1 = findViewById(R.id.editText11);
        e2 = findViewById(R.id.editText12);
        e3 = findViewById(R.id.editText13);
        e4 = findViewById(R.id.editText14);
        e5 = findViewById(R.id.editText15);
        b1 = findViewById(R.id.button4);
        OnClicker();
    }

    private void OnClicker() {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getSharedPreferences("MySharedPreferences1", MODE_PRIVATE);
                ed = sp.edit();
                ed.putString("id1", e1.getText().toString());
                ed.putString("id2", e2.getText().toString());
                ed.putString("id3", e3.getText().toString());
                ed.putString("id4", e4.getText().toString());
                ed.putString("id5", e5.getText().toString());
                ed.commit();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });
    }
}

