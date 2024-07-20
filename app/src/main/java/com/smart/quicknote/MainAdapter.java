package com.smart.quicknote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.TvShowViewHolder>{

    private Context context;
    private List<NoteLists> noteLists;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(List<NoteLists> noteLists, int position);
        void onItemDeleted(List<NoteLists> noteLists, int position);
        void  onTvShowAction(Boolean isSelected);
    }

    public MainAdapter(Context context, List<NoteLists> noteLists, OnItemClickListener listener) {
        this.context = context;
        this.noteLists = noteLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TvShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TvShowViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.home_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TvShowViewHolder holder, int position) {
        holder.bindTvShow(noteLists.get(position), position);
    }

    @Override
    public int getItemCount() {
        return noteLists.size();
    }

    public List<NoteLists> getSelectedTvShows(){
        List<NoteLists> selectedTvShows=new ArrayList<>();
        for (NoteLists tvShow:noteLists){
            if (tvShow.isSelected){
                selectedTvShows.add(tvShow);
            }
        }
        return selectedTvShows;
    }

    public void searchDataList(ArrayList<NoteLists> searchList){
        noteLists = searchList;
        notifyDataSetChanged();
    }

    class TvShowViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout mainLayout;
        View cardLayout;
        RoundedImageView noteImage;
        TextView noteTitle, noteDate, noteDesc;
        ImageView noteEdit, noteDelete;

        TvShowViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            cardLayout = itemView.findViewById(R.id.cardLayout);
            noteImage = itemView.findViewById(R.id.noteImage);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteDate = itemView.findViewById(R.id.noteDate);
            noteDesc = itemView.findViewById(R.id.noteDesc);
            noteEdit = itemView.findViewById(R.id.noteEdit);
            noteDelete = itemView.findViewById(R.id.noteDelete);
        }

        void bindTvShow(final NoteLists tvShow, int position){
            if (tvShow.getDataImage().isEmpty())
                noteImage.setBackground(context.getResources().getDrawable(R.drawable.icon_notes));
            else
                Glide.with(context).load(tvShow.getDataImage()).into(noteImage);

            noteTitle.setText(tvShow.getDataTitle());
            noteDesc.setText(tvShow.getDataDesc());
            noteDate.setText(tvShow.getDataLang());

            if (tvShow.isSelected){
                cardLayout.setBackgroundResource(R.drawable.selected_background);
                noteEdit.setVisibility(View.VISIBLE);
                noteDelete.setVisibility(View.VISIBLE);
            }else {
                cardLayout.setBackgroundResource(R.drawable.curve_background);
                noteEdit.setVisibility(View.GONE);
                noteDelete.setVisibility(View.GONE);
            }

            noteEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(noteLists, position);
                }
            });

            noteDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemDeleted(noteLists, position);
                }
            });

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvShow.isSelected){
                        cardLayout.setBackgroundResource(R.drawable.curve_background);
                        noteEdit.setVisibility(View.GONE);
                        noteDelete.setVisibility(View.GONE);
                        tvShow.isSelected=false;
                        if (getSelectedTvShows().size()==0){
                            listener.onTvShowAction(false);
                        }
                    }else {
                        cardLayout.setBackgroundResource(R.drawable.selected_background);
                        noteEdit.setVisibility(View.VISIBLE);
                        noteDelete.setVisibility(View.VISIBLE);
                        tvShow.isSelected=true;
                        listener.onTvShowAction(true);
                    }
                }
            });
        }
    }
}
