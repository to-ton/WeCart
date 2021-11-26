package com.lobomarket.wecart.UserHomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lobomarket.wecart.AgentHomeScreen.AgentActivity;
import com.lobomarket.wecart.CustomAdapters.AgentOrderListCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.TrackOrderListCustomAdapter;
import com.lobomarket.wecart.Models.AgentCustomerList;
import com.lobomarket.wecart.Models.TrackOrder;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class fragmentTrackOrder extends Fragment implements TrackOrderListCustomAdapter.OnItemClickListener {

    View view;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TrackOrderListCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<TrackOrder> orders;
    private TextView noResult;
    private int dataCount;
    RequestQueue requestQueue;
    String username;

    //Constraint layout variables
    ConstraintLayout trackOrderLoading, trackOrderNoInternet, trackOrderNoProducts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_track_order, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            //Constraint layout declaration
            trackOrderLoading = view.findViewById(R.id.loadingLayoutTrackOrder);
            trackOrderNoInternet = view.findViewById(R.id.noInternetLayoutTrack);
            trackOrderNoProducts = view.findViewById(R.id.noProductsAvailableTrack);

            recyclerView = view.findViewById(R.id.trackOrder);
            noResult = view.findViewById(R.id.textView38);
            orders = new ArrayList<>();
            swipeRefreshLayout = view.findViewById(R.id.trackOrderRefreshLayout);
            layoutManager = new LinearLayoutManager(getActivity());
            cAdapter = new TrackOrderListCustomAdapter(getActivity(), orders);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            username = getArguments().getString("uname");

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });
        } catch (Exception e){
            Log.e("TrackOrder", "exception", e);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataCount == 0){
            swipeRefreshLayout.setRefreshing(true);
        }
        refreshList();
    }

    private void refreshList() {
        try {
            swipeRefreshLayout.setRefreshing(false);
            trackOrderLoading.setVisibility(View.VISIBLE);
            String JSON_URL = "https://www.google.com/";
            requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            trackOrderNoInternet.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            showTracking();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    swipeRefreshLayout.setRefreshing(false);
                    trackOrderLoading.setVisibility(View.GONE);
                    trackOrderNoInternet.setVisibility(View.VISIBLE);
                }
            });
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("MYAPP", "exception", e);

        }

    }

    private void showTracking() {
        try {
            String txtbuyer = null;
            try {
                txtbuyer = URLEncoder.encode(username.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            orders.clear();
            String JSON_URL = "https://wecart.gq/wecart-api/show-orders.php?tracklist&username="+ txtbuyer +"";
            requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            trackOrderNoProducts.setVisibility(View.GONE);
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    TrackOrder u = new TrackOrder();

                                    u.setTrackingId(userObject.getString("tracking_id"));
                                    u.setDeliveryStatus(userObject.getString("delivery_status"));
                                    u.setFinalTotal(userObject.getString("final_total"));
                                    u.setDelivery_fee(userObject.getString("delivery_fee"));
                                    u.setAdd_fee(userObject.getString("add_fee"));

                                    orders.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(orders);
                            cAdapter.setOnItemClickListener(fragmentTrackOrder.this);
                            dataCount = cAdapter.getItemCount();
                            trackOrderLoading.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onResponse: " + error.getMessage());
                    trackOrderNoProducts.setVisibility(View.VISIBLE);
                    trackOrderNoInternet.setVisibility(View.GONE);
                    trackOrderLoading.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("MYAPP", "exception", e);


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
    public void onItemClick(int position) {
        try{
            try{
                TrackOrder clickedItem = orders.get(position);

                Intent intent = new Intent(getActivity(), TrackOrderList.class);
                intent.putExtra("trackingID", clickedItem.getTrackingId());
                intent.putExtra("buyerusername", username);
                intent.putExtra("status", clickedItem.getDeliveryStatus());
                intent.putExtra("finalTotal", clickedItem.getFinalTotal());
                intent.putExtra("devfee", clickedItem.getDelivery_fee());
                intent.putExtra("addfee", clickedItem.getAdd_fee());

                startActivity(intent);
            } catch (IndexOutOfBoundsException e){
                Log.e("Track", "exception", e);
                Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Log.e("Track", "exception", e);
        }

    }
}