package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.SellerModel;
import com.lobomarket.wecart.R;

import java.util.List;

public class SellerReportCustomAdapter extends RecyclerView.Adapter<SellerReportCustomAdapter.ViewHolder> {
        List<SellerModel> users;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public SellerReportCustomAdapter(Context context, List<SellerModel> users) {
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cardview_seller_list_report, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public int getItemCount() {
            return users.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name;

            public ViewHolder(final View itemView){
                    super(itemView);
                    name = itemView.findViewById(R.id.dashboardStoreName);


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
        public void onBindViewHolder(@NonNull SellerReportCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            SellerModel u = users.get(position);

            holder.name.setText(u.getShopName());

        }

    public void updateDataSet(List<SellerModel> newResult){
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
