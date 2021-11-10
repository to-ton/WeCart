package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lobomarket.wecart.AgentHomeScreen.AgentGroceryList;
import com.lobomarket.wecart.Models.Shop;
import com.lobomarket.wecart.Models.ViewCartModel;
import com.lobomarket.wecart.R;
import com.lobomarket.wecart.SellerHomeScreen.EditDeleteProducts;
import com.lobomarket.wecart.TransactionScreenFragment.TransactionScreen;
import com.lobomarket.wecart.UserHomeScreen.ProductDetails;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

public class ViewCartCustomAdapter extends RecyclerView.Adapter<ViewCartCustomAdapter.ViewHolder> {
        List<ViewCartModel> cart;
        Context context;
        private OnItemClickListener mListener;
        private MyCallback myCallback = null;


        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public interface MyCallback {
            void addQuantity(double newPrice);
            void subtractQuantity(double newPrice);
        }


        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public void setString(MyCallback mCallback){
            this.myCallback = mCallback;
        }

        public ViewCartCustomAdapter(Context context, List<ViewCartModel> cart) {
            this.cart = cart;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cardview_cart, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public int getItemCount() {
            return cart.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView productName;
            public TextView price;
            public TextView quantity;
            public Button btnAdd, btnMinus;

            public ViewHolder(final View itemView){
                    super(itemView);
                    productName = itemView.findViewById(R.id.carProductName);
                    price = itemView.findViewById(R.id.carProductPrice);
                    quantity = itemView.findViewById(R.id.carQuantity);
                    btnAdd = itemView.findViewById(R.id.btnPlus);
                    btnMinus =itemView.findViewById(R.id.btnminus);

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    mListener.onItemClick(position);
                                }
                            }
                        }
                    });


                }
        }

        String txtProductName, quantity;
        double totalAmount;

        @Override
        public void onBindViewHolder(@NonNull ViewCartCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            DecimalFormat formatter = new DecimalFormat("#,###.00");

            ViewCartModel u = cart.get(position);
            holder.productName.setText(u.getProductName());
            holder.quantity.setText(u.getQuantity());

            double price = Double.parseDouble(u.getTotal());
            holder.price.setText("₱" + formatter.format(price));
            String seller = u.getSeller();
            String buyer = u.getUsername();




            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txtProductName = holder.productName.getText().toString();
                    int currentQuantity = Integer.parseInt(holder.quantity.getText().toString());
                    int origPrice = Integer.parseInt(u.getProductPrice());
                    int currentStock = Integer.parseInt(u.getStock());

                    if (currentQuantity < currentStock){
                        currentQuantity++;

                        double newPrice = currentQuantity * origPrice;
                        Log.v("TEST", "Updated " + currentQuantity + "New Price: " + newPrice);

                        holder.price.setText("₱" + formatter.format(newPrice));
                        holder.quantity.setText(String.valueOf(currentQuantity));

                        if (myCallback != null) {
                            myCallback.addQuantity(origPrice);
                        }

                        String txtBuyer = null;
                        String txtSeller = null;
                        String txtProduct = null;
                        String quantity = null;
                        try {
                            txtBuyer = URLEncoder.encode(buyer, "utf-8");
                            txtSeller = URLEncoder.encode(seller, "utf-8");
                            txtProduct = URLEncoder.encode(txtProductName, "utf-8");
                            quantity = URLEncoder.encode(String.valueOf(currentQuantity), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String url = "https://wecart.gq/wecart-api/show-orders.php?update&username="+ txtBuyer +"&product_name="+ txtProduct +"&seller_uname="+ txtSeller +"&quantity="+ quantity +"";
                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST,
                                url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //With internet
                                        Log.v("TEST", "Updated " + response );
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //No internet
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                        );
                        stringRequest.setTag("CANCEL");
                        requestQueue.add(stringRequest);

                    } else {
                        Toast.makeText(context, "You've reached the maximum order for this item", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txtProductName = holder.productName.getText().toString();
                    String itemPrice = holder.price.getText().toString().replaceAll("[^\\d.]", "");
                    double itemPriceDouble = Double.parseDouble(itemPrice);


                    int currentQuantity = Integer.parseInt(holder.quantity.getText().toString());
                    int origPrice = Integer.parseInt(u.getProductPrice());

                    if(currentQuantity > 1){
                        currentQuantity--;
                        itemPriceDouble -= origPrice;
                        holder.price.setText("₱" + formatter.format(itemPriceDouble));
                        holder.quantity.setText(String.valueOf(currentQuantity));
                        Log.v("TEST", "Updated " + currentQuantity + "Orig price: " + origPrice);

                        if (myCallback != null) {
                            myCallback.subtractQuantity(origPrice);
                        }

                        String txtBuyer = null;
                        String txtSeller = null;
                        String txtProduct = null;
                        String quantity = null;
                        try {
                            txtBuyer = URLEncoder.encode(buyer, "utf-8");
                            txtSeller = URLEncoder.encode(seller, "utf-8");
                            txtProduct = URLEncoder.encode(txtProductName, "utf-8");
                            quantity = URLEncoder.encode(String.valueOf(currentQuantity), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String url = "https://wecart.gq/wecart-api/show-orders.php?update&username="+ txtBuyer +"&product_name="+ txtProduct +"&seller_uname="+ txtSeller +"&quantity="+ quantity +"";
                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST,
                                url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //With internet
                                        Log.v("TEST", "Updated " + response );
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //No internet
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                        );
                        stringRequest.setTag("CANCEL");
                        requestQueue.add(stringRequest);

                    } else {
                        String txtBuyer = null;
                        String txtSeller = null;
                        String txtProduct = null;
                        String quantity = null;
                        try {
                            txtBuyer = URLEncoder.encode(buyer.replace("'","\\'"), "utf-8");
                            txtSeller = URLEncoder.encode(seller.replace("'","\\'"), "utf-8");
                            txtProduct = URLEncoder.encode(txtProductName.replace("'","\\'"), "utf-8");
                            quantity = URLEncoder.encode(String.valueOf(currentQuantity), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String url = "https://wecart.gq/wecart-api/delete.php?buyer_user=" + txtBuyer + "&seller_user=" + txtSeller + "&product=" + txtProduct + "";
                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST,
                                url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //With internet
                                        Log.v("TEST", "Deleted " + response);
                                        cart.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeRemoved(position, cart.size());

                                        if (myCallback != null) {
                                            myCallback.subtractQuantity(origPrice);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //No internet
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                        );
                        stringRequest.setTag("CANCEL");
                        requestQueue.add(stringRequest);
                    }
                }
            });


        }

    public void updateDataSet(List<ViewCartModel> newResult){
        if(newResult!=null){
            cart = newResult;
        }
        notifyDataSetChanged();
    }

    public ViewCartModel getItemAt(int position){
            return cart.get(position);
    }


}
