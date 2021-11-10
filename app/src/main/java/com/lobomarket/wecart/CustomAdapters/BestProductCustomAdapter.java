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

import com.lobomarket.wecart.Models.Shop;
import com.lobomarket.wecart.Models.TopProducts;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BestProductCustomAdapter extends RecyclerView.Adapter<BestProductCustomAdapter.ViewHolder> {
        List<TopProducts> shop;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public BestProductCustomAdapter(Context context, List<TopProducts> shop) {
            this.shop = shop;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.best_product, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public int getItemCount() {
            return shop.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView shopName;
            public TextView typeOfShop, place;
            public ImageView shopPhoto;

            public ViewHolder(final View itemView){
                    super(itemView);
                    shopName = itemView.findViewById(R.id.shopName);
                    typeOfShop = itemView.findViewById(R.id.typeOfStore);
                    shopPhoto = itemView.findViewById(R.id.shopPhoto);
                    place = itemView.findViewById(R.id.shopName2);

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
        public void onBindViewHolder(@NonNull BestProductCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            TopProducts u = shop.get(position);

            holder.shopName.setText(u.getProduct_name());
            holder.typeOfShop.setText("Total Sold: " + u.getTotal_sold());
            holder.place.setText(String.valueOf(position + 1));

            if(!(u.getProduct_image().equals("null"))){
                Picasso.get().load(u.getProduct_image()).into(holder.shopPhoto);
            }
        }

    public void updateDataSet(List<TopProducts> newResult){
        if(newResult!=null){
            shop = newResult;
        }
        notifyDataSetChanged();
    }
}
