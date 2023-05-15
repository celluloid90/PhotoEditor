package com.example.photo_editor.editor.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photo_editor.R
import com.example.photo_editor.databinding.ColorItemBinding
import com.example.photo_editor.editor.model.ColorModel

class ColorAdapter(
    val context: Context, val colorModelList: ArrayList<ColorModel>,
    val listener: ColorAdapter.OnColorItemClickListener
) :
    RecyclerView.Adapter<ColorAdapter.ColorModelViewHolder>() {

    private lateinit var binding: ColorItemBinding

    inner class ColorModelViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        init {
            itemview.setOnClickListener(object : OnClickListener {
                override fun onClick(v: View?) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onColorItemClick(position)
                    }
                }
            })
        }
    }

    interface OnColorItemClickListener {
        fun onColorItemClick(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorModelViewHolder {
        binding = ColorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorModelViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ColorModelViewHolder, position: Int) {
        with(holder) {
            if (position == 0) {
                binding.colorItem.setImageResource(R.drawable.dropper_one)
            } else {
                binding.colorItem.setBackgroundColor(Color.parseColor(colorModelList[position].colorName))
            }
        }
    }

    override fun getItemCount(): Int {
        return colorModelList.size;
    }
}