package com.lobomarket.wecart.UserHomeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lobomarket.wecart.TransactionScreenFragment.TransactionScreen;
import com.lobomarket.wecart.Models.Product;
import com.lobomarket.wecart.CustomAdapters.ProductsCustomAdapter;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.lobomarket.wecart.UserHomeScreen.fragmentHomeScreen.EXTRA_BUYER_USERNAME;
import static com.lobomarket.wecart.UserHomeScreen.fragmentHomeScreen.EXTRA_SELLER;
import static com.lobomarket.wecart.UserHomeScreen.fragmentHomeScreen.EXTRA_SHOP_NAME;
import static com.lobomarket.wecart.UserHomeScreen.fragmentHomeScreen.EXTRA_TYPE;

public class ShopsScreen extends AppCompatActivity implements ProductsCustomAdapter.OnItemClickListener {

    public String shopType, sName, sellerUsername, buyerUserName, image;
    private Button fruits, veggies, cannedGoods, beverages, dairy, hygiene, all, processedMeat, condiments, plastic, glass, back;
    private Button pork, chicken, beef, otherMeat, fish, crustaceans, shellFish, otherFish, dish, refreshments, baked, raw;
    private TextView noProduct, secondaryShopName;

    private RecyclerView recyclerView;
    private ProductsCustomAdapter cAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Product> product;

    ImageView shopBanner;

    public static final String EXTRA_PRODUCT_NAME = "name";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_PRICE = "price";
    public static final String EXTRA_PHOTO = "photo";
    public static final String EXTRA_SELLER_USER = "seller";
    public static final String EXTRA_BUYER = "buyer";
    public static final String EXTRA_SHOP_USERNAME = "fromShop";

    CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    //Constraint layout variables
    ConstraintLayout shopLoading, shopNoInternet, noProductsAvailabale, shopBannerAppBar;
    RelativeLayout collapseAppBar;

    NestedScrollView nestedScrollView;

    private int dataCount;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        try {
            //Constraint layout declaration
            secondaryShopName = findViewById(R.id.secondaryName);
            shopLoading = findViewById(R.id.loadingScreenShop);
            shopNoInternet = findViewById(R.id.noInternetShop);
            noProductsAvailabale = findViewById(R.id.noProductsAvailabale);
            shopBannerAppBar = findViewById(R.id.shopBannerAppBar);
            collapseAppBar = findViewById(R.id.collapseAppBar);

            fab = findViewById(R.id.fabCart);

            nestedScrollView = findViewById(R.id.scrollShop);

            shopBanner = findViewById(R.id.shopbanner);
            swipeRefreshLayout = findViewById(R.id.shopScreenRefresh);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            TextView name = findViewById(R.id.txtShopName);
            Intent intent = getIntent();
            fruits = findViewById(R.id.fruits);
            veggies = findViewById(R.id.vegetables);
            cannedGoods = findViewById(R.id.cannedGoods);
            beverages = findViewById(R.id.beverage);
            dairy = findViewById(R.id.dairy);
            hygiene = findViewById(R.id.hygiene);
            processedMeat = findViewById(R.id.processedMeat);
            condiments = findViewById(R.id.condiments);
            plastic = findViewById(R.id.plastic);
            glass = findViewById(R.id.glass);
            pork = findViewById(R.id.pork);
            chicken = findViewById(R.id.chicken);
            beef = findViewById(R.id.beef);
            otherMeat = findViewById(R.id.otherMeat);
            fish = findViewById(R.id.fish);
            crustaceans = findViewById(R.id.crustaceans);
            shellFish = findViewById(R.id.shellFish);
            otherFish = findViewById(R.id.otherSeaFood);
            dish = findViewById(R.id.dish);
            refreshments = findViewById(R.id.refreshment);
            baked = findViewById(R.id.bakedGoods);
            raw = findViewById(R.id.raw);
            noProduct = findViewById(R.id.noProducts);
            back = findViewById(R.id.btnSecondaryButton);

            all = findViewById(R.id.btnAll);

            sName = intent.getStringExtra(EXTRA_SHOP_NAME);
            shopType = intent.getStringExtra(EXTRA_TYPE);
            sellerUsername = intent.getStringExtra(EXTRA_SELLER);
            buyerUserName = intent.getStringExtra(EXTRA_BUYER_USERNAME);
            image = intent.getStringExtra("shopBanner");

            recyclerView = findViewById(R.id.shopList);
            product = new ArrayList<>();

            layoutManager = new GridLayoutManager(ShopsScreen.this, 2, GridLayoutManager.VERTICAL, false);
            cAdapter = new ProductsCustomAdapter(ShopsScreen.this, product);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cAdapter);

            name.setText(sName);
            secondaryShopName.setText(sName);
            Picasso.get().load(image).into(shopBanner);
            checkShopType();

            checkInternet();

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    checkInternet();
                }
            });


            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) {
                        fab.hide();
                        collapseAppBar.setVisibility(View.VISIBLE);
                        //Toast.makeText(ShopsScreen.this, "Hello", Toast.LENGTH_SHORT).show();
                    } else {
                        fab.show();
                        collapseAppBar.setVisibility(View.GONE);
                        //Toast.makeText(ShopsScreen.this, "Hi", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e){
            Log.e("Shop", "exception", e);
            Toast.makeText(ShopsScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }
    }

    //onClick method for every category button
    public void categoryClicked(View v){

        try {
            switch(v.getId()){
                case R.id.btnAll:
                    showAllProducts();
                    break;
                case R.id.fruits:
                    sortProduct(sellerUsername, "fruits");
                    break;
                case R.id.vegetables:
                    sortProduct(sellerUsername, "Vegetables");
                    break;
                case R.id.cannedGoods:
                    sortProduct(sellerUsername, "Canned Goods");
                    break;
                case R.id.beverage:
                    sortProduct(sellerUsername, "Beverage");
                    break;
                case R.id.dairy:
                    sortProduct(sellerUsername, "Dairy");
                    break;
                case R.id.processedMeat:
                    sortProduct(sellerUsername, "Processed Meat");
                    break;
                case R.id.hygiene:
                    sortProduct(sellerUsername, "Hygiene");
                    break;
                case R.id.condiments:
                    sortProduct(sellerUsername, "Condiments");
                    break;
                case R.id.plastic:
                    sortProduct(sellerUsername, "Plastic ware");
                    break;
                case R.id.glass:
                    sortProduct(sellerUsername, "Glass ware");
                    break;
                //break
                case R.id.pork:
                    sortProduct(sellerUsername, "Pork Meat");
                    break;
                case R.id.chicken:
                    sortProduct(sellerUsername, "Chicken Meat");
                    break;
                case R.id.beef:
                    sortProduct(sellerUsername, "Beef Meat");
                    break;
                case R.id.otherMeat:
                    sortProduct(sellerUsername, "Other meats");
                    break;
                case R.id.fish:
                    sortProduct(sellerUsername, "Fish");
                    break;
                case R.id.crustaceans:
                    sortProduct(sellerUsername, "Crustaceans");
                    break;
                case R.id.shellFish:
                    sortProduct(sellerUsername, "Shellfish");
                    break;
                case R.id.otherSeaFood:
                    sortProduct(sellerUsername, "Other Sea Food");
                    break;
                case R.id.dish:
                    sortProduct(sellerUsername, "Dish");
                    break;
                case R.id.refreshment:
                    sortProduct(sellerUsername, "Refreshments");
                    break;
                case R.id.raw:
                    sortProduct(sellerUsername, "Raw Ingredients");
                    break;
                case R.id.bakedGoods:
                    sortProduct(sellerUsername, "Baked goods");
                    break;
            }
        } catch (Exception e){
            Log.e("Shop", "exception", e);
            Toast.makeText(ShopsScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();

        }
    }

    //Cart
    public void viewCart(View v){
        try {
            Intent intent = new Intent(ShopsScreen.this, TransactionScreen.class);
            intent.putExtra(EXTRA_SHOP_USERNAME, buyerUserName);
            startActivity(intent);
        } catch (Exception e){
            Log.e("Shop", "exception", e);
            Toast.makeText(ShopsScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    //backToHomeScreen
    public void back(View view){
        onBackPressed();
    }

    public void checkShopType(){
        try {
            switch (shopType){
                case "Fruits and Vegetables":
                    all.setVisibility(View.VISIBLE);
                    fruits.setVisibility(View.VISIBLE);
                    veggies.setVisibility(View.VISIBLE);
                    break;
                case "Meat Shop":
                    all.setVisibility(View.VISIBLE);
                    pork.setVisibility(View.VISIBLE);
                    chicken.setVisibility(View.VISIBLE);
                    beef.setVisibility(View.VISIBLE);
                    otherMeat.setVisibility(View.VISIBLE);
                    break;
                case "Sea Food Shop":
                    all.setVisibility(View.VISIBLE);
                    fish.setVisibility(View.VISIBLE);
                    crustaceans.setVisibility(View.VISIBLE);
                    shellFish.setVisibility(View.VISIBLE);
                    otherFish.setVisibility(View.VISIBLE);
                    break;
                case "Bakery":
                    all.setVisibility(View.VISIBLE);
                    baked.setVisibility(View.VISIBLE);
                    raw.setVisibility(View.VISIBLE);
                    break;
                case "Eatery":
                    all.setVisibility(View.VISIBLE);
                    dish.setVisibility(View.VISIBLE);
                    refreshments.setVisibility(View.VISIBLE);
                case "Others":
                    all.setVisibility(View.VISIBLE);
                    break;
                case "Grocery":
                    all.setVisibility(View.VISIBLE);
                    cannedGoods.setVisibility(View.VISIBLE);
                    beverages.setVisibility(View.VISIBLE);
                    hygiene.setVisibility(View.VISIBLE);
                    dairy.setVisibility(View.VISIBLE);
                    processedMeat.setVisibility(View.VISIBLE);
                    condiments.setVisibility(View.VISIBLE);
                    break;
                case "Plastic ware/Glass ware":
                    all.setVisibility(View.VISIBLE);
                    plastic.setVisibility(View.VISIBLE);
                    glass.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception e){
            Log.e("Shop", "exception", e);
            Toast.makeText(ShopsScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showAllProducts() {
        try {
            String tx = URLEncoder.encode(sellerUsername.replace("'","\\'"), "utf-8");
            swipeRefreshLayout.setRefreshing(true);
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/showproducts.php?seller="+ tx +"";
            product.clear();
            RequestQueue requestQueue = Volley.newRequestQueue(ShopsScreen.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            noProduct.setVisibility(View.GONE);
                            noProductsAvailabale.setVisibility(View.GONE);
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    Product u = new Product();
                                    u.setProductName(userObject.getString("product_name"));
                                    u.setProductPrice(userObject.getString("product_price"));
                                    u.setProductPhoto(userObject.getString("product_image"));
                                    u.setDescription(userObject.getString("description"));
                                    u.setStock(userObject.getString("stock"));

                                    product.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(product);
                            dataCount = cAdapter.getItemCount();
                            cAdapter.setOnItemClickListener(ShopsScreen.this);
                            collapseAppBar.setVisibility(View.GONE);
                            shopLoading.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", "error" + error.getMessage());
                    noProduct.setVisibility(View.VISIBLE);
                    collapseAppBar.setVisibility(View.GONE);
                    noProductsAvailabale.setVisibility(View.VISIBLE);
                    shopLoading.setVisibility(View.GONE);
                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Shop", "exception", e);
            Toast.makeText(ShopsScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void sortProduct(String sellerName, String productType) {
        try {
            fab.show();
            swipeRefreshLayout.setRefreshing(true);
            String txtseller = URLEncoder.encode(sellerName.replace("'","\\'"), "utf-8");
            String txtproducttype = URLEncoder.encode(productType.replace("'","\\'"), "utf-8");
            String JSON_URL = "https://jarvis.danlyt.ninja/wecart-api/v1/showproducts.php?seller="+ txtseller +"&product_type="+ txtproducttype +"";
            product.clear();
            RequestQueue requestQueue = Volley.newRequestQueue(ShopsScreen.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            noProductsAvailabale.setVisibility(View.GONE);
                            noProduct.setVisibility(View.GONE);
                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject userObject = response.getJSONObject(i);

                                    Product u = new Product();
                                    u.setProductName(userObject.getString("product_name"));
                                    u.setProductPrice(userObject.getString("product_price"));
                                    u.setProductPhoto(userObject.getString("product_image"));
                                    u.setDescription(userObject.getString("description"));
                                    u.setStock(userObject.getString("stock"));

                                    product.add(u);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            cAdapter.updateDataSet(product);
                            dataCount = cAdapter.getItemCount();
                            cAdapter.setOnItemClickListener(ShopsScreen.this);
                            shopLoading.setVisibility(View.GONE);
                            collapseAppBar.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MESSAGE", error.getMessage());
                    noProduct.setVisibility(View.VISIBLE);
                    collapseAppBar.setVisibility(View.GONE);
                    noProductsAvailabale.setVisibility(View.VISIBLE);
                    shopLoading.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            );

            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.e("Shop", "exception", e);
            Toast.makeText(ShopsScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClick(int position) {
        try {
            Product clickedItem = product.get(position);
            String productname = clickedItem.getProductName();
            String description = clickedItem.getDescription();
            String price = clickedItem.getProductPrice();
            String image = clickedItem.getProductPhoto();

            Intent intent = new Intent(ShopsScreen.this, ProductDetails.class);

            intent.putExtra(EXTRA_PRODUCT_NAME, productname);
            intent.putExtra(EXTRA_DESCRIPTION, description);
            intent.putExtra(EXTRA_PRICE, price);
            intent.putExtra(EXTRA_PHOTO, image);
            intent.putExtra(EXTRA_SELLER_USER, sellerUsername);
            intent.putExtra(EXTRA_BUYER, buyerUserName);
            intent.putExtra("EXTRA_STOCK", clickedItem.getStock());


            startActivity(intent);
        } catch (Exception e){
            Log.e("Shop", "exception", e);
            Toast.makeText(ShopsScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }


    }


    private void checkInternet() {
        try {
            swipeRefreshLayout.setRefreshing(false);
            shopLoading.setVisibility(View.VISIBLE);
            collapseAppBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://www.google.com/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //With internet
                            shopNoInternet.setVisibility(View.GONE);
                            showAllProducts();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //No internet
                    swipeRefreshLayout.setRefreshing(false);
                    shopLoading.setVisibility(View.GONE);
                    shopNoInternet.setVisibility(View.VISIBLE);
                    collapseAppBar.setVisibility(View.VISIBLE);
                }
            }
            );

            Volley.newRequestQueue(ShopsScreen.this).add(stringRequest);
        } catch (Exception e){
            Log.e("Shop", "exception", e);
            Toast.makeText(ShopsScreen.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }
}