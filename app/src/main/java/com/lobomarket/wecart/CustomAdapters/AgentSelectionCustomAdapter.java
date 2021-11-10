package com.lobomarket.wecart.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lobomarket.wecart.Models.AgentModel;
import com.lobomarket.wecart.R;

import java.util.List;

public class AgentSelectionCustomAdapter extends RecyclerView.Adapter<AgentSelectionCustomAdapter.ViewHolder> {
        List<AgentModel> agent;
        Context context;
        private boolean isNewRadioButtonChecked = false;
        private int lastCheckedPosition = -1;

        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
        }

        public AgentSelectionCustomAdapter(Context context, List<AgentModel> agent) {
            this.agent = agent;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cardview_list_for_agent_selection, parent, false);
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
            public RadioButton radioButton;
            public CardView row;

            public ViewHolder(final View itemView){
                    super(itemView);
                    name = itemView.findViewById(R.id.person_name);
                    subname = itemView.findViewById(R.id.subtext);
                    radioButton = itemView.findViewById(R.id.radioButton);
                    row = itemView.findViewById(R.id.row);

                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mListener != null) {
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    mListener.onItemClick(position);
                                }
                            }

                            isNewRadioButtonChecked = true;
                            agent.get(lastCheckedPosition).setSelected(false);
                            agent.get(getAdapterPosition()).setSelected(true);
                            lastCheckedPosition = getAdapterPosition();
                            notifyDataSetChanged();
                        }
                    });

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    mListener.onItemClick(position);
                                }
                            }

                            isNewRadioButtonChecked = true;
                            agent.get(lastCheckedPosition).setSelected(false);
                            agent.get(getAdapterPosition()).setSelected(true);
                            lastCheckedPosition = getAdapterPosition();
                            notifyDataSetChanged();
                        }
                    });

                }
        }

        @Override
        public void onBindViewHolder(@NonNull AgentSelectionCustomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            AgentModel u = agent.get(position);

            holder.name.setText(u.getFullName());
            holder.subname.setText(u.getContactNum());

            if(isNewRadioButtonChecked){
                holder.radioButton.setChecked(u.isSelected());
            } else {
                if(holder.getAdapterPosition() == 0){
                    holder.radioButton.setChecked(false);
                    lastCheckedPosition = 0;
                }
            }

        }



    public void updateDataSet(List<AgentModel> newResult){
        if(newResult!=null){
            agent = newResult;
        }
        notifyDataSetChanged();
    }
}
