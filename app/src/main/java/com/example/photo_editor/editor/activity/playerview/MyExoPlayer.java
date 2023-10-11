package com.example.photo_editor.editor.activity.playerview;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceHolder;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MyExoPlayer implements IABCPlayer {
    private SimpleExoPlayer mExoPlayer;
    private BandwidthMeter mBandwidthMeter;
    private MediaSource mVideoSource;
    private static final String APP_NAME = MainActivity.class.getSimpleName();
    private PreparedListener preparedListener;
    @Override
    public void createPlayer(Context context) {
       /* mBandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(mBandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);*/
    }
    @Override
    public void setSurfaceHolder(SurfaceHolder holder) {
        mExoPlayer.setVideoSurface(holder.getSurface());
    }
    @Override
    public void setVideoPath(String videoPath, Context context) {
        /*mBandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, APP_NAME),
                (DefaultBandwidthMeter) mBandwidthMeter);//note the type casting
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        mVideoSource = new ExtractorMediaSource(
                Uri.parse(videoPath),
                dataSourceFactory,
                extractorsFactory,
                null, null
        );*/
    }
    @Override
    public void preparePlayer() {

       /* mExoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
            }
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }
            @Override
            public void onLoadingChanged(boolean isLoading) {

            }
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                preparedListener.onPrepared();
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }
            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });*/
        mExoPlayer.prepare(mVideoSource);
    }

    @Override
    public void startPlayer() {
        mExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pausePlayer() {
        mExoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void stopPlayer() {
        mExoPlayer.stop();
    }

    @Override
    public void releasePlayer() {
        mExoPlayer.release();
    }

    @Override
    public boolean isPlaying() {
       return mExoPlayer.getPlayWhenReady();
    }

    @Override
    public void seekTo(long position) {
        mExoPlayer.seekTo(position);
    }

    @Override
    public long getCurrentPosition() {
        return mExoPlayer.getCurrentPosition();
    }

    @Override
    public long getVideoDuration() {
        return mExoPlayer.getDuration();
    }

    @Override
    public void setPreparedListener(PreparedListener preparedLister) {
        this.preparedListener = preparedLister;
    }


}