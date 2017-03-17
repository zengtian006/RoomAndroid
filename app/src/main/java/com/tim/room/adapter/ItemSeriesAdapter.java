package com.tim.room.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tim.room.MainActivity;
import com.tim.room.R;
import com.tim.room.activity.AddItemActivity;
import com.tim.room.activity.ItemFullScreenViewer;
import com.tim.room.activity.ItemViewerActivity;
import com.tim.room.model.ItemSeries;
import com.tim.room.model.Items;
import com.tim.room.model.User;
import com.tim.room.utils.LocaleUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeng on 2017/1/10.
 */


public class ItemSeriesAdapter extends RecyclerView.Adapter<ItemSeriesAdapter.ItemRowHolder> {

    private final static String TAG = ItemSeriesAdapter.class.getSimpleName();
    private ArrayList<ItemSeries> dataList;
    private Context mContext;
    private View mHeaderView;
    private User currentUser;

    public static final int TYPE_HEADER = 0;  //说明是带有Header的
    public static final int TYPE_NORMAL = 1;  //说明是不带有header和footer的

    public ItemSeriesAdapter(Context context, ArrayList<ItemSeries> dataList, User currentUser) {

        this.dataList = dataList;
        this.mContext = context;
        this.currentUser = currentUser;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            //第一个item应该加载Header
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new ItemRowHolder(mHeaderView);
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_series_view, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, final int i) {
        if (getItemViewType(i) == TYPE_HEADER) {
            return;
        }
        int position = 0;
        if (mHeaderView != null) {
            position = i - 1;
        } else {
            position = i;
        }
        final List itemsList = dataList.get(position).getItems();
//        String cate_title = dataList.get(position).getTitle();
        Log.v(TAG, "LOCALE: " + LocaleUtil.getLocale(mContext.getApplicationContext()));
        String cate_title = "";
        if (LocaleUtil.getLocale(mContext.getApplicationContext()).equals(LocaleUtil.SIMP_CHINESE)) {
            cate_title = dataList.get(position).getTitle_cn();
        } else if (LocaleUtil.getLocale(mContext.getApplicationContext()).equals(LocaleUtil.ENGLISH)) {
            cate_title = dataList.get(position).getTitle();
        }

        String cate_id = String.valueOf(dataList.get(position).getCate_id());

        itemRowHolder.tv_cate_title.setText(cate_title);
        itemRowHolder.tv_cate_id.setText(cate_id);
        if (itemsList.size() <= 0) {
            itemRowHolder.moreButton.setVisibility(View.GONE);
        }
        ItemsAdapter itemListDataAdapter = new ItemsAdapter(mContext, itemsList);

        itemRowHolder.recycler_view_list.setHasFixedSize(true);
        itemRowHolder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);
        itemRowHolder.recycler_view_list.setNestedScrollingEnabled(false);
        itemRowHolder.recycler_view_list.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(mContext, itemRowHolder.recycler_view_list, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("items", (Serializable) itemsList);
//                bundle.putInt("position", position);
//
//                FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
//                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
//                newFragment.setArguments(bundle);
//                newFragment.show(ft, "slideshow");
                if (itemsList.size() <= 0) {
                    mContext.startActivity(new Intent(mContext, AddItemActivity.class));

                    return;
                }
                Intent intent = new Intent(mContext, ItemFullScreenViewer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("items", (Serializable) itemsList);
                bundle.putSerializable("position", position);
                bundle.putSerializable("current_user", currentUser);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
//                Intent intent = new Intent(mContext, ItemSingleViewActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("items", (Serializable) itemsList.get(position));
//                bundle.putSerializable("current_user", currentUser);
//                intent.putExtras(bundle);
//                mContext.startActivity(intent);
            }
        }));
    }

    @Override
    public int getItemCount() {
        if (mHeaderView != null) {
            return dataList.size() + 1;
        }

        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected Button moreButton;
        protected TextView tv_cate_title, tv_cate_id;

        protected RecyclerView recycler_view_list;

        public ItemRowHolder(View view) {
            super(view);
            if (view == mHeaderView) {
                return;
            }

            this.moreButton = (Button) view.findViewById(R.id.moreButton);
            this.tv_cate_title = (TextView) view.findViewById(R.id.tv_cate_title);
            this.tv_cate_id = (TextView) view.findViewById(R.id.tv_cate_id);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);

            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = 0;
                    if (mHeaderView != null) {
                        position = getAdapterPosition() - 1;
                    } else {
                        position = getAdapterPosition();
                    }
                    Intent intent = new Intent(mContext, ItemViewerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("itemList", dataList.get(position));
                    bundle.putSerializable("current_user", currentUser);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
//                    mContext.startActivity(new Intent(mContext, ItemViewerActivity.class));
                }
            });

        }

    }

}