package com.lobomarket.wecart.UserHomeScreen;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.lobomarket.wecart.LoadingDialog;
import com.lobomarket.wecart.UserHomeScreen.UserHomeScreen;
import com.lobomarket.wecart.LoginLogOutSession.SessionManagement;
import com.lobomarket.wecart.LoginSignUpScreen.LoginSignupScreen;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.TransactionScreenFragment.TransactionScreen;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UserHomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout navDrawer;
    private NavController navController;
    private NavOptions navOptions;
    private NavigationView navigationView;
    private NavHostFragment navHostFragment;
    public String username;
    public static final String EXTRA_HOMESCREEN_USERNAME = "FromHomeScreen";

    LoadingDialog loadingDialog;

    public UserHomeScreen(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        try {
            navDrawer = findViewById(R.id.drawerHome);
            navigationView = (NavigationView) findViewById(R.id.sidebar);
            navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView3);
            loadingDialog = new LoadingDialog(UserHomeScreen.this);

            //method declaration for navigation drawer options
            setNavigationViewListener();

            NavigationView navigationView = (NavigationView) findViewById(R.id.sidebar);
            navigationView.getMenu().getItem(0).setChecked(true);
            Intent intent = getIntent();
            username = intent.getStringExtra("username");

            Toast.makeText(UserHomeScreen.this, "Welcome, " + username, Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Log.e("HomeScreen Activity", "exception", e);
            Toast.makeText(UserHomeScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    //to apply the navigation path of 'home_activity_navigation.xml' from Home Screen to other Fragments
    private void navigate(@IdRes int viewId){
        try {
            NavHostFragment navHostFragment =
                    (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView3);
            navController = navHostFragment.getNavController();
            Bundle bundle = new Bundle();
            bundle.putString("uname", username);

            navController.navigate(viewId, bundle);
            navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } catch (Exception e){
            Log.e("HomeScreen Activity", "exception", e);
            Toast.makeText(UserHomeScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }


    }

    //to apply the navigation path of 'home_activity_navigation.xml' from other Fragments to Home Screen
    private void navigateBack(@IdRes int viewId){
        try {
            navOptions = new NavOptions.Builder().setPopUpTo(R.id.fragmentHomeScreen, true)
                    .setEnterAnim(R.anim.slide_left_to_right)
                    .setExitAnim(R.anim.wait_anim)
                    .setPopEnterAnim(R.anim.wait_anim)
                    .setPopExitAnim(R.anim.slide_l2r_reverse)
                    .build();
            navController = navHostFragment.getNavController();

            navController.navigate(viewId, null, navOptions);
            navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } catch (Exception e){
            Log.e("HomeScreen Activity", "exception", e);
            Toast.makeText(UserHomeScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    //needed method for the implementation of NavigationView.OnNavigationItemSelectedListener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //close navigation drawer
        if(navDrawer.isDrawerOpen(GravityCompat.END)){
            navDrawer.closeDrawer(GravityCompat.END);
        }

        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.fragmentAccount:
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView3);
                navController = navHostFragment.getNavController();

                Bundle bundle = new Bundle();
                bundle.putString("uName", username);

                navController.navigate(R.id.action_fragmentHomeScreen_to_fragmentAccount, bundle);
                break;
            case R.id.viewCartActivity:
                Intent intent = new Intent(UserHomeScreen.this, TransactionScreen.class);
                intent.putExtra(EXTRA_HOMESCREEN_USERNAME, username);
                startActivity(intent);
                break;
            case R.id.fragmentCustomerCare:
                navigate(R.id.action_fragmentHomeScreen_to_fragmentCustomerCare);
                break;
            case R.id.trackOrder:
                navigate(R.id.action_fragmentHomeScreen_to_fragmentTrackOrder);
                break;
            case R.id.theme:
                navigate(R.id.action_fragmentHomeScreen_to_fragmentAppTheme);
                break;
            case R.id.nav_logout:
                dialogBoxLogout();
                break;
        }
        return true;
    }

    //this method sets the onclick listener
    private void setNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    //logout confirmation
    private void dialogBoxLogout(){

        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            logout();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            NavigationView navigationView = (NavigationView) findViewById(R.id.sidebar);
                            navigationView.getMenu().getItem(0).setChecked(true);
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to logout?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } catch (Exception e){
            Log.e("HomeScreen Activity", "exception", e);
            Toast.makeText(UserHomeScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    public void logout(){
        try {
            loadingDialog.startLoading("Logging out");
            String txtuser =null;
            try {
                txtuser = URLEncoder.encode(username.replace("'","\\'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String JSON_URL = "https://wecart.gq/wecart-api/logout.php?username="+ txtuser +"";
            RequestQueue requestQueue = Volley.newRequestQueue(UserHomeScreen.this);
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    JSON_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("TEST", response);
                            Intent logout = new Intent(UserHomeScreen.this, LoginSignupScreen.class);
                            loadingDialog.stopLoading();
                            SessionManagement sessionManagement = new SessionManagement(UserHomeScreen.this);
                            sessionManagement.removeSession();
                            startActivity(logout);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("TEST", "failed: " + error.getMessage());
                    Toast.makeText(UserHomeScreen.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoading();
                }
            }
            );

            requestQueue.add(postRequest);
        } catch (Exception e){
            Log.e("HomeScreen Activity", "exception", e);
            Toast.makeText(UserHomeScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    //Button onClicks event
    //to go back to Home Screen Fragment
    public void backToHomeFragment(View v){
        try {
            switch(v.getId()){
                case R.id.btnAccountToHome:
                    navigateBack(R.id.action_fragmentAccount_to_fragmentHomeScreen);
                    break;
                case R.id.btnCustomerToHome:
                    navigateBack(R.id.action_fragmentCustomerCare_to_fragmentHomeScreen);
                    break;
                case R.id.btnTrackOrderToHome:
                    navigateBack(R.id.action_fragmentTrackOrder_to_fragmentHomeScreen);
                    break;
                case R.id.btnAppThemeToHome:
                    navigateBack(R.id.action_fragmentAppTheme_to_fragmentHomeScreen);
                    break;
            }

            NavigationView navigationView = (NavigationView) findViewById(R.id.sidebar);
            navigationView.getMenu().getItem(0).setChecked(true);
        } catch (Exception e){
            Log.e("HomeScreen Activity", "exception", e);
            Toast.makeText(UserHomeScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    //Search bar
    public void search(View v){
        navigate(R.id.action_fragmentHomeScreen_to_fragmentSearch);
    }

    //to open sidebar by click
    public void sidebar(View view){
        navDrawer.openDrawer(GravityCompat.END);
    }

    public void backFromSearch(View view){
        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        navigateBack(R.id.action_fragmentSearch_to_fragmentHomeScreen);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }
}