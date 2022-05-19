package com.company.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerMainList extends RecyclerView.Adapter<RecyclerMainList.MyViewHolder>  {


    private Context mContext;
    private ArrayList<Collection> mData;

    public RecyclerMainList(Context mContext, ArrayList<Collection> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card,parent,false);
        //view = inflater.inflate(R.layout.item, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.card_title.setText(mData.get(position).getCategoryName());
        holder.card_image.setImageResource(mData.get(position).getImageCollection());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView card_title;
        ImageView card_image;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_title = itemView.findViewById(R.id.textViewCollectionTitle);
            card_image = itemView.findViewById(R.id.imageViewCollection);
            cardView = itemView.findViewById(R.id.cardViewItem);
        }
    }
}
