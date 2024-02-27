package com.tvisha.click2magic.CustomFontViews;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

public class TypeFaceProvider {


    public static final String TYPEFACE_FOLDER = "font";
    public static final String TYPEFACE_EXTENSION = ".ttf";

    private static Hashtable<String, Typeface> sTypeFaces = new Hashtable<String, Typeface>(
            4);

    public static Typeface getTypeFace(Context context, String fileName) {

        Typeface tempTypeface = sTypeFaces.get(fileName);

        if (tempTypeface == null) {
            /*String fontPath = new StringBuilder().append(fileName).toString();*/
            tempTypeface = Typeface.createFromAsset(context.getAssets(), fileName);
            sTypeFaces.put(fileName, tempTypeface);
        }

        return tempTypeface;
    }

}
