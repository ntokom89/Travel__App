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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerItemAdapter extends RecyclerView.Adapter<RecyclerItemAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<Item> mData;

    public RecyclerItemAdapter(Context mContext, ArrayList<Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_category_item,parent,false);
        //view = inflater.inflate(R.layout.item, parent,false);

        return new RecyclerItemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.card_ItemTitle.setText(mData.get(position).getNameItem());
        holder.card_Description.setText(mData.get(position).getDescriptionItem());
        holder.card_Date.setText(mData.get(position).getDateAquiredItem());
        Picasso.get().load(mData.get(position).getImageUri()).into(holder.card_imageItem);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView card_ItemTitle;
        TextView card_Description;
        TextView card_Date;
        ImageView card_imageItem;
        CardView cardViewItem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_ItemTitle = itemView.findViewById(R.id.textViewItemTitle);
            card_Description = itemView.findViewById(R.id.textViewDescription);
            card_Date = itemView.findViewById(R.id.textViewDateOfAquiresition);
            cardViewItem = itemView.findViewById(R.id.cardViewItem);
            card_imageItem = itemView.findViewById(R.id.imageViewItem);
        }
    }
}
