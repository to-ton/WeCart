package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.OrderBreakdown;
import com.lobomarket.wecart.R;

import java.text.DecimalFormat;
import java.util.List;

public class OrderBreakDownAgentCustomAdapter extends RecyclerView.Adapter<OrderBreakDownAgentCustomAdapter.ViewHolder> {
        List<OrderBreakdown> cart;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }


        public OrderBreakDownAgentCustomAdapter(Context context, List<OrderBreakdown> cart) {
            this.cart = cart;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cardview_orders_list_agent, parent, false);
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
            public TextView store;
            public TextView status;

            public ViewHolder(final View itemView){
                    super(itemView);
                    productName = itemView.findViewById(R.id.itemName);
                    price = itemView.findViewById(R.id.itemPrice);
                    quantity = itemView.findViewById(R.id.itemQuantity);
                    store = itemView.findViewById(R.id.textView37);
                    status = itemView.findViewById(R.id.sellerStatus);

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

        @Override
        public void onBindViewHolder(@NonNull OrderBreakDownAgentCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            OrderBreakdown u = cart.get(position);


            holder.productName.setText(u.getProductName());
            double price = Double.parseDouble(u.getProductPrice());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            holder.price.setText("₱" + formatter.format(price));
            holder.quantity.setText(u.getQuantity());
            String stallNum = u.getStall();

            try {
                if(!(stallNum.equals("null"))){
                    holder.store.setText(u.getStoreName() + " - " + stallNum);
                } else {
                    holder.store.setText(u.getStoreName());
                }
            } catch (NullPointerException e){
                holder.store.setText(u.getStoreName());
            }



            holder.status.setText(u.getDeliveryStatus());
        }

    public void updateDataSet(List<OrderBreakdown> newResult){
        if(newResult!=null){
            cart = newResult;
        }
        notifyDataSetChanged();
    }

}
