package com.lobomarket.wecart.testData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.Users;
import com.lobomarket.wecart.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    List<Users> users;
    LayoutInflater inflater;
    Context context;

    public CustomAdapter(Context context, List<Users> users) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.product_layout, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pductName;
        public TextView stock;
        public ImageView userPhoto;

        public ViewHolder(final View itemView){
            super(itemView);
            pductName = itemView.findViewById(R.id.productTitle);
            stock = itemView.findViewById(R.id.productStock);
            userPhoto = itemView.findViewById(R.id.productImage);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users u = users.get(position);
        String stock = "Stock: " + String.valueOf(u.getAge());

        String pductName = u.getFname() + " " + u.getLname();
        holder.pductName.setText(pductName);
        holder.stock.setText(stock);
        Picasso.get().load(u.getUserImg()).into(holder.userPhoto);

    }

    public void updateDataSet(List<Users> newResult){
        if(newResult!=null){
            users = newResult;
        }
        notifyDataSetChanged();
    }
}

