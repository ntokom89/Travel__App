package com.company.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerMainList extends RecyclerView.Adapter<RecyclerMainList.MyViewHolder>  {


    private Context mContext;
    private List<Collection> mData;

    public RecyclerMainList(Context mContext, List<Collection> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.item, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.card_title.setText(mData.get(position).getNameCollection());
        holder.card_image.setImageResource(mData.get(position).getImageCollection());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView card_title;
        ImageView card_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_title = itemView.findViewById(R.id.textViewCollectionName);
            card_image = itemView.findViewById(R.id.imageViewCollection);

        }
    }
}
