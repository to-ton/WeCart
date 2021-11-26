package com.lobomarket.wecart.AdminHomeScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.Models.Dashboard;
import com.lobomarket.wecart.R;

import org.json.JSONException;
import org.json.JSONObject;

public class fragmentDashboard extends Fragment {

    View view;
    private ToggleButton appTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view =  inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    TextView onlineUsers, onlineSellers, onlineAgent;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            onlineUsers = view.findViewById(R.id.onlineUsers);
            onlineAgent = view.findViewById(R.id.onlineAgents);
            onlineSellers = view.findViewById(R.id.onlineSellers);
            swipeRefreshLayout = view.findViewById(R.id.dashboardRefresh);

            //added toggleButton here
            appTheme = view.findViewById(R.id.toggleDayNight);

            //Declare SharedPreferences
            SharedPreferences preferences = this.getActivity().getPreferences(Context.MODE_PRIVATE);
            boolean tgpref = preferences.getBoolean("tgpref", true);  //default is true

            appTheme.setChecked(tgpref);

            //AppTheme Day/Night Mode
            appTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(appTheme.isChecked()){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            });

            //Toggle Button Day/Night Mode
            appTheme.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("tgpref", appTheme.isChecked()); // value to store
                    editor.commit();
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    checkInternet();
                }
            });
            
        } catch (Exception e) {
            Log.e("Dashboard", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkInternet() {

        String url = "https://jarvis.danlyt.ninja/wecart-api/v1/dashboard.php";


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.google.com/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //JsonObjectRequest will get the response (or result, i dunno what term i should use)
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response.toString());


                                            Dashboard t = new Dashboard();

                                            t.setUserCount(jsonObject.getString("online_buyer"));
                                            t.setSellerCount(jsonObject.getString("online_seller"));
                                            t.setAgentCount(jsonObject.getString("online_agent"));

                                            if(t.getUserCount().length() == 1){
                                                onlineUsers.setText("0" + t.getUserCount());
                                            } else {
                                                onlineUsers.setText(t.getUserCount());
                                            }

                                            if(t.getSellerCount().length() == 1){
                                                onlineSellers.setText("0" + t.getSellerCount());
                                            } else {
                                                onlineSellers.setText(t.getSellerCount());
                                            }

                                            if(t.getAgentCount().length() == 1){
                                                onlineAgent.setText("0" + t.getAgentCount());
                                            } else {
                                                onlineAgent.setText(t.getAgentCount());
                                            }

                                            swipeRefreshLayout.setRefreshing(false);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.v("ERROR", error.toString());
                                        Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                        );

                        requestQueue.add(jsonObjectRequest);
                    }

                }, new Response.ErrorListener() {
            //this will show the error message which there is no connection
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "No internet Connection, please try again", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        requestQueue.add(stringRequest);
    }


    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        checkInternet();
    }
}