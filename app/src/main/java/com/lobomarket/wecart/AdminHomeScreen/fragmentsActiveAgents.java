package com.lobomarket.wecart.AdminHomeScreen;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.CustomAdapters.AgentCustomAdapter;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.Models.AgentModel;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class fragmentsActiveAgents extends Fragment implements AgentCustomAdapter.OnItemClickListener {

    View view;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private AgentCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<AgentModel> agent;
    private int dataCount;

    private TextView noResult;
    String user2;

    ConstraintLayout noAgentListed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return view = inflater.inflate(R.layout.fragment_fragments_active_agents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            swipeRefreshLayout = view.findViewById(R.id.activeAgents);
            recyclerView = view.findViewById(R.id.agentsList);
            agent = new ArrayList<>();
            noResult = view.findViewById(R.id.textView9);
            noAgentListed = view.findViewById(R.id.noAgentListed);
            layoutManager = new LinearLayoutManager(getActivity());
            cAdapter = new AgentCustomAdapter(getActivity(), agent);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            if (dataCount == 0){
                swipeRefreshLayout.setRefreshing(true);
            }

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshList();
                }
            });
        } catch (Exception e) {
            Log.e("Agent List", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }

    }

    private void refreshList() {
        try {
            String JSON_URL = "https://www.google.com/";
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            showUser();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "No internet Connection, please try again", Toast.LENGTH_SHORT).show();
                }
            });
            Volley.newRequestQueue(getActivity()).add(stringRequest);
        } catch (Exception e) {
            Log.e("Agent List", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void showUser() {
        try {
            agent.clear();
            String JSON_URL = "https://wecart.gq/wecart-api/showusers.php?agentlist";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            cAdapter.clear();
                            noResult.setVisibility(View.GONE);
                            noAgentListed.setVisibility(View.GONE);
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    AgentModel u = new AgentModel();
                                    u.setUserName(userObject.getString("username"));
                                    u.setFullName(userObject.getString("full_name"));
                                    u.setContactNum(userObject.getString("contact_num"));
                                    u.setEmail(userObject.getString("contact_email"));
                                    String txtbrgy = userObject.getString("brgy");
                                    String txtSitio = userObject.getString("sitio");
                                    String txtstreet = userObject.getString("street");

                                    u.setAddress(txtstreet + ", " + txtSitio + ", " + txtbrgy);

                                    agent.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(agent);
                            cAdapter.setOnItemClickListener(fragmentsActiveAgents.this);
                            dataCount = cAdapter.getItemCount();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onResponse: " + error.getMessage());
                    noResult.setVisibility(View.VISIBLE);
                    noAgentListed.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            Log.e("Agent List", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    Dialog dialog;
    LoadingDialog loadingDialog;
    @Override
    public void onItemClick(int position) {
        try{
            AgentModel clickedItem = agent.get(position);

            loadingDialog = new LoadingDialog(getActivity());
            dialog = new Dialog(getActivity());
            //We have added a title in the custom layout. So let's disable the default title.
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
            dialog.setCancelable(true);
            //Mention the name of the layout of your custom dialog.
            dialog.setContentView(R.layout.agent_details);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //Initializing the views of the dialog.
            final TextView fullName = dialog.findViewById(R.id.agentDetailsFullName);
            final TextView contactNum = dialog.findViewById(R.id.agentDetailsContact);
            final TextView address = dialog.findViewById(R.id.agentDetailsAddress);
            final TextView username = dialog.findViewById(R.id.agentDetailsUsername);

            Button deleteUser = dialog.findViewById(R.id.btnDeleteSelleragent);


            contactNum.setText(clickedItem.getContactNum());
            address.setText(clickedItem.getEmail());
            user2 = clickedItem.getUserName();
            username.setText(user2);
            fullName.setText(clickedItem.getFullName());

            deleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    proceed();
                }
            });

            dialog.show();
        } catch (Exception e){
            Log.e("Agent List", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void proceed() {
        try {
            loadingDialog.startLoading("Deleting");
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            deleteAgent();
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
            Log.e("Agent List", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteAgent() {
        try {
            String uName = null;
            try {
                uName = URLEncoder.encode(user2.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/delete.php?agent="+ uName +"";
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Toast.makeText(getActivity(), "Agent deleted", Toast.LENGTH_SHORT).show();

                            recyclerView.setAdapter(cAdapter);
                            refreshList();
                            loadingDialog.stopLoading();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoading();
                }
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Agent List", "exception", e);
            Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }
}