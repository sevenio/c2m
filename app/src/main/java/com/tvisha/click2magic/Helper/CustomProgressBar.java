package com.tvisha.click2magic.Helper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tvisha.click2magic.R;


public class CustomProgressBar extends Dialog {

    String text;
    boolean isCancel=false;
    TextView txt;
    Context context;
    ImageView imageView;
    public CustomProgressBar(Context context) {

        super(context, R.style.common_dialog_progrss);
        this.context=context;


    }


    public CustomProgressBar(Context context, boolean isCancel) {
        super(context);
        this.isCancel=isCancel;
        this.context=context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress_bar);
        imageView=findViewById(R.id.image);

        Glide.with(context)
                .load(R.drawable.progressbar_rotate)
                .into(imageView);

    /*            Glide
                .with(context)
                .load(R.drawable.progressbar_rotate)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.progressbar_rotate)
                        .fitCenter())
                .into(imageView);*/


        setCancelable(isCancel);
        // setTitle("please wait...");
    }
}
