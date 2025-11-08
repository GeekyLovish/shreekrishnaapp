package com.raman.kumar.shrikrishan.util;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.raman.kumar.shrikrishan.application.MyApp;

/**
 *
 * Written By Sarangal
 *
 * */

public class ResourceHandler {

    /* Return String Resource */
    public static String getString (int stringID){
        return MyApp.getApplication().getResources().getString(stringID);
    }

    /* Return Drawable Resource */
    public static Drawable getDrawable (int drawableId){
        return ContextCompat.getDrawable(MyApp.getApplication(), drawableId);
    }

    /* Return Color Resource */
    public static int getColor (int colorId){
        return ContextCompat.getColor(MyApp.getApplication(), colorId);
    }

    /* Return Integer Resource */
    public static int getInteger (int integerId){
        return MyApp.getApplication().getResources().getInteger(integerId);
    }

    /* Return Dimen Resource */
    public static float getDimension (int dimensionId){
        return MyApp.getApplication().getResources().getDimensionPixelSize(dimensionId);
    }

}
