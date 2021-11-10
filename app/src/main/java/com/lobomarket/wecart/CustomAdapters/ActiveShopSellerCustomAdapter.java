package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.SellerModel;
import com.lobomarket.wecart.R;

import org.w3c.dom.Text;

import java.util.List;

public class ActiveShopSellerCustomAdapter extends RecyclerView.Adapter<ActiveShopSellerCustomAdapter.ViewHolder> {
        List<SellerModel> users;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public ActiveShopSellerCustomAdapter(Context context, List<SellerModel> users) {
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cardview_list_for_admin, parent, false);
            ViewHolder vh = new ViewHolder(itemView);
            return vh;
        }

        @Override
        public int getItemCount() {
            return users.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public TextView subname;

            public ViewHolder(final View itemView){
                    super(itemView);
                    name = itemView.findViewById(R.id.person_name);
                    subname = itemView.findViewById(R.id.subtext);

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
        public void onBindViewHolder(@NonNull ActiveShopSellerCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            SellerModel u = users.get(position);

            holder.name.setText(u.getShopName());
            if(!(u.getFullname().equals("null"))){
                holder.subname.setText(u.getFullname());
            }

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
