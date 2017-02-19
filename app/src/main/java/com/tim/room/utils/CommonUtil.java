package com.tim.room.utils;

import android.app.Activity;
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
}
