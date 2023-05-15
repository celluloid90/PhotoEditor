package com.example.photo_editor.editor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photo_editor.databinding.ItemRatioBinding
import com.example.photo_editor.editor.model.RatioModel

class RatioAdapter (val context: Context, val ratioModelList: ArrayList<RatioModel>,
                    val listener: OnItemClickListener) :
    RecyclerView.Adapter<RatioAdapter.RatioViewHolder>() {

    private lateinit var binding: ItemRatioBinding

    //private val debouncingListener = DebouncingOnClickListener()

    inner class RatioViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        init {
            itemview.setOnClickListener(object :OnClickListener{
                override fun onClick(v: View?) {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position)
                    }
                }
            })

        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatioViewHolder {
        binding = ItemRatioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatioViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RatioViewHolder, position: Int) {
        with(holder) {
            binding.title.setText(ratioModelList[position].gifName)
            binding.image.setImageResource(ratioModelList[position].image)
        }
    }
    override fun getItemCount(): Int {
        return ratioModelList.size
    }
}