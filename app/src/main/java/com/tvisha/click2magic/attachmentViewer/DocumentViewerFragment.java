package com.tvisha.click2magic.attachmentViewer;

import android.app.Dialog;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tvisha.click2magic.CustomFontViews.NestedWebView;
import com.tvisha.click2magic.DataBase.ConversationTable;
import com.tvisha.click2magic.Helper.Helper;
import com.tvisha.click2magic.Helper.MultipartUtility;
import com.tvisha.click2magic.Helper.Values;
import com.tvisha.click2magic.R;
import com.tvisha.click2magic.socket.AppSocket;


public class DocumentViewerFragment extends BottomSheetDialogFragment implements  View.OnTouchListener {


    @BindView(R.id.back_icon)
    ImageView actionBack;

    @BindView(R.id.actionShare)
    ImageView actionShare;

    @BindView(R.id.ContentLoadingProgressBar)
    ImageView ContentLoadingProgressBar;

    @BindView(R.id.docView)
    NestedWebView docView;

    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.actionLable)
    TextView actionLable;

    @BindView(R.id.actionBar)
    RelativeLayout actionBar;

    @OnClick(R.id.back_icon)
     void back(){
        dismiss();
    }
    @OnClick(R.id.actionShare)
     void share(){
        String path = (new ConversationTable(getActivity()).getTheDocumentPath(id,messageId,true));
        Helper.getInstance().shareFileToOtherApps(getContext(), path,true,Values.Gallery.GALLERY_PDF);
        dismiss();
    }

    View contentView;
    String docPath,id,url,receiver_name,user_name;
    int messageId=0;
    boolean actionBarToggle, isOpen=false;

    MultipartUtility multipart;
    CoordinatorLayout.Behavior behavior;


    @Override
    public void onResume() {
        Helper.getInstance().LogDetails("onResume","called");
        if(isOpen){
            dismiss();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        Helper.getInstance().LogDetails("onResume onPause","called");
        super.onPause();
    }



    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //Get the content View
        try{
        contentView = View.inflate(getContext(), R.layout.dialog_doc_viewer, null);
        dialog.setContentView(contentView);
        ButterKnife.bind(this,contentView);


        handleIntent();
        initViews();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        params.height = CoordinatorLayout.LayoutParams.MATCH_PARENT;
        behavior = params.getBehavior();

        AppSocket.SOCKET_OPENED_ACTIVITY = Values.AppActivities.GALLARY_PAGE;

        //Set callback
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            // ((BottomSheetBehavior) behavior).setPeekHeight(200);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int screenHeight = displaymetrics.heightPixels;
            ((BottomSheetBehavior) behavior).setPeekHeight(screenHeight);
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_COLLAPSED);
            //  ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleIntent() {

        try{

        Bundle bundle = getArguments();
        docPath = bundle.getString(Values.IntentData.ATTACHMENT_PATH);
        url = bundle.getString(Values.IntentData.IMAGE_PATH);
        receiver_name = bundle.getString(Values.IntentData.RECEIVER_NAME);
        user_name = bundle.getString(Values.IntentData.SENDER_NAME);
        id = bundle.getString(Values.IntentData.CONVERSATION_REFERENCE_ID);
        messageId = bundle.getInt(Values.IntentData.MESSAGE_ID);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initViews() {
        try {


            Glide.with(getActivity()).load(R.drawable.loading).into(ContentLoadingProgressBar);


            actionShare.setVisibility(View.GONE);
            ContentLoadingProgressBar.setVisibility(View.VISIBLE);

            if (id==null || id.equals("0") || id.trim().isEmpty() || id.equals("null"))
            {
                actionShare.setVisibility(View.GONE);
            }else {
                actionShare.setVisibility(View.VISIBLE);
            }

            actionLable.setText(user_name);

            //loadData();

            loadDoc();
            //toggleActionBar();
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadData(){
        try {
            if (Helper.getConnectivityStatus(getActivity())) {


                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(url);
            } else {
                Toast.makeText(getActivity(), "Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void loadDoc() {
        try {

            if (docPath == null || docPath.isEmpty()) {
                docPath=url;
            }

            if (docPath != null && !docPath.isEmpty()) {

                Helper.getInstance().LogDetails("loadDoc",docPath +" "+url);
                String url1 = "http://docs.google.com/gview?embedded=true&url=" + url;
                docView.getSettings().setJavaScriptEnabled(true);
                docView.loadUrl(url1);
                isOpen=true;

             /*   File file = new File(docPath);
                if(file!=null && file.exists())
                {
                    Helper.getInstance().LogDetails("loadDoc","file exists");
                    webView.loadUrl("file:///" + file);
                }
                else
                {
                    Helper.getInstance().LogDetails("loadDoc","file not  exists");
                 String url1 = "http://docs.google.com/gview?embedded=true&url=" + url;
                docView.getSettings().setJavaScriptEnabled(true);
                docView.loadUrl(url1);
                isOpen=true;
                }*/


                //File file = new File(docPath);
            /*    WebSettings settings = docView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setAllowFileAccessFromFileURLs(true);
                settings.setAllowUniversalAccessFromFileURLs(true);
                settings.setBuiltInZoomControls(true);
                settings.setAllowContentAccess(true);
                settings.setUseWideViewPort(true);

                docView.setVerticalScrollBarEnabled(true);
                docView.setHorizontalScrollBarEnabled(true);
                docView.setWebViewClient(new AppWebViewClients());
                //docView.loadUrl(docPath);
                //docPath = file.getName();
                if (id!=null && !id.equals("0") && !id.equals("null") && !id.trim().isEmpty()){
                docView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" +docPath+ "#zoom=page-width");
               // docView.loadUrl(url1);
                }else {
                    docView.loadUrl(url1);
                }*/
            }
            else
            {
                Helper.getInstance().LogDetails("loadDoc","empty");
              //  String path = (new ConversationTable(getActivity()).getTheDocumentPath(id,true));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Helper.getInstance().LogDetails("loadDoc","exception "+e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try{
        if (event.getAction()== MotionEvent.ACTION_SCROLL)
        {
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            return true;
        }else {
            return false;
        }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    public class AppWebViewClients extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            ContentLoadingProgressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);

        }
    }

}
