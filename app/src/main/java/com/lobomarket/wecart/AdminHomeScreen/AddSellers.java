package com.lobomarket.wecart.AdminHomeScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.SellerHomeScreen.AddNewProduct;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AddSellers extends AppCompatActivity {

    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE) {
                    return "";
                }
            }
            return null;
        }
    };

    private String blockCharacterSet = "'\"’";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    private EditText shopName, userName, txtPassword, txtEmail;
    private Spinner spinner;
    private Button add;

    LoadingDialog dialog;

    String emailPattern = "[a-zA-Z0-9._+-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sellers);
        try {
            shopName = findViewById(R.id.textShop);
            userName = findViewById(R.id.textSellerUsername);
            txtPassword = findViewById(R.id.txtSellerPassword);
            txtEmail = findViewById(R.id.txtSellerEmail);
            add = findViewById(R.id.btnAddSeller);

            dialog = new LoadingDialog(AddSellers.this);

            userName.setFilters(new InputFilter[]{EMOJI_FILTER, filter});

            spinner = findViewById(R.id.typeOfShop);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddSellers.this,
                    R.array.shop_type, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(shopName.getText().length() != 0 && userName.getText().length() != 0
                            && txtPassword.getText().length() != 0 && txtEmail.getText().length() != 0){

                        if (txtEmail.getText().toString().trim().matches(emailPattern)) {
                            txtEmail.setError(null);
                            dialog.startLoading("Adding Seller");
                            proceed();
                        } else {
                            txtEmail.setError("Invalid email address");
                        }
                    } else {
                        shopName.setError("Required");
                        userName.setError("Required");
                        txtPassword.setError("Required");
                        txtEmail.setError("Required");
                    }
                }
            });
        } catch (Exception e){
            Log.e("Add Sellers", "exception", e);

        }
    }

    private void addSeller() {
        try {
            String shop = null;
            String storetype = null;
            String username = null;
            String password = null;
            String txtmail = null;
            try {
                storetype = URLEncoder.encode(spinner.getSelectedItem().toString().replace("'","\\'"), "utf-8");
                shop = URLEncoder.encode(shopName.getText().toString().replace("'","\\'"), "utf-8");
                username = URLEncoder.encode(userName.getText().toString().replace("'","\\'"), "utf-8");
                password = URLEncoder.encode(txtPassword.getText().toString().replace("'","\\'"), "utf-8");
                txtmail = URLEncoder.encode(txtEmail.getText().toString().replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            String JSON_URL = "https://wecart.gq/wecart-api/register.php?seller&store_name="+ shop +"&store_type="+ storetype +"&description="+ null +"&name="+ null +"&username="+ username +"&password="+ password +"&brgy="+ null +"&sitio="+ null +"&street="+ null +"&contact_num="+ null +"&contact_email="+ txtmail +"";
            RequestQueue requestQueue = Volley.newRequestQueue(AddSellers.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);

                            if(response.equals("{\"status\":\"Username already taken.\"}")){
                                dialog.stopLoading();
                                Toast.makeText(AddSellers.this, "Username already taken", Toast.LENGTH_SHORT).show();
                            } else if(response.equals("{\"status\":\"Store name already taken.\"}")) {
                                dialog.stopLoading();
                                Toast.makeText(AddSellers.this, "Store name already taken", Toast.LENGTH_SHORT).show();
                            } else if (response.equals("{\"email_valid_status\":\"email already exist\"}")){
                                dialog.stopLoading();
                                Toast.makeText(AddSellers.this, "Email already exist", Toast.LENGTH_SHORT).show();
                            } else if (response.equals("{\"email_valid_status\":\"email already exist\"}{\"status\":\"Username already taken.\"}")){
                                dialog.stopLoading();
                                Toast.makeText(AddSellers.this, "Email/Username already exist", Toast.LENGTH_SHORT).show();
                            } else {
                                shopName.setText("");
                                userName.setText("");
                                txtPassword.setText("");
                                txtEmail.setText("");
                                dialog.stopLoading();
                                Toast.makeText(AddSellers.this, "Seller successfully added", Toast.LENGTH_LONG).show();
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed");
                    dialog.stopLoading();
                    Toast.makeText(AddSellers.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Add Sellers", "exception", e);

        }

    }

    private void proceed() {
        try {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            addSeller();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    dialog.stopLoading();
                    Toast.makeText(AddSellers.this, "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(AddSellers.this).add(stringRequest);
        } catch (Exception e) {
            Log.e("Add Sellers", "exception", e);

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void backToSellers(View view) {
        onBackPressed();
    }
}