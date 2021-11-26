package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.AgentCustomerList;
import com.lobomarket.wecart.Models.SellerOrderList;
import com.lobomarket.wecart.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AgentOrderListCustomAdapter extends RecyclerView.Adapter<AgentOrderListCustomAdapter.ViewHolder>{
        List<AgentCustomerList> users;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public AgentOrderListCustomAdapter(Context context, List<AgentCustomerList> users) {
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cardview_agent_customers_list, parent, false);
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
                fullName = itemView.findViewById(R.id.buyer_name);
                contact = itemView.findViewById(R.id.txtviewAd);


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
        public void onBindViewHolder(@NonNull AgentOrderListCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            AgentCustomerList u = users.get(position);
            holder.fullName.setText(u.getTrackingId());
            holder.contact.setText(u.getCustomerAddress());
        }

    public void updateDataSet(List<AgentCustomerList> newResult){
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
