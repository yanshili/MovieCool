package com.coolcool.moviecool.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by yanshili on 2016/4/4.
 */
public class TintDrawableUtil {

    @NonNull
    public static Drawable tintDrawable(@NonNull Context context
            ,@DrawableRes int resId,@ColorRes int color){
        Drawable drawable= VectorDrawableCompat
                .create(context.getResources(), resId, null);
        Drawable drawableWrapper;
        if (drawable!=null){
            drawable.mutate();
            drawableWrapper = DrawableCompat.wrap(drawable).mutate();
            int rightColor=context.getResources().getColor(color);
            DrawableCompat.setTint(drawableWrapper, rightColor);
            return drawableWrapper;
        }
        return null;
    }

}
