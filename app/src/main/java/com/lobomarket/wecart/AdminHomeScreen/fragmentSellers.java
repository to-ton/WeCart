package com.lobomarket.wecart.AdminHomeScreen;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
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
import com.lobomarket.wecart.CustomAdapters.ActiveShopSellerCustomAdapter;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.Models.SellerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class fragmentSellers extends Fragment implements ActiveShopSellerCustomAdapter.OnItemClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ActiveShopSellerCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<SellerModel> seller;
    private int dataCount;

    private TextView noResult;

    String username, user2;

    ConstraintLayout noSellerListed;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_active_sellers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            swipeRefreshLayout = view.findViewById(R.id.activeSellerRefresh);
            recyclerView = view.findViewById(R.id.sellerAdminList);
            seller = new ArrayList<>();
            noResult = view.findViewById(R.id.noseller);
            username = null;
            noSellerListed = view.findViewById(R.id.noSellerListed);
            layoutManager = new LinearLayoutManager(getActivity());
            cAdapter = new ActiveShopSellerCustomAdapter(getActivity(), seller);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);



            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });
        } catch (Exception e) {
            Log.e("Seller list", "exception", e);


        }

    }

    private void refreshList() {
        try {
            String JSON_URL = "https://wecart.gq/wecart-api/showusers.php?seller";
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
            String JSON_URL = "https://wecart.gq/wecart-api/showusers.php?sellerlist";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            cAdapter.clear();
                            noResult.setVisibility(View.GONE);
                            noSellerListed.setVisibility(View.GONE);
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    SellerModel u = new SellerModel();
                                    u.setShopName(userObject.getString("store_name"));
                                    u.setShopType(userObject.getString("store_type"));

                                    username = userObject.getString("username");
                                    u.setUserName(username);
                                    u.setFullname(userObject.getString("full_name"));
                                    u.setContactNumber(userObject.getString("contact_num"));

                                    seller.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(seller);
                            cAdapter.setOnItemClickListener(fragmentSellers.this);
                            dataCount = cAdapter.getItemCount();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onResponse: " + error.getMessage());
                    noResult.setVisibility(View.VISIBLE);
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

    Dialog dialog;
    LoadingDialog loadingDialog;
    @Override
    public void onItemClick(int position) {
        try{
            SellerModel clickedItem = seller.get(position);

            loadingDialog = new LoadingDialog(getActivity());
            dialog = new Dialog(getActivity());
            //We have added a title in the custom layout. So let's disable the default title.
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
            dialog.setCancelable(true);
            //Mention the name of the layout of your custom dialog.
            dialog.setContentView(R.layout.selleer_details);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //Initializing the views of the dialog.
            final TextView storeName = dialog.findViewById(R.id.storeName);
            final TextView shopType = dialog.findViewById(R.id.storeType);
            final TextView userName = dialog.findViewById(R.id.sellerUserName);
            final TextView fullName = dialog.findViewById(R.id.sellerFullName);
            final TextView contact = dialog.findViewById(R.id.sellerContactNum);

            Button deleteUser = dialog.findViewById(R.id.btnDeleteSeller);

            storeName.setText(clickedItem.getShopName());
            shopType.setText(clickedItem.getShopType());
            user2 = clickedItem.getUserName();
            userName.setText(user2);
            fullName.setText(clickedItem.getFullname());
            contact.setText(clickedItem.getContactNumber());

            deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    proceed();
                }
            });

            dialog.show();
        } catch (Exception e){
            Log.e("Seller List", "exception", e);

        }
    }



    private void deleteSellerAccount() {
        try {
            String uName = null;
            try {
                uName = URLEncoder.encode(user2.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/delete.php?seller="+ uName +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Toast.makeText(getActivity(), "Seller deleted", Toast.LENGTH_SHORT).show();

                            recyclerView.setAdapter(cAdapter);
                            refreshList();
                            loadingDialog.stopLoading();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoading();
                }
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Seller List", "exception", e);

        }

    }

    private void proceed() {
        try {
            loadingDialog.startLoading("Deleting");
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            deleteSellerAccount();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    loadingDialog.stopLoading();
                    Toast.makeText(getActivity(), "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e) {
            Log.e("Seller List", "exception", e);

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
}