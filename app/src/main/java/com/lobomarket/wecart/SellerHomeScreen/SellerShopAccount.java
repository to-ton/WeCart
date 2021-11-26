package com.lobomarket.wecart.SellerHomeScreen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.lobomarket.wecart.SellerHomeScreen.SellerShopAccount;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.LoginSignUpScreen.LoginSignupScreen;
import com.lobomarket.wecart.Models.Users;
import com.lobomarket.wecart.LoginLogOutSession.SessionManagement;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SellerShopAccount extends AppCompatActivity {

    private TextView name, uName, email, contact, address, shopName, storeType;
    private Button btnEdit, btnback;
    private ImageView photo;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Users u;
    String txtbrgy, txtSitio, txtstreet, txtEmail, sellername, img;
    private CoordinatorLayout coordinatorLayout;

    RequestQueue requestQueue;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_account);
        try {
            name = findViewById(R.id.sellerName);
            uName = findViewById(R.id.sellerusernmae);
            email = findViewById(R.id.sellerEmail);
            contact = findViewById(R.id.sellerContact);
            address =  findViewById(R.id.sellerAddress);
            btnEdit = findViewById(R.id.editSellerAccount);
            photo = findViewById(R.id.sellerEditPhoto);
            swipeRefreshLayout = findViewById(R.id.swipeSellerAccount);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            shopName = findViewById(R.id.sellerShopName);
            storeType = findViewById(R.id.sellerStoreType);
            sellername = getIntent().getStringExtra("sellername");
            btnback = findViewById(R.id.button2);
            txtbrgy = null;
            txtSitio = null;
            txtstreet = null;
            txtEmail = null;
            img = null;
            requestQueue = Volley.newRequestQueue(SellerShopAccount.this);
            loadingDialog = new LoadingDialog(SellerShopAccount.this);

            swipeRefreshLayout.setRefreshing(true);

            u = new Users();
            refreshList();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SellerShopAccount.this, EditSellerShopAccount.class);
                    intent.putExtra("shopName", shopName.getText().toString());
                    intent.putExtra("sellerName", sellername);
                    intent.putExtra("sellerUsername", uName.getText().toString());
                    intent.putExtra("storeType", storeType.getText().toString());
                    intent.putExtra("fullName", name.getText().toString());
                    intent.putExtra("email", email.getText().toString());
                    intent.putExtra("contact", contact.getText().toString());
                    intent.putExtra("sitio", address.getText().toString());
                    intent.putExtra("img", img);


                    try{
                        if(shopName.getText().toString().equals(". . . . . .")){
                            Toast.makeText(SellerShopAccount.this, "Please wait", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(intent);
                        }
                    } catch (NullPointerException e){
                        Log.e("MYAPP", "exception", e);
                        Toast.makeText(SellerShopAccount.this, "Please wait", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            btnback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            Log.e("Seller Account", "exception", e);
            Toast.makeText(SellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void logoutSeller(View v){
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            logout();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to logout?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } catch (Exception e) {
            Log.e("Seller Account", "exception", e);
            Toast.makeText(SellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }

    }

    public void logout(){
        try {
            loadingDialog.startLoading("Logging out");
            String txtSeller =null;
            try {
                txtSeller = URLEncoder.encode(sellername.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/logout.php?username="+ txtSeller +"";

            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            Intent logout = new Intent(SellerShopAccount.this, LoginSignupScreen.class);
                            loadingDialog.stopLoading();
                            SessionManagement sessionManagement = new SessionManagement(SellerShopAccount.this);
                            sessionManagement.removeSession();
                            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logout);
                            finish();
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    Toast.makeText(SellerShopAccount.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoading();
                }
            }
            );

            postRequest.setTag("CANCEL");
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Seller Account", "exception", e);
            Toast.makeText(SellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void showUser() {
        try {
            String uname = null;
            try {
                uname = URLEncoder.encode(sellername.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/profile_info.php?username="+ uname +"";

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
                                u.setStoreType(userObject.getString("store_type"));
                                u.setShopName(userObject.getString("store_name"));


                                img = userObject.getString("user_profile_image");

                                u.setAddress(userObject.getString("sitio"));
                                u.setUserImg(img);


                                setData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", error.getMessage().toString());
                }
            }
            );
            requestQueue.add(jsonObjectRequest);
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            Log.e("Seller Account", "exception", e);
            Toast.makeText(SellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void setData() {
        try {
            uName.setText(u.getUsername());
            shopName.setText(u.getShopName());
            storeType.setText(u.getStoreType());

            if(!(u.getEmail().equals("null"))){
                email.setText(u.getEmail());
            }

            if (!(u.getEmail().equals("null"))){
                contact.setText(u.getContact());
            }

            if (!(u.getAddress().equals("null"))){
                address.setText(u.getAddress());
            }

            if (!(u.getFname().equals("null"))){
                name.setText(u.getFname());
            }

            if (!(u.getUserImg().equals("null"))){
                Picasso.get().load(u.getUserImg()).into(photo);
            }
        } catch (Exception e) {
            Log.e("Seller Account", "exception", e);
            Toast.makeText(SellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshList() {
        try {
            swipeRefreshLayout.setRefreshing(true);
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
                    showSnackbar();
                }
            }
            );

            Volley.newRequestQueue(SellerShopAccount.this).add(stringRequest);
        } catch (Exception e) {
            Log.e("Seller Account", "exception", e);
            Toast.makeText(SellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    public void showSnackbar() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "No internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshList();
                    }
                });

        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll("CANCEL");
        }
    }
}