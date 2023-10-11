package com.example.photo_editor.editor.activity.playerview;

import android.content.Context;
import android.view.SurfaceHolder;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MyIJKPlayer implements IABCPlayer {
    private IjkMediaPlayer ijkMediaPlayer;
    private PreparedListener preparedListener;

    @Override
    public void createPlayer(Context context) {
        ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
    }

    @Override
    public void setSurfaceHolder(SurfaceHolder holder) {
        ijkMediaPlayer.setSurface(holder.getSurface());
    }

    @Override
    public void setVideoPath(String videoPath, Context context) {
        try {
            ijkMediaPlayer.setDataSource(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preparePlayer() {
        ijkMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                preparedListener.onPrepared();
            }
        });
        ijkMediaPlayer.prepareAsync();
    }

    @Override
    public void startPlayer() {
        ijkMediaPlayer.start();
    }

    @Override
    public void pausePlayer() {
        ijkMediaPlayer.pause();
    }

    @Override
    public void stopPlayer() {
        ijkMediaPlayer.stop();
    }

    @Override
    public void releasePlayer() {
        ijkMediaPlayer.release();
    }

    @Override
    public boolean isPlaying() {
        return ijkMediaPlayer.isPlaying();

    }

    @Override
    public void seekTo(long position) {

        ijkMediaPlayer.seekTo((int) position);

    }

    @Override
    public long getCurrentPosition() {
        return ijkMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getVideoDuration() {
        return ijkMediaPlayer.getDuration();
    }

    @Override
    public void setPreparedListener(PreparedListener preparedLister) {
        this.preparedListener = preparedLister;
    }

}


