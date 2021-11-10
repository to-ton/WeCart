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

import com.lobomarket.wecart.Models.AgentModel;
import com.lobomarket.wecart.R;

import java.util.List;

public class AgentCustomAdapter extends RecyclerView.Adapter<AgentCustomAdapter.ViewHolder> {
        List<AgentModel> agent;
        Context context;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public AgentCustomAdapter(Context context, List<AgentModel> agent) {
            this.agent = agent;
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
            return agent.size();
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
        public void onBindViewHolder(@NonNull AgentCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            AgentModel u = agent.get(position);

            holder.name.setText(u.getFullName());
            holder.subname.setText(u.getEmail() + " / " + u.getContactNum());
        }

    public void updateDataSet(List<AgentModel> newResult){
        if(newResult!=null){
            agent = newResult;
        }
        notifyDataSetChanged();
    }

    public void clear() {
        int size = agent.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                agent.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }
}
