package com.lobomarket.wecart.SellerHomeScreen;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lobomarket.wecart.Models.SellerOrderList;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SellerHomeScreen extends AppCompatActivity{

    private BottomNavigationView bottomNavigationView;
    String sellername;
    RequestQueue requestQueue;

    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home_screen);

        bottomNavigationView = findViewById(R.id.bottomNav);
        sellername = getIntent().getStringExtra("username");
        count = 0;

        //use to call the onClick Lister for Bottom Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Toast.makeText(SellerHomeScreen.this, "Hello, " + sellername, Toast.LENGTH_SHORT).show();

    }


    //This method will set the onClick lister of Bottom Navigation
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.fragmentSellerOrders:
                            if(!(item.isChecked())){ //to check if the item is clicked and to prevent from app crash
                                navigate(R.id.action_fragmentSellerProducts_to_fragmentSellerOrders);
                                refreshList();
                            }
                            break;
                        case R.id.fragmentSellerProducts:
                            if(!(item.isChecked())){
                                navigate(R.id.action_fragmentSellerOrders_to_fragmentSellerProducts);
                                refreshList();
                            }
                            break;
                    }
                    return true;
                }
            };

    //this method is used to navigate between orders and products
    private void navigate(@IdRes int viewId){
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerSeller);
        NavController navController = navHostFragment.getNavController();

        Bundle bundle = new Bundle();

        bundle.putString("sellerName", sellername);
        navController.navigate(viewId, bundle);
    }


    //this method handles back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    private void refreshList() {
        String JSON_URL = "https://www.google.com/";
        requestQueue = Volley.newRequestQueue(SellerHomeScreen.this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //With internet
                        showUser();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No internet
                Toast.makeText(SellerHomeScreen.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setTag("CANCEL");
        requestQueue.add(stringRequest);
    }

    private void showUser() {
        String txtbuyer = null;
        try {
            txtbuyer = URLEncoder.encode(sellername, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/show-orders.php?seller="+ txtbuyer +"";
        requestQueue = Volley.newRequestQueue(SellerHomeScreen.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                JSON_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = response.length();

                        if(count != 0){
                            bottomNavigationView.getOrCreateBadge(R.id.fragmentSellerOrders).setNumber(count);
                        } else {
                            bottomNavigationView.getOrCreateBadge(R.id.fragmentSellerOrders).clearNumber();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onResponse: " + error.getMessage());
            }
        }
        );
        jsonArrayRequest.setTag("CANCEL");
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll("CANCEL");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

}