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

public class RecyclerMainList extends RecyclerView.Adapter<RecyclerMainList.MyViewHolder>  {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private Context mContext;
    private ArrayList<Collection> mData;
    private  OnItemClickListener listener;


    public RecyclerMainList(Context mContext, ArrayList<Collection> mData, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.mData = mData;
        listener = onItemClickListener;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card,parent,false);
        //view = inflater.inflate(R.layout.item, parent,false);

        return new MyViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //holder.bind(mData.get(position), listener);

        holder.card_title.setText(mData.get(position).getCategoryName());
        Picasso.get().load(mData.get(position).getImageUri()).into(holder.card_image);
        //holder.card_image.setImageResource(mData.get(position).getImageUri());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView card_title;
        ImageView card_image;
        CardView cardView;
        OnItemClickListener onItemClickListener;
        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            card_title = itemView.findViewById(R.id.textViewCollectionTitle);
            card_image = itemView.findViewById(R.id.imageViewCollection);
            cardView = itemView.findViewById(R.id.cardViewItem);
            this.onItemClickListener = onItemClickListener;
            //itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            onItemClickListener.onItemClick(v, getAdapterPosition());
            //if (listener != null) {
            //    listener.onItemClick(v, getAdapterPosition());
            //}
        }
        /*
        public void bind(final Collection item, final OnItemClickListener listener) {
            card_title.setText(item.getCategoryName());
            Picasso.get().load(item.getImageUri()).into(card_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

         */
    }
}
