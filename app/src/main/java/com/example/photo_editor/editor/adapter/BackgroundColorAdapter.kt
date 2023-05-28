package com.example.photo_editor.editor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photo_editor.R
import com.example.photo_editor.databinding.ActivityEditBinding
import com.example.photo_editor.databinding.BackgroundColorItemBinding
import com.example.photo_editor.editor.model.BackgroundColorModel

class BackgroundColorAdapter(
    val context: Context, val backgroundColorList: ArrayList<BackgroundColorModel>,
    val listener: OnBackgroundColorItemClick
) : RecyclerView.Adapter<BackgroundColorAdapter.BackGroundColorHolder>() {

    private lateinit var binding: BackgroundColorItemBinding

    inner class BackGroundColorHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var colorItem: ImageView = itemView.findViewById(R.id.colorItem)
        var colorItemName: TextView = itemview.findViewById(R.id.colorItemName)
        init {
            itemview.setOnClickListener(object :OnClickListener{
                override fun onClick(v: View?) {
                    val position = adapterPosition
                    if (position!=RecyclerView.NO_POSITION)
                        listener.onBgColorItemClick(position)
                }
            })
        }
    }

    interface OnBackgroundColorItemClick {
        fun onBgColorItemClick(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackGroundColorHolder {
        /* binding =
             BackgroundColorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)*/
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.background_color_item, parent, false)
        return BackGroundColorHolder(itemView)
    }

    override fun getItemCount(): Int {
        return backgroundColorList.size
    }

    override fun onBindViewHolder(holder: BackGroundColorHolder, position: Int) {
        /*   binding.colorItem.setImageResource(backgroundColorList[position].colorImage)
           binding.colorItemName.text = backgroundColorList[position].colorName*/
        val items = backgroundColorList[position]
        holder.colorItem.setImageResource(items.colorImage)
        holder.colorItemName.text = items.colorName;
    }
}