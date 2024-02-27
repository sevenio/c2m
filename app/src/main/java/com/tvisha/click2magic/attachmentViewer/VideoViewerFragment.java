package com.tvisha.click2magic.attachmentViewer;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;


import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;

import java.io.File;

/**
 * Created by root on 29/6/17.
 */

public class VideoViewerFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    VideoView video;
    View contentView;
    ImageButton actionBack, actionShare;
    ImageView image;
    MediaController mediaController;
    DisplayMetrics dm;
    FrameLayout frame_layout;
    RelativeLayout layoutmain,layout;

    String path;


    //Bottom Sheet Callback
   /* private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(View bottomSheet, float slideOffset) {
        }
    };*/

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        //Get the content View

        contentView = View.inflate(getContext(), R.layout.dialog_video_player, null);
        dialog.setContentView(contentView);
        initViews();

        //Set the coordinator layout behavior
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
            params.height = CoordinatorLayout.LayoutParams.MATCH_PARENT;

            CoordinatorLayout.Behavior behavior = params.getBehavior();

            //Set callback
            if (behavior != null && behavior instanceof BottomSheetBehavior) {
                // ((BottomSheetBehavior) behavior).setPeekHeight(200);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int screenHeight = displaymetrics.heightPixels;

                ((BottomSheetBehavior) behavior).setPeekHeight(screenHeight);
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_COLLAPSED);

                //((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            }
    }

    public void initViews() {
        try {
            Bundle bundle = getArguments();
            //for video
            path = bundle.getString("videoPath");
            video = (VideoView) contentView.findViewById(R.id.video);
            loadVideo();
            //setupVideo();
            actionBack = (ImageButton) contentView.findViewById(R.id.actionBack);
            actionShare = (ImageButton) contentView.findViewById(R.id.actionShare);
            actionBack.setOnClickListener(this);
            actionShare.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadVideo() {
        try {
            File file = new File(path);
            path = file.getAbsolutePath();
            //File filePath = new File(path);

            if (path != null && !path.isEmpty()) {
                video.setVideoURI(Uri.parse(path));
                video.setZOrderOnTop(true);
                mediaController = new MediaController(getActivity());
                video.setMediaController(mediaController);
                mediaController.setAnchorView(video);
                video.start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionBack:
                dismiss();
                break;
            case R.id.actionShare:
                Helper.getInstance().shareFileToOtherApps(getContext(), path,true, Values.Gallery.GALLERY_VIDEO);
                dismiss();
                break;
        }
    }
    public void setupVideo(){
        if (mediaController == null) {
            mediaController = new
                    MediaController(getActivity());

            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);
        }
        try {
            video.setVideoURI(Uri.parse(path));
            video.setZOrderOnTop(true);
        } catch (Exception e) {
            //Logger.debug("Error: " + e.getMessage());
            e.printStackTrace();
        }
        video.requestFocus();
        video.start();

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                        mediaController.setAnchorView(video);
                    }
                });
            }
        });
        video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //AlertDialogFactory.buildAlertDialog(mContext, 0, R.string.video_playing_error).show();
                return false;
            }
        });
    }


    /*public static Uri buildURiPath(int resourceID)
    {
        String uriPath = String.valueOf(Uri.parse("android.resource://" + LoginActivity.PACKAGE_NAME + "/"
                +resourceID));
        Uri uri = Uri.parse(uriPath);
        return uri;
    }*/
}

