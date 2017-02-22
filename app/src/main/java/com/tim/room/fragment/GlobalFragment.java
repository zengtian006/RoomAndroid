package com.tim.room.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tim.room.R;
import com.tim.room.adapter.GalleryAdapter;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Zeng on 2017/1/3.
 */

public class GlobalFragment extends Fragment {

    Context mContext;
    RecyclerView recyclerViewImages;
    public static GalleryAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_global, container, false);
        findView(rootView);
        setView();
        this.mContext = getContext();
        return rootView;
    }

    private void setView() {
        RESTFulService findGlobalItemService = RESTFulServiceImp.createService(RESTFulService.class);
        findGlobalItemService.findAllGlobalItems().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Items>>() {
            @Override
            public void accept(List<Items> items) throws Exception {
                mAdapter = new GalleryAdapter(mContext, items);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                recyclerViewImages.setLayoutManager(mLayoutManager);
                recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
                recyclerViewImages.setAdapter(mAdapter);
            }
        });
    }

    private void findView(View rootView) {
        recyclerViewImages = (RecyclerView) rootView.findViewById(R.id.recycler_view_images);
    }
}
