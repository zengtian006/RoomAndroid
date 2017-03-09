package com.tim.room.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.R;
import com.tim.room.model.Items;
import com.tim.room.view.TagContainerLayout;

import java.util.List;

import static com.tim.room.MainActivity.session;
import static com.tim.room.app.AppConfig.IMG_BASE_URL;

public class ItemSingleViewActivity extends AppCompatActivity {

    Items mItem;
    ImageView ivFeedCenter;
    ImageButton btnComments;
    ImageButton btnLike;
    ImageButton btnMore;
    ImageView ivUserProfile;
    Switch stPublic;
    TextView itemTitle, userName;
    TagContainerLayout tagcontainerLayout;
    TextSwitcher tsLikesCounter;
    FrameLayout vImageRoot;
    View vBgLike;
    ImageView ivLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_single_view);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mItem = (Items) bundle.getSerializable("items");

        findView();
        setView();
    }

    private void setView() {
        Glide.with(this).load(IMG_BASE_URL + session.getUser().getId().toString() + "/" + mItem.getImageName())
                .thumbnail(0.5f)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivFeedCenter);
        if (mItem.getGlobal().equals("1")) {//public item
            stPublic.setChecked(true);
        } else {
            stPublic.setChecked(false);
        }
        itemTitle.setText(mItem.getTitle());
        userName.setText(mItem.getUser().getName());
        btnLike.setImageResource(mItem.isLiked() ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
        tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
                R.plurals.likes_count, mItem.getLikesCount(), mItem.getLikesCount()
        ));
    }

    private void findView() {
        ivFeedCenter = (ImageView) findViewById(R.id.ivFeedCenter);
        btnComments = (ImageButton) findViewById(R.id.btnComments);
        btnLike = (ImageButton) findViewById(R.id.btnLike);
        btnMore = (ImageButton) findViewById(R.id.btnMore);
        ivUserProfile = (ImageView) findViewById(R.id.ivUserProfile);
        stPublic = (Switch) findViewById(R.id.switcher_public);
        itemTitle = (TextView) findViewById(R.id.item_title);
        userName = (TextView) findViewById(R.id.user_name);
        tagcontainerLayout = (TagContainerLayout) findViewById(R.id.tagcontainerLayout);
        tsLikesCounter = (TextSwitcher) findViewById(R.id.tsLikesCounter);
        vImageRoot = (FrameLayout) findViewById(R.id.vImageRoot);
        vBgLike = findViewById(R.id.vBgLike);
        ivLike = (ImageView) findViewById(R.id.ivLike);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
