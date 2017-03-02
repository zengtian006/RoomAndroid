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

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.tim.room.R;
import com.tim.room.adapter.ItemSeriesAdapter;
import com.tim.room.helper.ProgressDialog;
import com.tim.room.model.ItemSeries;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.util.ArrayList;

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
    KenBurnsView ken_header_view;
    int i = 1;

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
                    ken_header_view = (KenBurnsView) header.findViewById(R.id.header_image_view);
                    ItemSeriesAdapter adapter = new ItemSeriesAdapter(getContext(), itemSerieList);
                    adapter.setHeaderView(header);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }
            }
        });
//
//        final Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {//接收消息，并处理
//                super.handleMessage(msg);
//                if (msg.what == 1)
//                    ken_header_view.setImageResource(R.drawable.bg_1);//设置变换后的图片资源
//                else if (msg.what == 2)
//                    ken_header_view.setImageResource(R.drawable.bg_2);//设置变换后的图片资源
//                else if (msg.what == 3)
//                    ken_header_view.setImageResource(R.drawable.bg_3);//设置变换后的图片资源
//                else
//                    ken_header_view.setImageResource(R.drawable.bg_4);//设置变换后的图片资源
//            }
//        };
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(6000);//暂停 6 秒
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Message message = new Message();
//                    message.what = i;
//                    handler.sendMessage(message);//发送消息
//                    //加上缩放动画
//                    AnimationSet set = new AnimationSet(true);
//                    ScaleAnimation scale = new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                    scale.setDuration(1000);
//                    set.addAnimation(scale);
//                    ken_header_view.setAnimation(set);
//                    if (i == 3)//3张图片播放完，重置
//                        i = 0;
//                    i++;
//                }
//            }
//        }).start();
    }

    private void findView(final View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
    }


}
