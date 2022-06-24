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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerPieChart2Adapter extends RecyclerView.Adapter<RecyclerPieChart2Adapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<Collection> mData;
    private double total;

    public RecyclerPieChart2Adapter(Context mContext, ArrayList<Collection> mData, double total) {
        this.mContext = mContext;
        this.mData = mData;
        this.total = total;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pie_chart_list,parent,false);
        //view = inflater.inflate(R.layout.item, parent,false);

        return new RecyclerPieChart2Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(mData.get(position).getCategoryName());
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        double percentage = (mData.get(position).getItems().size()/total) *100;
        holder.number.setText(df.format(percentage) +"%");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title, number;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.TextViewCategoryNameChart);
            number = itemView.findViewById(R.id.TextViewCategoryNameNumItems);
        }
    }
}
