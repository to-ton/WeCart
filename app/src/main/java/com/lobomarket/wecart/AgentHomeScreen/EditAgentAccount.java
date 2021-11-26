package com.lobomarket.wecart.AgentHomeScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.AgentHomeScreen.EditAgentAccount;
import com.lobomarket.wecart.AgentHomeScreen.EditAgentAccount;
import com.lobomarket.wecart.AgentHomeScreen.EditAgentAccount;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EditAgentAccount extends AppCompatActivity {

    private EditText editAgentName, editAgentUsername, editAgentContact,
            editAgentEmail, editAgentSitio, editAgentStreet, editAgentBrgy;
    private Button btnChangePass, btnBack, btnUpdate, clearFields;

    boolean isClearClicked;

    Dialog dialogChangePass;
    EditText oldPass;
    EditText newPass;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_account);
        try {
            editAgentName = findViewById(R.id.txtEditAgentName);
            editAgentUsername = findViewById(R.id.txtEditAgentUserName);
            editAgentContact = findViewById(R.id.txtEditAgentContact);
            editAgentEmail = findViewById(R.id.txtEditAgentEmail);
            editAgentSitio = findViewById(R.id.txtEditAgentLandmark);
            editAgentBrgy = findViewById(R.id.txtEditAgentBarangay);
            editAgentStreet = findViewById(R.id.txtEditAgentStreet);
            btnBack = findViewById(R.id.editAgentbtnBack);
            btnUpdate = findViewById(R.id.btnUpdateAgentAcc);
            clearFields = findViewById(R.id.btnClearFieldsAgent);
            loadingDialog = new LoadingDialog(EditAgentAccount.this);
            btnChangePass = findViewById(R.id.btnChange);

            Intent intent = getIntent();
            editAgentName.setText(intent.getStringExtra("name"));
            editAgentUsername.setText(intent.getStringExtra("username"));
            editAgentEmail.setText(intent.getStringExtra("email"));
            editAgentSitio.setText(intent.getStringExtra("sitio"));
            editAgentBrgy.setText(intent.getStringExtra("brgy"));
            editAgentStreet.setText(intent.getStringExtra("street"));
            editAgentContact.setText(intent.getStringExtra("contact"));

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            isClearClicked = false;
            clearFields.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!(isClearClicked)){
                        editAgentName.setText("");
                        editAgentEmail.setText("");
                        editAgentSitio.setText("");
                        editAgentBrgy.setText("");
                        editAgentStreet.setText("");
                        editAgentContact.setText("");

                        isClearClicked = true;
                        clearFields.setText("Undo");
                    } else {
                        editAgentName.setText(intent.getStringExtra("name"));
                        editAgentEmail.setText(intent.getStringExtra("email"));
                        editAgentSitio.setText(intent.getStringExtra("sitio"));
                        editAgentBrgy.setText(intent.getStringExtra("brgy"));
                        editAgentStreet.setText(intent.getStringExtra("street"));
                        editAgentContact.setText(intent.getStringExtra("contact"));


                        clearFields.setText("Clear");
                        isClearClicked = false;
                    }
                }
            });

            btnChangePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialogChangePass = new Dialog(EditAgentAccount.this);

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
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(editAgentName.getText().length() != 0 && editAgentEmail.getText().length() != 0 &&
                            editAgentBrgy.getText().length() != 0 && editAgentStreet.getText().length() != 0
                            && editAgentContact.getText().length() != 0){
                        if(editAgentContact.getText().length() == 11 || editAgentContact.getText().length() == 13){
                            loadingDialog.startLoading("Updating Account");

                            editAgentName.setError(null);
                            editAgentUsername.setError(null);
                            editAgentEmail.setError(null);
                            editAgentBrgy.setError(null);
                            editAgentStreet.setError(null);
                            editAgentContact.setError(null);
                            proceed();
                        } else {
                            editAgentContact.setError("Enter a valid number");
                        }
                    }else {
                        editAgentName.setError("Required");
                        editAgentEmail.setError("Required");
                        editAgentBrgy.setError("Required");
                        editAgentStreet.setError("Required");
                        editAgentContact.setError("Required");
                    }

                }
            });

        } catch (Exception e) {
            Log.e("Edit Agent Account", "Exception", e);
        }
    }

    private void proceedChangePass(){
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
                    Toast.makeText(EditAgentAccount.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(EditAgentAccount.this).add(stringRequest);
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }
    }

    private void changePass() {
        try {
            String oldPassText = null;
            String newPassText = null;
            String sUName = null;
            try {
                oldPassText = URLEncoder.encode(oldPass.getText().toString().replace("'","\\'"), "utf-8");
                newPassText = URLEncoder.encode(newPass.getText().toString().replace("'","\\'"), "utf-8");
                sUName = URLEncoder.encode(editAgentUsername.getText().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            RequestQueue requestQueue = Volley.newRequestQueue(EditAgentAccount.this);
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/changepass.php?username="+ sUName +"&oldpass="+ oldPassText +"&newpass="+ newPassText +"";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            if(response.equals("{\"status\":\"Success.\"}")){
                                loadingDialog.stopLoading();
                                Toast.makeText(EditAgentAccount.this, "Password changed", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingDialog.stopLoading();
                                Toast.makeText(EditAgentAccount.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                dialogChangePass.show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EditAgentAccount.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoading();
                }
            });

            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }
    }

    private void proceed(){
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
                    Toast.makeText(EditAgentAccount.this, "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(EditAgentAccount.this).add(stringRequest);
        } catch (Exception e) {
            Log.e("Edit Account details", "Error: ", e);

        }
    }

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
                txtfullname = URLEncoder.encode(editAgentName.getText().toString().replace("'","\\'"), "utf-8");
                txtcontact = URLEncoder.encode(editAgentContact.getText().toString().replace("'","\\'"), "utf-8");
                txtemail = URLEncoder.encode(editAgentEmail.getText().toString().replace("'","\\'"), "utf-8");
                txtbrgy = URLEncoder.encode(editAgentBrgy.getText().toString().replace("'","\\'"), "utf-8");
                txtsitio = URLEncoder.encode(editAgentSitio.getText().toString().replace("'","\\'"), "utf-8");
                txtstreet = URLEncoder.encode(editAgentStreet.getText().toString().replace("'","\\'"), "utf-8");
                txtusername = URLEncoder.encode(editAgentUsername.getText().toString().replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/edituserinfo.php?agent&username="+ txtusername +"&name="+ txtfullname +"&brgy="+ txtbrgy +"&sitio="+ txtsitio +"&street="+ txtstreet +"&contact_num="+ txtcontact +"&contact_email="+ txtemail +"";
            RequestQueue requestQueue = Volley.newRequestQueue(EditAgentAccount.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST ADD PRODUCT", response);

                            loadingDialog.stopLoading();
                            Toast.makeText(EditAgentAccount.this, "Account Updated", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed");
                    loadingDialog.stopLoading();
                    Toast.makeText(EditAgentAccount.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );
            requestQueue.add(postRequest);
        } catch (Exception e){
            Log.e("Edit Account details", "Error: ", e);

        }
    }
}