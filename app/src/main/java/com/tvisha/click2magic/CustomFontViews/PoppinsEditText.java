package com.tvisha.click2magic.CustomFontViews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * Created by rajkumar on 29/8/17.
 */

public class PoppinsEditText extends androidx.appcompat.widget.AppCompatEditText {

    public PoppinsEditText(Context context) {
        super(context);
      //  Typeface poppins_txt = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Regular.ttf");
      //  this.setTypeface(poppins_txt);
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-Regular.ttf"));
    }

    public PoppinsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Typeface poppins_txt = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Regular.ttf");
       // this.setTypeface(poppins_txt);
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-Regular.ttf"));
    }

    public PoppinsEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
       // Typeface poppins_txt = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Regular.ttf");
       // this.setTypeface(poppins_txt);
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-Regular.ttf"));
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }



}
