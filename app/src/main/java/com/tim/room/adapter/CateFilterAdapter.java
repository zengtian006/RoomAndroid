package com.tim.room.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.R;
import com.tim.room.activity.ItemViewerActivity;
import com.tim.room.model.Categories;
import com.tim.room.model.Items;

import java.util.ArrayList;
import java.util.List;

import static com.tim.room.app.AppConfig.IMG_BASE_URL;

/**
 * Created by Zeng on 2017/2/16.
 */

public class CateFilterAdapter extends RecyclerView.Adapter<CateFilterAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Categories> categories;
    List<Items> allItems;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageButton iv_cate_item;
        public RelativeLayout layout;

        public MyViewHolder(View view) {
            super(view);
            iv_cate_item = (ImageButton) view.findViewById(R.id.iv_cate_item);
            iv_cate_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("CATEITEMS", "Filtered Items size Before:" + allItems.size());
                    ItemViewerActivity.items.clear();
                    Log.v("CATEITEMS", "Filtered Items size:" + allItems.size());

                    for (Items item : allItems) {
                        if (item.getCateId().equals(iv_cate_item.getTag())) {
                            Log.v("CATEITEMS", "Filtered Items: " + item.getImageName());
//                            ItemViewerActivity.images.add(IMG_BASE_URL + session.getUser().getId().toString() + "/" + item.getImageName());
                            ItemViewerActivity.items.add(item);
                        }
                    }
                    ItemViewerActivity.mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public CateFilterAdapter(Context mContext, ArrayList<Categories> categories, List<Items> allItems) {
        this.mContext = mContext;
        this.categories = categories;
        this.allItems = allItems;
    }

    @Override
    public CateFilterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_cate_imagebutton, parent, false);

        return new CateFilterAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CateFilterAdapter.MyViewHolder holder, int position) {
        Categories cate = categories.get(position);

        Glide.with(mContext).load(IMG_BASE_URL + "categories/" + cate.getCateName() + ".png")
                .thumbnail(0.1f)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_cate_item);
        holder.iv_cate_item.setTag(cate.getId());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}

