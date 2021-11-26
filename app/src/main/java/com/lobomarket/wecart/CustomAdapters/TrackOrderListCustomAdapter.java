package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.SellerOrderList;
import com.lobomarket.wecart.Models.TrackOrder;
import com.lobomarket.wecart.R;

import java.util.List;

public class TrackOrderListCustomAdapter extends RecyclerView.Adapter<TrackOrderListCustomAdapter.ViewHolder> {
        List<TrackOrder> users;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public TrackOrderListCustomAdapter(Context context, List<TrackOrder> users) {
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cardview_track_order, parent, false);
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
                fullName = itemView.findViewById(R.id.trackingNum);
                contact = itemView.findViewById(R.id.status);


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
        public void onBindViewHolder(@NonNull TrackOrderListCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            TrackOrder u = users.get(position);
            holder.fullName.setText(u.getTrackingId());
            holder.contact.setText(u.getDeliveryStatus());
        }

    public void updateDataSet(List<TrackOrder> newResult){
        if(newResult!=null){
            users = newResult;
        }
        notifyDataSetChanged();
    }
}
