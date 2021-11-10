package com.lobomarket.wecart.SellerHomeScreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.AdminHomeScreen.AddSellers;
import com.lobomarket.wecart.ImageResizer;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EditSellerShopAccount extends AppCompatActivity {

    EditText editSellerStoreName, editSellerName, editContact, editEmail;
    EditText editSellerSitio, sellerUserName;
    Button choose, update, back, clear;
    ImageView photo;

    String shopName, sellerName, storeType, fullName, email, contact, brgy, sitio, street, img, uname;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageURI;
    private Bitmap bitmap, bitmapReduced;

    LoadingDialog loadingDialog;
    boolean isClearClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seller_shop_account);
        try {
            editSellerStoreName = findViewById(R.id.editStoreName);
            editSellerName = findViewById(R.id.editSellerFullName);
            sellerUserName = findViewById(R.id.editSellerUsername);
            editContact = findViewById(R.id.editSelllerContact);
            editEmail = findViewById(R.id.editSellerEmail);
            editSellerSitio = findViewById(R.id.editSellerSitio);
            choose = findViewById(R.id.btnChooseSellerPhoto);
            update = findViewById(R.id.updateSellerAccount);
            back = findViewById(R.id.button5);
            photo = findViewById(R.id.sellerEditPhoto);
            clear =findViewById(R.id.btnClearFields);
            shopName = getIntent().getStringExtra("shopName");
            sellerName = getIntent().getStringExtra("sellerName");
            storeType = getIntent().getStringExtra("storeType");
            fullName = getIntent().getStringExtra("fullName");
            email = getIntent().getStringExtra("email");
            contact = getIntent().getStringExtra("contact");
            sitio = getIntent().getStringExtra("sitio");
            img = getIntent().getStringExtra("img");
            uname = getIntent().getStringExtra("sellerUsername");


            try{
                editSellerStoreName.setText(shopName);
                editSellerName.setText(fullName);
                editContact.setText(contact);
                editEmail.setText(email);


                editSellerSitio.setText(sitio);



                sellerUserName.setText(uname);
                Picasso.get().load(img).into(photo);
                BitmapDrawable drawable = (BitmapDrawable) photo.getDrawable();
                bitmap = drawable.getBitmap();
            } catch (RuntimeException e){
                Log.e("MYAPP", "exception", e);
                Toast.makeText(EditSellerShopAccount.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                finish();
            }

            loadingDialog = new LoadingDialog(EditSellerShopAccount.this);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(editSellerStoreName.getText().length() != 0 && editSellerName.getText().length() != 0 &&
                            editContact.getText().length() != 0 && editSellerSitio.getText().length() != 0
                            && editEmail.getText().length() != 0 && sellerUserName.getText().length() != 0){
                        if(editContact.getText().length() == 11){
                            loadingDialog.startLoading("Updating Account");

                            editSellerStoreName.setError(null);
                            editSellerName.setError(null);
                            editSellerSitio.setError(null);
                            editEmail.setError(null);
                            editContact.setError(null);

                            proceed();
                        } else {
                            editContact.setError("Enter a valid number");
                        }
                    } else {
                        editSellerStoreName.setError("Required");
                        editSellerName.setError("Required");
                        editContact.setError("Required");
                        editSellerSitio.setError("Required");
                        editEmail.setError("Required");
                    }
                }
            });

            choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choosePhoto();
                }
            });

            isClearClicked = false;
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!(isClearClicked)){
                        editSellerName.setText("");
                        editContact.setText("");
                        editEmail.setText("");
                        editSellerSitio.setText("");

                        isClearClicked = true;
                        clear.setText("Undo");
                    } else {
                        editSellerStoreName.setText(shopName);
                        editSellerName.setText(fullName);
                        editContact.setText(contact);
                        editEmail.setText(email);
                        editSellerSitio.setText(sitio);

                        clear.setText("Clear");
                        isClearClicked = false;
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Edit Seller Account", "exception", e);
            Toast.makeText(EditSellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }

    }

    private void choosePhoto() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e("Edit Seller Account", "exception", e);
            Toast.makeText(EditSellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAccount() {
        try {
            String txtshopname = null;
            String txtfullname = null;
            String txtcontact = null;
            String txtemail = null;
            String txtbrgy = null;
            String txtsitio = null;
            String txtstreet = null;
            String txtstoretype = null;
            String txtsellername = null;
            try {
                txtshopname = URLEncoder.encode(editSellerStoreName.getText().toString().replace("'","\\'"), "utf-8");
                txtfullname = URLEncoder.encode(editSellerName.getText().toString().replace("'","\\'"), "utf-8");
                txtcontact = URLEncoder.encode(editContact.getText().toString(), "utf-8");
                txtemail = URLEncoder.encode(editEmail.getText().toString().replace("'","\\'"), "utf-8");
                txtsitio = URLEncoder.encode(editSellerSitio.getText().toString().replace("'","\\'"), "utf-8");
                txtstoretype = URLEncoder.encode(storeType.replace("'","\\'"), "utf-8");
                txtsellername = URLEncoder.encode(sellerName.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://wecart.gq//wecart-api/edituserinfo.php?seller&username="+ txtsellername +"&name="+ txtfullname +"&brgy="+ txtbrgy +"&sitio="+ txtsitio +"&street="+ txtstreet +"&contact_num="+ txtcontact +"&contact_email="+ txtemail +"&store_name="+ txtshopname +"&decription=null&store_type="+ txtstoretype +"";
            RequestQueue requestQueue = Volley.newRequestQueue(EditSellerShopAccount.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.v("TEST ADD PRODUCT", response);
                                uploadPhoto();
                                loadingDialog.stopLoading();
                                Toast.makeText(EditSellerShopAccount.this, "Account Updated", Toast.LENGTH_LONG).show();
                            } catch (Exception e){
                                Log.e("MYAPP", "exception", e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed");
                    loadingDialog.stopLoading();
                    Toast.makeText(EditSellerShopAccount.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Edit Seller Account", "exception", e);
            Toast.makeText(EditSellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && data != null && data.getData() != null) {
                imageURI = data.getData();
                Picasso.get().load(imageURI).into(photo);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                    bitmapReduced = ImageResizer.reduceBitmapSize(bitmap, 240000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e("Edit Seller Account", "exception", e);
            Toast.makeText(EditSellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadPhoto(){
        try {
            String JSON_URL = "https://wecart.gq/wecart-api/upload_user_profile.php";
            RequestQueue requestQueue = Volley.newRequestQueue(EditSellerShopAccount.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                Log.v("TEST", response);
                                loadingDialog.stopLoading();
                                onBackPressed();
                            } catch (Exception e){
                                Log.e("MYAPP", "exception", e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingDialog.stopLoading();
                    Log.v("TEST", "failed: " + error.getMessage());
                    onBackPressed();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String filename = set_image_filename();
                    double random_number = Math.random();
                    String random_string = String.valueOf(random_number);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imgBytes = byteArrayOutputStream.toByteArray();
                    String encode = Base64.encodeToString(imgBytes, Base64.DEFAULT);

                    Map<String, String> params = new HashMap<>();
                    params.put("name", filename);
                    params.put("image", encode);
                    params.put("username", sellerName);
                    params.put("random_string", random_string);

                    return  params;
                }
            };
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Edit Seller Account", "exception", e);
            Toast.makeText(EditSellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
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

    private void proceed() {
        try {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            updateAccount();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    loadingDialog.stopLoading();
                    Toast.makeText(EditSellerShopAccount.this, "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(EditSellerShopAccount.this).add(stringRequest);
        } catch (Exception e) {
            Log.e("Edit Seller Account", "exception", e);
            Toast.makeText(EditSellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    Dialog dialogChangePass;
    EditText oldPass;
    EditText newPass;

    public void changePassword(View v){
        try {
            dialogChangePass = new Dialog(EditSellerShopAccount.this);

            //We have added a title in the custom layout. So let's disable the default title.
            dialogChangePass.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
            dialogChangePass.setCancelable(true);
            //Mention the name of the layout of your custom dialog.
            dialogChangePass.setContentView(R.layout.change_password_details);
            dialogChangePass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            oldPass = dialogChangePass.findViewById(R.id.oldPassword);
            newPass = dialogChangePass.findViewById(R.id.newPassword);
            Button change = dialogChangePass.findViewById(R.id.changePassDialog);


            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogChangePass.dismiss();
                    proceedChangePass();
                }
            });

            dialogChangePass.show();
        } catch (Exception e) {
            Log.e("Edit Seller Account", "exception", e);
            Toast.makeText(EditSellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void changePass(){
        try {
            String oldPassText = null;
            String newPassText = null;
            String sUName = null;
            try {
                oldPassText = URLEncoder.encode(oldPass.getText().toString().replace("'","\\'"), "utf-8");
                newPassText = URLEncoder.encode(newPass.getText().toString().replace("'","\\'"), "utf-8");
                sUName = URLEncoder.encode(sellerUserName.getText().toString().replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            RequestQueue requestQueue = Volley.newRequestQueue(EditSellerShopAccount.this);
            String JSON_URL = "https://wecart.gq/wecart-api/changepass.php?username="+ sUName +"&oldpass="+ oldPassText +"&newpass="+ newPassText +"";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            if(response.equals("{\"status\":\"Success.\"}")){
                                loadingDialog.stopLoading();
                                Toast.makeText(EditSellerShopAccount.this, "Password changed", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingDialog.stopLoading();
                                Toast.makeText(EditSellerShopAccount.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                dialogChangePass.show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EditSellerShopAccount.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoading();
                }
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Edit Seller Account", "exception", e);
            Toast.makeText(EditSellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void proceedChangePass() {
        try {
            loadingDialog.startLoading("Changing password");
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            changePass();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    loadingDialog.stopLoading();
                    Toast.makeText(EditSellerShopAccount.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(EditSellerShopAccount.this).add(stringRequest);
        } catch (Exception e) {
            Log.e("Edit Seller Account", "exception", e);
            Toast.makeText(EditSellerShopAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }
}