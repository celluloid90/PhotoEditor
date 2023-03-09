package com.bcl.android.collage_editor.enums;

import com.bcl.android.collage_editor.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Raihan Uddin Piash on ৯/৩/২৩
 * <p>
 * Copyright (c) 2023 Brain Craft LTD.
 **/
public enum RatioItemType {
    RATIO_ONE_ONE(1, "1:1", R.drawable.ic_canvas_1_1, R.drawable.ic_canvas_1_1_selected), RATIO_FOUR_FIVE(2, "4:5", R.drawable.ic_canvas_4_5, R.drawable.ic_canvas_4_5_selected), RATIO_SIXTEEN_NINE(3, "16:9", R.drawable.ic_canvas_16_9, R.drawable.ic_canvas_16_9_selected), RATIO_NINE_SIXTEEN(4, "9:16", R.drawable.ic_canvas_9_16, R.drawable.ic_canvas_9_16_selected), RATIO_THREE_FOUR(5, "3:4", R.drawable.ic_canvas_3_4, R.drawable.ic_canvas_3_4_selected), RATIO_FOUR_THREE(6, "4:3", R.drawable.ic_canvas_4_3, R.drawable.ic_canvas_4_3_selected), RATIO_TWO_THREE(7, "2:3", R.drawable.ic_canvas_2_3, R.drawable.ic_canvas_2_3_selected), RATIO_THREE_TWO(8, "3:2", R.drawable.ic_canvas_3_2, R.drawable.ic_canvas_3_2_selected), RATIO_TWO_ONE(9, "2:1", R.drawable.ic_canvas_2_1, R.drawable.ic_canvas_2_1_selected), RATIO_ONE_TWO(10, "1:2", R.drawable.ic_canvas_1_2, R.drawable.ic_canvas_1_2_selected);

    private final int id, drawableId, selectedDrawableId;
    private final String name;

    RatioItemType(int id, String name, int drawableId, int selectedDrawableId) {
        this.id = id;
        this.drawableId = drawableId;
        this.name = name;
        this.selectedDrawableId = selectedDrawableId;
    }

    public int getId() {
        return this.id;
    }

    public int getDrawableId() {
        return this.drawableId;
    }

    public int getSelectedDrawableId() {
        return this.selectedDrawableId;
    }

    public String getName() {
        return this.name;
    }

    public static int getDrawableValueById(int id) {
        for (RatioItemType i : values()) {
            if (i.getId() == id) {
                return i.getDrawableId();
            }
        }
        return 0;
    }

    public static List<RatioItemType> getAllValues() {
        return Arrays.asList(values());
    }
}
