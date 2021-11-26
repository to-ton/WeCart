package com.lobomarket.wecart.SellerHomeScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lobomarket.wecart.Models.Product;
import com.lobomarket.wecart.MyRecyclerScroll;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.CustomAdapters.SellerProductsCustomAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class fragmentSellerProducts extends Fragment implements SellerProductsCustomAdapter.OnItemClickListener {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_seller_products, container, false);
        return view;
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SellerProductsCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Product> product;

    private int dataCount;
    private FloatingActionButton addNewProd;
    private ImageView account;


    public static final String EXTRA_PRODUCT_NAME = "name";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_PRICE = "price";
    public static final String EXTRA_PHOTO = "photo";
    public static final String EXTRA_STOCK = "stock";

    String storeType, sellerName, imageUrl;

    private ConstraintLayout noResult;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            swipeRefreshLayout = view.findViewById(R.id.swipeSellerAccount);
            recyclerView = view.findViewById(R.id.productsList);
            product = new ArrayList<>();
            addNewProd = view.findViewById(R.id.btnAddNewProducts);
            account = view.findViewById(R.id.btnSellerAccountDetails);
            noResult = view.findViewById(R.id.noProductsAvailableSeller);

            layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
            cAdapter = new SellerProductsCustomAdapter(getActivity(), product);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            storeType = "null";
            sellerName = getArguments().getString("sellerName");


            //Toast.makeText(getActivity(), "Hello, " + sellerName + ", " + storeType, Toast.LENGTH_SHORT).show();

            recyclerView.setOnScrollListener(new MyRecyclerScroll() {
                @Override
                public void show() {
                    addNewProd.show();
                }

                @Override
                public void hide() {
                    addNewProd.hide();
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });

            addNewProd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        Intent intent = new Intent(getActivity(), AddNewProduct.class);
                        intent.putExtra("sellerName", sellerName);
                        intent.putExtra("storeType", storeType);

                        if(storeType.equals("null")){
                            Toast.makeText(getActivity(), "Please wait", Toast.LENGTH_LONG).show();
                        } else {
                            startActivity(intent);
                        }
                    } catch (NullPointerException e){
                        Toast.makeText(getActivity(), "Please wait", Toast.LENGTH_LONG).show();
                    }

                }
            });

            account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        Intent intent =new Intent(getActivity(), SellerShopAccount.class);
                        intent.putExtra("sellername", sellerName);

                        startActivity(intent);
                    } catch (RuntimeException e){
                        Toast.makeText(getActivity(), "Please wait", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e){
            Log.e("Seller Products", "exception", e);
        }
    }

    private void refreshList() {
        try {
            recyclerView.setVisibility(View.GONE);
            String JSON_URL = "https://www.google.com/";
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            showUser();
                            getStoreType();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );

            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e) {
            Log.e("Seller Products", "exception", e);

        }

    }

    private void showUser() {
        try {
            String txtseller = null;

            txtseller = URLEncoder.encode(sellerName.replace("'","\\'"), "utf-8");


            product.clear();
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/showproducts.php?seller="+ txtseller +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                recyclerView.setVisibility(View.VISIBLE);
                                cAdapter.clear();
                                noResult.setVisibility(View.GONE);
                                for (int i = 0; i < response.length(); i++){
                                    try {
                                        JSONObject userObject = response.getJSONObject(i);

                                        Product u = new Product();
                                        u.setProductName(userObject.getString("product_name"));
                                        u.setPrice(userObject.getInt("product_price"));
                                        u.setProductPhoto(userObject.getString("product_image"));
                                        u.setDescription(userObject.getString("description"));
                                        u.setStock(userObject.getString("stock"));
                                        u.setProduct_type(userObject.getString("product_type"));

                                        product.add(u);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                swipeRefreshLayout.setRefreshing(false);
                                cAdapter.updateDataSet(product);
                                cAdapter.setOnItemClickListener(fragmentSellerProducts.this);
                                dataCount = cAdapter.getItemCount();
                            } catch (Exception e) {
                                Log.e("Seller Products", "exception", e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onResponse: " + error.getMessage());
                    noResult.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );


            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            Log.e("Seller Products", "exception", e);
        }

    }

    @Override
    public void onItemClick(int position) {
        try {
            Intent intent = new Intent(getActivity(), EditDeleteProducts.class);
            try {
                Product clickedItem = product.get(position);

                String productname = clickedItem.getProductName();
                String description = clickedItem.getDescription();
                int price = clickedItem.getPrice();

                String image = clickedItem.getProductPhoto();
                String stock = clickedItem.getStock();

                //comment here
                intent.putExtra(EXTRA_PRODUCT_NAME, productname);
                intent.putExtra(EXTRA_DESCRIPTION, description);
                intent.putExtra(EXTRA_PRICE, price);
                intent.putExtra(EXTRA_PHOTO, image);
                intent.putExtra(EXTRA_STOCK, stock);
                intent.putExtra("seller", sellerName);
                intent.putExtra("storeType", storeType);
                intent.putExtra("product_type", clickedItem.getProduct_type());

                startActivity(intent);
            } catch (IndexOutOfBoundsException e){
                Log.v("TEST", "Error, " + e.getMessage());
                Toast.makeText(getActivity(), "Please Try Again", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Seller Products", "exception", e);
        }

    }

    private void getStoreType(){
        try {
            String txt = URLEncoder.encode(sellerName.replace("'","\\'"), "utf-8");

            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/profile_info.php?username="+ txt +"";
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

                                storeType = userObject.getString("store_type");
                                imageUrl = userObject.getString("user_profile_image");

                                if(!(imageUrl.equals("null"))){
                                    Picasso.get().load(imageUrl).into(account);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "error: " + error.getMessage());
                }
            }
            );
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){

        }

    }


    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        refreshList();
    }
}