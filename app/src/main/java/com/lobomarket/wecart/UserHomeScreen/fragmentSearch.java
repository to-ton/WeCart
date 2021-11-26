package com.lobomarket.wecart.UserHomeScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.CustomAdapters.ProductsCustomAdapter;
import com.lobomarket.wecart.Models.Product;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class fragmentSearch extends Fragment implements ProductsCustomAdapter.OnItemClickListener {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_search, container, false);
    }

    EditText search;

    TextWatcher searchTxt = null;
    String searchQuery, buyerUsername;
    private RecyclerView recyclerView;
    private ProductsCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Product> product;
    private int dataCount;
    RequestQueue requestQueue;

    ConstraintLayout constraintLayout, loading;
    ConstraintLayout layout;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            search = view.findViewById(R.id.search);
            loading = view.findViewById(R.id.loadingSearch);
            showKeyboard(search);
            recyclerView = view.findViewById(R.id.searchList);
            product = new ArrayList<>();
            constraintLayout = view.findViewById(R.id.no_internet);
            buyerUsername = getArguments().getString("uname");
            layout = view.findViewById(R.id.notFound);


            layoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
            cAdapter = new ProductsCustomAdapter(getActivity(), product);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            searchTxt = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    product.clear();
                    loading.setVisibility(View.VISIBLE);
                    cAdapter.notifyDataSetChanged(); // let your adapter know about the changes and reload view.
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(cAdapter);
                    searchQuery = search.getText().toString();
                    layout.setVisibility(View.GONE);


                    if(search.getText().length() == 0){
                        cAdapter.clear();
                        loading.setVisibility(View.GONE);
                        layout.setVisibility(View.GONE);
                        product.clear();
                        cAdapter.updateDataSet(product);
                        layout.setVisibility(View.VISIBLE);
                    }
                    checkInternet2();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };

            search.addTextChangedListener(searchTxt);

            search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        checkInternet();
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e){
            Log.e("Search", "exception", e);
        }
    }

    public void showKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onItemClick(int position) {
        try {
            Product clickedItem = product.get(position);
            String productname = clickedItem.getProductName();
            String description = clickedItem.getDescription();
            String price = clickedItem.getProductPrice();
            String image = clickedItem.getProductPhoto();

            Intent intent = new Intent(getActivity(), ProductDetails.class);

            intent.putExtra("EXTRA_PRODUCT_NAME", productname);
            intent.putExtra("EXTRA_DESCRIPTION", description);
            intent.putExtra("EXTRA_PRICE", price);
            intent.putExtra("EXTRA_PHOTO", image);
            intent.putExtra("EXTRA_SELLER_USER", clickedItem.getUsername());
            intent.putExtra("EXTRA_BUYER", buyerUsername);
            intent.putExtra("EXTRA_STOCK", clickedItem.getStock());


            startActivity(intent);
        } catch (Exception e){
            Log.e("Search", "exception", e);
        }

    }

    private void checkInternet() {
        try {
            loading.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            search();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    loading.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                }
            }
            );
            stringRequest.setTag("CANCEL");

            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("Search", "exception", e);
        }

    }

    private void checkInternet2() {
        try {
            loading.setVisibility(View.VISIBLE);
            requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    loading.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                }
            }
            );
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("Search", "exception", e);
        }

    }

    private void search(){
        try {
            String txtquery = null;
            try {
                txtquery = URLEncoder.encode(searchQuery.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/search.php?product="+ txtquery +"";
            product.clear();
            requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            loading.setVisibility(View.GONE);
                            layout.setVisibility(View.GONE);
                            cAdapter.clear();
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    Product u = new Product();
                                    u.setProductName(userObject.getString("product_name"));
                                    u.setProductPrice(userObject.getString("product_price"));
                                    u.setProductPhoto(userObject.getString("product_image"));
                                    u.setDescription(userObject.getString("description"));
                                    u.setUsername(userObject.getString("username"));
                                    u.setStock(userObject.getString("stock"));

                                    product.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                            cAdapter.updateDataSet(product);
                            dataCount = cAdapter.getItemCount();
                            cAdapter.setOnItemClickListener(fragmentSearch.this);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "error" + error.getMessage());
                    Log.v("MESSAGE", searchQuery);
                    loading.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    product.clear();
                    cAdapter.updateDataSet(product);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Search", "exception", e);
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