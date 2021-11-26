package com.lobomarket.wecart.AgentHomeScreen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.lobomarket.wecart.CustomAdapters.AgentOrderListCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.SellerOrderListCustomAdapter;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.LoginLogOutSession.SessionManagement;
import com.lobomarket.wecart.LoginSignUpScreen.LoginSignupScreen;
import com.lobomarket.wecart.Models.AgentCustomerList;
import com.lobomarket.wecart.Models.SellerOrderList;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.SellerHomeScreen.SellerOrderListActivity;
import com.lobomarket.wecart.SellerHomeScreen.fragmentSellerOrders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AgentActivity extends AppCompatActivity implements AgentOrderListCustomAdapter.OnItemClickListener {

    Button agentProfile;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private AgentOrderListCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<AgentCustomerList> orders;
    private TextView noResult;
    private int dataCount;
    RequestQueue requestQueue;
    String agentUsername;
    LoadingDialog loadingDialog;
    ConstraintLayout noOrdersAgent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);
        try{
            Intent intent = getIntent();
            agentUsername = intent.getStringExtra("username");
            noResult = findViewById(R.id.textView33);
            //agent = findViewById(R.id.agent_logout);
            loadingDialog = new LoadingDialog(AgentActivity.this);
            noOrdersAgent = findViewById(R.id.noOrdersAgent);
            recyclerView = findViewById(R.id.agentCustomerList);
            orders = new ArrayList<>();
            swipeRefreshLayout = findViewById(R.id.swipeAgentCustomer);
            layoutManager = new LinearLayoutManager(AgentActivity.this);
            cAdapter = new AgentOrderListCustomAdapter(AgentActivity.this, orders);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);
            agentProfile = findViewById(R.id.agent_profile);




            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });

            agentProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent1 = new Intent(AgentActivity.this, AgentAccount.class);
                        intent1.putExtra("agent", agentUsername);
                        startActivity(intent1);
                    } catch (Exception e){
                        Toast.makeText(AgentActivity.this, "Please wait", Toast.LENGTH_LONG).show();
                    }

                }
            });

        } catch (Exception e){
            Log.e("Agent Activity", "exception", e);
            Toast.makeText(AgentActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }

    }



    private void refreshList() {
        try {
            swipeRefreshLayout.setRefreshing(true);
            String JSON_URL = "https://www.google.com/";
            requestQueue = Volley.newRequestQueue(AgentActivity.this);
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
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(AgentActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Agent Activity", "exception", e);
            Toast.makeText(AgentActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void showUser() {
        try {
            String txtbuyer = null;
            try {
                txtbuyer = URLEncoder.encode(agentUsername.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            orders.clear();
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/show-orders.php?agent="+ txtbuyer +"";
            requestQueue = Volley.newRequestQueue(AgentActivity.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            cAdapter.clear();
                            noOrdersAgent.setVisibility(View.GONE);
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    AgentCustomerList u = new AgentCustomerList();

                                    u.setStatus(userObject.getString("status"));
                                    u.setCustomer(userObject.getString("Customer"));
                                    u.setFullName(userObject.getString("full_name"));
                                    u.setContactNum(userObject.getString("contact_num"));
                                    u.setCustomerAddress(userObject.getString("customer_address"));
                                    u.setModeOfPayment(userObject.getString("mode_of_payment"));
                                    u.setStoreName(userObject.getString("store_name"));
                                    u.setSeller(userObject.getString("seller"));
                                    u.setFinalTotal(userObject.getString("Final_Total"));
                                    u.setAgent(userObject.getString("agent"));
                                    u.setTrackingId(userObject.getString("tracking_id"));

                                    orders.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(orders);
                            cAdapter.setOnItemClickListener(AgentActivity.this);
                            dataCount = cAdapter.getItemCount();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onResponse: " + error.getMessage());

                    noOrdersAgent.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Agent Activity", "exception", e);
            Toast.makeText(AgentActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onItemClick(int position) {
        try {
            AgentCustomerList itemClicked = orders.get(position);
            Intent intent = new Intent(AgentActivity.this, AgentGroceryList.class);
            intent.putExtra("status", itemClicked.getStatus());
            intent.putExtra("Customer", itemClicked.getCustomer());
            intent.putExtra("full_name", itemClicked.getFullName());
            intent.putExtra("contact_num", itemClicked.getContactNum());
            intent.putExtra("customer_address", itemClicked.getCustomerAddress());
            intent.putExtra("mode_of_payment", itemClicked.getModeOfPayment());
            intent.putExtra("store_name", itemClicked.getStoreName());
            intent.putExtra("seller", itemClicked.getSeller());
            intent.putExtra("Final_Total", itemClicked.getFinalTotal());
            intent.putExtra("agent", itemClicked.getAgent());
            intent.putExtra("tracking_id", itemClicked.getTrackingId());
            startActivity(intent);
        } catch (Exception e) {
            Log.e("Agent Activity", "exception", e);
            Toast.makeText(AgentActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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