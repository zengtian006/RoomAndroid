package com.tim.room.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tim.room.R;

import java.util.List;

/**
 * Created by Zeng on 2017/2/27.
 */

public class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.myViewHolder> {
    Context mContext;
    List<String> data;
    List<Integer> images;

    public CharityAdapter(Context mContext, List<String> data, List<Integer> images) {
        this.mContext = mContext;
        this.data = data;
        this.images = images;

    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView iv;

        public myViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_charity_1);
            iv = (ImageView) itemView.findViewById(R.id.iv_bg);
        }
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.charity_card, parent, false);
        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        holder.tv.setText(data.get(position));
        holder.iv.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
