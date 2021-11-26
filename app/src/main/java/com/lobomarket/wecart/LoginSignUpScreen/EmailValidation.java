package com.lobomarket.wecart.LoginSignUpScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavOptions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
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
import com.github.ybq.android.spinkit.SpinKitView;
import com.lobomarket.wecart.LoginSignUpScreen.EmailValidation;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EmailValidation extends AppCompatActivity {

    TextView resendCode;


    RequestQueue requestQueue;

    LoadingDialog loadingDialog;

    String email, isVerify;

    SpinKitView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_validation);
        try {
            resendCode = findViewById(R.id.resend);
            loading = findViewById(R.id.loadingVerify);
            email = getIntent().getStringExtra("email");

            loadingDialog = new LoadingDialog(EmailValidation.this);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        checkInternet();
                    } catch (Exception e) {
                        Log.e("Resend code", "Exception", e);
                    }
                }
            });


            isVerify = null;
            checkVerify();


        } catch (Exception e){
            Log.e("Email Validation", "exception", e);
        }

    }

    private void checkInternet() {
        try {
            loadingDialog.startLoading("Please wait");
            String JSON_URL = "https://www.google.com/";
            requestQueue = Volley.newRequestQueue(EmailValidation.this);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            resend();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    Toast.makeText(EmailValidation.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("Email Validation", "exception", e);
        }

    }


    private void resend() {
        String validate = null;

        try {
            validate = URLEncoder.encode(email.replace("'","\\'"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/resend-code.php?email="+ validate +"";
        requestQueue = Volley.newRequestQueue(EmailValidation.this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //With internet
                        loadingDialog.stopLoading();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No internet
                Toast.makeText(EmailValidation.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                loadingDialog.stopLoading();
            }
        });
        stringRequest.setTag("CANCEL");
        requestQueue.add(stringRequest);
    }

    private void checkVerify(){
        String txtemail = null;
        try {
            txtemail  = URLEncoder.encode(email, "utf-8");
            Log.v("TEST email", "email: " + txtemail);
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/confirm.php?email="+ txtemail +"";
            RequestQueue requestQueue = Volley.newRequestQueue(EmailValidation.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST REGISTRATION", response);
                            isVerify = response;
                            //Toast.makeText(EmailValidation.this, "Is verified? " + isVerify, Toast.LENGTH_SHORT).show();
                            if(isVerify.equals("no")){
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkVerify();
                                    }
                                }, 3000);
                            } else {
                                //Toast.makeText(EmailValidation.this, "Is verified? " + isVerify, Toast.LENGTH_SHORT).show();
                                Toast.makeText(EmailValidation.this, "Verified", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Verify", "Exception", error);
                    Toast.makeText(EmailValidation.this, "Failed", Toast.LENGTH_LONG).show();
                }
            }
            );
            requestQueue.add(postRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int key_code, KeyEvent key_event) {
        if (key_code== KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event);
            return true;
        }
        return false;
    }
}
