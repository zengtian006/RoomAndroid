package com.tim.room.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tim.room.R;
import com.tim.room.model.Items;

import java.util.List;

import static com.tim.room.app.AppConfig.IMG_BASE_URL;

/**
 * Created by Lincoln on 31/03/16.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<Items> items;
    private Context mContext;
    private final static int PHOTO_ANIMATION_DELAY = 600;
    private final static Interpolator INTERPOLATOR = new DecelerateInterpolator();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public RelativeLayout rl;
        public CheckBox cb;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            cb = (CheckBox) view.findViewById(R.id.checkBox);
            rl = (RelativeLayout) view.findViewById(R.id.imageSquareLayout);
            cb.setVisibility(View.GONE);
        }
    }

    public GalleryAdapter(Context context, List<Items> items) {
        mContext = context;
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Items item = items.get(position);
        String image = IMG_BASE_URL + item.getUser().getId().toString() + "/" + item.getImageName();

        Glide.with(mContext).load(image)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                animatePhoto(holder);
                return false;
            }
        })
                .into(holder.thumbnail);
    }

    private void animatePhoto(MyViewHolder viewHolder) {

        long animationDelay = PHOTO_ANIMATION_DELAY + viewHolder.getPosition() * 30;

        viewHolder.rl.setScaleY(0f);
        viewHolder.rl.setScaleX(0f);

        viewHolder.rl.animate()
                .scaleY(1f)
                .scaleX(1f)
                .setDuration(200)
                .setInterpolator(INTERPOLATOR)
                .setStartDelay(animationDelay)
                .start();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GalleryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GalleryAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}