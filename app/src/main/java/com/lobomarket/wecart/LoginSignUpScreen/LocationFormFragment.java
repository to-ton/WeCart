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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.lobomarket.wecart.VerificationActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import hari.bounceview.BounceView;

public class LocationFormFragment extends Fragment {

    View view;
    private EditText txtSitio, txtStreet;
    private Spinner spinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_location_form, container, false);
    }

    private NavController navController;
    Button back, setAddress;
    LoadingDialog dialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            back = view.findViewById(R.id.btnLocToRegForm);
            BounceView.addAnimTo(back);
            setAddress = view.findViewById(R.id.btnSetAddress);
            navController = Navigation.findNavController(view);
            txtSitio = view.findViewById(R.id.txtSitio);
            txtStreet = view.findViewById(R.id.txtStreet);

            spinner = view.findViewById(R.id.txtBarangay);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.brgy_lobo, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);

            dialog = new LoadingDialog(getActivity());

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    back();
                }
            });



            setAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(txtSitio.getText().length() != 0){
                        try{
                            dialog.startLoading("Registering");
                            proceed();
                        } catch (Exception e){
                            Log.v("TEST", "Error: " + e.getMessage());
                        }
                    } else {
                        txtSitio.setError("Required");
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Location Fragment", "exception", e);
        }


    }

    private void back(){
        try {
            NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.registerForm, true)
                    .setEnterAnim(R.anim.slide_left_to_right)
                    .setExitAnim(R.anim.wait_anim)
                    .setPopEnterAnim(R.anim.wait_anim)
                    .setPopExitAnim(R.anim.slide_l2r_reverse)
                    .build();

            Bundle bundle = new Bundle();
            bundle.putString("fullName", getArguments().getString("fullName"));
            bundle.putString("username", getArguments().getString("username"));
            bundle.putString("password", getArguments().getString("password"));
            bundle.putString("contact", getArguments().getString("contact"));
            bundle.putString("email", getArguments().getString("email"));

            navController.navigate(R.id.action_locationFormFragment_to_registerForm, bundle, navOptions);
        } catch (Exception e){
            Log.e("Location Fragment", "exception", e);
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
                            registerUser();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    dialog.stopLoading();
                    Toast.makeText(getActivity(), "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
            );

            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e){
            Log.e("Location Fragment", "exception", e);
        }

    }

    private void registerUser() {
        try {
            String fullname = null;
            String username = null;
            String password = null;
            String contact = null;
            String email = null;
            String brgy = null;
            String sitio = null;
            String street = null;
            try {
                fullname = URLEncoder.encode(getArguments().getString("fullName").replace("'","\\'"), "utf-8");
                username = URLEncoder.encode(getArguments().getString("username").replace("'","\\'"), "utf-8");
                password = URLEncoder.encode(getArguments().getString("password").replace("'","\\'"), "utf-8");
                contact = URLEncoder.encode(getArguments().getString("contact").replace("'","\\'"), "utf-8");
                email = URLEncoder.encode(getArguments().getString("email").replace("'","\\'"), "utf-8");
                brgy = URLEncoder.encode(spinner.getSelectedItem().toString().replace("'","\\'"), "utf-8");
                sitio = URLEncoder.encode(txtSitio.getText().toString().replace("'","\\'"), "utf-8");

                if(txtStreet.getText().length() != 0){
                    street = URLEncoder.encode(txtStreet.getText().toString(), "utf-8");
                } else {
                    street = URLEncoder.encode("N/A", "utf-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/register.php?buyer&name="+fullname+"&username="+ username +"&password="+ password +"&brgy="+ brgy +"&sitio="+ sitio +"&street="+ street +"&contact_num="+ contact +"&contact_email="+ email +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST REGISTRATION", response);

                            if(response.equals("{\"email_valid_status\":\"email already exist\"}")) {
                                Toast.makeText(getActivity(), "Email already exist", Toast.LENGTH_LONG).show();
                                dialog.stopLoading();
                                back();
                            } else if (response.equals("{\"email_valid_status\":\"email already exist\"}{\"status\":\"Username already taken.\"}")){
                                Toast.makeText(getActivity(), "Email/Username already taken", Toast.LENGTH_LONG).show();
                                dialog.stopLoading();
                                back();
                            } else if (response.equals("{\"status\":\"Username already taken.\"}")){
                                Toast.makeText(getActivity(), "Username already taken", Toast.LENGTH_LONG).show();
                                dialog.stopLoading();
                                back();
                            } else {
                                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.welcomeScreenFragment, true)
                                        .setEnterAnim(R.anim.slide_left_to_right)
                                        .setExitAnim(R.anim.wait_anim)
                                        .setPopEnterAnim(R.anim.wait_anim)
                                        .setPopExitAnim(R.anim.slide_l2r_reverse)
                                        .build();
                                dialog.stopLoading();
                                Toast.makeText(getActivity(), "Please check your email.", Toast.LENGTH_LONG).show();

                                navController.navigate(R.id.action_locationFormFragment_to_welcomeScreenFragment2, null, navOptions);
                                Intent intent = new Intent(getActivity(), EmailValidation.class);
                                intent.putExtra("email", getArguments().getString("email"));
                                startActivity(intent);
                            }




                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed");
                    Log.e("Volley Register", "Exception: ", error);
                    dialog.stopLoading();
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                }
            }
            );
            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("Location Fragment", "exception", e);
        }

    }


}