package com.lobomarket.wecart.CategoryFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.UserHomeScreen.ProductDetails;
import com.lobomarket.wecart.CustomAdapters.ProductsCustomAdapter;
import com.lobomarket.wecart.Models.Product;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class fragmentAllProducts extends Fragment implements ProductsCustomAdapter.OnItemClickListener {

    View view;

    private RecyclerView recyclerView;
    private ProductsCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<Product> product;
    private int dataCount;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String EXTRA_PRODUCT_NAME = "name";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_PRICE = "price";
    public static final String EXTRA_PHOTO = "photo";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_all_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.allProducts);
        product = new ArrayList<>();
        swipeRefreshLayout = view.findViewById(R.id.allProductsRefresh);

        layoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        cAdapter = new ProductsCustomAdapter(getActivity(), product);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cAdapter);

        if (dataCount == 0){
            swipeRefreshLayout.setRefreshing(true);
        }

        refreshList();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
    }

    private void showUser() {
        String JSON_URL = "https://my-json-server.typicode.com/cubby02/Fake_API/vegetables";
        product.clear();
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

                                Product u = new Product();
                                u.setProductName(userObject.getString("product_name"));
                                u.setPrice(userObject.getInt("product_price"));
                                u.setProductPhoto(userObject.getString("product_image"));
                                u.setDescription(userObject.getString("description"));

                                product.add(u);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        cAdapter.updateDataSet(product);
                        dataCount = cAdapter.getItemCount();
                        cAdapter.setOnItemClickListener(fragmentAllProducts.this);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("MESSAGE", error.getMessage().toString());
            }
        }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void refreshList() {
        String JSON_URL = "https://my-json-server.typicode.com/cubby02/Fake_API/vegetables";
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
            }
        }
        );

        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    @Override
    public void onItemClick(int position) {
        Product clickedItem = product.get(position);
        String productname = clickedItem.getProductName();
        String description = clickedItem.getDescription();
        int price = clickedItem.getPrice();
        String image = clickedItem.getProductPhoto();

        Intent intent = new Intent(getActivity(), ProductDetails.class);

        intent.putExtra(EXTRA_PRODUCT_NAME, productname);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        intent.putExtra(EXTRA_PRICE, price);
        intent.putExtra(EXTRA_PHOTO, image);

        startActivity(intent);
    }
}