package com.lobomarket.wecart.UserHomeScreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.AgentHomeScreen.AgentActivity;
import com.lobomarket.wecart.CustomAdapters.AgentOrderListCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.TransactionHistoryCustomAdapter;
import com.lobomarket.wecart.Models.AgentCustomerList;
import com.lobomarket.wecart.Models.TransactionHistory;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class fragmentTransactionHistory extends Fragment {

    View view;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TransactionHistoryCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<TransactionHistory> orders;
    private TextView noResult;
    private int dataCount;
    RequestQueue requestQueue;
    String username;

    //Constraint layout variables
    ConstraintLayout transactionLoading, transactionNoInternet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            username = getArguments().getString("uname");

            //Constraint layout declaration
            transactionLoading = view.findViewById(R.id.loadingScreenTransaction);
            transactionNoInternet = view.findViewById(R.id.noInternetTransaction);


            recyclerView = view.findViewById(R.id.transaction_history_list);
            noResult = view.findViewById(R.id.textView39);
            orders = new ArrayList<>();
            swipeRefreshLayout = view.findViewById(R.id.historyRefresh);
            layoutManager = new LinearLayoutManager(getActivity());
            cAdapter = new TransactionHistoryCustomAdapter(getActivity(), orders);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });
        } catch (Exception e){
            Log.e("TransactionHistory", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

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
            swipeRefreshLayout.setRefreshing(false);
            transactionLoading.setVisibility(View.VISIBLE);
            String JSON_URL = "https://www.google.com/";
            requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet

                            transactionNoInternet.setVisibility(View.GONE);
                            showHistory();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    swipeRefreshLayout.setRefreshing(false);
                    transactionLoading.setVisibility(View.GONE);
                    transactionNoInternet.setVisibility(View.VISIBLE);
                }
            });
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Transaction History", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }

    }

    private void showHistory() {
        try {
            String txtbuyer = null;
            try {
                txtbuyer = URLEncoder.encode(username.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            orders.clear();
            String JSON_URL = "https://wecart.gq/wecart-api/show-orders.php?history="+ txtbuyer +"";
            requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            noResult.setVisibility(View.GONE);
                            cAdapter.clear();
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    TransactionHistory u = new TransactionHistory();

                                    u.setStoreName(userObject.getString("store_name"));
                                    u.setFinalTotal(userObject.getString("Final_Total"));
                                    u.setDate(userObject.getString("date"));


                                    orders.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(orders);
                            dataCount = cAdapter.getItemCount();
                            transactionLoading.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onResponse: " + error.getMessage());
                    transactionLoading.setVisibility(View.GONE);
                    noResult.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("MYAPP", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll("CANCEL");
        }
    }
}