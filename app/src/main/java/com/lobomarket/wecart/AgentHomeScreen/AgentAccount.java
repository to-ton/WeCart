package com.lobomarket.wecart.AgentHomeScreen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.LoginLogOutSession.SessionManagement;
import com.lobomarket.wecart.LoginSignUpScreen.LoginSignupScreen;
import com.lobomarket.wecart.Models.Users;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.UserHomeScreen.EditAccountDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AgentAccount extends AppCompatActivity {

    Button btnAgentLogout, btnEditAcc, btnBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView agentUsername, agentFullName, agentEmail, agentContact, agentAddress;

    LoadingDialog loadingDialog;
    RequestQueue requestQueue;

    String agentUname;

    ConstraintLayout accountLoading, accountNoInternet;

    String txtbrgy, txtSitio, txtstreet;

    Users u;

    ToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_account);
        try {
            btnBack = findViewById(R.id.btn2);
            accountLoading = findViewById(R.id.agentAccountLoading);
            accountNoInternet = findViewById(R.id.agentAccountNoInternet);
            agentUname = getIntent().getStringExtra("agent");
            //Toast.makeText(AgentAccount.this, "Hello " + agentUname, Toast.LENGTH_SHORT).show();
            loadingDialog = new LoadingDialog(AgentAccount.this);
            btnAgentLogout = findViewById(R.id.btnLogoutAgent);
            btnEditAcc = findViewById(R.id.editAgentAccount);
            swipeRefreshLayout = findViewById(R.id.swipeAgentAccount);

            agentUsername = findViewById(R.id.agentProfUsername);
            agentFullName = findViewById(R.id.agentProfFullName);
            agentEmail = findViewById(R.id.agentProfEmail);
            agentContact = findViewById(R.id.agentProfContact);
            agentAddress = findViewById(R.id.agentProfAddress);

            toggle = (ToggleButton) findViewById(R.id.statusToggleButton);
            toggle.setText("Available");

            SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
            boolean tgpref = preferences.getBoolean("tgpref", true);

            toggle.setChecked(tgpref);
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // The toggle is enabled
                        awayOn();

                    } else {
                        // The toggle is disabled
                        awayOff();

                    }
                }
            });

            toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("tgpref", toggle.isChecked()); // value to store
                    editor.commit();
                }
            });

            btnEditAcc.setEnabled(false);

            u = new Users();

            btnAgentLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    logout();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(AgentAccount.this);
                    builder.setMessage("Are you sure you want to logout?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            btnEditAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AgentAccount.this, EditAgentAccount.class);
                    intent.putExtra("name", u.getFname());
                    intent.putExtra("username", u.getUsername());
                    intent.putExtra("email", u.getEmail());
                    intent.putExtra("sitio", txtSitio);
                    intent.putExtra("brgy", txtbrgy);
                    intent.putExtra("street", txtstreet);
                    intent.putExtra("contact", u.getContact());


                    startActivity(intent);
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    checkInternet();
                }
            });
        } catch (Exception e) {
            Log.e("Agent Profile", "Exception", e);
        }
    }

    private void awayOff() {
        try {
            String txtAgent = null;
            try {
                txtAgent = URLEncoder.encode(agentUname.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/agent.php?away_off="+ txtAgent +"";
            requestQueue = Volley.newRequestQueue(AgentAccount.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            checkInternet();
                            Toast.makeText(AgentAccount.this, "Agent is available", Toast.LENGTH_SHORT).show();
                            toggle.setText("Available");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    Toast.makeText(AgentAccount.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Active toggle button", "Exception", e);
        }
    }

    private void awayOn() {
        try {
            String txtAgent = null;
            try {
                txtAgent = URLEncoder.encode(agentUname.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/agent.php?away_on="+ txtAgent +"";
            requestQueue = Volley.newRequestQueue(AgentAccount.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            checkInternet();
                            Toast.makeText(AgentAccount.this, "Agent is away", Toast.LENGTH_SHORT).show();
                            toggle.setText("Away");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    Toast.makeText(AgentAccount.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Active toggle button", "Exception", e);
        }
    }

    public void logout(){
        try {
            loadingDialog.startLoading("Logging out");
            String txtAgent =null;
            try {
                txtAgent = URLEncoder.encode(agentUname.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/logout.php?username="+ txtAgent +"";
            requestQueue = Volley.newRequestQueue(AgentAccount.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            Intent logout = new Intent(AgentAccount.this, LoginSignupScreen.class);

                            SessionManagement sessionManagement = new SessionManagement(AgentAccount.this);
                            sessionManagement.removeSession();
                            loadingDialog.stopLoading();
                            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logout);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    Toast.makeText(AgentAccount.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    loadingDialog.stopLoading();
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Agent Activity", "exception", e);
            Toast.makeText(AgentAccount.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkInternet(){
        try {
            requestQueue = Volley.newRequestQueue(AgentAccount.this);
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            accountNoInternet.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            showAgentAccount();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    accountLoading.setVisibility(View.GONE);
                    accountNoInternet.setVisibility(View.VISIBLE);
                }
            }
            );

            requestQueue.add(request);
        } catch (Exception e){
            Log.e("Agent account", "Exception", e);
        }
    }

    private void showAgentAccount() {
        try{
            String txtagent = URLEncoder.encode(agentUname.replace("'","\\'"), "utf-8");

            String url = "https://wecart.gq/wecart-api/profile_info.php?username="+ txtagent +"";
            requestQueue = Volley.newRequestQueue(AgentAccount.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(0);


                                u.setUsername(jsonObject.getString("username"));
                                u.setFname(jsonObject.getString("full_name"));
                                u.setEmail(jsonObject.getString("contact_email"));
                                u.setContact(jsonObject.getString("contact_num"));




                                txtbrgy = jsonObject.getString("brgy");
                                txtSitio = jsonObject.getString("sitio");
                                txtstreet = jsonObject.getString("street");

                                u.setAddress(txtstreet + ", " + txtSitio + ", " + txtbrgy);



                                agentUsername.setText(u.getUsername());
                                agentFullName.setText(u.getFname());
                                agentEmail.setText(u.getEmail());
                                agentContact.setText(u.getContact());
                                agentAddress.setText(u.getAddress());
                                accountLoading.setVisibility(View.GONE);
                                btnEditAcc.setEnabled(true);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Agent Account", "Volley Error", error);
                    accountNoInternet.setVisibility(View.VISIBLE);
                    accountLoading.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Agent Account", "Exception", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternet();
    }
}