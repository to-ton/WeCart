package com.lobomarket.wecart.SellerHomeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.SellerHomeScreen.SellerOrderListActivity;
import com.lobomarket.wecart.SellerHomeScreen.SellerOrderListActivity;
import com.lobomarket.wecart.CustomAdapters.OrderBreakDownCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.SellerOrderListCustomAdapter;
import com.lobomarket.wecart.Models.OrderBreakdown;
import com.lobomarket.wecart.Models.SellerOrderList;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SellerOrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderBreakDownCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<OrderBreakdown> breakdowns;
    private TextView noResult;

    private ImageView qrCodePlaceHolder;
    private ProgressBar loadingQr;
    TextView buyerName, buyerDetails, totalOrder, paymentMethod, agentName, agentContact;
    Button btnPrepare, buttonBack, btnClose;

    String user, address, mop, final_total, trackingId, fullName, contact, agent, sellerName;

    RequestQueue requestQueue;

    int isReadyForPickUp = 0;

    LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_list);
        try{
            buyerName = findViewById(R.id.buyerName);
            buyerDetails = findViewById(R.id.buyerDetails);
            totalOrder = findViewById(R.id.totalOrderSummary);
            paymentMethod = findViewById(R.id.paymentMethod);
            agentName = findViewById(R.id.agentName);
            agentContact = findViewById(R.id.agentContactDetails);
            btnPrepare = findViewById(R.id.btnPreparing);
            recyclerView = findViewById(R.id.order_summary_card_lis);
            buttonBack = findViewById(R.id.buttonBack);
            breakdowns = new ArrayList<>();
            loadingDialog = new LoadingDialog(SellerOrderListActivity.this);
            qrCodePlaceHolder = findViewById(R.id.qrCodeImageView);
            loadingQr = findViewById(R.id.loadingQr);
            btnClose = findViewById(R.id.btnDone);


            Intent intent = getIntent();
            user = intent.getStringExtra("user");
            address = intent.getStringExtra("user_address");
            mop = intent.getStringExtra("mode_of_payment");
            final_total = intent.getStringExtra("Final_Total");
            agent = intent.getStringExtra("agent");
            trackingId = intent.getStringExtra("tracking_id");
            fullName = intent.getStringExtra("fullName");
            contact = intent.getStringExtra("contact");
            sellerName = intent.getStringExtra("sellerUsername");

            if(mop.equals("Pick up"))
                btnPrepare.setText("Ready for Pick up");


            buyerName.setText(fullName);
            buyerDetails.setText(contact + "\n" + address);
            double finalAmount = Double.parseDouble(final_total);
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            totalOrder.setText("₱"+formatter.format(finalAmount));
            paymentMethod.setText(mop);
            showAgent();




            layoutManager = new GridLayoutManager(SellerOrderListActivity.this, 1, GridLayoutManager.VERTICAL, false);
            cAdapter = new OrderBreakDownCustomAdapter(SellerOrderListActivity.this, breakdowns);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            checkInternet();

            buttonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });


            btnPrepare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    proceedReadyForPickUp();
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } catch(Exception e){
            Log.e("Order Breakdown", "exception", e);
            Toast.makeText(SellerOrderListActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void proceedReadyForPickUp() {
        try {
            loadingQr.setVisibility(View.VISIBLE);
            requestQueue = Volley.newRequestQueue(SellerOrderListActivity.this);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            generateQRCode();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    loadingQr.setVisibility(View.GONE);
                    Toast.makeText(SellerOrderListActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
            );
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Order Breakdown", "exception", e);
            Toast.makeText(SellerOrderListActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void generateQRCode() {
        try {
            String txtTracking = null;
            String txtseller = null;
            try {
                txtTracking = URLEncoder.encode(trackingId, "utf-8");
                txtseller = URLEncoder.encode(sellerName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/deliver.php?ship_ready&tracking_id="+ txtTracking +"&uname_1="+ txtseller +"";
            requestQueue = Volley.newRequestQueue(SellerOrderListActivity.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            getQRCode();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    loadingQr.setVisibility(View.GONE);
                    Toast.makeText(SellerOrderListActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e){
            Log.e("Order Breakdown", "exception", e);
            Toast.makeText(SellerOrderListActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }


    private void getQRCode() {
        try {
            String txtbuyer = null;
            String txtseller = null;
            try {
                txtbuyer = URLEncoder.encode(trackingId, "utf-8");
                txtseller = URLEncoder.encode(sellerName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/deliver.php?ship_ready&tracking_id="+ txtbuyer +"&seller="+ txtseller +"";

            requestQueue = Volley.newRequestQueue(SellerOrderListActivity.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try{
                                JSONObject obj = response.getJSONObject(0);

                                String qrCode = obj.getString("qr");

                                loadingQr.setVisibility(View.GONE);
                                Picasso.get().load(qrCode).into(qrCodePlaceHolder);
                                qrCodePlaceHolder.setVisibility(View.VISIBLE);
                                btnPrepare.setVisibility(View.GONE);
                                btnClose.setVisibility(View.VISIBLE);

                            } catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "erro: " + error.getMessage());
                    Toast.makeText(SellerOrderListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    loadingQr.setVisibility(View.GONE);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Order Breakdown", "exception", e);
            Toast.makeText(SellerOrderListActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkQrCode() {
        try {
            String txtbuyer = null;
            String txtseller = null;
            try {
                txtbuyer = URLEncoder.encode(trackingId, "utf-8");
                txtseller = URLEncoder.encode(sellerName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/deliver.php?seller="+ txtseller +"&tracking_id="+ txtbuyer +"";

            requestQueue = Volley.newRequestQueue(SellerOrderListActivity.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try{
                                JSONObject obj = response.getJSONObject(0);

                                String qrCode = obj.getString("qr");

                                if(!(qrCode.equals("null"))){
                                    btnClose.setVisibility(View.VISIBLE);
                                    getQRCode();
                                } else {
                                    btnPrepare.setVisibility(View.VISIBLE);
                                }

                            } catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "erro: " + error.getMessage());
                    Toast.makeText(SellerOrderListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            Log.e("Order Breakdown", "exception", e);
            Toast.makeText(SellerOrderListActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkInternet() {
        try {
            requestQueue = Volley.newRequestQueue(SellerOrderListActivity.this);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            checkQrCode();
                            showSummary();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    Toast.makeText(SellerOrderListActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
            );
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("Order Breakdown", "exception", e);
            Toast.makeText(SellerOrderListActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void showSummary() {
        try {
            String txtbuyer = null;
            String txtseller = null;
            try {
                txtbuyer = URLEncoder.encode(trackingId, "utf-8");
                txtseller = URLEncoder.encode(sellerName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/deliver.php?seller="+ txtseller +"&tracking_id="+ txtbuyer +"";

            breakdowns.clear();
            requestQueue = Volley.newRequestQueue(SellerOrderListActivity.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            for(int b = 1; b < response.length(); b++){
                                try{
                                    JSONObject obj = response.getJSONObject(b);

                                    OrderBreakdown k = new OrderBreakdown();
                                    k.setProductName(obj.getString("product_name"));
                                    k.setQuantity(obj.getString("quantity"));
                                    k.setProductPrice(obj.getString("total"));

                                    breakdowns.add(k);

                                    breakdowns.sort(new Comparator<OrderBreakdown>() {
                                        @Override
                                        public int compare(OrderBreakdown orderSummary, OrderBreakdown t1) {
                                            try {
                                                return (orderSummary.getProductName().toLowerCase().compareTo(t1.getProductName().toLowerCase()));
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
                            //dataCount = cAdapter.getItemCount();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "erro: " + error.getMessage());
                    Toast.makeText(SellerOrderListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            Log.e("Order Breakdown", "exception", e);
            Toast.makeText(SellerOrderListActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    public void showAgent(){
        try {
            String txtbuyer = null;
            try {
                txtbuyer = URLEncoder.encode(agent.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/profile_info.php?username="+ txtbuyer +"";
            requestQueue = Volley.newRequestQueue(SellerOrderListActivity.this);
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject userObject = response.getJSONObject(0);

                                agentContact.setText(userObject.getString("contact_num"));
                                agentName.setText(userObject.getString("full_name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", error.getMessage().toString());
                    agentName.setText("No available agent for pick up");
                    agentContact.setVisibility(View.GONE);
                }
            }
            );
            jsonObjectRequest.setTag("CANCEL");
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e("Order Breakdown", "exception", e);
            Toast.makeText(SellerOrderListActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}