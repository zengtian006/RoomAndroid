package com.tim.room.view;

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

public class LoadingProgressDialog extends Dialog {

    Context mContext;

    private static int default_width = 100;
    private static int default_height = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public LoadingProgressDialog(Context context) {
        super(context);
        this.mContext = getContext();
        Log.v("LoadingProgressDialog", "LoadingProgressDialog");
        setContentView(R.layout.layout_progress_dialog);

        setCanceledOnTouchOutside(false);
        //set window params
        Window window = getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (dm.widthPixels * 0.3);
//        params.height = (int) (dm.widthPixels * 0.3);
        params.gravity = Gravity.CENTER;

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setAttributes(params);

//        float density = getDensity(context);
//
//        params.width = (int) (default_width * density);
//        params.height = (int) (default_height * density);
//        params.gravity = Gravity.CENTER;
//
//        window.setAttributes(params);
    }

    public LoadingProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoadingProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }

}
