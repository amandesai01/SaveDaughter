package com.example.safety_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {
    EditText e1, e2, e4, e5, e6;
    Button b1;
    String ngrkoid = "http://8e6780ca.ngrok.io/register?";
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    String error = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e1 = findViewById(R.id.editText);
        e2 = findViewById(R.id.editText5);
        e4 = findViewById(R.id.editText7);
        e5 = findViewById(R.id.editText8);
        e6 = findViewById(R.id.editText9);
        b1 = findViewById(R.id.button3);
        sp = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        OnClicker();
    }

    private void OnClicker() {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = Verhoeff.validateVerhoeff(e2.getText().toString());
                if (!result) {
                    Toast.makeText(RegisterActivity.this, "Entered Aadhaar card number is wrong", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (!e5.getText().toString().equals(e6.getText().toString())) {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                        String url = "https://8e6780ca.ngrok.io/register?name=" + e1.getText().toString() + "&aadhar=" + e2.getText().toString() + "&phoneno=" + e4.getText().toString() + "&password=" + e5.getText().toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            if (response != null) {
                                                System.out.println("Reading");
                                                JSONObject jobj = new JSONObject(response);
                                                String status = jobj.getString("status");
                                                String message = jobj.getString("message");
                                                error = jobj.getString("assigneduserid");
                                                if (status.equals("OK")) {
                                                    ed = sp.edit();
                                                    ed.putString("id", error);
                                                    System.out.println(response);
                                                    ed.commit();
                                                    startActivity(new Intent(getApplicationContext(), RegisterActivity2.class));
                                                    finish();
                                                }
                                            }
                                        } catch (Exception e) {
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
                }

            }
        });
    }


}
//
//class RegisterCall1 extends AsyncTask<String , Void, String> {
//    String status = "";
//    String message = "";
//    String error = "";
//    boolean stat = false;
//    String JSONstr = "";
//    String line = "";
//
//    @Override
//    protected String doInBackground(String... strings) {
//        URL url = null;
//        System.out.println("Do");
//        try {
//            url = new URL(strings[0]);
//            System.out.println(url);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.connect();
//            System.out.println(con);
//            InputStream isr = con.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(isr));
//            while ((line = br.readLine()) != null) {
//                System.out.println("Writing");
//                JSONstr += line + '\n';
//            }
//            if (JSONstr != null) {
//                System.out.println("Reading");
//                JSONObject jobj = new JSONObject(JSONstr);
//                status = jobj.getString("status");
//                message = jobj.getString("message");
//                error = jobj.getString("error");
//                if (status.equals("OK")) {
//                    stat = true;
//                }
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return message;
//    }
//
////    @Override
////    protected String doInBackground(Void... voids) {
////        URL url = null;
////        System.out.println("Do");
////        try {
////            url = new URL(strings[0]);
////            System.out.println(url);
////            HttpURLConnection con = (HttpURLConnection) url.openConnection();
////            con.connect();
////            InputStream isr = con.getInputStream();
////            BufferedReader br = new BufferedReader(new InputStreamReader(isr));
////            while ((line = br.readLine()) != null) {
////                System.out.println("Writing");
////                JSONstr += line + '\n';
////            }
////            if (JSONstr != null) {
////                System.out.println("Reading");
////                JSONObject jobj = new JSONObject(JSONstr);
////                status = jobj.getString("status");
////                message = jobj.getString("message");
////                error = jobj.getString("error");
////                if (status.equals("OK")) {
////                    stat = true;
////                }
////            }
////        } catch (MalformedURLException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////        return null;
////    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//    }
//
////    public boolean isStat() {
////        return stat;
////    }
////
////    public String getError() {
////        return error;
////    }
//}

