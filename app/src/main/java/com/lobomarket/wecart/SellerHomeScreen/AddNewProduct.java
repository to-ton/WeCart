package com.lobomarket.wecart.SellerHomeScreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.ImageResizer;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.LoginLogOutSession.SessionManagement;
import com.lobomarket.wecart.LoginSignUpScreen.LoginSignupScreen;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddNewProduct extends AppCompatActivity {

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

    String stringPattern = "[a-zA-Z0-9]";


    private EditText productName, description, stock, price;
    private Button btnAddProducts, btn2, btnChoosePhoto;
    private ImageView pProduct;
    private Spinner spinner;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageURI;
    private Bitmap bitmap, bitmapReduced;

    String storeType, sellerName;

    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_products);
        try {
            productName = findViewById(R.id.txtPName);
            description = findViewById(R.id.txtDescrip);
            stock = findViewById(R.id.txtStocks);
            price = findViewById(R.id.txtAmount);
            pProduct = findViewById(R.id.pProduct);
            btnAddProducts = findViewById(R.id.btnAddProducts);
            btn2 = findViewById(R.id.btnToProducts);
            btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
            storeType = getIntent().getStringExtra("storeType");

            dialog = new LoadingDialog(AddNewProduct.this);

            try{
                loadCategories();
            } catch (NullPointerException e){
                Log.v("ERROR", "Null Exception: " + e.toString());
            }

            productName.setFilters(new InputFilter[]{EMOJI_FILTER});

            btnAddProducts.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //Toast.makeText(AddNewProduct.this, "Description " + description.getText().toString(), Toast.LENGTH_SHORT).show();

                    if(productName.getText().length() != 0 && stock.getText().length() != 0 &&
                            price.getText().length() != 0){
                        if(bitmap == null){
                            proceed();
                        } else {
                            checkInternet();
                        }
                    } else {
                        productName.setError("Required");
                        stock.setError("Required");
                        price.setError("Required");
                    }
                }
            });

            btn2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choosePhoto();
                }
            });



        } catch (Exception e) {
            Log.e("Add Product", "exception", e);
            Toast.makeText(AddNewProduct.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void proceed() {
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            checkInternet();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(AddNewProduct.this);
            builder.setMessage("You didn't set the Product Image.\nDo you want to continue?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } catch (Exception e){
            Log.e("Add Product", "exception", e);
            Toast.makeText(AddNewProduct.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void choosePhoto() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e("Add Product", "exception", e);
            Toast.makeText(AddNewProduct.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && data != null && data.getData() != null) {
                imageURI = data.getData();
                Picasso.get().load(imageURI).into(pProduct);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                    bitmapReduced = ImageResizer.reduceBitmapSize(bitmap, 240000);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e("Add Product", "exception", e);
            Toast.makeText(AddNewProduct.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    protected String set_image_filename() {
        String charlist = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) {
            int index = (int) (rnd.nextFloat() * charlist.length());
            salt.append(charlist.charAt(index));
        }
        String final__ = salt.toString();
        return final__;

    }


    private void uploadPhoto() {
        try {
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/product_image_upload.php";
            RequestQueue requestQueue = Volley.newRequestQueue(AddNewProduct.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST Upload photo", response);
                            Toast.makeText(AddNewProduct.this, "Product Added", Toast.LENGTH_LONG).show();
                            productName.setText("");
                            stock.setText("");
                            price.setText("");
                            description.setText("");
                            pProduct.setBackgroundResource(R.drawable.default_image);
                            dialog.stopLoading();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST","Add photo product error: " + error.getMessage());
                    Log.e("Add Product Photo", "Exception", error);
                    Toast.makeText(AddNewProduct.this, "No Photo uploaded", Toast.LENGTH_LONG).show();
                    productName.setText("");
                    stock.setText("");
                    price.setText("");
                    description.setText("");
                    dialog.stopLoading();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String pName = productName.getText().toString().trim().replace("'","\\'");
                    String filename = set_image_filename();
                    double random_number = Math.random();
                    String random_string = String.valueOf(random_number);

                    String encode = null;
                    try{
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmapReduced.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                        byte[] imgBytes = byteArrayOutputStream.toByteArray();
                        encode = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                    } catch (OutOfMemoryError e){
                        //Toast.makeText(AddNewProduct.this, "Image too large", Toast.LENGTH_SHORT).show();
                    }

                    Map<String, String> params = new HashMap<>();
                    params.put("name", filename);
                    params.put("image", encode);
                    params.put("username", sellerName);
                    params.put("product_name", pName);
                    params.put("random_string", random_string);

                    return  params;
                }
            };
            requestQueue.add(postRequest);
        } catch (Exception e){
            Log.e("Add Product", "exception", e);
            Toast.makeText(AddNewProduct.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCategories(){
        try {
            spinner = findViewById(R.id.productType);
            ArrayAdapter<CharSequence> adapter = null;

            switch (storeType){
                case "Fruits and Vegetables":
                    adapter = ArrayAdapter.createFromResource(AddNewProduct.this,
                            R.array.fruits_vegetable, android.R.layout.simple_spinner_item);
                    break;
                case "Meat Shop":
                    adapter = ArrayAdapter.createFromResource(AddNewProduct.this,
                            R.array.meat_shop, android.R.layout.simple_spinner_item);
                    break;
                case "Sea Food Shop":
                    adapter = ArrayAdapter.createFromResource(AddNewProduct.this,
                            R.array.sea_food, android.R.layout.simple_spinner_item);
                    break;
                case "Grocery":
                    adapter = ArrayAdapter.createFromResource(AddNewProduct.this,
                            R.array.grocery, android.R.layout.simple_spinner_item);
                    break;
                case "Eatery":
                    adapter = ArrayAdapter.createFromResource(AddNewProduct.this,
                            R.array.eatery, android.R.layout.simple_spinner_item);
                    break;
                case "Bakery":
                    adapter = ArrayAdapter.createFromResource(AddNewProduct.this,
                            R.array.bakery, android.R.layout.simple_spinner_item);
                    break;
                case "Plastic ware/Glass ware":
                    adapter = ArrayAdapter.createFromResource(AddNewProduct.this,
                            R.array.plastic_glass, android.R.layout.simple_spinner_item);
                    break;
                case "Others":
                    adapter = ArrayAdapter.createFromResource(AddNewProduct.this,
                            R.array.others, android.R.layout.simple_spinner_item);
                    break;
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception e){
            Log.e("Add Product", "exception", e);
            Toast.makeText(AddNewProduct.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void addProducts(){

        try {
            String sellerNameTxt = null;
            sellerName = getIntent().getStringExtra("sellerName");
            String txtproductName = null;
            String txtproductType = null;
            String txtdescription = null;
            String txtstock = null;
            String txtprice = null;
            try {

                sellerNameTxt = URLEncoder.encode(sellerName.replace("'","\\'"), "utf-8");
                txtproductName = URLEncoder.encode(productName.getText().toString().replace("'","\\'"), "utf-8");
                txtproductType = URLEncoder.encode(spinner.getSelectedItem().toString().replace("'","\\'"), "utf-8");
                txtdescription = URLEncoder.encode(description.getText().toString().replace("'","\\'"), "utf-8");
                txtstock = URLEncoder.encode(stock.getText().toString(), "utf-8");
                txtprice = URLEncoder.encode(price.getText().toString(), "utf-8");


                Log.v("TEST", "Description: " + txtdescription);

                String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/add_product.php?username="+ sellerNameTxt +"&product_name="+ txtproductName +"&product_type="+ txtproductType +"&description="+ txtdescription +"&stock="+txtstock +"&price="+ txtprice +"";
                RequestQueue requestQueue = Volley.newRequestQueue(AddNewProduct.this);
                StringRequest postRequest = new StringRequest(
                        Request.Method.POST,
                        JSON_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v("TEST ADD PRODUCT", response);
                                uploadPhoto();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("TEST", "failed");
                        Log.e("Volley Error", "Exception", error);
                        dialog.stopLoading();
                        Toast.makeText(AddNewProduct.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
                );
                requestQueue.add(postRequest);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("Add Product", "exception", e);
            Toast.makeText(AddNewProduct.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkInternet() {
        try {
            dialog.startLoading("Adding Product");
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            addProducts();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    Toast.makeText(AddNewProduct.this, "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                    dialog.stopLoading();
                }
            }
            );

            Volley.newRequestQueue(AddNewProduct.this).add(stringRequest);
        } catch (Exception e) {
            Log.e("Add Product", "exception", e);
            Toast.makeText(AddNewProduct.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}