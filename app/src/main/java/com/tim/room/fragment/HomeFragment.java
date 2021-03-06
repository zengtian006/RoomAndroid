package com.tim.room.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.MainActivity;
import com.tim.room.R;
import com.tim.room.activity.AddItemActivity;
import com.tim.room.adapter.ItemSeriesAdapter;
import com.tim.room.model.ItemLikes;
import com.tim.room.model.ItemSeries;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.utils.CircleTransformation;
import com.tim.room.view.KenBurnsView;
import com.tim.room.view.LoopViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.bottomBar;
import static com.tim.room.MainActivity.dialog;
import static com.tim.room.MainActivity.session;

/**
 * Created by Zeng on 2017/1/3.
 */

public class HomeFragment extends Fragment {
    private final static String TAG = HomeFragment.class.getSimpleName();

    private final static int INTENT_REQUEST_ADD_ITEM = 1;

    RecyclerView recyclerView;
    Context mContext;
    FloatingActionButton fab_add;
    public static final Integer[] IMAGES_RESOURCE_MAN = new Integer[]{
            R.drawable.bg_man_1,
            R.drawable.bg_man_2,
            R.drawable.bg_man_4,
            R.drawable.bg_man_3
    };
    public static final Integer[] IMAGES_RESOURCE_WOMEN = new Integer[]{
            R.drawable.bg_women_1,
            R.drawable.bg_women_2,
            R.drawable.bg_women_3,
            R.drawable.bg_women_4
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        this.mContext = getContext();
        findView(rootView);
        setView();
        setListener();
        return rootView;
    }

    private void setListener() {
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mContext, AddItemActivity.class), INTENT_REQUEST_ADD_ITEM);
            }
        });
    }

    private void setView() {


        final RESTFulService findAllItemsService = RESTFulServiceImp.createService(RESTFulService.class);

        dialog.show();
        findAllItemsService.findAllItems(session.getUser()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<ItemSeries>>() {
            @Override
            public void accept(ArrayList<ItemSeries> itemSeries) throws Exception {
                for (ItemSeries series : itemSeries) {
                    for (Items item : series.getItems()) {
                        item.setLiked(false);
                        for (ItemLikes itemLike : item.getItemLikes()) {
                            if (itemLike.getUserId().equals(MainActivity.session.getUser().getId())) {
                                item.setLiked(true);
                                break;
                            }
                        }
                    }
                }
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(mLayoutManager);

                View header = LayoutInflater.from(mContext).inflate(R.layout.home_header, recyclerView, false);
                initializeKenBurnsView(header);
                ItemSeriesAdapter adapter = new ItemSeriesAdapter(getContext(), itemSeries, session.getUser());
                adapter.setHeaderView(header);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
//                }
            }
        });
    }

    private void initializeKenBurnsView(final View view) {
        //Title
        TextView title = (TextView) view.findViewById(R.id.header_title);
        TextView sub_title = (TextView) view.findViewById(R.id.sub_title);
        ImageView iv_user = (ImageView) view.findViewById(R.id.iv_user);
        Glide.with(mContext).load(R.drawable.bg_man_1)
                .thumbnail(0.5f)
                .fitCenter()
                .crossFade()
                .bitmapTransform(new CircleTransformation(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_user);

        LinearLayout layout_marked_items = (LinearLayout) view.findViewById(R.id.layoutMarkedItems);
        layout_marked_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomBar.selectTabWithId(R.id.my_account);
            }
        });

        LinearLayout layout_my_items = (LinearLayout) view.findViewById(R.id.layoutMyItems);
        layout_my_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Header height:" + view.getHeight());
                recyclerView.scrollToPosition(2);
            }
        });


        title.setText(session.getUser().getRoomName());
        sub_title.setText(session.getUser().getName());
        // KenBurnsView
        final KenBurnsView kenBurnsView = (KenBurnsView) view.findViewById(R.id.ken_burns_view);
        kenBurnsView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        kenBurnsView.setSwapMs(5750);
        kenBurnsView.setFadeInOutMs(750);

        // ResourceIDs
        List<Integer> resourceIDs = new ArrayList<>();
        if (session.getUser().getGender().equals("M")) {
            resourceIDs = Arrays.asList(IMAGES_RESOURCE_MAN);
        } else {
            resourceIDs = Arrays.asList(IMAGES_RESOURCE_WOMEN);
        }
        kenBurnsView.loadResourceIDs(resourceIDs);
        // LoopViewListener
        LoopViewPager.LoopViewPagerListener listener = new LoopViewPager.LoopViewPagerListener() {
            @Override
            public View OnInstantiateItem(int page) {
                TextView counterText = new TextView(view.getContext());
//                counterText.setText(String.valueOf(page));
                return counterText;
            }

            @Override
            public void onPageScroll(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                kenBurnsView.forceSelected(position);
            }

            @Override
            public void onPageScrollChanged(int page) {
            }
        };
        // LoopView
        LoopViewPager loopViewPager = new LoopViewPager(view.getContext(), resourceIDs.size(), listener);

        FrameLayout viewPagerFrame = (FrameLayout) view.findViewById(R.id.view_pager_frame);
        viewPagerFrame.addView(loopViewPager);

        kenBurnsView.setPager(loopViewPager);
    }

    private void findView(final View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        fab_add = (FloatingActionButton) rootView.findViewById(R.id.fabAdd);
    }
}
