package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.OrderBreakdown;
import com.lobomarket.wecart.Models.OrderSummary;
import com.lobomarket.wecart.R;

import java.text.DecimalFormat;
import java.util.List;

public class OrderSummaryCustomAdapter extends RecyclerView.Adapter<OrderSummaryCustomAdapter.ViewHolder> {
        List<OrderSummary> cart;
        List<OrderBreakdown> cart2;
        Context context;

    public OrderSummaryCustomAdapter(List<OrderSummary> cart, List<OrderBreakdown> cart2, Context context) {
        this.cart = cart;
        this.cart2 = cart2;
        this.context = context;
    }

    public OrderSummaryCustomAdapter(Context context, List<OrderSummary> cart) {
            this.cart = cart;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cardview_orders, parent, false);
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
            public RecyclerView rv;

            public ViewHolder(final View itemView){
                    super(itemView);
                    productName = itemView.findViewById(R.id.orderShopName);
                    price = itemView.findViewById(R.id.orderSummaryPrice);
                    rv = itemView.findViewById(R.id.orders_list);
                }
        }

        String txtProductName, quantity;
    OrderBreakDownCustomAdapter breakDownCustomAdapter;
        @Override
        public void onBindViewHolder(@NonNull OrderSummaryCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            OrderSummary u = cart.get(position);

            holder.productName.setText(u.getStoreName());
            double price = Double.parseDouble(u.getFinalTotal());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            holder.price.setText("₱" + formatter.format(price));
        }

    public void updateDataSet(List<OrderSummary> newResult){
        if(newResult!=null){
            cart = newResult;
        }
        notifyDataSetChanged();

    }



}
