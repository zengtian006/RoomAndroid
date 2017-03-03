package com.tim.room.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tim.room.R;
import com.tim.room.adapter.ItemFeedAdapter;
import com.tim.room.adapter.ItemFeedAnimator;
import com.tim.room.helper.ItemFeedScaleHelper;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ItemFullScreenViewer extends AppCompatActivity implements ItemFeedAdapter.OnFeedItemClickListener {
    private static final String TAG = ItemFullScreenViewer.class.getSimpleName();

    List<Items> mItems;
    int position;
    private RecyclerView mRecyclerView;
    private ItemFeedScaleHelper mCardScaleHelper = null;
    RESTFulService updateItemService;
    ItemFeedAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_full_screen_viewer);

        updateItemService = RESTFulServiceImp.createService(RESTFulService.class);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mItems = (List<Items>) bundle.getSerializable("items");
        position = (int) bundle.getSerializable("position");

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        setRecyclerView();

    }

    private void setRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        cardAdapter = new ItemFeedAdapter(this, mItems);
        cardAdapter.setOnFeedItemClickListener(this);
        mRecyclerView.setAdapter(cardAdapter);
        mRecyclerView.setItemAnimator(new ItemFeedAnimator());
        // mRecyclerView绑定scale效果
        mCardScaleHelper = new ItemFeedScaleHelper();
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
        }
    }

    @Override
    public void onProfileClick(View v) {
        Toast.makeText(ItemFullScreenViewer.this, "Profile click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
