package com.example.safety_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button b1, b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = findViewById(R.id.button);
        SharedPreferences sp = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String id = "id";
//        if(sp.getString("id", null).length() > 0){
//            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//            finish();
//        }
        onClicker();
    }

    private void onClicker() {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }
}
