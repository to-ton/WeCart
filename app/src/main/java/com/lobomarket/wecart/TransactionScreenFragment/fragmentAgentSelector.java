package com.lobomarket.wecart.TransactionScreenFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.AdminHomeScreen.AddAgent;
import com.lobomarket.wecart.AdminHomeScreen.fragmentsActiveAgents;
import com.lobomarket.wecart.CustomAdapters.AgentCustomAdapter;
import com.lobomarket.wecart.CustomAdapters.AgentSelectionCustomAdapter;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.Models.AgentModel;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.SellerHomeScreen.AddNewProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class fragmentAgentSelector extends Fragment implements AgentSelectionCustomAdapter.OnItemClickListener {

    View view;

    RequestQueue requestQueue;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private AgentSelectionCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<AgentModel> agent;
    private int dataCount;
    private Button selectAgent, back;

    String agentUsername, buyerUsername, txtMop;
    LoadingDialog loadingDialog;

    ConstraintLayout noAgent, noInternet, loadingAgent;
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_agent_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            buyerUsername = getArguments().getString("buyerName");
            txtMop = getArguments().getString("payment");

            swipeRefreshLayout = view.findViewById(R.id.agentRefresh);
            recyclerView = view.findViewById(R.id.agentSelectionList);
            agent = new ArrayList<>();
            selectAgent = view.findViewById(R.id.btnSelectAgent);
            agentUsername = null;
            loadingDialog = new LoadingDialog(getActivity());
            noAgent = view.findViewById(R.id.noAgentLayout);
            layoutManager = new LinearLayoutManager(getActivity());
            cAdapter = new AgentSelectionCustomAdapter(getActivity(), agent);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);
            loadingAgent = view.findViewById(R.id.loadingAgent);
            noInternet = view.findViewById(R.id.noInternetAgent);
            requestQueue = Volley.newRequestQueue(getActivity());

            back = view.findViewById(R.id.btnAgentToCart);
            handler = new Handler();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });

            selectAgent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(agentUsername == null){
                        Toast.makeText(getActivity(), "Please select an Agent", Toast.LENGTH_SHORT).show();
                    } else {
                        checkInternet();
                    }
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                    handler.removeCallbacksAndMessages(null);
                }
            });
        } catch (Exception e){
            Log.e("AgentSelector", "exception", e);

        }
    }


    private void confirmAgent() {
        try {
            String txtuser = null;
            String txtagent = null;
            try {
                txtuser = URLEncoder.encode(buyerUsername.replace("'","\\'"), "utf-8");
                txtagent = URLEncoder.encode(agentUsername.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/add-to-cart.php?action=choose_agent&username="+ txtuser +"&agent="+ txtagent +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            if(!(agentUsername.equals("No available agents at the moment"))){
                                loadingDialog.stopLoading();
                            }
                            NavController navController = Navigation.findNavController(view);
                            Bundle bundle = new Bundle();
                            bundle.putString("buyerName", buyerUsername);
                            bundle.putString("agent", agentUsername);
                            bundle.putString("payment", txtMop);
                            navController.navigate(R.id.action_fragmentAgentSelector_to_fragmentTransaction, bundle);
                            if(!(agentUsername.equals("No available agents at the moment"))){
                                Toast.makeText(getActivity(), "You have selected, " + agentUsername + " as your agent, " + buyerUsername, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    loadingDialog.stopLoading();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e) {
            Log.e("AgentSelector", "exception", e);

        }
    }

    private void checkInternet() {
        try {
            loadingDialog.startLoading("Please wait");
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            confirmAgent();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    Toast.makeText(getActivity(), "Pleas check your internet connection", Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoading();
                }
            }
            );

            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("AgentSelector", "exception", e);

        }
    }

    private void refreshList() {
        try {
            String JSON_URL = "https://wecart.gq/wecart-api/showusers.php?agents";
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            swipeRefreshLayout.setRefreshing(false);
                            noInternet.setVisibility(View.GONE);
                            loadingAgent.setVisibility(View.VISIBLE);
                            showUser();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    noInternet.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            stringRequest.setTag("CANCEL");
            requestQueue.add(stringRequest);
        } catch (Exception e){
            Log.e("AgentSelector", "exception", e);

        }

    }

    private void showUser() {
        try {
            agent.clear();
            String JSON_URL = "https://wecart.gq/wecart-api/agent.php?active_list";

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    loadingAgent.setVisibility(View.GONE);
                                    noAgent.setVisibility(View.GONE);
                                    JSONObject userObject = response.getJSONObject(i);

                                    AgentModel u = new AgentModel();
                                    u.setUserName(userObject.getString("username"));
                                    u.setFullName(userObject.getString("full_name"));
                                    u.setContactNum(userObject.getString("contact_num"));

                                    agent.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(agent);
                            cAdapter.setOnItemClickListener(fragmentAgentSelector.this);
                            dataCount = cAdapter.getItemCount();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onResponse: " + error.getMessage());
                    swipeRefreshLayout.setRefreshing(false);
                    loadingAgent.setVisibility(View.GONE);
                    noAgent.setVisibility(View.VISIBLE);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            agentUsername = "No available agents at the moment";
                            confirmAgent();
                        }
                    }, 2000);

                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("AgentSelector", "exception", e);

        }
    }

    @Override
    public void onItemClick(int position) {
        try {
            AgentModel clickedItem = agent.get(position);
            agentUsername = clickedItem.getUserName();
        } catch (Exception e){
            Log.e("AgentSelector", "exception", e);
            Toast.makeText(getActivity(), "Too fast", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        refreshList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll("CANCEL");
        }
    }


}