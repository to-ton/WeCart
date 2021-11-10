package com.lobomarket.wecart.AdminHomeScreen;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.lobomarket.wecart.LoginSignUpScreen.LoginSignupScreen;
import com.lobomarket.wecart.LoginLogOutSession.SessionManagement;
import com.lobomarket.wecart.Models.AgentModel;
import com.lobomarket.wecart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navigationView;
    private DrawerLayout navDrawer;
    private NavController navController;
    private NavOptions navOptions;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);
        try {
            navigationView = findViewById(R.id.adminSidebar);
            navDrawer = findViewById(R.id.drawer);
            navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContAdmin);

            setNavigationViewListener();
        } catch (Exception e) {
            Log.e("Admin Activity", "exception", e);
            Toast.makeText(AdminScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //close navigation drawer
        if(navDrawer.isDrawerOpen(GravityCompat.END)){
            navDrawer.closeDrawer(GravityCompat.END);
        }
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.fragmentAddSellers:
                navigate(R.id.action_fragmentDashboard2_to_fragmentAddSellers2);
                break;
            case R.id.fragmentsActiveAgents:
                navigate(R.id.action_fragmentDashboard2_to_fragmentsActiveAgents);
                break;
            case R.id.fragmentSalesReport:
                navigate(R.id.action_fragmentDashboard2_to_fragmentsSellerSalesReport);
                break;
            case R.id.adminLogout:
                dialogBoxLogout();
                break;
        }
        return true;
    }

    private void setNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void navigate(@IdRes int viewId){
        try {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContAdmin);
            navController = navHostFragment.getNavController();

            navController.navigate(viewId);
            navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } catch (Exception e) {
            Log.e("Admin Activity", "exception", e);
            Toast.makeText(AdminScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBack(@IdRes int viewId){
        try {
            navOptions = new NavOptions.Builder().setPopUpTo(R.id.fragmentDashboard2, true)
                    .setEnterAnim(R.anim.slide_left_to_right)
                    .setExitAnim(R.anim.wait_anim)
                    .setPopEnterAnim(R.anim.wait_anim)
                    .setPopExitAnim(R.anim.slide_l2r_reverse)
                    .build();
            navController = navHostFragment.getNavController();

            navController.navigate(viewId, null, navOptions);
            navigationView.getMenu().getItem(0).setChecked(true);
            navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } catch (Exception e){
            Log.e("Admin Activity", "exception", e);
            Toast.makeText(AdminScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void dialogBoxLogout(){
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent logout = new Intent(AdminScreen.this, LoginSignupScreen.class);

                            SessionManagement sessionManagement = new SessionManagement(AdminScreen.this);
                            sessionManagement.removeSession();

                            startActivity(logout);
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            navigationView.getMenu().getItem(0).setChecked(true);
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to logout?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } catch (Exception e){
            Log.e("Admin Activity", "exception", e);
            Toast.makeText(AdminScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    public void backtoDashboard(View v){
        try {
            switch(v.getId()){
                case R.id.btnBackToDashboard:
                    navigateBack(R.id.action_fragmentAddSellers2_to_fragmentDashboard2);
                    break;
                case R.id.btnAgentToDashboard:
                    navigateBack(R.id.action_fragmentsActiveAgents_to_fragmentDashboard2);
                    break;
                case R.id.btnSalesToDashboard:
                    navigateBack(R.id.action_fragmentsSellerSalesReport_to_fragmentDashboard2);
                    break;
            }
        } catch (Exception e){
            Log.e("Admin Activity", "exception", e);
            Toast.makeText(AdminScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    public void adminSidebar(View view){
        navDrawer.openDrawer(GravityCompat.END);
    }

    public void backToSellers(View view){

    }


    public void add(View view){
        Intent intent = new Intent(AdminScreen.this, AddSellers.class);
        startActivity(intent);
    }

    public void addAgent(View view) {
        Intent intent = new Intent(AdminScreen.this, AddAgent.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void closeAddAgent(View view) {
    }
}