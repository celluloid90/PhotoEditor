package com.example.photo_editor.editor.activity.playerview;

import android.content.Context;
import android.view.SurfaceHolder;

public interface IABCPlayer {

    void createPlayer(Context context);

    void setSurfaceHolder(SurfaceHolder holder);

    void setVideoPath(String videoPath, Context context);

    void preparePlayer();

    void startPlayer();

    void pausePlayer();

    void stopPlayer();

    void releasePlayer();

    boolean isPlaying();

    void seekTo(long position);

    long getCurrentPosition();

    long getVideoDuration();

    void setPreparedListener(PreparedListener preparedLister);

}
