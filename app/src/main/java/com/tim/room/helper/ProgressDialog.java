package com.tim.room.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.tim.room.R;
import com.tim.room.helper.GifImageView;

/**
 * Created by Tim on 18/2/17.
 */

public class ProgressDialog extends Dialog {

    Context mContext;

    private static int default_width = 150;
    private static int default_height = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("ProgressDialog", "OnCreate");
    }

    public ProgressDialog(Context context) {
        super(context);
        setContentView(R.layout.layout_progress_dialog);
        this.mContext = getContext();
        Log.v("ProgressDialog", "ProgressDialog");
        setCanceledOnTouchOutside(false);
        //set window params

//        Window window = getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//
//        float density = getDensity(context);
//
//        params.width = (int) (default_width * density);
//        params.height = (int) (default_height * density);
//        params.gravity = Gravity.CENTER;
//
//        window.setAttributes(params);

//        GifImageView gifImageView = (GifImageView) findViewById(R.id.gif_img);
//        gifImageView.setGifImageResource(R.drawable.progress);

//        Glide.with(context).load("https://media.giphy.com/media/l2R0aKwejYr8ycKAg/giphy.gif")
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
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
