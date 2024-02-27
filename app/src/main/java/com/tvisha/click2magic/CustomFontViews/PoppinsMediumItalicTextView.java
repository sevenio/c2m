package com.tvisha.click2magic.CustomFontViews;

import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by koti on 24/5/17.
 */

public class PoppinsMediumItalicTextView extends AppCompatTextView {
    public PoppinsMediumItalicTextView(Context context) {
        super(context);
        //this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Poppins-MediumItalicItalic.ttf"));
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-MediumItalicItalic.ttf"));
    }

    public PoppinsMediumItalicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Poppins-MediumItalic.ttf"));
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-MediumItalicItalic.ttf"));
    }

    public PoppinsMediumItalicTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       // this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Poppins-MediumItalic.ttf"));
        this.setTypeface(TypeFaceProvider.getTypeFace(context,"fonts/Poppins-MediumItalicItalic.ttf"));
    }
}
