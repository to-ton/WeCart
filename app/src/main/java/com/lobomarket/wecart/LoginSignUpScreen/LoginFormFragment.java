package com.lobomarket.wecart.LoginSignUpScreen;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.AdminHomeScreen.AdminScreen;
import com.lobomarket.wecart.AgentHomeScreen.AgentActivity;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.SellerHomeScreen.SellerHomeScreen;
import com.lobomarket.wecart.LoginLogOutSession.SessionManagement;
import com.lobomarket.wecart.LoginLogOutSession.Users;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.UserHomeScreen.UserHomeScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.security.auth.callback.Callback;

import hari.bounceview.BounceView;

public class LoginFormFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_login_form, container, false);
    }

    private Button back, login;
    private NavController navController;
    private NavOptions navOptions;
    private EditText txtUsername, txtPassword;
    private TextView failed_login, forgoet_password;
    LoadingDialog loadingDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            navController = Navigation.findNavController(view);
            back = view.findViewById(R.id.btnLoginToWelcome);
            BounceView.addAnimTo(back);
            login = view.findViewById(R.id.btnLogin);
            BounceView.addAnimTo(login);
            txtUsername = view.findViewById(R.id.txtUsername);
            txtPassword = view.findViewById(R.id.txtPassword);
            failed_login = view.findViewById(R.id.login_failed);
            loadingDialog = new LoadingDialog(getActivity());
            forgoet_password = view.findViewById(R.id.forgoet_password);

            navOptions = new NavOptions.Builder().setPopUpTo(R.id.welcomeScreenFragment, true)
                    .setEnterAnim(R.anim.slide_left_to_right)
                    .setExitAnim(R.anim.wait_anim)
                    .setPopEnterAnim(R.anim.wait_anim)
                    .setPopExitAnim(R.anim.slide_l2r_reverse)
                    .build();


            forgoet_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(getActivity());
                    //We have added a title in the custom layout. So let's disable the default title.
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
                    dialog.setCancelable(true);
                    //Mention the name of the layout of your custom dialog.
                    dialog.setContentView(R.layout.reset_password_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    EditText email = dialog.findViewById(R.id.txtEmailReset);
                    Button buttonSend = dialog.findViewById(R.id.button3);


                    buttonSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                dialog.dismiss();
                                loadingDialog.startLoading("Please wait");
                                if(email.getText().length() != 0){
                                    email.setError(null);
                                    String txtEmail = URLEncoder.encode(email.getText().toString().replace("'","\\'"), "utf-8");
                                    StringRequest stringRequest = new StringRequest(
                                            Request.Method.GET,
                                            "https://wecart.gq/wecart-api/forgot_password.php?email="+ txtEmail +"",
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    if(response.equals("{\"status\":\"Success.\"}")){
                                                        Toast.makeText(getActivity(), "Please check your mail box", Toast.LENGTH_SHORT).show();
                                                        loadingDialog.stopLoading();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Invalid email", Toast.LENGTH_SHORT).show();
                                                        loadingDialog.stopLoading();
                                                        dialog.show();
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("Forgot Password", "Exception", error);
                                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    );

                                    Volley.newRequestQueue(getActivity()).add(stringRequest);
                                } else {
                                    email.setError("Required");
                                }
                            } catch (Exception e) {
                                Log.e("Login", "exception", e);
                                Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.show();
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navController.navigate(R.id.action_loginFormFragment_to_welcomeScreenFragment, null, navOptions);
                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(txtUsername.getText().length() != 0 && txtPassword.getText().length() != 0){
                        loadingDialog.startLoading("Logging in");
                        checkInternet();
                    } else {
                        txtUsername.setError("Required");
                        txtPassword.setError("Required");
                    }


                }
            });
        } catch (Exception e) {
            Log.e("Login", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void toUserHomeScreen(String username){
        Intent userHome = new Intent(getActivity(), UserHomeScreen.class);
        userHome.putExtra("username", username);
        startActivity(userHome);
        getActivity().finish();
    }

    private void toSellerHomeScreen(String username){
        Intent sellerHome = new Intent(getActivity(), SellerHomeScreen.class);
        sellerHome.putExtra("username", username);
        startActivity(sellerHome);
        getActivity().finish();
    }

    private void toAdminHomeScreen(String username){
        Intent admin = new Intent(getActivity(), AdminScreen.class);
        admin.putExtra("username", username);
        startActivity(admin);
        getActivity().finish();
    }

    private void toAgentHomeScreen(String username){
        Intent agent = new Intent(getActivity(), AgentActivity.class);
        agent.putExtra("username", username);
        startActivity(agent);
        getActivity().finish();
    }

    private void checkInternet() {
        try {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            validate();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    loadingDialog.stopLoading();
                    Toast.makeText(getActivity(), "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e) {
            Log.e("Login", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void login(){
        try {
            String username = null;
            String password = null;
            try {
                username = URLEncoder.encode(txtUsername.getText().toString().replace("'","\\'"), "utf-8");
                password = URLEncoder.encode(txtPassword.getText().toString().replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://wecart.gq/wecart-api/login.php?username="+ username +"&password="+ password +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            String finalUsername = username;
            JsonArrayRequest postRequest = new JsonArrayRequest(
                    Request.Method.POST,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject userObject = response.getJSONObject(0);

                                String accountType = userObject.getString("acc_type");

                                SessionManagement sessionManagement = new SessionManagement(getActivity());

                                String role1 = "buyer";
                                String role2 = "seller";
                                String role3 = "admin";
                                String role4 = "agent";


                                loadingDialog.stopLoading();
                                if(accountType.equals(role1)){
                                    Users users = new Users(1, role1, finalUsername);
                                    sessionManagement.saveSession(users);
                                    toUserHomeScreen(finalUsername);
                                } else if (accountType.equals(role2)){
                                    Users users = new Users(2, role2, finalUsername);
                                    sessionManagement.saveSession(users);
                                    toSellerHomeScreen(finalUsername);
                                } else if (accountType.equals(role3)){
                                    Users users = new Users(3, role3, finalUsername);
                                    sessionManagement.saveSession(users);
                                    toAdminHomeScreen(finalUsername);
                                } else if (accountType.equals(role4)){
                                    Users users = new Users(3, role4, finalUsername);
                                    sessionManagement.saveSession(users);
                                    toAgentHomeScreen(finalUsername);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST LOGIN", "failed: " + error.getMessage());
                    loadingDialog.stopLoading();
                    failed_login.setVisibility(View.VISIBLE);
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Login", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void validate(){
        try {
            String username = null;
            String password = null;
            try {
                username = URLEncoder.encode(txtUsername.getText().toString(), "utf-8");
                password = URLEncoder.encode(txtPassword.getText().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String JSON_URL = "https://wecart.gq/wecart-api/login.php?username="+ username +"&password="+ password +"";

            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("{\"status\":\"not verified\"}")){
                                failed_login.setVisibility(View.VISIBLE);
                                failed_login.setText("Please verify your email");
                                loadingDialog.stopLoading();
                            } else if (response.equals("{\"status\":\"User does not exist.\"}")) {
                                failed_login.setVisibility(View.VISIBLE);
                                failed_login.setText("The username or password you entered is incorrect.");
                                loadingDialog.stopLoading();
                            } else {
                                login();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    loadingDialog.stopLoading();
                    Toast.makeText(getActivity(), "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e) {
            Log.e("Login", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }
}