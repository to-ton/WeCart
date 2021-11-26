package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.TrackOrder;
import com.lobomarket.wecart.Models.TransactionHistory;
import com.lobomarket.wecart.R;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

public class TransactionHistoryCustomAdapter extends RecyclerView.Adapter<TransactionHistoryCustomAdapter.ViewHolder> {
        List<TransactionHistory> users;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public TransactionHistoryCustomAdapter(Context context, List<TransactionHistory> users) {
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.transaction_history_cardview, parent, false);
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
            public TextView date;

            public ViewHolder(final View itemView){
                    super(itemView);
                fullName = itemView.findViewById(R.id.shopName);
                contact = itemView.findViewById(R.id.totalPrice_history);
                date = itemView.findViewById(R.id.orderDate);


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
        public void onBindViewHolder(@NonNull TransactionHistoryCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            TransactionHistory u = users.get(position);
            holder.fullName.setText(u.getStoreName());
            double price = Double.parseDouble(u.getFinalTotal());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            holder.contact.setText("₱" + formatter.format(price));
            holder.date.setText(u.getDate());
        }

    public void updateDataSet(List<TransactionHistory> newResult){
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
