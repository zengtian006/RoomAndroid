package com.tim.room.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.R;
import com.tim.room.model.Categories;

import java.util.List;

import static com.tim.room.app.AppConfig.IMG_BASE_URL;

/**
 * Created by Zeng on 2017/2/14.
 */

public class CateAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    List<Categories> cates;
    Context mContext;

    public CateAdapter(Context mContext, List<Categories> cates) {
        this.mContext = mContext;
        this.myInflater = LayoutInflater.from(mContext);
        this.cates = cates;
    }

    @Override
    public int getCount() {
        return cates.size();
    }

    @Override
    public Object getItem(int i) {
        return cates.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        TextView id;
        TextView name;
        ImageView iv;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder mViewHolder = null;
        if (view == null) {
            view = myInflater.inflate(R.layout.list_item_cates, null);
            mViewHolder = new ViewHolder();
            mViewHolder.id = (TextView) view.findViewById(R.id.cate_id);
            mViewHolder.name = (TextView) view.findViewById(R.id.cate_name);
            mViewHolder.iv = (ImageView) view.findViewById(R.id.cate_iv);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        Categories cate = cates.get(i);
        mViewHolder.id.setText(String.valueOf(cates.get(i).getId()));
        mViewHolder.name.setText(cates.get(i).getCateName());
//        TextView id = (TextView) view.findViewById(R.id.cate_id);
//        TextView name = (TextView) view.findViewById(R.id.cate_name);
//        ImageView iv = (ImageView) view.findViewById(R.id.cate_iv);
//        id.setText(String.valueOf(cates.get(i).getId()));
//        name.setText(cates.get(i).getCateName());
        Glide.with(mContext).load(IMG_BASE_URL + "categories/" + cates.get(i).getCateName() + ".png")
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mViewHolder.iv);
        return view;
    }
}
