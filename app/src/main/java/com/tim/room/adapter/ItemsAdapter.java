package com.tim.room.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.R;
import com.tim.room.model.Items;
import com.tim.room.utils.CommonUtil;

import java.util.List;

import static com.tim.room.MainActivity.session;
import static com.tim.room.app.AppConfig.IMG_BASE_URL;


/**
 * Created by Zeng on 2017/1/10.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Items> dealEventItemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, brand, id;
        public ImageView thumbnail;
        public RelativeLayout layout;

        public MyViewHolder(View view) {
            super(view);
            layout = (RelativeLayout) view.findViewById(R.id.item_layout);
            title = (TextView) view.findViewById(R.id.item_title);
            brand = (TextView) view.findViewById(R.id.item_brand);
            thumbnail = (ImageView) view.findViewById(R.id.item_thumbnail);
            id = (TextView) view.findViewById(R.id.item_hex_id);

            View.OnClickListener itemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(view.getContext(), EventItemActivity.class);
//                    intent.putExtra("item_hex_id", id.getText());
//                    view.getContext().startActivity(intent);
                }
            };
            view.setOnClickListener(itemClickListener);
            thumbnail.setOnClickListener(itemClickListener);
        }
    }


    public ItemsAdapter(Context mContext, List<Items> dealEventItemList) {
        this.mContext = mContext;
        this.dealEventItemList = dealEventItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Items item = dealEventItemList.get(position);
        holder.title.setText(item.getTitle());
        holder.brand.setText(item.getBrand());
//        holder.id.setText(dealEventItem.getHexId());

        Glide.with(mContext).load(IMG_BASE_URL + session.getUser().getId().toString() + "/" + item.getImageName())
                .thumbnail(0.5f)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.thumbnail);
        int proportionalWidth = CommonUtil.containerWidth((Activity) mContext, 2.2);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(proportionalWidth, FrameLayout.LayoutParams.WRAP_CONTENT); // (width, height)
        holder.layout.setLayoutParams(params);

    }

    @Override
    public int getItemCount() {
        return dealEventItemList.size();
    }
}
