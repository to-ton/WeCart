package com.lobomarket.wecart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lobomarket.wecart.LoginSignUpScreen.LoginSignupScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeScreen = new Intent(SplashScreen.this, LoginSignupScreen.class);
                startActivity(homeScreen);

                finish();
            }
        }, 4*1000);
    }
}