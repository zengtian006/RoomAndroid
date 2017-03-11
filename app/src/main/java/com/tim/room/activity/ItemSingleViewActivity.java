package com.tim.room.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.R;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.view.TagContainerLayout;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;
import static com.tim.room.app.AppConfig.IMG_BASE_URL;

public class ItemSingleViewActivity extends AppCompatActivity {
    private static final String TAG = ItemSingleViewActivity.class.getSimpleName();

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
    RESTFulService updateItemService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_single_view);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mItem = (Items) bundle.getSerializable("items");

        updateItemService = RESTFulServiceImp.createService(RESTFulService.class);

        findView();
        setView();
        setListener();
    }

    private void setListener() {
        stPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isChecked = stPublic.isChecked();
                String message = "";
                String resultMsg = "";
                if (isChecked) {
                    message = "Do you want to set this item as public?";
                    resultMsg = "This item has been set as public";
                    mItem.setGlobal("1");
                } else {
                    message = "Do you want to set this item as private?";
                    resultMsg = "This item has been set as private";
                    mItem.setGlobal("0");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ItemSingleViewActivity.this);
                builder.setTitle("Room");
                builder.setMessage(message);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stPublic.setChecked(!isChecked);
                    }
                });
                final String finalResultMsg = resultMsg;
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateItemService.updateItem(mItem).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                Log.v(TAG, "result: " + aBoolean);
                                if (aBoolean) {
                                    Toast.makeText(ItemSingleViewActivity.this, finalResultMsg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.show();
            }
        });
    }

    private void setView() {
        Glide.with(this).load(IMG_BASE_URL + mItem.getUser().getId().toString() + "/" + mItem.getImageName())
                .thumbnail(0.5f)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivFeedCenter);
        if (mItem.getUser().getId().equals(session.getUser().getId())) {//only set public to your own item
            stPublic.setVisibility(View.VISIBLE);
            if (mItem.getGlobal().equals("1")) {//public item
                stPublic.setChecked(true);
            } else {
                stPublic.setChecked(false);
            }
        } else {
            stPublic.setVisibility(View.GONE);
        }
        itemTitle.setText(mItem.getTitle());
        userName.setText(mItem.getUser().getName());
        if (!mItem.getTags().isEmpty()) {
            tagcontainerLayout.setTags(mItem.getTags());
        }
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
