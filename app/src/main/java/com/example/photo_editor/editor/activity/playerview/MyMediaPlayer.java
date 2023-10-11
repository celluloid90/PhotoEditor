package com.example.photo_editor.editor.activity.playerview;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

public class MyMediaPlayer implements IABCPlayer {
    private MediaPlayer mMediaPlayer;
    private PreparedListener preparedListener;

    @Override
    public void createPlayer(Context context) {
        mMediaPlayer=new MediaPlayer();
    }

    @Override
    public void setSurfaceHolder(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void setVideoPath(String videoPath,Context context) {
        try {
            mMediaPlayer.setDataSource(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preparePlayer() {
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
               preparedListener.onPrepared();
            }
        });
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void startPlayer() {
        mMediaPlayer.start();
    }

    @Override
    public void pausePlayer() {
        mMediaPlayer.pause();
    }

    @Override
    public void stopPlayer() {
        mMediaPlayer.stop();
    }

    @Override
    public void releasePlayer() {
        mMediaPlayer.release();
    }

    @Override
    public boolean isPlaying() {
       return  mMediaPlayer.isPlaying();


    }

    @Override
    public void seekTo(long position) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mMediaPlayer.seekTo(position, MediaPlayer.SEEK_CLOSEST);
        }else {
            mMediaPlayer.seekTo((int) position);
        }

        String TAG = "MyMediaPlayer";
        Log.d(TAG,position+"");
    }

    @Override
    public long getCurrentPosition() {
        return (long)mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getVideoDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void setPreparedListener(PreparedListener preparedLister) {
        this.preparedListener = preparedLister;
    }

}
