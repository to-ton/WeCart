package com.lobomarket.wecart.UserHomeScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.Models.Shop;
import com.lobomarket.wecart.CustomAdapters.ShopsCustomAdapter;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class fragmentHomeScreen extends Fragment implements ShopsCustomAdapter.OnItemClickListener {

    View view;

    private RecyclerView recyclerView;
    private ShopsCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Shop> shopList;
    private static String JSON_URL = "https://wecart.gq/wecart-api/showusers.php?storelist";
    private int dataCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;

    public static final String EXTRA_SHOP_NAME = "name";
    public static final String EXTRA_TYPE = "description";
    public static final String EXTRA_SELLER = "seller name";
    public static final String EXTRA_BUYER_USERNAME = "buyerUserName";

    ConstraintLayout homeLoading, homeNoInternet;

    String buyerUsername;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        return view;
    }

    //with try catch
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            homeLoading = view.findViewById(R.id.loadingScreenHome);
            homeNoInternet = view.findViewById(R.id.noInternetHome);


            recyclerView = view.findViewById(R.id.shopList);
            shopList = new ArrayList<>();
            swipeRefreshLayout = view.findViewById(R.id.homeScreenRefresh);
            coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

            layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
            cAdapter = new ShopsCustomAdapter(getActivity(), shopList);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);


            Intent intent = getActivity().getIntent();
            buyerUsername = intent.getStringExtra("username");

            refreshList();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });
        } catch (Exception e){
            Log.e("Homescreen", "exception", e);
        }
    }

    //with try catch
    private void showUser() {
        try {
            shopList.clear();
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    Shop u = new Shop();
                                    u.setShopname(userObject.getString("store_name"));
                                    u.setTypeofShop(userObject.getString("store_type"));
                                    u.setShopImage(userObject.getString("user_profile_image"));
                                    u.setSellerName(userObject.getString("username"));

                                    shopList.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(shopList);
                            dataCount = cAdapter.getItemCount();
                            cAdapter.setOnItemClickListener(fragmentHomeScreen.this);
                            homeLoading.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "Error: " + error.getMessage());
                    homeLoading.setVisibility(View.GONE);
                    homeNoInternet.setVisibility(View.VISIBLE);
                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("MYAPP", "exception", e);
        }

    }

    //with try catch
    private void refreshList() {
        try{
            swipeRefreshLayout.setRefreshing(false);
            homeLoading.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            homeNoInternet.setVisibility(View.GONE);
                            showUser();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    homeNoInternet.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );

            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e) {
            Log.e("MYAPP", "exception", e);
        }
    }

    @Override
    public void onItemClick(int position) {
        try {
            Intent intent = new Intent(getActivity(), ShopsScreen.class);
            Shop clickedItem = shopList.get(position);

            intent.putExtra(EXTRA_SHOP_NAME, clickedItem.getShopname());
            intent.putExtra(EXTRA_TYPE, clickedItem.getTypeofShop());
            intent.putExtra(EXTRA_SELLER, clickedItem.getSellerName());
            intent.putExtra(EXTRA_BUYER_USERNAME, buyerUsername);
            intent.putExtra("shopBanner", clickedItem.getShopImage());

            startActivity(intent);
        } catch (Exception e){
            Log.e("MYAPP", "exception", e);
        }
    }
}