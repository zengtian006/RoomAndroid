package com.tim.room.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tim.room.R;
import com.tim.room.adapter.ItemSeriesAdapter;
import com.tim.room.helper.KenBurnsView;
import com.tim.room.helper.LoopViewPager;
import com.tim.room.helper.ProgressDialog;
import com.tim.room.model.ItemSeries;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;

/**
 * Created by Zeng on 2017/1/3.
 */

public class HomeFragment extends Fragment {
    private final static String TAG = HomeFragment.class.getSimpleName();
    RecyclerView recyclerView;
    Context mContext;
    public static final Integer[] IMAGES_RESOURCE = new Integer[]{
            R.drawable.bg_1,
            R.drawable.bg_2,
            R.drawable.bg_4,
            R.drawable.bg_3
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
        return rootView;
    }

    private void setView() {
        final RESTFulService findAllItemsService = RESTFulServiceImp.createService(RESTFulService.class);

        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.show();
        findAllItemsService.findAllItems(session.getUser()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<ItemSeries>>() {
            @Override
            public void accept(ArrayList<ItemSeries> itemSeries) throws Exception {
                for (ItemSeries series : itemSeries) {
                    Log.v(TAG, "Title: " + series.getTitle());
                    Log.v(TAG, "Item size : " + series.getItems().size());
                    ArrayList<ItemSeries> itemSerieList = new ArrayList<ItemSeries>();
                    itemSerieList = itemSeries;
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);

                    View header = LayoutInflater.from(mContext).inflate(R.layout.home_header, recyclerView, false);
//                    ken_header_view = (KenBurnsView) header.findViewById(R.id.header_image_view);
                    initializeKenBurnsView(header);
                    ItemSeriesAdapter adapter = new ItemSeriesAdapter(getContext(), itemSerieList);
                    adapter.setHeaderView(header);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }
            }
        });
//

    }

    private void initializeKenBurnsView(final View view) {
        // KenBurnsView
        final KenBurnsView kenBurnsView = (KenBurnsView) view.findViewById(R.id.ken_burns_view);
        kenBurnsView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        kenBurnsView.setSwapMs(5750);
        kenBurnsView.setFadeInOutMs(750);

        // ResourceIDs
        List<Integer> resourceIDs = Arrays.asList(IMAGES_RESOURCE);
        kenBurnsView.loadResourceIDs(resourceIDs);

        // LoopViewListener
        LoopViewPager.LoopViewPagerListener listener = new LoopViewPager.LoopViewPagerListener() {
            @Override
            public View OnInstantiateItem(int page) {
                TextView counterText = new TextView(view.getContext());
                counterText.setText(String.valueOf(page));
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
    }


}
