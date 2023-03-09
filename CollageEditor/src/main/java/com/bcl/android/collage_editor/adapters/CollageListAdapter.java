package com.bcl.android.collage_editor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bcl.android.collage_editor.R;

/**
 * Created by Raihan Uddin Piash on ৭/৩/২৩
 * <p>
 * Copyright (c) 2023 Brain Craft LTD.
 **/
public class CollageListAdapter extends RecyclerView.Adapter<CollageListAdapter.CollageItemViewHolder> {
    private final LayoutInflater mColorViewInflater;
    private int selectedItem;
    private int fileSize;
    private CollageItemClickListener collageItemClickListener;

    public interface CollageItemClickListener {
        void onItemClicked(int position);
    }

    public CollageListAdapter(Context context, int fileSize) {
        this.mColorViewInflater = LayoutInflater.from(context);
        this.fileSize = fileSize;
        selectedItem = 0;
    }

    @NonNull
    @Override
    public CollageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View collageView = mColorViewInflater.inflate(R.layout.single_collage_item_view, null, false);
        return new CollageItemViewHolder(collageView);
    }

    @Override
    public void onBindViewHolder(@NonNull CollageItemViewHolder holder, int position) {
        int itemCount = position + 1;
        holder.itemNumber.setTextColor(holder.itemNumber.getContext().getResources().getColor(R.color.color_gray_dark));
        holder.itemNumber.setText(itemCount + "");
        holder.collageView.setText(fileSize + ":" + itemCount);
        holder.selectedView.setVisibility(View.INVISIBLE);

        if (selectedItem == position) {
            holder.selectedView.setVisibility(View.VISIBLE);
            holder.itemNumber.setTextColor(holder.itemNumber.getContext().getResources().getColor(R.color.color_white));
            collageItemClickListener.onItemClicked(position);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem = position;
                notifyDataSetChanged();
            }
        });

    }

    public void setItemClickListener(CollageItemClickListener collageItemClickListener) {
        this.collageItemClickListener = collageItemClickListener;
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public static class CollageItemViewHolder extends RecyclerView.ViewHolder {

        TextView collageView;
        TextView itemNumber;
        View selectedView;

        public CollageItemViewHolder(@NonNull View itemView) {
            super(itemView);
            collageView = itemView.findViewById(R.id.collage_view_item);
            itemNumber = itemView.findViewById(R.id.collage_number_item);
            selectedView = itemView.findViewById(R.id.selected_view);
        }
    }
}
