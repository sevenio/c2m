package com.tvisha.click2magic.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.tvisha.click2magic.CustomFontViews.ZoomImageView;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.Handlers.HandlerHolder;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.SimpleGestureFilter;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.model.GalleryModel;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;


public class ImageviewActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.back_icon)
    ImageView actionBack;

    @BindView(R.id.actionShare)
    ImageView actionShare;

    @BindView(R.id.image)
    ImageView image;


    ImageView expanded_image;

    @BindView(R.id.actionBar)
    RelativeLayout actionBar;

    @BindView(R.id.actionLable)
    TextView actionLable;


    @BindView(R.id.parent_view)
    LinearLayout parent_view;

    @BindView(R.id.image_flippers)
    ViewPager image_viewpager;


    boolean actionBarToggle,zooming = false,is_video_playing = false;;
    String item_position = "0",user_name, receiver_name,conversationReferenceId,imagePath, receiver_id, sender_id;
    long mShortAnimationDuration = 10;
    int height, width,share_position = 0;;
    List<GalleryModel> galleryModels;
    Uri uriFile;
    ConversationTable conversationTable;
    ImagePagerAdapter adapter;




    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    private Animator mCurrentAnimator;
    private SimpleGestureFilter detector;
    private GestureDetectorCompat mDetector;
    private float mX, mY;
    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;
    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;

    public static Bitmap getThumbnail(Context context, String path) throws Exception {
        ContentResolver cr = context.getContentResolver();
        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }

        ca.close();
        return null;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        ButterKnife.bind(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        HandlerHolder.imageviewHandler = uiHandler;

        getScreenHeightAndWidth();
        handleIntent();
        intializeWidgets();
    }

    private void handleIntent() {
        try {

            Bundle bundle = getIntent().getExtras();
            imagePath = bundle.getString(Values.IntentData.IMAGE_PATH);
            receiver_id = bundle.getString(Values.IntentData.RECEIVER_ID);
            conversationReferenceId = bundle.getString(Values.IntentData.CONVERSATION_REFERENCE_ID);
            sender_id = bundle.getString(Values.IntentData.SENDER_ID);
            receiver_name = bundle.getString(Values.IntentData.RECEIVER_NAME);
            user_name = bundle.getString(Values.IntentData.SENDER_NAME);
            Helper.getInstance().LogDetails("previewhandleIntent", " data " + imagePath + " " + receiver_id + " " + conversationReferenceId + " " + sender_id + " " + receiver_id + " " + receiver_name + " " + user_name);
        }catch (Exception e){
            e.printStackTrace();
        }
        }

    private void getScreenHeightAndWidth() {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = (displayMetrics.widthPixels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void intializeWidgets() {
        try {

            conversationTable = new ConversationTable(getApplicationContext());

            actionBack.setVisibility(View.VISIBLE);
            actionBack.setOnClickListener(this);


            actionShare.setOnClickListener(this);
            actionShare.setVisibility(View.GONE);



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                actionShare.setImageResource(R.drawable.ic_share);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    actionShare.setImageResource(R.drawable.ic_share);
                } else {
                    Drawable shareDrawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_share, null);
                    actionShare.setImageDrawable(shareDrawable);
                }
            }
            loadImage();

            image_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (position != -1) {
                        if (galleryModels != null && galleryModels.size() > 0) {
                            if (is_video_playing) {
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        share_position = position;

                        try {
                            if (galleryModels != null && galleryModels.size() > 0) {
                                String created_at = galleryModels.get(position).getCreatedAt();
                                String senderId = galleryModels.get(position).getSenderid();

                                // action_lable_date.setText(TimeHelper.getInstance().getMessagesDateAdapter(created_at).toLowerCase());
                                // action_lable_date.setTypeface(action_lable_date.getTypeface(), Typeface.ITALIC);
                                if (sender_id.equals(senderId)) {
                                    if(user_name!=null && !user_name.trim().isEmpty())
                                    {
                                        actionLable.setText(Helper.getInstance().capitalize(user_name));
                                    }

                                } else {
                                    if(receiver_name!=null && !receiver_name.trim().isEmpty())
                                    {
                                        actionLable.setText(Helper.getInstance().capitalize(receiver_name));
                                    }

                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            actionBack.setOnClickListener(this);
            actionShare.setOnClickListener(this);
            image.setOnClickListener(this);

            image_viewpager.setPageTransformer(false, new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View page, float position) {
                    int pageWidth = image_viewpager.getMeasuredWidth() - image_viewpager.getPaddingLeft() - image_viewpager.getPaddingRight();
                    int pageHeight = image_viewpager.getHeight();
                    int paddingLeft = image_viewpager.getPaddingLeft();
                    float transformPos = (float) (page.getLeft() - (image_viewpager.getScrollX() + paddingLeft)) / pageWidth;


                    int max = -pageHeight / 100;

                    if (transformPos < -1) { // [-Infinity,-1)
                        page.setTranslationY(0);
                    } else if (transformPos <= 1) { // [-1,1]
                        page.setTranslationY(max * (1 - Math.abs(transformPos)));
                    } else {
                        page.setTranslationY(0);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIsZoom(boolean b) {
        zooming = b;
        if (zooming) {

            if (actionBar != null) {
                actionBar.setVisibility(View.GONE);
            }
        } else {
            if (actionBar != null) {
                actionBar.setVisibility(View.VISIBLE);
            }
        }
    }

    public void loadImage() {
        try {

            galleryModels = conversationTable.getMedia(receiver_id, conversationReferenceId, sender_id);

            Helper.getInstance().LogDetails("getMedia", "loadImage" + galleryModels.size());


            if (galleryModels != null && galleryModels.size() > 0) {
                Collections.reverse(galleryModels);
                adapter = new ImagePagerAdapter(galleryModels);
                int count = 0;

                String senderId = galleryModels.get(0).getSenderid();
                if (sender_id.equals(senderId)) {
                    if(user_name!=null && !user_name.trim().isEmpty())
                    {
                        actionLable.setText(Helper.getInstance().capitalize(user_name));
                    }

                } else {
                    if(receiver_name!=null && !receiver_name.trim().isEmpty()){
                        actionLable.setText(Helper.getInstance().capitalize(receiver_name));
                    }

                }


                for (int j = 0; j < galleryModels.size(); j++) {

                    String image_paths = galleryModels.get(j).getPath();

                    if (image_paths.trim().toLowerCase().equals(imagePath.trim().toLowerCase())) {
                        count = j;
                        item_position = String.valueOf(j);
                        image_viewpager.setCurrentItem(j);
                        share_position = j;
                        break;
                    }
                    image_viewpager.setAdapter(adapter);
                }
                if (count == 0) {
                    image_viewpager.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleActionBar() {
        try {
            if (actionBarToggle) {
                actionBar.animate().translationY(-actionBar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
                actionBarToggle = false;
            } else {
                actionBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
                actionBarToggle = true;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void closeUpAndDismissDialog(int currentPosition) {
        isClosing = true;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(parent_view, "y", currentPosition, -parent_view.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        positionAnimator.start();
    }

    public void closeDownAndDismissDialog(int currentPosition) {
        isClosing = true;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(parent_view, "y", currentPosition, screenHeight + parent_view.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        positionAnimator.start();
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.back_icon:
                    onBackPressed();
                    break;
                case R.id.actionShare:
                    if (share_position != -1) {

                        if (galleryModels != null && galleryModels.size() > 0) {
                            String image_path = galleryModels.get(share_position).getPath();
                        /*final String packageName = ImageviewActivity.this.getPackageName();
                        authority = new StringBuilder(packageName).append(".fileprovider").toString();*/

                            File file = new File(image_path);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uriFile = FileProvider.getUriForFile(ImageviewActivity.this, "com.tvisha.troopmessenger.fileprovider", file);
                            } else {
                                uriFile = Uri.fromFile(new File(image_path));
                            }


                            int file_type = galleryModels.get(share_position).getType();
                            Intent shareIntent = new Intent();
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uriFile);
                            if (file_type == Values.Gallery.GALLERY_IMAGE) {
                                shareIntent.setType("image/*");
                            } else if (file_type == Values.Gallery.GALLERY_VIDEO) {
                                shareIntent.setType("video/*");
                            } else {
                                shareIntent.setType("application/*");
                            }
                            startActivity(Intent.createChooser(shareIntent, "Share to"));
                        }
                    }
                    break;
                case R.id.image:
                    toggleActionBar();
                    break;


                //  Navigation.getInstance().openGallery(this, entityId, receiver_id,WORKSPACEID);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        try {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("imagePath", imagePath);
            intent.putExtra("receiver_id", receiver_id);

            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onRestart();
    }

    private void zoomImageFromThumb(final View thumbView, String imagePath) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        Glide.with(ImageviewActivity.this).load(imagePath).into(expanded_image);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.relative_layout)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expanded_image.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expanded_image.setPivotX(0f);
        expanded_image.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expanded_image, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expanded_image, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expanded_image, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expanded_image,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expanded_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expanded_image, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expanded_image,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expanded_image,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expanded_image,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expanded_image.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expanded_image.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    private static enum State {NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM}

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    public class ImagePagerAdapter extends PagerAdapter {
        List<GalleryModel> galleryModels;

        public ImagePagerAdapter(List<GalleryModel> galleryModels) {
            this.galleryModels = galleryModels;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {


            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_image_viewer, container, false);


            final ZoomImageView imageView = (ZoomImageView) view.findViewById(R.id.imageViewer);
            final ImageView gif_attachment = (ImageView) view.findViewById(R.id.gif_attachment);


            String devicePath = galleryModels.get(position).getPath();
            String path = galleryModels.get(position).getImageUrl();

            String attachmentType;
            int index = path.lastIndexOf(".");
            attachmentType = path.substring(index + 1);

            // Helper.getInstance().LogDetails("ImagePagerAdapter",devicePath+" "+path+" "+attachmentType);


            if (devicePath != null && !devicePath.trim().isEmpty()) {

                File imgFile = new File(devicePath);
                if (imgFile.exists()) {



                    //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if ( attachmentType != null && !attachmentType.toLowerCase().equals("gif") ) {
                        imageView.setVisibility(View.GONE);
                        gif_attachment.setVisibility(View.VISIBLE);
                      //  imageView.setImageURI(Uri.fromFile(imgFile));
                       // imageView.setImageBitmap(myBitmap);
                        RequestOptions options = new RequestOptions()
                                .error(R.drawable.ic_attachment_img)
                                .disallowHardwareConfig()
                                .priority(Priority.HIGH);
                        Glide.with(ImageviewActivity.this)
                                .load(devicePath)
                                .apply(options)
                                .into(gif_attachment);
                        Helper.getInstance().LogDetails("devicePath***",devicePath);
                    } else {
                        if (attachmentType != null && attachmentType.toLowerCase().equals("gif")) {
                            imageView.setVisibility(View.GONE);
                            gif_attachment.setVisibility(View.VISIBLE);
                            RequestOptions options = new RequestOptions()
                                    .error(R.drawable.ic_attachment_img)
                                    .disallowHardwareConfig()
                                    .priority(Priority.HIGH);
                            path = path.replace("\"", "");
                            Glide.with(ImageviewActivity.this)
                                    .load(path)
                                    .apply(options)
                                    .into(gif_attachment);
                        } else {
                            imageView.setVisibility(View.VISIBLE);
                            gif_attachment.setVisibility(View.GONE);
                            if (path != null && !path.trim().isEmpty()) {
                                path = path.replace("\"", "");
                                RequestOptions options = new RequestOptions()
                                        .error(R.drawable.ic_attachment_img)
                                        .disallowHardwareConfig()
                                        .priority(Priority.HIGH);
                                Glide.with(ImageviewActivity.this)
                                        .load(path)
                                        .apply(options)
                                        .into(imageView);

                            }

                        }


                    }


                } else {
                    if (path != null && !path.trim().isEmpty()) {
                        path = path.replace("\"", "");
                        RequestOptions options = new RequestOptions()
                                .error(R.drawable.ic_attachment_img)
                                .disallowHardwareConfig()
                                .priority(Priority.HIGH);
                        Glide.with(ImageviewActivity.this)
                                .load(path)
                                .apply(options)
                                .into(imageView);

                    }
                }
            } else if (path != null && !path.trim().isEmpty()) {
                path = path.replace("\"", "");
                RequestOptions options = new RequestOptions()
                        .error(R.drawable.ic_attachment_img)
                        .disallowHardwareConfig()
                        .priority(Priority.HIGH);
                Glide.with(ImageviewActivity.this)
                        .load(path)
                        .apply(options)
                        .into(imageView);

            }

/*
            final ImageView gif_attachment = (ImageView) view.findViewById(R.id.gif_attachment);
            final ImageView video_button = (ImageView) view.findViewById(R.id.video_button);
            final VideoView videoView = (VideoView) view.findViewById(R.id.video);
            expanded_image = view.findViewById(R.id.expanded_image);
            final String imagePaths = galleryModels.get(position).getPath();
            final int file_type = galleryModels.get(position).getType();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                video_button.setImageResource(R.drawable.ic_video);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    video_button.setImageResource(R.drawable.ic_video);
                } else {
                    Drawable videoDrawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_video, null);
                    video_button.setImageDrawable(videoDrawable);

                }
            }
            if (file_type == Values.Gallery.GALLERY_VIDEO) {
                imageView.setVisibility(View.GONE);
                loadingview.setVisibility(View.GONE);
                gif_attachment.setVisibility(View.VISIBLE);
                video_button.setVisibility(View.VISIBLE);
                try {
                    video_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.video_button:
                                    imageView.setVisibility(View.GONE);
                                    loadingview.setVisibility(View.GONE);
                                    video_button.setVisibility(View.GONE);
                                    gif_attachment.setVisibility(View.GONE);
                                    videoView.setVisibility(View.VISIBLE);
                                    try {

                                        videoView.setVideoURI(Uri.parse(imagePaths));

                                        is_video_playing = true;
                                        MediaController mediaController = new MediaController(ImageviewActivity.this);
                                        videoView.setMediaController(mediaController);
                                        //mediaController.setAnchorView(videoView);
                                        videoView.requestFocus();
                                        videoView.start();
                                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                is_video_playing = false;
                                                videoView.setVisibility(View.GONE);
                                                imageView.setVisibility(View.GONE);
                                                loadingview.setVisibility(View.GONE);
                                                gif_attachment.setVisibility(View.VISIBLE);
                                                video_button.setVisibility(View.VISIBLE);
                                            }
                                        });
                                        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                                            @Override
                                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                                is_video_playing = false;
                                                Navigation.getInstance().openFiles(ImageviewActivity.this, imagePaths, galleryModels.get(position).getId());
                                                return true;
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        is_video_playing = false;
                                    }
                                    break;
                            }
                        }
                    });



                } catch (Exception e) {
                    is_video_playing = false;
                    e.printStackTrace();
                }

            } else {
                is_video_playing = false;
            }
            boolean val = FileFormatHelper.getInstance().isGif(imagePaths);
            if (val) {
                is_video_playing = false;
                imageView.setVisibility(View.VISIBLE);
                loadingview.setVisibility(View.GONE);
                gif_attachment.setVisibility(View.VISIBLE);

                Glide.with(ImageviewActivity.this).
                        load(imagePaths)
                        .into(imageView);
            } else {
                try {
                    int[] loc = new int[2];
                    int left = 0;
                    int top = 0;
                    imageView.getLocationOnScreen(loc);

                    if (file_type == Values.Gallery.GALLERY_IMAGE) {
                        imageView.setVisibility(View.VISIBLE);
                        gif_attachment.setVisibility(View.GONE);
                        is_video_playing = false;

                        try {
                            imageView.setImageURI(Uri.parse(imagePaths));
                        } catch (OutOfMemoryError e) {
                            try {
                                expanded_image.setVisibility(View.VISIBLE);
                                imageView.setVisibility(View.GONE);
                                zoomImageFromThumb(imageView, imagePaths);
                            } catch (OutOfMemoryError e1) {
                                ToastUtil.getInstance().showToast(ImageviewActivity.this, "OutOfMemory");
                                e.printStackTrace();
                            }

                        }

                    } else {
                        is_video_playing = false;
                        imageView.setVisibility(View.GONE);
                        loadingview.setVisibility(View.GONE);
                        gif_attachment.setVisibility(View.VISIBLE);
                        Glide.with(ImageviewActivity.this)
                                .load(imagePaths)
                                .into(gif_attachment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
         */

            container.addView(view);
            return view;


        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            //return false;
            return view == object;
        }

        @Override
        public int getCount() {
            if(galleryModels!=null && galleryModels.size()>0){
                return galleryModels.size();
            }
            else
            {
                return 0;
            }

        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

    }

    public class TrashDragListener implements View.OnDragListener {
        private static final String TAG = "TrashDragListener";

        private int enterShape;
        private int normalShape;
        private boolean hit;
        private String image_path;

        public TrashDragListener() {
            this.enterShape = enterShape;
            this.normalShape = normalShape;
            this.image_path = image_path;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {

            try {
                final ViewPager containerView = (ViewPager) v;
                final ViewPager draggedView = (ViewPager) event.getLocalState();

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        //Log.d(TAG, "onDrag: ACTION_DRAG_STARTED");
                        hit = false;
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        //Log.d(TAG, "onDrag: ACTION_DRAG_ENTERED");
                        assert containerView != null;
                        //containerView.setImageURI(Uri.parse(image_path));
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        //Log.d(TAG, "onDrag: ACTION_DRAG_EXITED");
                        assert containerView != null;
                        //containerView.setImageURI(Uri.parse(image_path));
                        finish();
                        return true;
                    case DragEvent.ACTION_DROP:
                        //Log.d(TAG, "onDrag: ACTION_DROP");
                        hit = true;
                        draggedView.post(new Runnable() {
                            @Override
                            public void run() {
                                draggedView.setVisibility(View.GONE);
                            }
                        });
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // Log.d(TAG, "onDrag: ACTION_DRAG_ENDED");
                        assert containerView != null;
                        //containerView.setImageURI(Uri.parse(image_path));
                        v.setVisibility(View.VISIBLE);
                        if (!hit) {
                            draggedView.post(new Runnable() {
                                @Override
                                public void run() {
                                    draggedView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        assert containerView != null;
                        //containerView.setImageURI(Uri.parse(image_path));
                        v.setVisibility(View.VISIBLE);
                        return true;
                    default:
                        return true;
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
    }
}
