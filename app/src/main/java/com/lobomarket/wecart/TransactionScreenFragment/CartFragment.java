package com.lobomarket.wecart.TransactionScreenFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.snackbar.Snackbar;
import com.lobomarket.wecart.CustomAdapters.ShopsCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.ViewCartCustomAdapter;
import com.lobomarket.wecart.Models.Shop;
import com.lobomarket.wecart.Models.ViewCartModel;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.UserHomeScreen.ProductDetails;
import com.lobomarket.wecart.UserHomeScreen.fragmentHomeScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.lobomarket.wecart.UserHomeScreen.ProductDetails.EXTRA_PRODUCT_USERNAME;
import static com.lobomarket.wecart.UserHomeScreen.ShopsScreen.EXTRA_PRODUCT_NAME;
import static com.lobomarket.wecart.UserHomeScreen.ShopsScreen.EXTRA_SHOP_USERNAME;
import static com.lobomarket.wecart.UserHomeScreen.UserHomeScreen.EXTRA_HOMESCREEN_USERNAME;

public class CartFragment extends Fragment implements ViewCartCustomAdapter.MyCallback {

    View view;
    NavController navController;
    Button checkout;
    TextView totalOrderPrice;

    private RecyclerView recyclerView;
    private ViewCartCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<ViewCartModel> cart;
    private int dataCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    RequestQueue requestQueue;
    Button btn, goAddCart;

    ConstraintLayout noProductsAvailabe, loadingLayout, noInternetLayout;

    String userAccountName;

    String overAllPrice, pricetext;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            navController = Navigation.findNavController(view);
            checkout = view.findViewById(R.id.btnCheckout);
            totalOrderPrice = view.findViewById(R.id.totalOrderPrice);
            swipeRefreshLayout = view.findViewById(R.id.cartRefresh);
            recyclerView = view.findViewById(R.id.cartList);
            cart = new ArrayList<>();
            btn = view.findViewById(R.id.btnCartToHome);
            noProductsAvailabe = view.findViewById(R.id.noProductsAvailabale);
            goAddCart = view.findViewById(R.id.goAddCart);
            loadingLayout = view.findViewById(R.id.loadingLayout);
            noInternetLayout = view.findViewById(R.id.noInternetLayout);

            layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
            cAdapter = new ViewCartCustomAdapter(getActivity(), cart);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(recyclerView);

            goAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });

            checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dataCount == 0){
                        Toast.makeText(getActivity(), "Cart is empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("buyerName", userAccountName);
                        navController.navigate(R.id.action_cartFragment_to_fragmentAgentSelector, bundle);
                    }
                }
            });
        } catch (Exception e){
            Log.e("Cart", "exception", e);

        }
    }

    private void refreshList() {
        try {
            requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            swipeRefreshLayout.setRefreshing(false);
                            loadingLayout.setVisibility(View.VISIBLE);
                            noInternetLayout.setVisibility(View.GONE);
                            showCart();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    swipeRefreshLayout.setRefreshing(false);
                    noInternetLayout.setVisibility(View.VISIBLE);
                }
            }
            );
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("Cart", "exception", e);

        }

    }

    private void showCart() {
        try {
            String txtuser = URLEncoder.encode(userAccountName.replace("'","\\'"), "utf-8");

            String JSON_URL = "https://wecart.gq/wecart-api/show-orders.php?iscart="+ txtuser +"";
            cart.clear();
            requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 1; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    ViewCartModel u = new ViewCartModel();
                                    u.setProductName(userObject.getString("product_name"));
                                    u.setTotal(userObject.getString("total"));
                                    u.setQuantity(userObject.getString("quantity"));
                                    u.setSeller(userObject.getString("seller"));
                                    u.setUsername(userObject.getString("username"));
                                    u.setStock(userObject.getString("stock"));
                                    u.setProductPrice(userObject.getString("product_price"));

                                    cart.add(u);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(cart);
                            dataCount = cAdapter.getItemCount();
                            showFinalTotalPrice();
                            noProductsAvailabe.setVisibility(View.GONE);
                            loadingLayout.setVisibility(View.GONE);
                            cAdapter.setString(CartFragment.this);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error Cart", "Exception", error);
                    loadingLayout.setVisibility(View.GONE);
                    noProductsAvailabe.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );
            jsonArrayRequest.setTag("CANCEL");
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Cart", "exception", e);


        }

    }

    public void loadUserName(){
        try {
            try {
                String homeScreen = getActivity().getIntent().getStringExtra(EXTRA_HOMESCREEN_USERNAME);
                String product = getActivity().getIntent().getStringExtra(EXTRA_PRODUCT_USERNAME);
                String shop = getActivity().getIntent().getStringExtra(EXTRA_SHOP_USERNAME);

                HashMap<String, String> username = new HashMap<String, String>();
                username.put("user1", homeScreen);
                username.put("user2", product);
                username.put("user3", shop);

                if(username.get("user1") == null && username.get("user2") == null){
                    userAccountName = username.get("user3");

                } else if(username.get("user2") == null && username.get("user3") == null){
                    userAccountName = username.get("user1");

                } else if(username.get("user1") == null && username.get("user3") == null){
                    userAccountName = username.get("user2");
                } else {
                    userAccountName = "hello";
                }

                Log.v("TEST", "USername: " + userAccountName);
            } catch (NullPointerException e){
                Log.e("TEST", "exception ", e);
            }
        } catch (Exception e) {
            Log.e("Cart", "exception", e);

        }


    }

    public void showFinalTotalPrice(){
        try {
            String uname = null;
            try {
                uname = URLEncoder.encode(userAccountName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/show-orders.php?iscart="+ uname +"";
            requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject userObject = response.getJSONObject(0);
                                overAllPrice = userObject.getString("Final_Total");

                                double price = Double.parseDouble(overAllPrice);
                                DecimalFormat formatter = new DecimalFormat("#,###.00");
                                totalOrderPrice.setText("₱" + formatter.format(price));


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
            jsonObjectRequest.setTag("CANCEL");
            requestQueue.add(jsonObjectRequest);
            swipeRefreshLayout.setRefreshing(false);
        } catch (Exception e){
            Log.e("Cart", "exception", e);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        overAllPrice = null;

        loadUserName();

        refreshList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll("CANCEL");
        }
    }


    @Override
    public void addQuantity(double newPrice) {
        try {
            String amountTotal = totalOrderPrice.getText().toString();
            amountTotal = amountTotal.replaceAll("[^\\d.]", "");
            double amount = Double.parseDouble(amountTotal);
            amount += newPrice;
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            totalOrderPrice.setText("₱" + formatter.format(amount));
            //Toast.makeText(getActivity(), "Overall Price: " + amount, Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Log.e("Cart", "exception", e);

        }
    }

    @Override
    public void subtractQuantity(double newPrice) {
        try {
            String amountTotal = totalOrderPrice.getText().toString();
            amountTotal = amountTotal.replaceAll("[^\\d.]", "");
            double amount = Double.parseDouble(amountTotal);
            amount -= newPrice;
            DecimalFormat formatter = new DecimalFormat("#,###.00");

            if(!(amount <= 0.0)){
                totalOrderPrice.setText("₱" + formatter.format(amount));
            } else{
                noProductsAvailabe.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            Log.e("Cart", "exception", e);

        }
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            try {
                ViewCartModel item = cart.get(viewHolder.getAdapterPosition());

                String txtBuyer = null;
                String txtSeller = null;
                String txtProduct = null;
                String quantity = null;
                try {
                    txtBuyer = URLEncoder.encode(item.getUsername().replace("'","\\'"), "utf-8");
                    txtSeller = URLEncoder.encode(item.getSeller().replace("'","\\'"), "utf-8");
                    txtProduct = URLEncoder.encode(item.getProductName().replace("'","\\'"), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String url = "https://wecart.gq/wecart-api/delete.php?buyer_user=" + txtBuyer + "&seller_user=" + txtSeller + "&product=" + txtProduct + "";
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //With internet
                                Log.v("TEST", "Deleted " + response);
                                Toast.makeText(getActivity(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                                String amountTotal = totalOrderPrice.getText().toString();
                                amountTotal = amountTotal.replaceAll("[^\\d.]", "");
                                double amount = Double.parseDouble(amountTotal);
                                double getTotalItemPrice = Double.parseDouble(item.getTotal());

                                amount -= getTotalItemPrice;
                                DecimalFormat formatter = new DecimalFormat("#,###.00");
                                totalOrderPrice.setText("₱" + formatter.format(amount));
                                cart.remove(viewHolder.getAdapterPosition());
                                cAdapter.notifyDataSetChanged();

                                if(amount <= 0){
                                    noProductsAvailabe.setVisibility(View.VISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //No internet
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                );
                stringRequest.setTag("CANCEL");
                requestQueue.add(stringRequest);

            } catch (Exception e){
                Log.e("Cart Slider", "Exception", e);
            }
        }
    };
}