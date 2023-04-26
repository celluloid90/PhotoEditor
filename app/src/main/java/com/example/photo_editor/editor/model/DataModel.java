package com.example.photo_editor.editor.model;

import android.util.Log;

import com.example.photo_editor.editor.utils.CheckButtonType;
import com.example.photo_editor.editor.view.RatioView;

public class DataModel {


    public static float x;
    public static float y;

    public static float setViewRatio(RatioView ratioView) {
        float ratio = ratioView.getViewWidth() / ratioView.getViewHeight();
        // float ratioFromRealView = ratioView.getWidth() / ratioView.getHeight();
        Log.d("TAG", "ratio: " + ratio);
        return ratio;
    }

    public static float setCenterX(RatioView ratioView) {

        float finalWidth = ratioView.getFinalWidth();
        float finalHeight = ratioView.getFinalHeight();
        Log.d("TAG", "setCenterX: "+"setText");
        float ratio = ratioView.getViewWidth() / ratioView.getViewHeight();
        if (ratioView.mCheckButtonType() == CheckButtonType.CENTER) {
            x = .5f;
            return x;
        } else if (ratioView.mCheckButtonType() == CheckButtonType.LEFT) {
            if (ratioView.getViewWidth() > finalWidth) {
                x = ((finalWidth / 2) / ratioView.getViewWidth());
            } else if (ratioView.getViewHeight() > finalHeight) {
                x = .5f;
            }
            Log.d("TAG", "setCenterX: "+x);
            return x;
        } else if (ratioView.mCheckButtonType() == CheckButtonType.RIGHT) {
            if (ratioView.getViewWidth() > finalWidth) {
                x = (1 - (finalWidth / 2) / ratioView.getViewWidth());
            } else if (ratioView.getViewHeight() > finalHeight) {
                x = .5f;
            }
        }
        return x;
    }

    public static float setCenterY(RatioView ratioView) {
        float finalWidth = ratioView.getFinalWidth();
        float finalHeight = ratioView.getFinalHeight();
        if (ratioView.mCheckButtonType() == CheckButtonType.CENTER) {
            y = .5f;
            return y;
        } else if (ratioView.mCheckButtonType() == CheckButtonType.LEFT) {
            if (ratioView.getViewWidth() > finalWidth) {
                y = .5f;
            } else if (ratioView.getViewHeight() > finalHeight) {
                y = ((finalHeight) / 2) / ratioView.getViewHeight();
            }
            return y;
        } else if (ratioView.mCheckButtonType() == CheckButtonType.RIGHT) {
            if (ratioView.getViewWidth() > finalWidth) {
                y = 0.5f;
            } else if (ratioView.getViewHeight() > finalHeight) {
                y = (1 - (finalHeight / 2) / ratioView.getViewHeight());
            }
        }
        return y;
    }
}
