package com.smart.mynote;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder> {

    private List<Item> itemList;
    Context context;

    public HorizontalAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    public static class HorizontalViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;

        public HorizontalViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
        }
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.horizontal_item_layout, parent, false);
//        return new HorizontalViewHolder(itemView);
        return new HorizontalViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.horizontal_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        Item item = itemList.get(position);
        Log.d("sanju", "onBindViewHolder: "+ itemList.get(position).getDataTitle());
        holder.title.setText(itemList.get(position).getDataTitle());
        holder.description.setText(itemList.get(position).getDataDesc());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}