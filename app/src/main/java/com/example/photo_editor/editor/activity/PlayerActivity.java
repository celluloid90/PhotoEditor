package com.example.photo_editor.editor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.photo_editor.R;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    SurfaceView surfaceView;
    IjkMediaPlayer mIjkMediaPlayer;
    Uri uri;
    String video_path =  "storage/emulated/0/DCIM/video_one.mp4";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.onboard_video_three_new);
        Log.d("TAG", "onActivityResult: "+uri.toString());
        surfaceView = findViewById(R.id.surface_view);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mIjkMediaPlayer = new IjkMediaPlayer();
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);

        surfaceView.getHolder().addCallback(this);




    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mIjkMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mIjkMediaPlayer.setDisplay(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //mIjkMediaPlayer.setDataSource(this, url);
            mIjkMediaPlayer.setDataSource(video_path);
            mIjkMediaPlayer.prepareAsync();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        mIjkMediaPlayer.start();
    }
}