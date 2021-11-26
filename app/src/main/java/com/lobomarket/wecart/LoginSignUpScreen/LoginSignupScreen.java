package com.lobomarket.wecart.LoginSignUpScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.lobomarket.wecart.AgentHomeScreen.AgentActivity;
import com.lobomarket.wecart.SellerHomeScreen.SellerHomeScreen;
import com.lobomarket.wecart.UserHomeScreen.UserHomeScreen;
import com.lobomarket.wecart.AdminHomeScreen.AdminScreen;
import com.lobomarket.wecart.LoginLogOutSession.SessionManagement;
import com.lobomarket.wecart.R;

public class LoginSignupScreen extends AppCompatActivity {

    SessionManagement sessionManagement;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup_screen);

        sessionManagement = new SessionManagement(LoginSignupScreen.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String roleType = sessionManagement.getSession2();
        userName = sessionManagement.getSession3();

        if(roleType.equals("buyer")){
            checkBuyerSession();
        } else if (roleType.equals("seller")){
            checkSellerSession();
        } else if (roleType.equals("admin")){
            checkAdminSession();
        } else if (roleType.equals("agent")){
            checkAgentSession();
        }
    }

    private void checkBuyerSession(){
        int userID = sessionManagement.getSession();

        if(userID != -1){
            toUserHomeScreen(userName);
        }
    }

    private void checkAgentSession(){
        int userID = sessionManagement.getSession();

        if(userID != -1){
            toAgentHomeScreen(userName);
        }
    }

    private void checkSellerSession(){
        int userID = sessionManagement.getSession();

        if(userID != -1){
            toSellerHomeScreen(userName);
        }
    }

    private void checkAdminSession(){
        int userID = sessionManagement.getSession();

        if(userID != -1){
            toAdminScreen(userName);
        }
    }

    private void toUserHomeScreen(String userName){
        Intent userHome = new Intent(LoginSignupScreen.this, UserHomeScreen.class);
        userHome.putExtra("username", userName);
        startActivity(userHome);
        finish();
    }

    private void toSellerHomeScreen(String userName){
        Intent sellerHome = new Intent(LoginSignupScreen.this, SellerHomeScreen.class);
        sellerHome.putExtra("username", userName);
        startActivity(sellerHome);
        finish();
    }

    private void toAdminScreen(String userName){
        Intent admin = new Intent(LoginSignupScreen.this, AdminScreen.class);
        admin.putExtra("username", userName);
        startActivity(admin);
        finish();
    }

    private void toAgentHomeScreen(String username){
        Intent agent = new Intent(LoginSignupScreen.this, AgentActivity.class);
        agent.putExtra("username", username);
        startActivity(agent);
        finish();
    }
}