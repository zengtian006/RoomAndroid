package com.tim.room.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Zeng on 13/1/2017.
 */

public class CommonUtil {

    public static int containerWidth(Activity ba, double divide) {
        DisplayMetrics dm = new DisplayMetrics();
        ba.getWindowManager().getDefaultDisplay().getMetrics(dm);

        //get predefined value
        double ratio = divide;

        return (int) (dm.widthPixels / ratio);
    }

    public static int containerHeight(Activity ba, double divide) {
        DisplayMetrics dm = new DisplayMetrics();
        ba.getWindowManager().getDefaultDisplay().getMetrics(dm);

        //get predefined value
        double ratio = divide;

        return (int) (dm.heightPixels / ratio);
    }

    public static float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
