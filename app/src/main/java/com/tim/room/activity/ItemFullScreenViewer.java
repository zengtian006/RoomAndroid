package com.tim.room.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tim.room.R;
import com.tim.room.adapter.CardAdapter;
import com.tim.room.helper.CardScaleHelper;
import com.tim.room.model.Items;

import java.util.List;

public class ItemFullScreenViewer extends AppCompatActivity {
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
        mRecyclerView.setAdapter(new CardAdapter(this, mItems));
        // mRecyclerView绑定scale效果
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.setCurrentItemPos(position);
        mCardScaleHelper.attachToRecyclerView(mRecyclerView);
    }
}
