package com.varunsaini.android.vitwifi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {


    private String[] dataSet;
    AssetManager assetManager;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cardTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.cardTextView = (TextView) itemView.findViewById(R.id.cardTextView);
        }
    }

    public CustomAdapter(String[] data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);
        assetManager = parent.getContext().getAssets();
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView cardTextView = holder.cardTextView;

        cardTextView.setText(dataSet[listPosition]);
        //setting typefaces
        Typeface tf = Typeface.createFromAsset(assetManager,"fonts/Montserrat.ttf");
        cardTextView.setTypeface(tf);



    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }
}