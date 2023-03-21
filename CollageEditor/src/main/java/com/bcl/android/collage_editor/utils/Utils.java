package com.bcl.android.collage_editor.utils;

import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * Created by Raihan Uddin Piash on ৯/৩/২৩
 * <p>
 * Copyright (c) 2023 Brain Craft LTD.
 **/
public class Utils {
    public static void viewSlideUp(View view) {
        TranslateAnimation animate = new TranslateAnimation(0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        //animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    public static void viewSlideDown(View view) {
        view.setVisibility(View.INVISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        //animate.setFillAfter(true);
        view.startAnimation(animate);
    }
}
