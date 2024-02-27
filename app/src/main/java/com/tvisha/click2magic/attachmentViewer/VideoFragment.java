package com.tvisha.click2magic.attachmentViewer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;


/**
 * Created by tvisha on 17/11/17.
 */

public class VideoFragment extends Activity implements View.OnClickListener, MediaPlayer.OnCompletionListener {
    VideoView video;
    View contentView;
    ImageButton actionBack, actionShare;
    ImageView image;
    MediaController mediaController;
    DisplayMetrics dm;
    FrameLayout frame_layout;
    FrameLayout layoutmain, root;

    String path;
    Bundle bundle;
    View rootview;

    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.dialog_video_player,container,false);



        bundle = getArguments();

        path = bundle.getString("path");

        video = (VideoView) rootview.findViewById(R.id.video);
        PlayVideo();
        actionBack = (ImageButton) rootview.findViewById(R.id.actionBack);
        actionShare = (ImageButton) rootview.findViewById(R.id.actionShare);
        actionBack.setOnClickListener(this);
        actionShare.setOnClickListener(this);
        return rootview;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)   getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.dialog_video_player, null);
        setContentView(R.layout.dialog_video_player);


        /*CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)  root.getLayoutParams();
        params.height = FrameLayout.LayoutParams.MATCH_PARENT;

        CoordinatorLayout.Behavior behavior = params.getBehavior();

        //Set callback
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            // ((BottomSheetBehavior) behavior).setPeekHeight(200);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int screenHeight = displaymetrics.heightPixels;

            ((BottomSheetBehavior) behavior).setPeekHeight(screenHeight);
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_COLLAPSED);

            //((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }*/
        //setupVideo();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //video
    public void PlayVideo() {
        try {

            final MediaController mediaController = new MediaController(getApplicationContext());
            mediaController.setAnchorView(video);

            Uri uri = Uri.parse(path);
            video.setMediaController(mediaController);
            video.setVideoURI(uri);
            video.requestFocus();
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mediaController.show(0);
                    video.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //audio

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionBack:
               onBackPressed();
                break;
            case R.id.actionShare:
                Helper.getInstance().shareFileToOtherApps(getApplicationContext(), path,true, Values.Gallery.GALLERY_VIDEO);
               onBackPressed();
                break;
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
