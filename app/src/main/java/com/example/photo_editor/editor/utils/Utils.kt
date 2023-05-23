package com.example.photo_editor.editor.utils

import android.view.View.OnClickListener
import com.example.photo_editor.databinding.ActivityUpdateBinding

class Utils {
    companion object{
        fun initListener(binding: ActivityUpdateBinding,listener: OnClickListener) {
            binding.crop.setOnClickListener(listener)
            binding.ratio.setOnClickListener(listener)
            binding.border.setOnClickListener(listener)
            binding.rotate.setOnClickListener(listener)
            binding.verticalFlip.setOnClickListener(listener)
            binding.horizontalFlip.setOnClickListener(listener)
            binding.white.setOnClickListener(listener)
            binding.black.setOnClickListener(listener)
            binding.blur.setOnClickListener(listener)
            binding.gallery.setOnClickListener(listener)
            binding.gradient.setOnClickListener(listener)
            binding.color.setOnClickListener(listener)
            binding.bottomView.rightSideImageView.setOnClickListener(listener)
            binding.left.setOnClickListener(listener)
            binding.center.setOnClickListener(listener)
            binding.right.setOnClickListener(listener)
        }

    }
}