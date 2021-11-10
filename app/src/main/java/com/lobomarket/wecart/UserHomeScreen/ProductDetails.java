package com.lobomarket.wecart.UserHomeScreen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.TransactionScreenFragment.TransactionScreen;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Base64;

import static com.lobomarket.wecart.UserHomeScreen.ShopsScreen.EXTRA_BUYER;
import static com.lobomarket.wecart.UserHomeScreen.ShopsScreen.EXTRA_SELLER_USER;
import static com.lobomarket.wecart.CategoryFragments.fragmentCateg1.EXTRA_DESCRIPTION;
import static com.lobomarket.wecart.CategoryFragments.fragmentCateg1.EXTRA_PHOTO;
import static com.lobomarket.wecart.CategoryFragments.fragmentCateg1.EXTRA_PRICE;
import static com.lobomarket.wecart.CategoryFragments.fragmentCateg1.EXTRA_PRODUCT_NAME;

public class ProductDetails extends AppCompatActivity {

    String pName, description, price, imageURL, seller, buyer, stock;
    TextView name, tPrice, desc, qText, totalAmount;
    ImageView photo;
    Button minus, add, addToCart;

    CoordinatorLayout coordinatorLayout;

    int intQuantity = 1;
    int stockInt;
    double totalPrice, productPrice;

    public static final String EXTRA_PRODUCT_USERNAME = "fromProduct";

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        try {
            try{
                Intent intent = getIntent();
                pName = intent.getStringExtra(EXTRA_PRODUCT_NAME);
                description = intent.getStringExtra(EXTRA_DESCRIPTION);
                price = intent.getStringExtra(EXTRA_PRICE);
                imageURL = intent.getStringExtra(EXTRA_PHOTO);
                seller = intent.getStringExtra(EXTRA_SELLER_USER);
                buyer = intent.getStringExtra(EXTRA_BUYER);
                stock = intent.getStringExtra("EXTRA_STOCK");
                stockInt = Integer.parseInt(stock);
                productPrice = Double.parseDouble(price);
                totalPrice = productPrice;
            } catch (Exception e){
                Intent intent = getIntent();
                pName = intent.getStringExtra("EXTRA_PRODUCT_NAME");
                description = intent.getStringExtra("EXTRA_DESCRIPTION");
                price = intent.getStringExtra("EXTRA_PRICE");
                imageURL = intent.getStringExtra("EXTRA_PHOTO");
                seller = intent.getStringExtra("EXTRA_SELLER_USER");
                buyer = intent.getStringExtra("EXTRA_BUYER");
                stock = intent.getStringExtra("EXTRA_STOCK");
                stockInt = Integer.parseInt(stock);
                productPrice = Double.parseDouble(price);
                totalPrice = productPrice;
            }

            //Toast.makeText(ProductDetails.this, "Stock: " + stock, Toast.LENGTH_SHORT).show();

            loadingDialog = new LoadingDialog(ProductDetails.this);

            photo = findViewById(R.id.productPhoto);
            name = findViewById(R.id.productName);
            tPrice = findViewById(R.id.txtPrice);
            desc = findViewById(R.id.description);
            qText = findViewById(R.id.quantityTxt);
            totalAmount = findViewById(R.id.totalPrice);
            minus = findViewById(R.id.btnMinus);
            add = findViewById(R.id.btnPlus);
            addToCart = findViewById(R.id.btnAddToCart);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);

            Picasso.get().load(imageURL).into(photo);
            name.setText(pName);
            DecimalFormat formatter = new DecimalFormat("#,###.00");

            tPrice.setText("₱" + formatter.format(productPrice));
            totalAmount.setText("₱" + formatter.format(totalPrice));
            desc.setText(description);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (intQuantity < stockInt){
                        intQuantity++;
                        qText.setText(String.valueOf(intQuantity));

                        totalPrice = productPrice * intQuantity;
                        DecimalFormat formatter = new DecimalFormat("#,###.00");
                        totalAmount.setText("₱" + formatter.format(totalPrice));
                    } else {
                        Toast.makeText(ProductDetails.this, "You've reached the maximum order for this item", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(intQuantity > 1){
                        intQuantity--;
                        totalPrice -= productPrice;
                    } else if (intQuantity == 1){
                        Toast.makeText(ProductDetails.this, "Select at least one (1) item", Toast.LENGTH_SHORT).show();
                    }
                    qText.setText(String.valueOf(intQuantity));
                    totalAmount.setText("₱" + String.format("%.2f", totalPrice));

                }
            });

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(stockInt < 1){
                        Toast.makeText(ProductDetails.this, "This product is not available right now", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetails.this, "Quantity " + qText.getText().toString() , Toast.LENGTH_SHORT).show();
                        checkInternet();
                    }
                }
            });
        } catch (Exception e){
            Log.e("Product Details", "exception", e);
            Toast.makeText(ProductDetails.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }



    }

    private void checkInternet() {
        try {

            loadingDialog.startLoading("Adding to cart");
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            addToCart.setEnabled(false);
                            addProductToCart();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    Toast.makeText(ProductDetails.this, "Please check your internet connection", Toast.LENGTH_LONG);
                    loadingDialog.stopLoading();
                    addToCart.setEnabled(true);
                }
            }
            );

            Volley.newRequestQueue(ProductDetails.this).add(stringRequest);
        } catch (Exception e){
            Log.e("Product Details", "exception", e);
            Toast.makeText(ProductDetails.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addProductToCart() {
        try {
            String buyerUname = null;
            String sellerUname = null;
            String quantity = null;
            String product = null;

            buyerUname = buyer.replace("'","\\'");
            sellerUname = seller.replace("'","\\'");
            quantity = qText.getText().toString();
            product = name.getText().toString().replace("'","\\'");

            String buyer_name =  URLEncoder.encode(Base64.getEncoder().encodeToString(buyerUname.getBytes()), "utf-8");
            String seller_name =  URLEncoder.encode(Base64.getEncoder().encodeToString(sellerUname.getBytes()), "utf-8");
            String quantity_count =  URLEncoder.encode(Base64.getEncoder().encodeToString(quantity.getBytes()), "utf-8");
            String prod_name = URLEncoder.encode(Base64.getEncoder().encodeToString(product.getBytes()), "utf-8");
            Log.v("TEST", buyer_name+':'+seller_name+':'+quantity_count+':'+prod_name);

            String JSON_URL = "https://wecart.gq/wecart-api/add-to-cart.php?action=add_cart&username="+ buyer_name +"&seller="+ seller_name +"&product_name="+ prod_name +"&quantity="+ quantity_count +"";
            RequestQueue requestQueue = Volley.newRequestQueue(ProductDetails.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            loadingDialog.stopLoading();
                            loadingDialog.addedToCart();
                            Handler handler = new Handler();
                            try {

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingDialog.stopLoading();
                                    }
                                }, 2500);
                            } catch (Exception e) {
                                Log.e("Handler in Add cart", "Exception", e);
                                handler.removeCallbacksAndMessages(null);
                            }

                            addToCart.setEnabled(true);
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    loadingDialog.stopLoading();
                    Toast.makeText(ProductDetails.this, "Sorry, Something went wrong", Toast.LENGTH_LONG);
                    addToCart.setEnabled(true);
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e){
            Log.e("Product Details", "exception", e);
            Toast.makeText(ProductDetails.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }


    public void detailsToHome(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showSnackbar() {
        try {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Product Added", Snackbar.LENGTH_LONG)
                    .setAction("VIEW CART", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProductDetails.this, TransactionScreen.class);
                            intent.putExtra(EXTRA_PRODUCT_USERNAME, buyer);
                            startActivity(intent);
                        }
                    });

            snackbar.show();
        } catch (Exception e){
            Log.e("Product Details", "exception", e);
            Toast.makeText(ProductDetails.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

}