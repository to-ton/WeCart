package com.lobomarket.wecart.UserHomeScreen;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.lobomarket.wecart.AgentHomeScreen.AgentActivity;
import com.lobomarket.wecart.AgentHomeScreen.AgentGroceryList;
import com.lobomarket.wecart.AgentHomeScreen.ScanSellerCode;
import com.lobomarket.wecart.UserHomeScreen.TrackOrderList;
import com.lobomarket.wecart.UserHomeScreen.TrackOrderList;
import com.lobomarket.wecart.CustomAdapters.AgentOrderListCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.OrderBreakDownAgentCustomAdapter;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.Models.AgentCustomerList;
import com.lobomarket.wecart.Models.OrderBreakdown;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class TrackOrderList extends AppCompatActivity implements OrderBreakDownAgentCustomAdapter.OnItemClickListener {

    Button back, btnScan;
    private RecyclerView recyclerView;
    private OrderBreakDownAgentCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<OrderBreakdown> breakdowns;
    private TextView noResult, agent, number;
    RequestQueue requestQueue;
    String agentUsername;
    LoadingDialog loadingDialog;
    TextView finalAmount, mop;
    SwipeRefreshLayout swipeRefreshLayout;

    String userName, trackingID, txtAgent, finalAmountPrice, qrKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order_list);
        try {
            swipeRefreshLayout = findViewById(R.id.order_track_Refresh);
            finalAmount = findViewById(R.id.finalTotalOrderSummary_track);
            loadingDialog = new LoadingDialog(TrackOrderList.this);
            recyclerView = findViewById(R.id.order_list_track);
            breakdowns = new ArrayList<>();
            mop = findViewById(R.id.mop_track);

            agent = findViewById(R.id.agentFname);
            number = findViewById(R.id.agentContactDetails);

            layoutManager = new LinearLayoutManager(TrackOrderList.this);
            cAdapter = new OrderBreakDownAgentCustomAdapter(TrackOrderList.this, breakdowns);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            back = findViewById(R.id.btnTrackOrderToHome);
            btnScan = findViewById(R.id.btnScanBuyer);

            Intent intent = getIntent();
            userName = intent.getStringExtra("buyerusername");
            trackingID = intent.getStringExtra("trackingID");
            finalAmountPrice = intent.getStringExtra("finalTotal");

            double total = Double.parseDouble(finalAmountPrice);
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            finalAmount.setText("₱" + formatter.format(total));


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

            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ScanOptions options = new ScanOptions();
                    options.setOrientationLocked(false);
                    options.setCaptureActivity(ScanSellerCode.class);
                    barcodeLauncher.launch(options);
                }
            });
        } catch (Exception e){
            Log.e("TrackOrder", "exception", e);
            Toast.makeText(TrackOrderList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkInternet() {
        try {
            requestQueue = Volley.newRequestQueue(TrackOrderList.this);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            showSummary();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    Toast.makeText(TrackOrderList.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("TrackOrder", "exception", e);
            Toast.makeText(TrackOrderList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void showSummary() {
        try {
            String txtTracking = null;
            try {

                txtTracking = URLEncoder.encode(trackingID.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/show-orders.php?track="+ txtTracking +"";

            breakdowns.clear();
            RequestQueue requestQueue = Volley.newRequestQueue(TrackOrderList.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            for(int b = 0; b < response.length(); b++){
                                try{
                                    JSONObject obj = response.getJSONObject(b);

                                    OrderBreakdown k = new OrderBreakdown();
                                    k.setProductName(obj.getString("product_name"));
                                    k.setQuantity(obj.getString("quantity"));
                                    k.setProductPrice(obj.getString("total"));
                                    k.setStoreName(obj.getString("store_name"));
                                    k.setDeliveryStatus(obj.getString("delivery_status"));
                                    txtAgent = obj.getString("agent");

                                    showAgent();
                                    breakdowns.add(k);

                                    breakdowns.sort(new Comparator<OrderBreakdown>() {
                                        @Override
                                        public int compare(OrderBreakdown orderSummary, OrderBreakdown t1) {
                                            try {
                                                return (orderSummary.getStoreName().toLowerCase().compareTo(t1.getStoreName().toLowerCase()));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                return 0;
                                            }
                                        }
                                    });

                                    JSONObject jsonObject = response.getJSONObject(0);


                                    if (jsonObject.getString("mode_of_payment").equals("cod")){
                                        btnScan.setVisibility(View.GONE);
                                        mop.setText("Cash on Delivery");
                                    } else {
                                        btnScan.setVisibility(View.VISIBLE);
                                        mop.setText("Pick up");
                                    }

                                    swipeRefreshLayout.setRefreshing(false);

                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }

                            cAdapter.updateDataSet(breakdowns);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "erro: " + error.getMessage());
                    Toast.makeText(TrackOrderList.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("TrackOrder", "exception", e);
            Toast.makeText(TrackOrderList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAgent(){
        try {
            String txtbuyer = null;
            try {
                txtbuyer = URLEncoder.encode(txtAgent, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/profile_info.php?username="+ txtbuyer +"";
            RequestQueue requestQueue = Volley.newRequestQueue(TrackOrderList.this);
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject userObject = response.getJSONObject(0);

                                number.setText(userObject.getString("contact_num"));
                                agent.setText(userObject.getString("full_name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", error.getMessage().toString());
                    agent.setText("No available agent for pick up");
                    number.setVisibility(View.GONE);
                }
            }
            );
            jsonObjectRequest.setTag("CANCEL");
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.e("TrackOrder", "exception", e);
            Toast.makeText(TrackOrderList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll("CANCEL");
        }
    }

    @Override
    public void onItemClick(int position) {

    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("Track Order", "Cancelled scan");
                        Toast.makeText(TrackOrderList.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("Track Order", "Cancelled scan due to missing camera permission");
                        Toast.makeText(TrackOrderList.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("Track Order", "Scanned");
                    qrKey = result.getContents();
                    itemRecieved();
                    //Toast.makeText(TrackOrderList.this, "Scanned: " + qrKey, Toast.LENGTH_LONG).show();
                }
            });

    public void itemRecieved(){
        try {
            String txtTracking = null;
            String txtQRkey =null;
            String txtUsername = null;
            try {
                txtTracking = URLEncoder.encode(trackingID, "utf-8");
                txtQRkey = URLEncoder.encode(qrKey, "utf-8");
                txtUsername = URLEncoder.encode(userName.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            String url = "https://jarvis.danlyt.ninja/wecart-api/v1/deliver.php?buyer_delivered&buyer="+ txtUsername +"&tracking_id="+ txtTracking +"&key="+ txtQRkey +"";
            requestQueue = Volley.newRequestQueue(TrackOrderList.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            if(!(response.equals("{\"status\":\"Invalid key.\"}"))){
                                Toast.makeText(TrackOrderList.this, "Order Received", Toast.LENGTH_LONG).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(TrackOrderList.this, "Invalid", Toast.LENGTH_LONG).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    Toast.makeText(TrackOrderList.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Agent Breakdown", "exception", e);
            Toast.makeText(TrackOrderList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }
}