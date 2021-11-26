package com.lobomarket.wecart.AdminHomeScreen;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.CustomAdapters.ActiveShopSellerCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.AgentCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.SellerReportCustomAdapter;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.Models.AgentModel;
import com.lobomarket.wecart.Models.SellerModel;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class fragmentsSellerSalesReport extends Fragment implements SellerReportCustomAdapter.OnItemClickListener {

    View view;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SellerReportCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<SellerModel> seller;
    private int dataCount;
    ConstraintLayout noSellerListed;
    Button btnback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_seller_sales_report, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            swipeRefreshLayout = view.findViewById(R.id.dashboardRefresh);
            recyclerView = view.findViewById(R.id.sellerReport);
            seller = new ArrayList<>();
            noSellerListed = view.findViewById(R.id.noSellerListedReport);
            layoutManager = new LinearLayoutManager(getActivity());
            cAdapter = new SellerReportCustomAdapter(getActivity(), seller);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);
            btnback = view.findViewById(R.id.btnSalesToDashboard);

            btnback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });


        } catch (Exception e) {
            Log.e("Seller report", "Exception", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        refreshList();
    }

    private void refreshList() {
        try {
            String JSON_URL = "https://www.google.com/";
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
                    Toast.makeText(getActivity(), "No internet Connection, please try again", Toast.LENGTH_SHORT).show();
                }
            });
            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e){
            Log.e("Seller List", "exception", e);

        }
    }

    private void showUser() {
        try {
            seller.clear();
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/showusers.php?sellerlist";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            cAdapter.clear();
                            noSellerListed.setVisibility(View.GONE);
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    SellerModel u = new SellerModel();
                                    u.setShopName(userObject.getString("store_name"));
                                    u.setShopType(userObject.getString("store_type"));
                                    u.setUserName(userObject.getString("username"));
                                    u.setFullname(userObject.getString("full_name"));
                                    u.setContactNumber(userObject.getString("contact_num"));

                                    seller.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(seller);
                            cAdapter.setOnItemClickListener(fragmentsSellerSalesReport.this);
                            dataCount = cAdapter.getItemCount();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onResponse: " + error.getMessage());
                    noSellerListed.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            Log.e("Seller List", "exception", e);
        }
    }

    @Override
    public void onItemClick(int position) {
        try {
            try{
                SellerModel u = seller.get(position);

                Intent intent = new Intent(getActivity(), IndividualStoreReport.class);
                intent.putExtra("shop", u.getShopName());
                intent.putExtra("seller", u.getUserName());

                startActivity(intent);
            } catch (IndexOutOfBoundsException e){
                Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Log.e("Seller Report", "Exception", e);
        }

    }


}