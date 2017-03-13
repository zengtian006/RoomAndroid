package com.tim.room.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tim.room.R;
import com.tim.room.adapter.CharityAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeng on 2017/1/3.
 */

public class CharityFragment extends Fragment {
    private static final String TAG = CharityFragment.class.getName();

    RecyclerView rv;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charity, container, false);
        this.mContext = getContext();
        findView(view);
        setView();
        return view;
    }

    private void setView() {
        List<String> test = new ArrayList<>();
        test.add("设计师推荐搭配");
        test.add("每日最佳搭配");
        test.add("服装师问答");
        test.add("旧衣回收");

        List<Integer> testimage = new ArrayList<>();
        testimage.add(R.drawable.designer);
        testimage.add(R.drawable.collocation);
        testimage.add(R.drawable.question);
        testimage.add(R.drawable.recycling);

        CharityAdapter charityAdapter = new CharityAdapter(mContext, test, testimage);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(charityAdapter);
    }

    private void findView(View view) {
        rv = (RecyclerView) view.findViewById(R.id.rv_charity);
    }

}
