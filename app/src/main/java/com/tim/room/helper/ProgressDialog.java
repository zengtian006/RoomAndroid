package com.tim.room.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.tim.room.R;

/**
 * Created by Tim on 18/2/17.
 */

public class ProgressDialog extends Dialog {

    Context mContext;

    private static int default_width = 100;
    private static int default_height = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Log.v("ProgressDialog", "OnCreate");
    }

    public ProgressDialog(Context context) {
        super(context);
        setContentView(R.layout.layout_progress_dialog);
        this.mContext = getContext();
        Log.v("ProgressDialog", "ProgressDialog");
//
        setCanceledOnTouchOutside(false);
        //set window params

        Window window = getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (dm.widthPixels * 0.3);
        params.height = params.width;
        params.gravity = Gravity.CENTER;

        window.setAttributes(params);

//        float density = getDensity(context);
//
//        params.width = (int) (default_width * density);
//        params.height = (int) (default_height * density);
//        params.gravity = Gravity.CENTER;
//
//        window.setAttributes(params);
    }

    public ProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }

}
