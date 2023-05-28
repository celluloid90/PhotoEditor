package com.example.photo_editor.editor.utils

import android.view.View
import android.view.View.OnClickListener
import android.view.animation.TranslateAnimation
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

        fun slideUp(view: View) {
            view.visibility = View.VISIBLE
            val animate = TranslateAnimation(
                0f,  // fromXDelta
                0f,  // toXDelta
                view.height.toFloat(),  // fromYDelta
                0f
            ) // toYDelta
            animate.duration = 400
            animate.fillAfter = true
            view.startAnimation(animate)
        }
        fun slideDown(view: View) {
            val animate = TranslateAnimation(
                0f,  // fromXDelta
                0f,  // toXDelta
                0f,  // fromYDelta
                view.height.toFloat()
            ) // toYDelta
            animate.duration = 400
            animate.fillAfter = true
            view.startAnimation(animate)

        }

    }
}