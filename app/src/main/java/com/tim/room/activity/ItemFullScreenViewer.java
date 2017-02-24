package com.tim.room.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tim.room.R;
import com.tim.room.adapter.CardAdapter;
import com.tim.room.helper.CardScaleHelper;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ItemFullScreenViewer extends AppCompatActivity implements CardAdapter.OnFeedItemClickListener {
    private static final String TAG = ItemFullScreenViewer.class.getSimpleName();

    List<Items> mItems;
    int position;
    private RecyclerView mRecyclerView;
    private CardScaleHelper mCardScaleHelper = null;
    RESTFulService updateItemService;
    CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_full_screen_viewer);

        updateItemService = RESTFulServiceImp.createService(RESTFulService.class);


        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mItems = (List<Items>) bundle.getSerializable("items");
        position = (int) bundle.getSerializable("position");
        setRecyclerView();

    }

    private void setRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        cardAdapter = new CardAdapter(this, mItems);
        cardAdapter.setOnFeedItemClickListener(this);
        mRecyclerView.setAdapter(cardAdapter);
        // mRecyclerView绑定scale效果
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.setCurrentItemPos(position);
        mCardScaleHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onCommentsClick(View v, int position) {
        Toast.makeText(ItemFullScreenViewer.this, "Comment click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreClick(View v, int position) {
        Toast.makeText(ItemFullScreenViewer.this, "More click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPublicClick(View v, int position, boolean b) {
        if (b) {
            mItems.get(position).setGlobal("1");
            updateItemService.updateItem(mItems.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    Log.v(TAG, "result: " + aBoolean);
                    if (aBoolean) {
                        Toast.makeText(ItemFullScreenViewer.this, "This item has been set as public", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            mItems.get(position).setGlobal("0");
            updateItemService.updateItem(mItems.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        Toast.makeText(ItemFullScreenViewer.this, "This item has been set as private", Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            cardAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onProfileClick(View v) {
        Toast.makeText(ItemFullScreenViewer.this, "Profile click", Toast.LENGTH_SHORT).show();
    }
}
