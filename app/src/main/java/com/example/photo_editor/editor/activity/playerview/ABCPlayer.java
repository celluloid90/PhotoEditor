package com.example.photo_editor.editor.activity.playerview;

import android.content.Context;
import android.view.SurfaceHolder;

public class ABCPlayer implements IABCPlayer,IABCPlayerType {
    private PlayerType playerType;
    private IABCPlayer abcPlayerInterface;
    private boolean createPlayerState=false;
    private boolean setSurfaceHolderState=false;
    private boolean setVideoPathState=false;
    private boolean setPreparePlayerState =false;

    private PreparedListener preparedListener;


    @Override
    public void createPlayer(Context context) {

        if (playerType == PlayerType.MEDIAPLAYER) {
            abcPlayerInterface = new MyMediaPlayer();
        } else if (playerType == PlayerType.EXOPLAYER) {
            abcPlayerInterface = new MyExoPlayer();
        } else if (playerType == PlayerType.IJKPLAYER) {
            abcPlayerInterface = new MyIJKPlayer();
        }
        abcPlayerInterface.createPlayer(context);
        createPlayerState=true;
    }


    @Override
    public void setSurfaceHolder(SurfaceHolder holder) {
        if (!createPlayerState){
            throw new IllegalStateException(" Not yet Created");
        }
        abcPlayerInterface.setSurfaceHolder(holder);
        setSurfaceHolderState=true;
    }

    @Override
    public void setVideoPath(String videoPath, Context context) {
        if (!createPlayerState){
            throw new IllegalStateException("Not yet Created");
        }
        if(!setSurfaceHolderState){
            throw new IllegalStateException(" Not yet surfaceHolder created");
        }
        abcPlayerInterface.setVideoPath(videoPath, context);
        setVideoPathState=true;
    }

    @Override
    public void preparePlayer() {
        if (!createPlayerState){
            throw new IllegalStateException("Not yet Created");
        }
        if(!setSurfaceHolderState){
            throw new IllegalStateException("Not yet surfaceHolder created");
        }
        if(!setVideoPathState){
            throw new IllegalStateException("Video Path  Not yet set");
        }

        abcPlayerInterface.setPreparedListener(new PreparedListener() {
            @Override
            public void onPrepared() {
                setPreparePlayerState = true;
                preparedListener.onPrepared();
            }
        });

        abcPlayerInterface.preparePlayer();


    }

    @Override
    public void startPlayer() {
        if (!createPlayerState){
            throw new IllegalStateException("Not yet Created");
        }
        if(!setSurfaceHolderState){
            throw new IllegalStateException("Not yet surfaceHolder created");
        }
        if(!setVideoPathState){
            throw new IllegalStateException("Video Path Not yet set");
        }if(!setPreparePlayerState){
            throw new IllegalStateException("player Not yet prepared ");
        }
        abcPlayerInterface.startPlayer();
        boolean setstartPlayerState = true;

    }

    @Override
    public void pausePlayer() {
        abcPlayerInterface.pausePlayer();
    }

    @Override
    public void stopPlayer() {
        abcPlayerInterface.stopPlayer();
    }

    @Override
    public void releasePlayer() {
        abcPlayerInterface.releasePlayer();
    }

    @Override
    public boolean isPlaying() {
        return abcPlayerInterface.isPlaying();
    }

    @Override
    public void seekTo(long position) {
        abcPlayerInterface.seekTo(position);
    }

    @Override
    public long getCurrentPosition() {
        return abcPlayerInterface.getCurrentPosition();
    }

    @Override
    public long getVideoDuration() {
        return abcPlayerInterface.getVideoDuration();
    }

    @Override
    public void setPreparedListener(PreparedListener preparedLister) {
        this.preparedListener = preparedLister;
    }

    @Override
    public void setPlayerType(PlayerType playerEnum) {
        this.playerType = playerEnum;
    }


}
