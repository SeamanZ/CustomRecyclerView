package com.example.customrecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.NonNull;

/**
 * Created by Seaman on 2019-07-12.
 * Banggood Ltd
 */
public class Utils {

    private Utils(){}

    public static int getScreenHeight(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static int pxToDp(float px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, Resources.getSystem().getDisplayMetrics());
    }
}
