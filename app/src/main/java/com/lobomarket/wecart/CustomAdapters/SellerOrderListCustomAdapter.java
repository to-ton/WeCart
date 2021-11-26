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
import com.lobomarket.wecart.Models.SellerOrderList;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SellerOrderListCustomAdapter extends RecyclerView.Adapter<SellerOrderListCustomAdapter.ViewHolder> {
        List<SellerOrderList> users;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public SellerOrderListCustomAdapter(Context context, List<SellerOrderList> users) {
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cardview_seller_orders, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public int getItemCount() {
            return users.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView fullName;
            public TextView contact;

            public ViewHolder(final View itemView){
                    super(itemView);
                fullName = itemView.findViewById(R.id.txtview1);
                contact = itemView.findViewById(R.id.txtview2);


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
        public void onBindViewHolder(@NonNull SellerOrderListCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            SellerOrderList u = users.get(position);
            holder.fullName.setText(u.getFullName());
            holder.contact.setText(u.getContact());
        }

    public void updateDataSet(List<SellerOrderList> newResult){
        if(newResult!=null){
            users = newResult;
        }
        notifyDataSetChanged();
    }
}
