package com.lobomarket.wecart.SellerHomeScreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.AdminHomeScreen.fragmentsActiveAgents;
import com.lobomarket.wecart.CustomAdapters.AgentCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.SellerOrderListCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.SellerProductsCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.ViewCartCustomAdapter;
import com.lobomarket.wecart.Models.AgentModel;
import com.lobomarket.wecart.Models.Product;
import com.lobomarket.wecart.Models.SellerOrderList;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class fragmentSellerOrders extends Fragment implements SellerOrderListCustomAdapter.OnItemClickListener {

    /*
    private UpdateOrderCount updateOrderCount = null;

    public interface UpdateOrderCount {
        void updateCount(int newOrderCount);
    }

    public void setUpdateOrderCount(UpdateOrderCount mUpdateOrderCount){
        this.updateOrderCount = mUpdateOrderCount;
    }
    public fragmentSellerOrders(){

    }
    */

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_seller_orders, container, false);
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SellerOrderListCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<SellerOrderList> orders;
    private ConstraintLayout noOrdersLayout;

    private int dataCount;
    private ToggleButton appTheme;

    String seller;

    RequestQueue requestQueue;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            swipeRefreshLayout = view.findViewById(R.id.orderSwipe);
            recyclerView = view.findViewById(R.id.ordersList);
            orders = new ArrayList<>();
            noOrdersLayout = view.findViewById(R.id.noOrdersLayout);

            seller = getActivity().getIntent().getStringExtra("username");

            Toast.makeText(getActivity(), "Hello, " + seller, Toast.LENGTH_SHORT).show();

            layoutManager = new LinearLayoutManager(getActivity());
            cAdapter = new SellerOrderListCustomAdapter(getActivity(), orders);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);



            //added toggleButton here
                appTheme = view.findViewById(R.id.toggleDayNight);

            //AppTheme Day/Night Mode
            SharedPreferences preferences = this.getActivity().getPreferences(Context.MODE_PRIVATE);
            boolean tgpref = preferences.getBoolean("tgpref", false);  //default is true

            appTheme.setChecked(tgpref);

            //Toggle Button Day/Night Mode
            appTheme.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("tgpref", appTheme.isChecked()); // value to store
                    editor.commit();
                }
            });

            //AppTheme Day/Night Mode
            appTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if(appTheme.isChecked()){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            });


            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });

        } catch (Exception e){
            Log.e("Seller Orders", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void refreshList() {
        try {
            swipeRefreshLayout.setRefreshing(true);
            String JSON_URL = "https://www.google.com/";
            requestQueue = Volley.newRequestQueue(getActivity());
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
                    Toast.makeText(getActivity(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Seller Orders", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }

    }

    private void showUser() {
        try {
            String txtbuyer = null;
            try {
                txtbuyer = URLEncoder.encode(seller.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            orders.clear();
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/show-orders.php?seller="+ txtbuyer +"";
            requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            noOrdersLayout.setVisibility(View.GONE);
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    SellerOrderList u = new SellerOrderList();

                                    u.setUser(userObject.getString("user"));
                                    u.setUserAddress(userObject.getString("user_address"));
                                    u.setModeOfPayment(userObject.getString("mode_of_payment"));
                                    u.setFinalTotal(userObject.getString("Final_Total"));
                                    u.setAgent(userObject.getString("agent"));
                                    u.setTrackingId(userObject.getString("tracking_id"));
                                    u.setFullName(userObject.getString("full_name"));
                                    u.setContact(userObject.getString("contact_num"));

                                    orders.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(orders);
                            cAdapter.setOnItemClickListener(fragmentSellerOrders.this);
                            dataCount = cAdapter.getItemCount();
                            /*
                            if (updateOrderCount != null) {
                                updateOrderCount.updateCount(dataCount);
                            } */
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onResponse: " + error.getMessage());
                    noOrdersLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            Log.e("Seller Orders", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClick(int position) {
        try {
            SellerOrderList itemClicked = orders.get(position);
            Intent intent = new Intent(getActivity(), SellerOrderListActivity.class);
            intent.putExtra("user", itemClicked.getUser());
            intent.putExtra("user_address", itemClicked.getUserAddress());
            intent.putExtra("mode_of_payment", itemClicked.getModeOfPayment());
            intent.putExtra("Final_Total", itemClicked.getFinalTotal());
            intent.putExtra("agent", itemClicked.getAgent());
            intent.putExtra("tracking_id", itemClicked.getTrackingId());
            intent.putExtra("fullName", itemClicked.getFullName());
            intent.putExtra("contact", itemClicked.getContact());
            intent.putExtra("sellerUsername", seller);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("Seller Orders", "exception", e);
            Toast.makeText(getActivity(), "No orders at the momment", Toast.LENGTH_SHORT).show();
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
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        refreshList();
    }
}