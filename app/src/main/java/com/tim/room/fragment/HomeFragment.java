package com.tim.room.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tim.room.MainActivity;
import com.tim.room.R;
import com.tim.room.adapter.ItemSeriesAdapter;
import com.tim.room.model.ItemLikes;
import com.tim.room.model.ItemSeries;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.view.KenBurnsView;
import com.tim.room.view.LoopViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.dialog;
import static com.tim.room.MainActivity.session;

/**
 * Created by Zeng on 2017/1/3.
 */

public class HomeFragment extends Fragment {
    private final static String TAG = HomeFragment.class.getSimpleName();
    RecyclerView recyclerView;
    Context mContext;
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
        return rootView;
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
        title.setText(session.getUser().getRoomName());
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
    }
}
