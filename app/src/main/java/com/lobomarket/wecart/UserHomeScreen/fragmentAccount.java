package com.lobomarket.wecart.UserHomeScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.lobomarket.wecart.Models.Users;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class fragmentAccount extends Fragment {

    View view;

    private TextView name, uName, pass, contact, address;
    private Button btnEdit;
    private ImageView photo;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Users u;
    String txtbrgy, txtSitio, txtstreet, txtEmail, img;
    private CoordinatorLayout coordinatorLayout;

    //Constraint layout variables
    ConstraintLayout accountLoading, accountNoInternet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
//Constraint layout declaration
            accountLoading = view.findViewById(R.id.loadingScreenAccount);
            accountNoInternet = view.findViewById(R.id.noInternetAccount);

            name = view.findViewById(R.id.txtName);
            uName = view.findViewById(R.id.txtUsern);
            pass= view.findViewById(R.id.txtPass);
            contact = view.findViewById(R.id.txtContact);
            address =  view.findViewById(R.id.Address);
            btnEdit = view.findViewById(R.id.btnEdit);
            photo = view.findViewById(R.id.userPhoto);
            swipeRefreshLayout = view.findViewById(R.id.accountRefreshLayout);
            coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
            txtbrgy = null;
            txtSitio = null;
            txtstreet = null;
            txtEmail = null;
            img = null;

            u = new Users();
            //refreshList();

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(name.getText().toString().equals(". . . . . .")){
                        Toast.makeText(getActivity(), "Please wait", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getActivity(), EditAccountDetails.class);
                        intent.putExtra("name", u.getFname());
                        intent.putExtra("username", u.getUsername());
                        intent.putExtra("email", u.getEmail());
                        intent.putExtra("sitio", txtSitio);
                        intent.putExtra("brgy", txtbrgy);
                        intent.putExtra("street", txtstreet);
                        intent.putExtra("contact", u.getContact());
                        intent.putExtra("img", img);

                        startActivity(intent);
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
            Log.e("Account", "exception", e);

        }


    }

    private void showUser() {
        try {
            String uname = null;
            try {
                uname = URLEncoder.encode(getArguments().getString("uName").replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https:// jarvis.danlyt.ninja/wecart-api/v1/profile_info.php?username="+ uname +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject userObject = response.getJSONObject(0);


                                u.setFname(userObject.getString("full_name"));
                                u.setUsername(userObject.getString("username"));
                                u.setPassword(userObject.getString("password"));
                                u.setContact(userObject.getString("contact_num"));
                                u.setEmail(userObject.getString("contact_email"));

                                txtbrgy = userObject.getString("brgy");
                                txtSitio = userObject.getString("sitio");
                                txtstreet = userObject.getString("street");
                                img = userObject.getString("user_profile_image");

                                u.setAddress(txtstreet + ", " + txtSitio + ", " + txtbrgy);
                                u.setUserImg(img);


                                setData();
                                accountLoading.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "Error: " + error.getMessage());
                    accountLoading.setVisibility(View.GONE);
                    accountNoInternet.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            requestQueue.add(jsonObjectRequest);
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e){
            Log.e("Account", "exception", e);


        }

    }

    private void setData() {
        try{
            Picasso.get().load(img).into(photo);
            name.setText(u.getFname());
            uName.setText(u.getUsername());
            pass.setText(u.getEmail());
            contact.setText(u.getContact());
            address.setText(u.getAddress());
        } catch (Exception e){
            Log.e("Account", "exception", e);

        }
    }

    private void refreshList() {
        try{
            String uname = null;
            try {
                uname = URLEncoder.encode(getArguments().getString("uName"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
            accountLoading.setVisibility(View.VISIBLE);
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/profile_info.php?username="+ uname +"";
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            swipeRefreshLayout.setRefreshing(false);
                            accountNoInternet.setVisibility(View.GONE);
                            showUser();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    swipeRefreshLayout.setRefreshing(false);
                    accountLoading.setVisibility(View.GONE);
                    accountNoInternet.setVisibility(View.VISIBLE);
                }
            }
            );

            Volley.newRequestQueue(getActivity()).add(stringRequest);
        }catch (Exception e){
            Log.e("Account", "exception", e);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.v("TEST", "onresume");
        refreshList();
    }
}