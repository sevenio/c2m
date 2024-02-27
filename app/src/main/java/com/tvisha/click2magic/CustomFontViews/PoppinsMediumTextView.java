package com.tvisha.click2magic.CustomFontViews;

import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by koti on 24/5/17.
 */

public class PoppinsMediumTextView extends AppCompatTextView {
    public PoppinsMediumTextView(Context context) {
        super(context);
       // this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Poppins-Medium.ttf"));
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-Medium.ttf"));
    }

    public PoppinsMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Poppins-Medium.ttf"));
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-Medium.ttf"));
    }

    public PoppinsMediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       // this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Poppins-Medium.ttf"));
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-Medium.ttf"));
    }
}
