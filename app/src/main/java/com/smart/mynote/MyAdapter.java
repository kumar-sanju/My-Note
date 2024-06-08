package com.smart.mynote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(List<DataClass> dataList, int position);
    }

    public MyAdapter(Context context, List<DataClass> dataList, OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (dataList.get(position).getDataImage().isEmpty())
            holder.recImage.setBackground(context.getResources().getDrawable(R.drawable.camera));
        else
            Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);

//        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getDataTitle());
        holder.recDesc.setText(dataList.get(position).getDataDesc());
        holder.recLang.setText(dataList.get(position).getDataLang());
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(dataList, position);

//                Intent intent = new Intent(context, DetailActivity.class);
//                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
//                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
//                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
//                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
//                intent.putExtra("Language", dataList.get(holder.getAdapterPosition()).getDataLang());
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView recImage;
        TextView recTitle, recDesc, recLang;
        CardView recCard;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recImage = itemView.findViewById(R.id.recImage);
            recCard = itemView.findViewById(R.id.recCard);
            recDesc = itemView.findViewById(R.id.recDesc);
            recLang = itemView.findViewById(R.id.recLang);
            recTitle = itemView.findViewById(R.id.recTitle);
        }
    }
}