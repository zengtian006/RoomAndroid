package com.tim.room.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.tim.room.R;
import com.tim.room.adapter.GalleryAdapter;
import com.tim.room.model.Items;
import com.tim.room.model.ItemsResponse;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountOverdueSubFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountOverdueSubFragment extends Fragment {
    private static final String TAG = AccountOverdueSubFragment.class.getSimpleName();
    private static final String INTERVAL = "interval";

    private int mParam1;
    RecyclerView recyclerViewImages;
    Spinner spinner;
    GalleryAdapter mAdapter;
    List<Items> itemsList;
    RESTFulService itemService;
    LinearLayout linearLayout_spinner;
    boolean isFirstCreate;

    public AccountOverdueSubFragment() {
        // Required empty public constructor
    }

    public static AccountOverdueSubFragment newInstance(int param1) {
        AccountOverdueSubFragment fragment = new AccountOverdueSubFragment();
        Bundle args = new Bundle();
        args.putInt(INTERVAL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(INTERVAL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_overdue_sub, container, false);
        findView(rootView);
        setView();
        setListener();
        isFirstCreate = true;
        return rootView;
    }

    private void setListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isFirstCreate) {
                    switch (i) {
                        case 0:
                            updateData(1);
                            break;
                        case 1:
                            updateData(3);
                            break;
                        case 2:
                            updateData(6);
                            break;
                        case 3:
                            updateData(12);
                            break;
                    }
                } else {
                    isFirstCreate = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setView() {
        Log.v(TAG, "mparam: " + mParam1);
        if (mParam1 == 0 || mParam1 == 99) {
            linearLayout_spinner.setVisibility(View.GONE);
        } else {
            linearLayout_spinner.setVisibility(View.VISIBLE);
        }
        iniData(mParam1);
    }

    private void findView(View rootView) {
        recyclerViewImages = (RecyclerView) rootView.findViewById(R.id.recycler_view_images);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        linearLayout_spinner = (LinearLayout) rootView.findViewById(R.id.linearLayout_spinner);
    }

    private void iniData(int date) {
        if (date == 99) {
            itemsList = new ArrayList<>();
            itemService = RESTFulServiceImp.createService(RESTFulService.class);
            itemService.findAllLikedItems(session.getUser()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ItemsResponse>() {
                        @Override
                        public void accept(ItemsResponse itemsResponse) throws Exception {
                            if (!itemsResponse.isSuccess()) {
                                return;
                            }
                            itemsList = itemsResponse.getItems();
                            mAdapter = new GalleryAdapter(getContext(), itemsList);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                            recyclerViewImages.setLayoutManager(mLayoutManager);
                            recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
                            recyclerViewImages.setAdapter(mAdapter);
                            recyclerViewImages.setHasFixedSize(true);
                        }
                    });
        } else {
            itemsList = new ArrayList<>();
            itemService = RESTFulServiceImp.createService(RESTFulService.class);
            itemService.findAlmostOverdueItem(date, session.getUser().getId().toString()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Items>>() {
                        @Override
                        public void accept(List<Items> items) throws Exception {
                            itemsList = items;
                            mAdapter = new GalleryAdapter(getContext(), itemsList);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                            recyclerViewImages.setLayoutManager(mLayoutManager);
                            recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
                            recyclerViewImages.setAdapter(mAdapter);
                            recyclerViewImages.setHasFixedSize(true);
                        }
                    });
        }
    }

    private void updateData(int date) {
        itemService.findAlmostOverdueItem(date, session.getUser().getId().toString()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Items>>() {
                    @Override
                    public void accept(List<Items> items) throws Exception {
                        itemsList.clear();
                        itemsList.addAll(items);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }
}
