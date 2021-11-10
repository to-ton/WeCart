package com.lobomarket.wecart.AgentHomeScreen;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.lobomarket.wecart.CustomAdapters.OrderBreakDownAgentCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.OrderBreakDownCustomAdapter;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.Models.OrderBreakdown;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.AgentHomeScreen.AgentGroceryList;
import com.lobomarket.wecart.AgentHomeScreen.AgentGroceryList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentGroceryList extends AppCompatActivity{

    private RecyclerView recyclerView;
    private OrderBreakDownAgentCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<OrderBreakdown> breakdowns;
    private TextView noResult;
    String qrKey;

    String statusTxt, customerUsername, fullName, contactNum, address, mop, storeNameTxt, sellerUsername, finalTotal, agentUsername, trackingID;
    TextView status, buyerName, buyerDetails, storeName, totalOrder;
    Button btnDelivered, back, scan;

    RequestQueue requestQueue;

    LoadingDialog loadingDialog;

    SwipeRefreshLayout swipeRefreshLayout;

    int dataCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_grocery_list);
        try{
            loadingDialog = new LoadingDialog(AgentGroceryList.this);
            buyerName = findViewById(R.id.buyerName);
            buyerDetails = findViewById(R.id.buyerDetails);
            storeName = findViewById(R.id.shopName);
            totalOrder = findViewById(R.id.totalOrderSummary);
            btnDelivered = findViewById(R.id.btnDelivered);
            back = findViewById(R.id.buttonBack);
            recyclerView = findViewById(R.id.order_summary_card_lis_agent);
            breakdowns = new ArrayList<>();
            scan = findViewById(R.id.btnScan);
            qrKey = null;
            swipeRefreshLayout = findViewById(R.id.swipeAgentCustomerBreakdown);
            btnDelivered.setEnabled(false);
            btnDelivered.setBackgroundResource(R.drawable.bg_btn_diable);

            Intent intent = getIntent();
            statusTxt = intent.getStringExtra("status");
            customerUsername = intent.getStringExtra("Customer");
            fullName = intent.getStringExtra("full_name");
            contactNum = intent.getStringExtra("contact_num");
            address = intent.getStringExtra("customer_address");
            mop = intent.getStringExtra("mode_of_payment");
            storeNameTxt = intent.getStringExtra("store_name");
            sellerUsername = intent.getStringExtra("seller");
            finalTotal = intent.getStringExtra("Final_Total");
            agentUsername = intent.getStringExtra("agent");
            trackingID = intent.getStringExtra("tracking_id");

            //status.setText(statusTxt);
            buyerName.setText(fullName);
            buyerDetails.setText(contactNum + "\n" + address);
            double total = Double.parseDouble(finalTotal);
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            totalOrder.setText("₱" + formatter.format(total));

            layoutManager = new GridLayoutManager(AgentGroceryList.this, 1, GridLayoutManager.VERTICAL, false);
            cAdapter = new OrderBreakDownAgentCustomAdapter(AgentGroceryList.this, breakdowns);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            if (dataCount == 0){
                swipeRefreshLayout.setRefreshing(true);
            }

            checkInternet();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    checkInternet();
                }
            });

            btnDelivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    proceedMarkAsDelivered();
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ScanOptions options = new ScanOptions();
                    options.setOrientationLocked(false);
                    options.setCaptureActivity(ScanSellerCode.class);
                    barcodeLauncher.launch(options);
                }
            });
        } catch(Exception e){
            Log.e("Agent Breakdown", "exception", e);
            Toast.makeText(AgentGroceryList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkInternet() {
        try {
            swipeRefreshLayout.setRefreshing(true);
            requestQueue = Volley.newRequestQueue(AgentGroceryList.this);
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
                    Toast.makeText(AgentGroceryList.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Agent Breakdown", "exception", e);
            Toast.makeText(AgentGroceryList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void proceedMarkAsDelivered() {
        try {
            loadingDialog.startLoading("Please wait");
            requestQueue = Volley.newRequestQueue(AgentGroceryList.this);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            markAsDelivered();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    Toast.makeText(AgentGroceryList.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
            );
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Agent Breakdown", "exception", e);
            Toast.makeText(AgentGroceryList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void markAsDelivered() {
        try {
            String txtTracking = null;
            try {
                txtTracking = URLEncoder.encode(trackingID.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/deliver.php?ship_success&tracking_id="+ txtTracking +"";
            requestQueue = Volley.newRequestQueue(AgentGroceryList.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            loadingDialog.stopLoading();
                            Toast.makeText(AgentGroceryList.this, "Status: Order Complete", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    loadingDialog.stopLoading();
                    Toast.makeText(AgentGroceryList.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Agent Breakdown", "exception", e);
            Toast.makeText(AgentGroceryList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void showSummary() {
        try {
            String txtagent = null;
            String txtTracking = null;
            try {
                txtagent = URLEncoder.encode(agentUsername.replace("'","\\'"), "utf-8");
                txtTracking = URLEncoder.encode(trackingID.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://wecart.gq/wecart-api/deliver.php?agent="+ txtagent +"&tracking_id="+ txtTracking +"";

            breakdowns.clear();
            RequestQueue requestQueue = Volley.newRequestQueue(AgentGroceryList.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            int statusCount;

                            ArrayList<String> countRepeat = new ArrayList<String>();
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    countRepeat.add(userObject.getString("delivery_status"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Map<String, Integer> hm = new HashMap<String, Integer>();
                            for (String a : countRepeat) {
                                Integer b = hm.get(a);
                                hm.put(a, (b == null) ? 1 : b + 1);
                            }
                            try{
                                statusCount = hm.get("Out For Delivery.");

                            } catch (Exception e){
                                statusCount = 0;
                            }

                            if(statusCount == response.length()){
                                btnDelivered.setEnabled(true);
                                btnDelivered.setBackgroundResource(R.drawable.bg_btn);
                            } else {
                                btnDelivered.setEnabled(false);
                                btnDelivered.setBackgroundResource(R.drawable.bg_btn_diable);
                            }

                            for(int b = 0; b < response.length(); b++){
                                try{
                                    JSONObject obj = response.getJSONObject(b);

                                    OrderBreakdown k = new OrderBreakdown();
                                    k.setProductName(obj.getString("product_name"));
                                    k.setQuantity(obj.getString("quantity"));
                                    k.setProductPrice(obj.getString("total"));
                                    k.setStoreName(obj.getString("store_name"));
                                    k.setDeliveryStatus(obj.getString("delivery_status"));
                                    k.setStall(obj.getString("stall_address"));

                                    breakdowns.add(k);

                                    breakdowns.sort(new Comparator<OrderBreakdown>() {
                                        @Override
                                        public int compare(OrderBreakdown orderSummary, OrderBreakdown t1) {
                                            try {
                                                return (orderSummary.getStoreName().toLowerCase().compareTo(t1.getStoreName().toLowerCase()));
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                                return 0;
                                            }
                                        }
                                    });

                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }

                            cAdapter.updateDataSet(breakdowns);
                            swipeRefreshLayout.setRefreshing(false);
                            dataCount = cAdapter.getItemCount();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "erro: " + error.getMessage());
                    Toast.makeText(AgentGroceryList.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            Log.e("Agent Breakdown", "exception", e);
            Toast.makeText(AgentGroceryList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll("CANCEL");
        }
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("AgentGroceryList", "Cancelled scan");
                        Toast.makeText(AgentGroceryList.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("AgentGroceryList", "Cancelled scan due to missing camera permission");
                        Toast.makeText(AgentGroceryList.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("AgentGroceryList", "Scanned");
                    qrKey = result.getContents();
                    outForDelivery();
                    //Toast.makeText(AgentGroceryList.this, "Scanned: " + qrKey, Toast.LENGTH_LONG).show();
                }
            });


    public void outForDelivery(){
        try {
            String txtTracking = null;
            String txtSellername = null;
            String txtQRkey =null;
            String txtAgent = null;
            try {
                txtTracking = URLEncoder.encode(trackingID, "utf-8");
                txtSellername = URLEncoder.encode(sellerUsername.replace("'","\\'"), "utf-8");
                txtQRkey = URLEncoder.encode(qrKey, "utf-8");
                txtAgent = URLEncoder.encode(agentUsername.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            String url = "https://wecart.gq/wecart-api/deliver.php?ship_now&rider="+ txtAgent +"&tracking_id="+ txtTracking +"&key="+ txtQRkey +"";
            requestQueue = Volley.newRequestQueue(AgentGroceryList.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            if(!(response.equals("{\"status\":\"Invalid key.\"}"))){
                                Toast.makeText(AgentGroceryList.this, "Success, Ride safely!", Toast.LENGTH_LONG).show();
                                checkInternet();
                            } else {
                                Toast.makeText(AgentGroceryList.this, "Invalid", Toast.LENGTH_LONG).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    Toast.makeText(AgentGroceryList.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Agent Breakdown", "exception", e);
            Toast.makeText(AgentGroceryList.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

}