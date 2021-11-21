package com.lobomarket.wecart.TransactionScreenFragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.CustomAdapters.OrderBreakDownCustomAdapter;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.Models.OrderBreakdown;
import com.lobomarket.wecart.Models.OrderSummary;
import com.lobomarket.wecart.CustomAdapters.OrderSummaryCustomAdapter;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.SellerHomeScreen.SellerOrderListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  fragmentTransaction extends Fragment {

    View view;

    private RecyclerView recyclerView;
    private OrderBreakDownCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<OrderBreakdown> breakdowns;
    private int dataCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    TextView finalTotalOrderSummary, address, agent, payment, number, shippingCost, txtShipping;
    CardView modeOfpayment;
    Button btnPayOrder;
    LoadingDialog loadingDialog;


    String buyer, overAllPrice, txtAddress, txtAgent, txtMop, txtMopFromAgent;
    int counter;

    Button btnTransactToAgent;

    //Constraint layout variables
    ConstraintLayout checkoutLoading, checkoutNoInternet;

    LinearLayout shippingCostLayout;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_place_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            counter = 0;
            navController = Navigation.findNavController(view);

            //Constraint layout declaration
            checkoutLoading = view.findViewById(R.id.loadingScreenCheckout);
            checkoutNoInternet = view.findViewById(R.id.noInternetCheckout);

            shippingCostLayout = view.findViewById(R.id.shippingCostLayout);

            btnTransactToAgent = view.findViewById(R.id.btnTransactToAgent);

            swipeRefreshLayout = view.findViewById(R.id.orderRefresh);
            recyclerView = view.findViewById(R.id.order_summary_card_lis);
            //summary = new ArrayList<>();
            breakdowns = new ArrayList<>();
            finalTotalOrderSummary = view.findViewById(R.id.finalTotalOrderSummary);
            address = view.findViewById(R.id.textAddress);
            payment = view.findViewById(R.id.mop);
            agent = view.findViewById(R.id.agentFname);
            number = view.findViewById(R.id.agentContactDetails);
            modeOfpayment = view.findViewById(R.id.payment_card_view);
            btnPayOrder = view.findViewById(R.id.btnPayOrder);
            loadingDialog = new LoadingDialog(getActivity());
            txtMop = "null";

            buyer = getArguments().getString("buyerName");
            //txtAgent= getArguments().getString("agent");

            shippingCost = view.findViewById(R.id.shippingCost);
            txtShipping = view.findViewById(R.id.txtShippingCost);


            layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
            cAdapter = new OrderBreakDownCustomAdapter(getActivity(), breakdowns);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            refreshList();

            txtMopFromAgent = getArguments().getString("payment");

            if(!(txtMopFromAgent == null)){
                txtMop = txtMopFromAgent;
                payment.setText("Cash on Delivery");
                //setMOP();
                showNewOrderTotal();
            }

            btnTransactToAgent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    counter = 1;
                    txtMop = "pickup";
                    setMOP();
                    getActivity().onBackPressed();
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });

            modeOfpayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(getActivity());
                    //We have added a title in the custom layout. So let's disable the default title.
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
                    dialog.setCancelable(true);
                    //Mention the name of the layout of your custom dialog.
                    dialog.setContentView(R.layout.mop_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    RadioButton cod = dialog.findViewById(R.id.cod);
                    RadioButton mop = dialog.findViewById(R.id.pickup);

                    cod.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            payment.setText("Cash on Delivery");
                            txtMop = "cod";
                            //setMOP();
                            showNewOrderTotal();
                            dialog.dismiss();

                            Bundle bundle = new Bundle();
                            bundle.putString("buyerName", buyer);
                            bundle.putString("payment", txtMop);
                            navController.navigate(R.id.action_fragmentTransaction_to_fragmentAgentSelector, bundle);
                        }
                    });

                    mop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            payment.setText("Pick Up");
                            txtMop = "pickup";
                            setMOP();
                            dialog.dismiss();
                            number.setVisibility(View.GONE);
                            agent.setText(R.string.no_agent);
                        }
                    });

                    dialog.show();
                }
            });

            btnPayOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(txtMop.equals("null")){
                        Toast.makeText(getActivity(), "Please choose you Mode of Payment", Toast.LENGTH_SHORT).show();
                    } else {
                        checkInternet();
                    }
                }
            });
        } catch (Exception e){
            Log.e("Place Order", "exception", e);


        }


    }

    private void placeOrder() {
        try {
            String txtbuyer = null;
            try {
                txtbuyer = URLEncoder.encode(buyer, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://wecart.gq/wecart-api/add-to-cart.php?action=place_order&username="+ txtbuyer +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest jsonArrayRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("MESSAGE", response);
                            loadingDialog.stopLoading();
                            showThankyouDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "error: " + error.getMessage());
                    loadingDialog.stopLoading();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Place Order", "exception", e);

        }

    }

    private void showThankyouDialog() {
        try {
            Dialog dialog = new Dialog(getActivity());
            //We have added a title in the custom layout. So let's disable the default title.
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
            dialog.setCancelable(false);
            //Mention the name of the layout of your custom dialog.
            dialog.setContentView(R.layout.thank_you_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button proceed = dialog.findViewById(R.id.btnToHome);

            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    getActivity().finish();
                }
            });

            dialog.show();
        } catch (Exception e){
            Log.e("Place Order", "exception", e);

        }

    }

    public void setMOP(){
        try {
            String txtbuyer = null;
            String mTxtmop = null;
            try {
                txtbuyer = URLEncoder.encode(buyer.replace("'","\\'"), "utf-8");
                mTxtmop = URLEncoder.encode(txtMop.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://wecart.gq/wecart-api/add-to-cart.php?action=order_summary&username="+ txtbuyer +"&mop="+ mTxtmop +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.POST,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            if (counter == 0) {
                                Toast.makeText(getActivity(), "Mode of Payment:" + txtMop, Toast.LENGTH_SHORT).show();
                            }
                            JSONObject userObject2 = null;
                            try {
                                userObject2 = response.getJSONObject(0);
                                overAllPrice = userObject2.getString("Final_Total");

                                double price = Double.parseDouble(overAllPrice);
                                DecimalFormat formatter = new DecimalFormat("#,###.00");
                                finalTotalOrderSummary.setText("₱" + formatter.format(price));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            shippingCostLayout.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "erro: " + error.getMessage());
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Place Order", "exception", e);

        }

    }

    private void checkInternet(){
        try {
            loadingDialog.startLoading("Please wait");
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            placeOrder();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    Toast.makeText(getActivity(), "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoading();
                }
            }
            );

            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e){
            Log.e("Place Order", "exception", e);

        }

    }

    private void refreshList() {
        try {
            swipeRefreshLayout.setRefreshing(false);
            checkoutLoading.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            showSummary();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    swipeRefreshLayout.setRefreshing(false);
                    checkoutLoading.setVisibility(View.GONE);
                    checkoutNoInternet.setVisibility(View.VISIBLE);
                }
            }
            );

            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e){
            Log.e("Place Order", "exception", e);

        }

    }

    private void showSummary() {
        try {
            String txtbuyer = null;
            try {
                txtbuyer = URLEncoder.encode(buyer.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://wecart.gq/wecart-api/add-to-cart.php?action=order_summary&username="+ txtbuyer +"&mop=null";

            breakdowns.clear();
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for(int b = 4; b < response.length(); b++){
                                try{
                                    JSONObject obj = response.getJSONObject(b);

                                    OrderBreakdown k = new OrderBreakdown();
                                    k.setProductName(obj.getString("product_name"));
                                    k.setQuantity(obj.getString("quantity"));
                                    k.setProductPrice(obj.getString("Total"));
                                    k.setStoreName(obj.getString("store_name"));

                                    breakdowns.add(k);

                                    breakdowns.sort(new Comparator<OrderBreakdown>() {
                                        @Override
                                        public int compare(OrderBreakdown orderSummary, OrderBreakdown t1) {
                                            try {
                                                return (orderSummary.getStoreName().toLowerCase().compareTo(t1.getStoreName().toLowerCase()));
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                                return 0;
                                            }
                                        }
                                    });

                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }

                            JSONObject userObject2 = null;
                            JSONObject userObject3 = null;
                            try {
                                userObject2 = response.getJSONObject(0);
                                userObject3 = response.getJSONObject(1);
                                String newPrice = userObject3.getString("Final_Total_with_shipping");
                                overAllPrice = userObject2.getString("Final_Total");

                                double price = Double.parseDouble(overAllPrice);
                                double price2 = Double.parseDouble(newPrice);

                                DecimalFormat formatter = new DecimalFormat("#,###.00");

                                if(!(txtMopFromAgent == null)){
                                    finalTotalOrderSummary.setText("₱" + formatter.format(price2));
                                } else {
                                    finalTotalOrderSummary.setText("₱" + formatter.format(price));
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONObject userObject = response.getJSONObject(3);
                                txtAddress = userObject.getString("user_address");
                                txtAgent = userObject.getString("agent");

                                address.setText(txtAddress);
                                showAgent();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(breakdowns);
                            dataCount = cAdapter.getItemCount();
                            checkoutLoading.setVisibility(View.GONE);


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "erro: " + error.getMessage());
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Place Order", "exception", e);

        }
    }

    public void showAgent(){
        try {
            String txtbuyer = null;
            try {
                txtbuyer = URLEncoder.encode(txtAgent.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://wecart.gq/wecart-api/profile_info.php?username="+ txtbuyer +"";
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

                                number.setText(userObject.getString("contact_num"));
                                agent.setText(userObject.getString("full_name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "Error: " + error.getMessage());
                    number.setVisibility(View.GONE);
                    agent.setText(R.string.no_agent);
                }
            }
            );
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.e("Place Order", "exception", e);

        }

    }


    public void showNewOrderTotal(){
        try {
            String txtbuyer = null;
            String mTxtmop = null;
            try {
                txtbuyer = URLEncoder.encode(buyer.replace("'","\\'"), "utf-8");
                mTxtmop = URLEncoder.encode(txtMop.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://wecart.gq/wecart-api/add-to-cart.php?action=order_summary&username="+ txtbuyer +"&mop="+ mTxtmop +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject userObject5 = null;
                            try {
                                userObject5 = response.getJSONObject(1);
                                overAllPrice = userObject5.getString("Final_Total_with_shipping");

                                double price = Double.parseDouble(overAllPrice);
                                DecimalFormat formatter = new DecimalFormat("#,###.00");
                                shippingCostLayout.setVisibility(View.VISIBLE);
                                finalTotalOrderSummary.setText("₱" + formatter.format(price));
                                Toast.makeText(getActivity(), "Mode of Payment:" + txtMop, Toast.LENGTH_SHORT).show();
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
            Log.e("Place Order", "exception", e);

        }
    }
}