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


// A Adapter for the category list in the Goal page(Codexpedia, 2022)(Lackner, 2020)
public class RecyclerCategoryListGoal extends RecyclerView.Adapter<RecyclerCategoryListGoal.MyViewHolder> {

    public interface OnItemClickListener2 {
        void onItemClick(View view, int position);
    }

    private Context mContext;
    private ArrayList<Collection> mData;
    private RecyclerCategoryListGoal.OnItemClickListener2 listener;

    public RecyclerCategoryListGoal(Context mContext, ArrayList<Collection> mData, OnItemClickListener2 listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return  new MyViewHolder(view, listener);
    }

    //A Method that sets the values of the components (Lackner, 2020)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.card_name.setText(mData.get(position).getCategoryName());
        Picasso.get().load(mData.get(position).getImageUri()).into(holder.card_imageView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener(RecyclerCategoryListGoal.OnItemClickListener2 itemClickListener) {
        this.listener = itemClickListener;
    }
    //A viewHolder with the components and it implements a onClickListener (Codexpedia, 2022)(Lackner, 2020)
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView card_name;
        ImageView card_imageView;
        OnItemClickListener2 onItemClickListener;

     public MyViewHolder(@NonNull View itemView,OnItemClickListener2 onItemClickListener) {
         super(itemView);
         card_name = itemView.findViewById(R.id.textViewCollectionNameGoal);
         card_imageView = itemView.findViewById(R.id.imageViewCollectionGoal);
         cardView = itemView.findViewById(R.id.cardViewItemGoal);
         this.onItemClickListener = onItemClickListener;
         //itemView.setTag(itemView);
         itemView.setOnClickListener(this);

     }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
