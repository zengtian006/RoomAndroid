package com.tim.room.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.tim.room.R;
import com.tim.room.adapter.CardAdapter;
import com.tim.room.helper.CardScaleHelper;
import com.tim.room.model.Items;

import java.util.List;

public class ItemFullScreenViewer extends AppCompatActivity implements CardAdapter.OnFeedItemClickListener {
    public static List<Items> mItems;
    private RecyclerView mRecyclerView;
    private CardScaleHelper mCardScaleHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_full_screen_viewer);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mItems = (List<Items>) bundle.getSerializable("items");
        int position = (int) bundle.getSerializable("position");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        CardAdapter cardAdapter = new CardAdapter(this, mItems);
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
    public void onProfileClick(View v) {
        Toast.makeText(ItemFullScreenViewer.this, "Profile click", Toast.LENGTH_SHORT).show();
    }
}
