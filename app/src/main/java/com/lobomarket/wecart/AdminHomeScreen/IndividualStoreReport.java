package com.lobomarket.wecart.AdminHomeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.CustomAdapters.BestProductCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.OrderBreakDownAgentCustomAdapter;
import com.lobomarket.wecart.Models.Dashboard;
import com.lobomarket.wecart.Models.IndividualReport;
import com.lobomarket.wecart.Models.OrderBreakdown;
import com.lobomarket.wecart.Models.TopProducts;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndividualStoreReport extends AppCompatActivity {

    TextView store, inventory, orders, visitors;
    String sellerName;
    SwipeRefreshLayout swipeRefreshLayout;
    Button back;

    private RecyclerView recyclerView;
    private BestProductCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<TopProducts> breakdowns;

    RelativeLayout noTopProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_store_report);
        try {
            noTopProduct = findViewById(R.id.noTopProduct);

            recyclerView = findViewById(R.id.topProductsList); //not id
            breakdowns = new ArrayList<>();

            layoutManager = new GridLayoutManager(IndividualStoreReport.this, 1, GridLayoutManager.VERTICAL, false);
            cAdapter = new BestProductCustomAdapter(IndividualStoreReport.this, breakdowns);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            back = findViewById(R.id.btnSalesToDashboard);

            sellerName = getIntent().getStringExtra("seller");

            swipeRefreshLayout = findViewById(R.id.individualReport);

            store = findViewById(R.id.storeNameAdmin);
            inventory = findViewById(R.id.totalProducts);
            orders = findViewById(R.id.totalShopOrders);
            visitors = findViewById(R.id.totalDailyVisitors);


            store.setText(getIntent().getStringExtra("shop"));


            swipeRefreshLayout.setRefreshing(true);
            checkInternet();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    checkInternet();
                }
            });


            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } catch (Exception e){
            Log.e("Individual report", "exception", e);
            Toast.makeText(IndividualStoreReport.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }


    }

    private void checkInternet() {
        String txtSeller =null;
        try {
            txtSeller = URLEncoder.encode(sellerName.replace("'","\\'"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "https://jarvis.danlyt.ninja/wecart-api/v1/dashboard.php?seller="+ txtSeller +"";


        RequestQueue requestQueue = Volley.newRequestQueue(IndividualStoreReport.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.google.com/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //JsonObjectRequest will get the response (or result, i dunno what term i should use)
                        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                                Request.Method.GET,
                                url,
                                null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            JSONObject jsonObject = response.getJSONObject(0);


                                            IndividualReport t = new IndividualReport();

                                            t.setSale_inventory(jsonObject.getString("sale_inventory"));
                                            t.setOrder_fulfilled(jsonObject.getString("order_fulfilled"));
                                            t.setDaily_visitors(jsonObject.getString("daily_visitors"));

                                            inventory.setText(t.getSale_inventory());
                                            orders.setText(t.getOrder_fulfilled());
                                            visitors.setText(t.getDaily_visitors());


                                            getTopProducts();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.v("ERROR", error.toString());
                                        Toast.makeText(IndividualStoreReport.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                        );

                        requestQueue.add(jsonObjectRequest);
                    }

                }, new Response.ErrorListener() {
            //this will show the error message which there is no connection
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(IndividualStoreReport.this, "No internet Connection, please try again", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        requestQueue.add(stringRequest);
    }

    private void getTopProducts(){
        try {
            String txtagent = null;
            try {
                txtagent = URLEncoder.encode(sellerName.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/dashboard.php?seller="+ txtagent +"";

            breakdowns.clear();
            RequestQueue requestQueue = Volley.newRequestQueue(IndividualStoreReport.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for(int b = 1; b < 4; b++){
                                try{
                                    JSONObject obj = response.getJSONObject(b);

                                    TopProducts k = new TopProducts();
                                    k.setProduct_name(obj.getString("product_name"));
                                    k.setTotal_sold(obj.getString("total_sold"));
                                    k.setProduct_image(obj.getString("product_image"));

                                    breakdowns.add(k);
                                    noTopProduct.setVisibility(View.GONE);
                                    swipeRefreshLayout.setRefreshing(false);

                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }

                            cAdapter.updateDataSet(breakdowns);
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "erro: " + error.getMessage());
                    noTopProduct.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            Log.e("Agent Breakdown", "exception", e);
        }
    }
}