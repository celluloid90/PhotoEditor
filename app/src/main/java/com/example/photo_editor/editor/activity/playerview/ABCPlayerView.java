package com.example.photo_editor.editor.activity.playerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photo_editor.R;

public class ABCPlayerView extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = "CustomView";
    private ABCPlayer mAbcPlayer;
    private PlayerType mPlayerType;
    private String VIDEO_PATH;
    private int videoWidth;
    private int videoHeight;
    private int mTextPos;
    private static final int MEIDAPLAYERVALUE =0;
    private static final int IJKPLAYERVALUE =1;
    private static final int EXOPLAYERVALUE =2;
    long a;


    public void setmPlayerType(PlayerType mPlayerType) {
        this.mPlayerType = mPlayerType;
        Log.d(TAG,"PLAYER TYPE "+ mPlayerType);
    }

    public void setVideoPath(String videoPath) {
        this.VIDEO_PATH = videoPath;
    }

    public ABCPlayerView(Context context) {
        super(context);
        setCallBack();
        getEnumValue(null);
        playerSet();

    }

    private void setCallBack() {
        getHolder().addCallback(this);
    }

    public ABCPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCallBack();
        getEnumValue(attrs);
        playerSet();

    }

    public ABCPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCallBack();
        getEnumValue(attrs);
        playerSet();


    }

    public ABCPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setCallBack();
        getEnumValue(attrs);
        playerSet();
    }

    private void playerSet() {
        if (setTextPos()==MEIDAPLAYERVALUE){
            setmPlayerType(PlayerType.MEDIAPLAYER);
        }
        else if (setTextPos()==IJKPLAYERVALUE){
            setmPlayerType(PlayerType.IJKPLAYER);
        }
        else if (setTextPos()==EXOPLAYERVALUE){
            setmPlayerType(PlayerType.EXOPLAYER);
        }
    }

    private void getEnumValue(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ABCPlayerView,
                0, 0);

        try {
            mTextPos = a.getInteger(R.styleable.ABCPlayerView_typePlayer, 4);
            Log.d(TAG,"mTextPos"+ mTextPos);

        } finally {
            a.recycle();
        }
    }

    public int setTextPos(){
        return mTextPos;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //setRatio();
        mAbcPlayer = new ABCPlayer();
        mAbcPlayer.setPlayerType(mPlayerType);
        mAbcPlayer.createPlayer(getContext());
        mAbcPlayer.setSurfaceHolder(holder);
        mAbcPlayer.setVideoPath(VIDEO_PATH, getContext());
        mAbcPlayer.setPreparedListener(new PreparedListener() {
            @Override
            public void onPrepared() {
                long a = mAbcPlayer.getVideoDuration();
                Log.d(TAG, "A" + a);
            }
        });
        mAbcPlayer.preparePlayer();
    }

    private void setRatio() {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(VIDEO_PATH);
        videoWidth = Integer.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        videoHeight = Integer.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        int ratioHeight = screenHeight / videoHeight;
        int ratioWidth = screenWidth / videoWidth;
        android.view.ViewGroup.LayoutParams params = getLayoutParams();
        if (ratioWidth > ratioHeight) {
            params.width = (int) (screenHeight * videoProportion);
            params.height = screenHeight;
        } else {
            params.width = screenWidth;
            params.height = (int) (screenWidth / videoProportion);
        }
        setLayoutParams(params);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(VIDEO_PATH);
        videoWidth = Integer.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        videoHeight = Integer.valueOf(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (videoProportion > screenProportion) {
            layoutParams.width = screenWidth;
            layoutParams.height = (int) ((float) screenWidth / videoProportion);
        } else {
            layoutParams.width = (int) (videoProportion * (float) screenHeight);
            layoutParams.height = screenHeight;
        }
        setLayoutParams(layoutParams);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    public long getDurationOfVideo(){
        mAbcPlayer.setPreparedListener(new PreparedListener() {
            @Override
            public void onPrepared() {
                 a = mAbcPlayer.getVideoDuration();
                Log.d(TAG, "A" + a);
            }
        });
        mAbcPlayer.preparePlayer();
        return a;
    }
    public void start() {
        mAbcPlayer.startPlayer();
        /*mAbcPlayer.setPreparedListener(new PreparedListener() {
            @Override
            public void onPrepared() {
                mAbcPlayer.startPlayer();
            }
        });
        mAbcPlayer.preparePlayer();*/
    }

    public void pause() {
        mAbcPlayer.pausePlayer();
    }

    public boolean isPlaying() {
        return mAbcPlayer.isPlaying();
    }

    public void seekTo(long position) {
        mAbcPlayer.seekTo(position);
    }

    public long getTime() {
       /* MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(VIDEO_PATH);
        String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(time);*/
        return 0;
    }

    @SuppressLint("CheckResult")
    public void scrubberSet(ImageView[] IMGS) {
       /* long frameTime = 0;
        RequestOptions options = new RequestOptions();
        for (ImageView img : IMGS) {
            options.frame(frameTime);
            Glide.with(getContext()).load(VIDEO_PATH).centerCrop().override(200, 200).apply(options).into(img);
            frameTime = frameTime + (getTime() / IMGS.length) * 1000;


        }*/
    }
}
