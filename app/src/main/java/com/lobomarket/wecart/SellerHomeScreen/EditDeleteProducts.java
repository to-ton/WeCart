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

import static com.lobomarket.wecart.CategoryFragments.fragmentCateg1.EXTRA_DESCRIPTION;
import static com.lobomarket.wecart.CategoryFragments.fragmentCateg1.EXTRA_PHOTO;
import static com.lobomarket.wecart.CategoryFragments.fragmentCateg1.EXTRA_PRICE;
import static com.lobomarket.wecart.CategoryFragments.fragmentCateg1.EXTRA_PRODUCT_NAME;
import static com.lobomarket.wecart.SellerHomeScreen.fragmentSellerProducts.EXTRA_STOCK;

public class EditDeleteProducts extends AppCompatActivity {

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

    private EditText description, stock, price, productName;
    private Button btnUpdate, btn2, btnChoosePhoto, btnDelete;
    private ImageView pProduct;
    private Spinner spinner;
    //TextView productName;

    private static String CHECK_INTERNET_URL = "https://www.google.com/";

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageURI;
    String sellerName, storeType;
    private Bitmap bitmap, bitmapReduced;

    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);
        try {
            productName = findViewById(R.id.txtEditPName);
            description = findViewById(R.id.txtEditDescrip);
            stock = findViewById(R.id.txtEditStocks);
            price = findViewById(R.id.txtEdiAmount);
            pProduct = findViewById(R.id.editPhotoProduct);
            btnUpdate = findViewById(R.id.btnUpdate);
            btn2 = findViewById(R.id.btnBack);
            btnDelete = findViewById(R.id.btnDelete);
            btnChoosePhoto = findViewById(R.id.btnChoosePhoto);

            Intent intent = getIntent();
            String pName = intent.getStringExtra(EXTRA_PRODUCT_NAME);
            String desc = intent.getStringExtra(EXTRA_DESCRIPTION);
            int amount = intent.getIntExtra(EXTRA_PRICE, 0);
            String stocks = intent.getStringExtra(EXTRA_STOCK);
            String imageURL = intent.getStringExtra(EXTRA_PHOTO);
            sellerName = intent.getStringExtra("seller");
            storeType = intent.getStringExtra("storeType");

            productName.setText(pName);
            description.setText(desc);
            stock.setText(stocks);
            price.setText(String.valueOf(amount));
            Picasso.get().load(imageURL).into(pProduct);

            productName.setFilters(new InputFilter[] {EMOJI_FILTER});
            description.setFilters(new InputFilter[] {EMOJI_FILTER});

            dialog = new LoadingDialog(EditDeleteProducts.this);

            try{
                loadCategories();
            } catch (NullPointerException e){
                Log.v("ERROR", "Null Exception: " + e.toString());
            }


            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDelete();
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(productName.getText().length() != 0 && stock.getText().length() != 0 &&
                            price.getText().length() != 0 && description.getText().length() != 0){
                        dialog.startLoading("Updating");
                        proceed();
                    } else {
                        productName.setError("Required");
                        stock.setError("Required");
                        price.setError("Required");
                        description.setError("Required");
                    }
                }
            });

            btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choosePhoto();
                }
            });
        } catch (Exception e) {
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void choosePhoto() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
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
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    //added spinner here - nette
    private void loadCategories(){
        try {
            spinner = findViewById(R.id.productType);
            ArrayAdapter<CharSequence> adapter = null;

            switch (storeType){
                case "Fruits and Vegetables":
                    adapter = ArrayAdapter.createFromResource(EditDeleteProducts.this,
                            R.array.fruits_vegetable, android.R.layout.simple_spinner_item);
                    break;
                case "Meat Shop":
                    adapter = ArrayAdapter.createFromResource(EditDeleteProducts.this,
                            R.array.meat_shop, android.R.layout.simple_spinner_item);
                    break;
                case "Sea Food Shop":
                    adapter = ArrayAdapter.createFromResource(EditDeleteProducts.this,
                            R.array.sea_food, android.R.layout.simple_spinner_item);
                    break;
                case "Grocery":
                    adapter = ArrayAdapter.createFromResource(EditDeleteProducts.this,
                            R.array.grocery, android.R.layout.simple_spinner_item);
                    break;
                case "Eatery":
                    adapter = ArrayAdapter.createFromResource(EditDeleteProducts.this,
                            R.array.eatery, android.R.layout.simple_spinner_item);
                    break;
                case "Bakery":
                    adapter = ArrayAdapter.createFromResource(EditDeleteProducts.this,
                            R.array.bakery, android.R.layout.simple_spinner_item);
                    break;
                case "Plastic ware/Glass ware":
                    adapter = ArrayAdapter.createFromResource(EditDeleteProducts.this,
                            R.array.plastic_glass, android.R.layout.simple_spinner_item);
                    break;
                case "Others":
                    adapter = ArrayAdapter.createFromResource(EditDeleteProducts.this,
                            R.array.others, android.R.layout.simple_spinner_item);
                    break;
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            String type = getIntent().getStringExtra("product_type");
            if (type != null) {
                int spinnerPosition = adapter.getPosition(type);
                spinner.setSelection(spinnerPosition);
            }
        } catch (Exception e) {
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateProduct() {
        try {
            String txtusername = null;
            String txtproductname = null;
            String txtstock = null;
            String txtprice = null;
            String txtdesc = null;
            String txttype = null;
            String oldproduct = null;

            try {
                txtusername = URLEncoder.encode(sellerName.replace("'","\\'"), "utf-8");
                oldproduct = URLEncoder.encode(getIntent().getStringExtra(EXTRA_PRODUCT_NAME).replace("'","\\'"),"utf-8");
                txtproductname = URLEncoder.encode(productName.getText().toString().replace("'","\\'"), "utf-8");
                txtstock = URLEncoder.encode(stock.getText().toString(), "utf-8");
                txtprice = URLEncoder.encode(price.getText().toString(), "utf-8");
                txtdesc = URLEncoder.encode(description.getText().toString().replace("'","\\'"), "utf-8");
                txttype = URLEncoder.encode(spinner.getSelectedItem().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/editproductinfo.php?username="+ txtusername +"&old_product="+ oldproduct +"&product_name="+ txtproductname +"&product_type="+ txttype +"&description="+ txtdesc +"&price="+ txtprice +"&stock="+ txtstock +"";
            RequestQueue requestQueue = Volley.newRequestQueue(EditDeleteProducts.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST ADD PRODUCT", response);
                            uploadPhoto();
                            //Toast.makeText(EditDeleteProducts.this, "Product Updated", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed");
                    Toast.makeText(EditDeleteProducts.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadPhoto() {
        try {
            String JSON_URL = "https://wecart.gq/wecart-api/product_image_upload.php";
            RequestQueue requestQueue = Volley.newRequestQueue(EditDeleteProducts.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            Toast.makeText(EditDeleteProducts.this, "Product Added", Toast.LENGTH_LONG).show();
                            dialog.stopLoading();
                            onBackPressed();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.stopLoading();
                    Log.e("Update Product Photo", "Exception", error);
                    onBackPressed();
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
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] imgBytes = byteArrayOutputStream.toByteArray();
                        encode = Base64.encodeToString(imgBytes, Base64.DEFAULT);

                    } catch (NullPointerException e){

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
        } catch (Exception e) {
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    protected String set_image_filename() {
        String charlist = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) {
            int index = (int) (rnd.nextFloat() * charlist.length());
            salt.append(charlist.charAt(index));
        }
        String final__ = salt.toString();
        return final__;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                            updateProduct();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    Toast.makeText(EditDeleteProducts.this, "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(EditDeleteProducts.this).add(stringRequest);
        } catch (Exception e) {
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteProduct(){
        try {
            String name = null;
            String ProductName = null;
            try {
                name = URLEncoder.encode(sellerName.replace("'","\\'"), "utf-8");
                ProductName = URLEncoder.encode(productName.getText().toString().replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="https://wecart.gq/wecart-api/delete.php?seller="+ name +"&product_name="+ ProductName +"";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", "Delete product " + response);
                            dialog.stopLoading();
                            Toast.makeText(EditDeleteProducts.this, "Product deleted", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "Delete product " + error.getMessage());
                    dialog.stopLoading();
                    Toast.makeText(EditDeleteProducts.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            });

            queue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    public void confirmDelete(){
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            proceedDelete();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this product?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } catch (Exception e){
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void proceedDelete() {
        try {
            dialog.startLoading("Deleting product");
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            deleteProduct();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    dialog.stopLoading();
                    Toast.makeText(EditDeleteProducts.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(EditDeleteProducts.this).add(stringRequest);
        } catch (Exception e) {
            Log.e("Edit Products", "exception", e);
            Toast.makeText(EditDeleteProducts.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }
}