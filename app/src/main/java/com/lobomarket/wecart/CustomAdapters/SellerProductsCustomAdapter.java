package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.Product;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class SellerProductsCustomAdapter extends RecyclerView.Adapter<SellerProductsCustomAdapter.ViewHolder> {
        List<Product> users;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public SellerProductsCustomAdapter(Context context, List<Product> users) {
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.seller_product_layout_final, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public int getItemCount() {
            return users.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView pductName;
            public TextView productPrice;
            public ImageView userPhoto;

            public ViewHolder(final View itemView){
                    super(itemView);
                    pductName = itemView.findViewById(R.id.productTitle);
                    productPrice = itemView.findViewById(R.id.productPrice);
                    userPhoto = itemView.findViewById(R.id.productImage);

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

        @Override
        public void onBindViewHolder(@NonNull SellerProductsCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Product u = users.get(position);

            // added formatter here
            DecimalFormat formatter = new DecimalFormat("#,###.00");

            String stock = "₱" + formatter.format(u.getPrice());


            String pductName = u.getProductName();
            holder.pductName.setText(pductName);
            holder.productPrice.setText(stock);
            if(!(u.getProductPhoto().equals("null"))){
                Picasso.get().load(u.getProductPhoto()).into(holder.userPhoto);
            }

        }

    public void updateDataSet(List<Product> newResult){
        if(newResult!=null){
            users = newResult;
        }
        notifyDataSetChanged();
    }

    public void clear() {
        int size = users.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                users.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }
}
