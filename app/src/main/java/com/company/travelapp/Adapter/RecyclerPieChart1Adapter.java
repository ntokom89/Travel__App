package com.company.travelapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.travelapp.Model.Collection;
import com.company.travelapp.Model.Item;
import com.company.travelapp.R;

import java.util.ArrayList;

public class RecyclerPieChart1Adapter extends RecyclerView.Adapter<RecyclerPieChart1Adapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<Collection> mData;
    private ArrayList<Integer> randomColors;


    public RecyclerPieChart1Adapter(Context mContext, ArrayList<Collection> mData, ArrayList<Integer> randomColors) {
        this.mContext = mContext;
        this.mData = mData;
        this.randomColors = randomColors;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pie_chart_list_colors,parent,false);
        //view = inflater.inflate(R.layout.item, parent,false);

        return new RecyclerPieChart1Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.view.setBackgroundColor(randomColors.get(position));
        holder.title.setText(mData.get(position).getCategoryName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.viewColor);
            title = itemView.findViewById(R.id.categoryNameChart1);
        }
    }
}
