package com.bcl.android.collage_editor.datasource;

import android.content.Context;
import android.os.Build;

import com.braincraft.droid.filepicker.config.Configurations;
import com.braincraft.droid.filepicker.utils.Constant;
import com.braincraftapps.mediaFetcher.loader.FetcherManager;

import java.util.ArrayList;
import java.util.Arrays;

public class FilePickerManager {

    boolean ignoreNoMedia = true;
    Configurations.Builder configurationsBuilder = new Configurations.Builder()
            .setCheckPermission(true)
            .setSkipZeroSizeFiles(true)
            .enableImageCapture(true)
            .enableVideoCapture(false)
            .setSingleChoiceMode(false)
            .enableCaptureTop(true)
            .setShowFileDuration(false)
            .setEnableDropDown(true)
            .setShowVideos(false)
            .setShowAudios(false)
            .setShowFiles(false)
            .setIgnoreNoMedia(true)
            .setShowImages(true)
            .setMaxSelection(20)
            .setOptionMenuEnabled(false)
            .setPortraitSpanCount(4)
            .setGalleryViewType(Configurations.DESC)
            .setLayoutManagerType(Constant.GRID_LAYOUT_MANAGER)
            .setSingleClickSelection(true);

    public Configurations getGalleryConfig() {

        FetcherManager.setFileMimeTypes(new ArrayList<>(Arrays.asList("image/jpeg", "image/png", "image/jpg")), FetcherManager.MIME_TYPES_IMAGE);

        if (Build.MODEL.equalsIgnoreCase(Constant.IGNORE_MODEL_NAME_PIXEL_3A) || Build.MODEL.equalsIgnoreCase(Constant.IGNORE_MODEL_NAME_PIXEL_3)) {
            ignoreNoMedia = false;
        }

        Configurations configurations = configurationsBuilder.setIgnoreNoMedia(ignoreNoMedia).build();

        return configurations;
    }
}
