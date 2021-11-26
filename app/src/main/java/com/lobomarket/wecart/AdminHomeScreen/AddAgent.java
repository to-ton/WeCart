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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AddAgent extends AppCompatActivity {

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

    EditText username, agentFullName, pass, email, contactNum, sitio, street;
    Spinner barangay;
    Button btnAddAgent, back;

    LoadingDialog dialog;

    String emailPattern = "[a-zA-Z0-9._+-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agent);

        username = findViewById(R.id.agentUsername);
        agentFullName = findViewById(R.id.agentFullName);
        email = findViewById(R.id.textEmail);
        pass = findViewById(R.id.passwordAgent);
        contactNum = findViewById(R.id.agentContactNum);

        username.setFilters(new InputFilter[]{EMOJI_FILTER, filter});

        /*
        sitio = findViewById(R.id.sitioAgent);
        street = findViewById(R.id.streetAgent);
         */

        btnAddAgent = findViewById(R.id.addAgen);
        back = findViewById(R.id.backAgent);

        dialog = new LoadingDialog(AddAgent.this);

        /*barangay = findViewById(R.id.bgryAgent);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddAgent.this,
                R.array.brgy_lobo, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        barangay.setAdapter(adapter); */

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAddAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().length() != 0 && agentFullName.getText().length() != 0 &&
                        pass.getText().length() != 0 && email.getText().length() != 0 && contactNum.getText().length() != 0){
                    if(contactNum.getText().length() == 11 || contactNum.getText().length() == 13){

                        if (email.getText().toString().trim().matches(emailPattern)) {
                            email.setError(null);
                            proceed();
                        } else {
                            email.setError("Invalid email address");
                        }
                    } else {
                        contactNum.setError("Enter a valid number");
                    }

                } else {
                    username.setError("Required");
                    agentFullName.setError("Required");
                    pass.setError("Required");
                    email.setError("Required");
                    contactNum.setError("Required");
                }
            }
        });


    }

    private void addAgent(){
        String txtusername = null;
        String txtname = null;
        String txtemail = null;
        String password = null;
        String txtcontact = null;
        String txtstreet = null;
        String txtsitio = null;
        String brgy = null;

        try {
            txtusername = URLEncoder.encode(username.getText().toString().replace("'","\\'"), "utf-8");
            txtname = URLEncoder.encode(agentFullName.getText().toString().replace("'","\\'"), "utf-8");
            txtemail = URLEncoder.encode(email.getText().toString().replace("'","\\'"), "utf-8");
            password = URLEncoder.encode(pass.getText().toString().replace("'","\\'"), "utf-8");
            txtcontact = URLEncoder.encode(contactNum.getText().toString().replace("'","\\'"), "utf-8");
            txtstreet = URLEncoder.encode("N/A", "utf-8");
            txtsitio = URLEncoder.encode("N/A", "utf-8");
            brgy = URLEncoder.encode("N/A", "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/register.php?agent&name="+ txtname +"&username="+ txtusername +"&password="+ password +"&brgy="+ brgy +"&sitio="+ txtsitio +"&street="+ txtstreet +"&contact_num="+ txtcontact +"&contact_email="+ txtemail +"";
        RequestQueue requestQueue = Volley.newRequestQueue(AddAgent.this);
        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("TEST", response);
                        if(response.equals("{\"status\":\"Username already taken.\"}")){
                            dialog.stopLoading();
                            Toast.makeText(AddAgent.this, "Username already taken", Toast.LENGTH_SHORT).show();
                        } else if (response.equals("{\"email_valid_status\":\"email already exist\"}")){
                            dialog.stopLoading();
                            Toast.makeText(AddAgent.this, "Email already exist", Toast.LENGTH_SHORT).show();
                        } else if (response.equals("{\"email_valid_status\":\"email already exist\"}{\"status\":\"Username already taken.\"}")){
                            dialog.stopLoading();
                            Toast.makeText(AddAgent.this, "Email/Username already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            username.setText("");
                            agentFullName.setText("");
                            pass.setText("");
                            email.setText("");
                            contactNum.setText("");
                            //sitio.setText("");
                            //street.setText("");
                            dialog.stopLoading();
                            Toast.makeText(AddAgent.this, "Agent successfully added", Toast.LENGTH_SHORT).show();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("TEST", "failed: " + error.getMessage());
                Log.e("Agent Add", "Exception", error);
                dialog.stopLoading();
                Toast.makeText(AddAgent.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
        );

        requestQueue.add(postRequest);
    }

    private void proceed() {
        dialog.startLoading("Adding agent");
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "https://www.google.com/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //With internet
                        addAgent();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No internet
                dialog.stopLoading();
                Toast.makeText(AddAgent.this, "Pleas check your internet connection", Toast.LENGTH_LONG).show();
            }
        }
        );

        Volley.newRequestQueue(AddAgent.this).add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}