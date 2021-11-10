package com.lobomarket.wecart.UserHomeScreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.SellerHomeScreen.EditSellerShopAccount;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EditAccountDetails extends AppCompatActivity {

    private EditText editName, editUsername, editContact, editEmail, editSitio, editStreet;
    private ImageView photo;
    private Button btnChoose, btnBack, btnUpdate, clearFields;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageURI;
    private Bitmap bitmap, bitmapReduced;

    boolean isClearClicked;

    LoadingDialog loadingDialog;

    Spinner spinner;

    //with try catch
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_details);

        try{
            editName = findViewById(R.id.editName);
            editUsername = findViewById(R.id.editUsername);
            editContact = findViewById(R.id.editContact);
            editEmail = findViewById(R.id.editEmail);
            editSitio = findViewById(R.id.editSitio);
            editStreet = findViewById(R.id.editStreet);
            photo = findViewById(R.id.imageView3);
            btnChoose = findViewById(R.id.btnChooseAccntPhoto);
            btnBack = findViewById(R.id.button);
            btnUpdate = findViewById(R.id.btnUpdateAccount);
            clearFields = findViewById(R.id.btnClearFieldsBuyer);

            Intent intent = getIntent();
            editName.setText(intent.getStringExtra("name"));
            editUsername.setText(intent.getStringExtra("username"));
            editEmail.setText(intent.getStringExtra("email"));
            editSitio.setText(intent.getStringExtra("sitio"));
            editStreet.setText(intent.getStringExtra("street"));
            editContact.setText(intent.getStringExtra("contact"));
            Picasso.get().load(intent.getStringExtra("img")).into(photo);

            loadingDialog = new LoadingDialog(EditAccountDetails.this);

            spinner = findViewById(R.id.brgySelection);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditAccountDetails.this,
                    R.array.brgy_lobo, android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

            if (intent.getStringExtra("brgy") != null) {
                int spinnerPosition = adapter.getPosition(intent.getStringExtra("brgy"));
                spinner.setSelection(spinnerPosition);
            }


            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(editName.getText().length() != 0 && editEmail.getText().length() != 0 && editSitio.getText().length() != 0 &&
                            editStreet.getText().length() != 0
                            && editContact.getText().length() != 0){
                        if(editContact.getText().length() == 11 || editContact.getText().length() == 13){
                            loadingDialog.startLoading("Updating Account");

                            editName.setError(null);
                            editUsername.setError(null);
                            editEmail.setError(null);
                            editSitio.setError(null);
                            editStreet.setError(null);
                            editContact.setError(null);
                            proceed();
                        } else {
                            editContact.setError("Enter a valid number");
                        }
                    }else {
                        editName.setError("Required");
                        editEmail.setError("Required");
                        editSitio.setError("Required");
                        editStreet.setError("Required");
                        editContact.setError("Required");
                    }

                }
            });

            btnChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choosePhoto();
                }
            });

            isClearClicked = false;
            clearFields.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!(isClearClicked)){
                        editName.setText("");
                        editEmail.setText("");
                        editSitio.setText("");
                        editStreet.setText("");
                        editContact.setText("");

                        isClearClicked = true;
                        clearFields.setText("Undo");
                    } else {
                        editName.setText(intent.getStringExtra("name"));
                        editEmail.setText(intent.getStringExtra("email"));
                        editSitio.setText(intent.getStringExtra("sitio"));
                        editStreet.setText(intent.getStringExtra("street"));
                        editContact.setText(intent.getStringExtra("contact"));

                        clearFields.setText("Clear");
                        isClearClicked = false;
                    }
                }
            });
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }
    }

    //with try catch
    private void proceed() {
        try{
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
                    Toast.makeText(EditAccountDetails.this, "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(EditAccountDetails.this).add(stringRequest);
        } catch (Exception e) {
            Log.e("Edit Account details", "Error: ", e);

        }

    }

    //with try catch
    private void updateAccount() {
        try{
            String txtusername = null;
            String txtfullname = null;
            String txtcontact = null;
            String txtemail = null;
            String txtbrgy = null;
            String txtsitio = null;
            String txtstreet = null;

            try {
                txtfullname = URLEncoder.encode(editName.getText().toString().replace("'","\\'"), "utf-8");
                txtcontact = URLEncoder.encode(editContact.getText().toString().replace("'","\\'"), "utf-8");
                txtemail = URLEncoder.encode(editEmail.getText().toString().replace("'","\\'"), "utf-8");
                txtbrgy = URLEncoder.encode(spinner.getSelectedItem().toString().replace("'","\\'"), "utf-8");
                txtsitio = URLEncoder.encode(editSitio.getText().toString().replace("'","\\'"), "utf-8");
                txtstreet = URLEncoder.encode(editStreet.getText().toString().replace("'","\\'"), "utf-8");
                txtusername = URLEncoder.encode(editUsername.getText().toString().replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Log.v("TEST ADD PRODUCT", txtstreet);
            String JSON_URL = "https://wecart.gq//wecart-api/edituserinfo.php?buyer&username="+ txtusername +"&name="+ txtfullname +"&brgy="+ txtbrgy +"&sitio="+ txtsitio +"&street="+ txtstreet +"&contact_num="+ txtcontact +"&contact_email="+ txtemail +"";
            RequestQueue requestQueue = Volley.newRequestQueue(EditAccountDetails.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST ADD PRODUCT", response);
                            uploadPhoto();
                            Toast.makeText(EditAccountDetails.this, "Account Updated", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed");
                    loadingDialog.stopLoading();
                    Toast.makeText(EditAccountDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );
            requestQueue.add(postRequest);
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }


    }

    //with try catch
    private void uploadPhoto() {
        try{
            String JSON_URL = "https://wecart.gq/wecart-api/upload_user_profile.php";
            RequestQueue requestQueue = Volley.newRequestQueue(EditAccountDetails.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            loadingDialog.stopLoading();
                            onBackPressed();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
                    params.put("username", getIntent().getStringExtra("username"));
                    params.put("random_string", random_string);

                    return  params;
                }
            };
            requestQueue.add(postRequest);
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //with try catch
    private void choosePhoto() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }
    }

    //with try catch
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
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }
    }

    Dialog dialogChangePass;
    EditText oldPass;
    EditText newPass;

    //with try catch
    public void changePasswordBuyer(View v){
        try {
            dialogChangePass = new Dialog(EditAccountDetails.this);

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
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }

    }

    //with try catch
    private void changePass(){
        try {
            String oldPassText = null;
            String newPassText = null;
            String sUName = null;
            try {
                oldPassText = URLEncoder.encode(oldPass.getText().toString().replace("'","\\'"), "utf-8");
                newPassText = URLEncoder.encode(newPass.getText().toString().replace("'","\\'"), "utf-8");
                sUName = URLEncoder.encode(editUsername.getText().toString().replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            RequestQueue requestQueue = Volley.newRequestQueue(EditAccountDetails.this);
            String JSON_URL = "https://wecart.gq/wecart-api/changepass.php?username="+ sUName +"&oldpass="+ oldPassText +"&newpass="+ newPassText +"";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            if(response.equals("{\"status\":\"Success.\"}")){
                                loadingDialog.stopLoading();
                                Toast.makeText(EditAccountDetails.this, "Password changed", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingDialog.stopLoading();
                                Toast.makeText(EditAccountDetails.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                dialogChangePass.show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EditAccountDetails.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoading();
                }
            });

            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }
    }

    //with try catch
    private void proceedChangePass() {

        try{
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
                    Toast.makeText(EditAccountDetails.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(EditAccountDetails.this).add(stringRequest);
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }
    }

}