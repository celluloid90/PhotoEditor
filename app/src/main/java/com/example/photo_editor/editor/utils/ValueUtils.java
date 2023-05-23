package com.example.photo_editor.editor.utils;

public class ValueUtils {
    CheckButtonType checkButtonType;
    float initialScale = 0.5f;
    public static void setValueXy(CheckButtonType checkButtonType,float centerX,float centerY,
                                  boolean centerBool,boolean leftBool,boolean rightBool,
                                  boolean leftBoolX, boolean leftBoolY, boolean rightBoolX, boolean rightBoolY,
                                  float height, float width,float finalWidth,float finalHeight) {
        if (checkButtonType.equals(CheckButtonType.CENTER)) {
            centerX = 0.5f;
            centerY = 0.5f;
            centerBool = true;
            leftBool = false;
            rightBool = false;
        }
        if (checkButtonType.equals(CheckButtonType.LEFT)) {
            centerBool = false;
            leftBool = true;
            rightBool = false;
            if (width > height) {
                centerX = ((finalWidth / 2) /width);
                centerY = 0.5f;
                leftBoolX = true;
                leftBoolY = false;
            } else {
                centerY = ((finalHeight) / 2) / height;
                centerX = 0.5f;
                leftBoolY = true;
                leftBoolX = false;
            }
        }
        if (checkButtonType.equals(CheckButtonType.RIGHT)) {
            centerBool = false;
            leftBool = false;
            rightBool = true;

            if (width > height) {
                centerX = (1 - (finalWidth / 2) / width);
                centerY = 0.5f;
                rightBoolX = true;
                rightBoolY = false;
            } else {
                centerY = (1 - (finalHeight / 2) / height);
                centerX = 0.5f;
                rightBoolY = true;
                rightBoolX = false;
            }
        }


    }
}
