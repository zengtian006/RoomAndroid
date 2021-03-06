package com.tim.room.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tim.room.R;
import com.tim.room.activity.HomeOthersActivity;
import com.tim.room.adapter.ItemFeedAdapter;
import com.tim.room.adapter.ItemFeedAnimator;
import com.tim.room.model.ItemLikes;
import com.tim.room.model.Items;
import com.tim.room.model.User;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.view.FeedContextMenu;
import com.tim.room.view.FeedContextMenuManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.dialog;
import static com.tim.room.MainActivity.session;


/**
 * Created by Zeng on 2017/1/3.
 */

public class FollowedFragment extends Fragment implements ItemFeedAdapter.OnFeedItemClickListener, FeedContextMenu.OnFeedContextMenuItemClickListener {
    private final static String TAG = FollowedFragment.class.getSimpleName();

    Context mContext;
    RecyclerView mRecyclerView;
    ItemFeedAdapter cardAdapter;
    List<Items> mItems;
    RESTFulService followedItemService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followed, container, false);
        followedItemService = RESTFulServiceImp.createService(RESTFulService.class);
        mItems = new ArrayList<>();
        this.mContext = getContext();
        findView(view);
        setView();
        return view;
    }

    private void findView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_followed);
    }

    private void setView() {
        dialog.show();
        followedItemService.findAllGlobalItems().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Items>>() {
            @Override
            public void accept(List<Items> items) throws Exception {
                for (Items item : items) {
                    item.setLiked(false);
                    for (ItemLikes itemLike : item.getItemLikes()) {
                        if (itemLike.getUserId().equals(session.getUser().getId())) {
                            item.setLiked(true);
                            break;
                        }
                    }
                }
                mItems.addAll(items);
//                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                cardAdapter = new ItemFeedAdapter(mContext, mItems, 1);
                cardAdapter.setOnFeedItemClickListener(FollowedFragment.this);
                mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
                    }
                });
                mRecyclerView.setAdapter(cardAdapter);
                mRecyclerView.setItemAnimator(new ItemFeedAnimator());
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onCommentsClick(View v, int position) {

    }

    @Override
    public void onMoreClick(View v, int position) {
        Log.v(TAG, "MoreClick");
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, position, this);
    }

    @Override
    public void onPublicClick(View v, int position, boolean b) {

        if (b) {
            mItems.get(position).setGlobal("1");
            followedItemService.updateItem(mItems.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    Log.v(TAG, "result: " + aBoolean);
                    if (aBoolean) {
                        Toast.makeText(mContext, "This item has been set as public", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            mItems.get(position).setGlobal("0");
            followedItemService.updateItem(mItems.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        Toast.makeText(mContext, "This item has been set as private", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onProfileClick(int position) {

        User user = mItems.get(position).getUser();
        if (!session.getUser().getId().equals(user.getId())) {
            Intent intent = new Intent(mContext, HomeOthersActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    @Override
    public void onReportClick(int feedItem) {

    }

    @Override
    public void onSharePhotoClick(int feedItem) {

    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {

    }

    @Override
    public void onCancelClick(int feedItem) {

    }
}
