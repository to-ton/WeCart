package com.lobomarket.wecart.TransactionScreenFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lobomarket.wecart.R;
import com.lobomarket.wecart.UserHomeScreen.ProductDetails;

public class TransactionScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_screen);
    }



    public void btnPrevious(View view){
        switch (view.getId()){
            case R.id.btnTransactToAgent:
            case R.id.btnAgentToCart:
                onBackPressed();
                break;
        }
    }

    public void placeOrder(View v){
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}