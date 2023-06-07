package com.bcl.android.collage_editor.activity;

import static com.bcl.android.collage_editor.utils.Constants.CAMERA_REQUEST_CODE;
import static com.bcl.android.collage_editor.utils.Constants.STORAGE_PERMISSION_DESCRIPTION_TEXT;
import static com.bcl.android.collage_editor.utils.Constants.STORAGE_PERMISSION_TITLE_TEXT;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bcl.android.collage_editor.R;
import com.bcl.android.collage_editor.datasource.FilePickerManager;
import com.bcl.android.collage_editor.utils.ChangeSystemBarColor;
import com.braincraft.droid.filepicker.fragments.FilePickerFragment;
import com.braincraft.droid.filepicker.interfaces.DropDownActionListener;
import com.braincraft.droid.filepicker.interfaces.DropDownViewContract;
import com.braincraft.droid.filepicker.interfaces.SelectionInterruption;
import com.braincraft.droid.filepicker.utils.Constant;
import com.braincraft.droid.filepicker.utils.PickerKeyConstants;
import com.braincraft.droid.filepicker.utils.ShowToast;
import com.braincraftapps.mediaFetcher.model.MediaFile;
import com.braincraftapps.mediaFetcher.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class CollagePicker extends AppCompatActivity implements FilePickerFragment.OnFilePickerItemClickListener, View.OnClickListener {

    private ImageView closeBtn, headerArrowIcon, cameraIcon;
    private TextView headerText;
    private LinearLayout headerContainer;
    private DropDownViewContract dropDownViewContract;
    private boolean dropDownState;
    private Uri lastCapturedUri;
    private SelectionInterruption selectionInterruption;

    @Override
    protected void onStart() {
        super.onStart();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof SelectionInterruption) {
                selectionInterruption = ((SelectionInterruption) fragment);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.No_Action_Bar_Theme);
        setContentView(R.layout.activity_collage_picker);

        if (savedInstanceState != null)
            lastCapturedUri = Uri.parse(savedInstanceState.getString("abc"));

        new ChangeSystemBarColor(this).changeStatusBarColor(R.color.color_black, R.color.color_black);
        initViews();
        initListeners();

        setupPickerFragment();
    }

    private void initListeners() {
        closeBtn.setOnClickListener(this);
        headerContainer.setOnClickListener(this);
        cameraIcon.setOnClickListener(this);
    }

    private void initViews() {
        closeBtn = findViewById(R.id.gallery_close);
        headerText = findViewById(R.id.header_text);
        headerContainer = findViewById(R.id.header_container);
        headerArrowIcon = findViewById(R.id.header_arrow_icon);
        cameraIcon = findViewById(R.id.camera_icon);
    }

    private void setupPickerFragment() {
        boolean needToCreatePickerFragment = true;
        FragmentManager manager = getSupportFragmentManager();

        for (Fragment frag : manager.getFragments()) {
            if (frag instanceof FilePickerFragment) {
                dropDownViewContract = (DropDownViewContract) frag;
                needToCreatePickerFragment = false;
                break;
            }
        }

        if (needToCreatePickerFragment) {
            FilePickerFragment fragment = (FilePickerFragment) FilePickerFragment.getInstance(new FilePickerManager().getGalleryConfig(), R.style.CustomGalleryTheme, getBundle());
            fragment.setDropDownActionListener(new DropDownActionListener() {
                @Override
                public void onViewOpened() {
                    dropDownState = true;
                    headerArrowIcon.setImageResource(R.drawable.ic_arrow_up);
                }

                @Override
                public void onViewClosed() {
                    dropDownState = false;
                    headerArrowIcon.setImageResource(R.drawable.ic_arrow_down);
                }
            });

            dropDownViewContract = fragment;
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.picker_frag_container, fragment);
            transaction.commit();
        }
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(PickerKeyConstants.picker_key_permission_dialog_title, STORAGE_PERMISSION_TITLE_TEXT);
        bundle.putString(PickerKeyConstants.picker_key_permission_dialog_description, getResources().getString(R.string.app_name) + STORAGE_PERMISSION_DESCRIPTION_TEXT);
        bundle.putInt(PickerKeyConstants.picker_key_permission_default_icon, R.drawable.ic_permission_storage);
        bundle.putInt(PickerKeyConstants.picker_key_permission_default_title_text_color, getResources().getColor(R.color.color_black));
        bundle.putInt(PickerKeyConstants.picker_key_permission_default_description_text_color, getResources().getColor(R.color.color_gray_dark));

        return bundle;
    }

    @Override
    public void onFilePickerItemClicked(List<MediaFile> mediaFiles) {
        if (mediaFiles != null && !mediaFiles.isEmpty()) {

            Intent intent = new Intent(this, EditCustomHome.class);
            intent.putExtra(Constant.MEDIA_FILES, new ArrayList<>(mediaFiles));
            startActivity(intent);
        }
    }

    @Override
    public void onHeaderNameChanged(String headerText) {
        this.headerText.setText(headerText);
    }

    @Override
    public void onClick(View v) {
        if (v == closeBtn) {
            onBackPressed();
        } else if (v == headerContainer) {
            if (!dropDownState) {
                dropDownViewContract.openView();
                headerArrowIcon.setImageResource(R.drawable.ic_arrow_up);
            } else {
                dropDownViewContract.closeView();
                headerArrowIcon.setImageResource(R.drawable.ic_arrow_down);
            }
        } else if (v == cameraIcon) {
            cameraOpener();
        }
    }

    private void cameraOpener() {
        try {
            //Open Camera Intent
            Intent intent;
            String fileName;
            String extension;

            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileName = "IMG_" + System.currentTimeMillis();
            extension = "jpeg";
            lastCapturedUri = FileUtils.generateMediaUri(this, "", fileName, extension, FileUtils.MEDIA_TYPE_IMAGE, true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, lastCapturedUri);

            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (ActivityNotFoundException exception) {
            deleteContentMedia();
            ShowToast.show(this, "System camera not found!");
        }
    }

    public void deleteContentMedia() {
        if (lastCapturedUri != null) {
            getContentResolver().delete(lastCapturedUri, null, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                doActionOnCapture();
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == CAMERA_REQUEST_CODE) {
            try {
                deleteContentMedia();
            } catch (SecurityException ignored) {
                // if failed to delete
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("abc", String.valueOf(lastCapturedUri));
    }

    private void doActionOnCapture() {
        String filePath = FileUtils.getAbsolutePathFromURI(this, lastCapturedUri);

        MediaScannerConnection.scanFile(this, new String[]{filePath}, null, (path, uri) -> {
            if (path != null && !path.isEmpty()) {
                runOnUiThread(() -> selectionInterruption.selectFirstPosition());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (dropDownState) dropDownViewContract.closeView();
        else super.onBackPressed();
    }
}