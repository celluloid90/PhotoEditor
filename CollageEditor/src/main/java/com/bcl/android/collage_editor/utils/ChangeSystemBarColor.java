package com.bcl.android.collage_editor.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

/**
 * Created by Raihan Uddin Piash on ৬/৩/২৩
 * <p>
 * Copyright (c) 2023 Brain Craft LTD.
 **/
public class ChangeSystemBarColor {
    private Context mContext;

    public ChangeSystemBarColor(Context context) {
        this.mContext = context;
    }

    public void changeStatusBarColor(int statusBarColor, int navigationBarColor) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((Activity) mContext).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }*/
        ((Activity) mContext).getWindow().setStatusBarColor(ContextCompat.getColor(mContext, statusBarColor));
        if (navigationBarColor != 0) {
            ((Activity) mContext).getWindow().setNavigationBarColor(ContextCompat.getColor(mContext, navigationBarColor));
        }
        ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ((Activity) mContext).getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }
}
