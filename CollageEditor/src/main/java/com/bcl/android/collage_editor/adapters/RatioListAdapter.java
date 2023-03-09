package com.bcl.android.collage_editor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bcl.android.collage_editor.R;
import com.bcl.android.collage_editor.enums.RatioItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raihan Uddin Piash on ৯/৩/২৩
 * <p>
 * Copyright (c) 2023 Brain Craft LTD.
 **/
public class RatioListAdapter extends RecyclerView.Adapter<RatioListAdapter.RatioItemViewHolder> {
    private final LayoutInflater mColorViewInflater;
    private int selectedItem;
    private RatioListAdapter.RatioItemClickListener ratioItemClickListener;
    private Context context;
    private List<RatioItemType> ratioItemList = new ArrayList<>();

    public interface RatioItemClickListener {
        void onItemClicked(int position);
    }

    public RatioListAdapter(Context context) {
        this.context = context;
        this.mColorViewInflater = LayoutInflater.from(context);
        ratioItemList = RatioItemType.getAllValues();
        selectedItem = 0;
    }

    @NonNull
    @Override
    public RatioListAdapter.RatioItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View collageView = mColorViewInflater.inflate(R.layout.single_ratio_item_view, null, false);
        return new RatioListAdapter.RatioItemViewHolder(collageView);
    }

    @Override
    public void onBindViewHolder(@NonNull RatioListAdapter.RatioItemViewHolder holder, int position) {
        int itemCount = position + 1;
        holder.ratioValue.setText(ratioItemList.get(position).getName());
        holder.ratioView.setImageResource(ratioItemList.get(position).getDrawableId());

        if (selectedItem == position) {
            holder.ratioView.setImageResource(ratioItemList.get(position).getSelectedDrawableId());
            ratioItemClickListener.onItemClicked(position);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem = position;
                notifyDataSetChanged();
            }
        });

    }

    public void setItemClickListener(RatioListAdapter.RatioItemClickListener ratioItemClickListener) {
        this.ratioItemClickListener = ratioItemClickListener;
    }

    @Override
    public int getItemCount() {
        return ratioItemList.size();
    }

    public static class RatioItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ratioView;
        TextView ratioValue;

        public RatioItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ratioView = itemView.findViewById(R.id.ratio_view_item_holder);
            ratioValue = itemView.findViewById(R.id.ratio_number_item);
        }
    }
}