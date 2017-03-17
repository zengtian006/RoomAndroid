package com.tim.room.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.R;
import com.tim.room.model.Items;
import com.tim.room.model.User;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.view.TagContainerLayout;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;
import static com.tim.room.app.AppConfig.IMG_BASE_URL;

public class ItemSingleViewActivity extends AppCompatActivity {
    private static final String TAG = ItemSingleViewActivity.class.getSimpleName();
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    Items mItem;
    ImageView ivFeedCenter;
    ImageButton btnComments;
    ImageButton btnLike;
    ImageButton btnMore;
    LinearLayout layout_profile;
    Switch stPublic;
    TextView itemTitle, userName, profile_name;
    TagContainerLayout tagcontainerLayout;
    TextSwitcher tsLikesCounter;
    FrameLayout vImageRoot;
    View vBgLike;
    ImageView ivLike;
    RESTFulService updateItemService;
    User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_single_view);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mItem = (Items) bundle.getSerializable("items");
        currentUser = (User) bundle.getSerializable("current_user");

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        updateItemService = RESTFulServiceImp.createService(RESTFulService.class);

        findView();
        setView();
        setListener();
    }

    private void setListener() {
        layout_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = mItem.getUser();
                Log.v(TAG, "CURRENTUSER: " + currentUser.getName());
                Log.v(TAG, "NOWTUSER: " + user.getName());
                if (!currentUser.getId().equals(user.getId())) {
                    Intent intent = new Intent(ItemSingleViewActivity.this, HomeOthersActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItem.isLiked()) { //cancel Like
                    updateLikesCounter(mItem.getLikesCount() - 1);
                    btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
                    mItem.setLiked(false);
                    //handel database

                } else {// add like
                    animateBtnLike();
                    //handel database
                }
            }
        });

        ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mItem.isLiked()) { //cancel Like
                    animatePhotoLike();
                    //handel database
                }
            }
        });

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

    private void animatePhotoLike() {
        vBgLike.setVisibility(View.VISIBLE);
        ivLike.setVisibility(View.VISIBLE);

        vBgLike.setScaleY(0.1f);
        vBgLike.setScaleX(0.1f);
        vBgLike.setAlpha(1f);
        ivLike.setScaleY(0.1f);
        ivLike.setScaleX(0.1f);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(vBgLike, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(vBgLike, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(vBgLike, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 0.1f, 1f);
        imgScaleUpYAnim.setDuration(300);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 0.1f, 1f);
        imgScaleUpXAnim.setDuration(300);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 1f, 0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 1f, 0f);
        imgScaleDownXAnim.setDuration(300);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                updateLikesCounter(mItem.getLikesCount() + 1);
                btnLike.setImageResource(R.drawable.ic_heart_red);
                mItem.setLiked(true);
            }
        });
        animatorSet.start();
    }

    private void animateBtnLike() {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(btnLike, "rotation", 0f, 360f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(btnLike, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(btnLike, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                updateLikesCounter(mItem.getLikesCount() + 1);
                btnLike.setImageResource(R.drawable.ic_heart_red);
                mItem.setLiked(true);
            }
        });

        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
        animatorSet.start();
    }

    private void updateLikesCounter(Integer likesCount) {
        String likesCountTextTo = tsLikesCounter.getResources().getQuantityString(
                R.plurals.likes_count, likesCount, likesCount
        );
        tsLikesCounter.setText(likesCountTextTo);
        mItem.setLikesCount(Integer.valueOf(likesCount));
    }

    private void setView() {
        profile_name.setText(mItem.getUser().getName());
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
        layout_profile = (LinearLayout) findViewById(R.id.layout_profile);
        stPublic = (Switch) findViewById(R.id.switcher_public);
        itemTitle = (TextView) findViewById(R.id.item_title);
        userName = (TextView) findViewById(R.id.user_name);
        tagcontainerLayout = (TagContainerLayout) findViewById(R.id.tagcontainerLayout);
        tsLikesCounter = (TextSwitcher) findViewById(R.id.tsLikesCounter);
        vImageRoot = (FrameLayout) findViewById(R.id.vImageRoot);
        vBgLike = findViewById(R.id.vBgLike);
        ivLike = (ImageView) findViewById(R.id.ivLike);
        profile_name = (TextView) findViewById(R.id.profile_name);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
