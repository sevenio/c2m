package com.tvisha.click2magic.CustomFontViews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * Created by rajkumar on 8/7/17.
 */
public class PoppinsTextView extends androidx.appcompat.widget.AppCompatTextView {

    public PoppinsTextView(Context context) {
        super(context);
       // Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Regular.ttf");
       // this.setTypeface(face);
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-Regular.ttf"));
    }

    public PoppinsTextView(Context context, AttributeSet attrs) {

        super(context, attrs);
        //Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Regular.ttf");
        //this.setTypeface(face);
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-Regular.ttf"));
    }

    public PoppinsTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Regular.ttf");
       // this.setTypeface(face);
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-Regular.ttf"));
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

}