package com.bcl.android.collage_editor.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bcl.android.collage_editor.R;
import com.bcl.android.collage_editor.adapters.CollageListAdapter;
import com.bcl.android.collage_editor.utils.ChangeSystemBarColor;
import com.braincraft.droid.filepicker.utils.Constant;
import com.braincraftapps.mediaFetcher.model.MediaFile;

import java.util.ArrayList;

public class CollageHome extends AppCompatActivity implements View.OnClickListener {

    private ImageView close, demo, done;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private RecyclerView collageRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.No_Action_Bar_Theme);
        setContentView(R.layout.activity_collage_home);
        getFiles();
        new ChangeSystemBarColor(this).changeStatusBarColor(R.color.color_black, R.color.color_black);
        initViews();
        setUpRecyclerView();
        initListeners();
    }

    private void getFiles() {
        mediaFiles.clear();
        mediaFiles.addAll(getIntent().getParcelableArrayListExtra(Constant.MEDIA_FILES));
    }

    private void setUpRecyclerView() {
        CollageListAdapter collageListAdapter = new CollageListAdapter(this, mediaFiles.size());
        collageRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        collageRecycler.setAdapter(collageListAdapter);
        collageListAdapter.setItemClickListener(new CollageListAdapter.CollageItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("ItemClicked: ", position + "");
            }
        });
    }

    private void initListeners() {
        close.setOnClickListener(this);
        demo.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    private void initViews() {
        close = findViewById(R.id.close);
        demo = findViewById(R.id.ic_demo);
        done = findViewById(R.id.ic_action_complete);
        collageRecycler = findViewById(R.id.collage_recycler);
    }

    @Override
    public void onClick(View v) {
        if (v == close) {
            finish();
        } else if (v == demo) {
            Toast.makeText(this, "Demo will added later", Toast.LENGTH_SHORT).show();
        } else if (v == done) {
            Toast.makeText(this, "Done in progress", Toast.LENGTH_SHORT).show();
        }
    }
}