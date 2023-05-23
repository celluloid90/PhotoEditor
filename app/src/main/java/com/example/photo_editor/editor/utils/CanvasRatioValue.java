package com.example.photo_editor.editor.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.photo_editor.editor.view.EditorView;

public class CanvasRatioValue {
    public static void setLayoutHeightWidth(EditorView view, int position, ConstraintLayout constraintLayout) {

        if (position == 1) {
            setWidthHeight(view,constraintLayout, 1f, 1f);
        }
        if (position == 2) {
            setWidthHeight(view, constraintLayout,4f, 5f);
        }
        if (position == 3) {
            setWidthHeight(view, constraintLayout,9f, 16f);
        }
        if (position == 4) {
            setWidthHeight(view, constraintLayout,3f, 4f);
        }
        if (position == 5) {
            setWidthHeight(view, constraintLayout,4f, 3f);
        }
        if (position == 6) {
            setWidthHeight(view, constraintLayout,2f, 3f);
        }
        if (position == 7) {
            setWidthHeight(view, constraintLayout,3f, 2f);
        }
        if (position == 8) {
            setWidthHeight(view, constraintLayout,5f, 4f);
        }
        if (position == 9) {
            setWidthHeight(view, constraintLayout,16f, 9f);
        }
    }

    public static void setWidthHeight(EditorView view,ConstraintLayout constraintLayout, float v, float v1) {
        float getRatio = v / v1;
        float pHeight = constraintLayout.getHeight();
        float pWidth = constraintLayout.getWidth();
        float hHeight = view.getHeight();
        float hWidth = view.getWidth();

        if (getRatio > 1) {
            hWidth = pWidth;
            hHeight = (hWidth / getRatio);
        }
        if (getRatio < 1) {
            hHeight = pHeight;
            hWidth = (pHeight * getRatio);

        }
        if (getRatio == 1f) {
            hHeight = pWidth;
            hWidth = pWidth;
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) hHeight;
        params.width = (int) hWidth;
        view.setLayoutParams(params);

    }
}
