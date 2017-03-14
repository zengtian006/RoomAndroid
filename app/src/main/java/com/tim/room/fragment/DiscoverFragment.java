package com.tim.room.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tim.room.R;
import com.tim.room.activity.ItemFullScreenViewer;
import com.tim.room.activity.ItemSingleViewActivity;
import com.tim.room.activity.ItemViewerActivity;
import com.tim.room.adapter.GalleryAdapter;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.view.MaterialSearchView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.tim.room.MainActivity.dialog;
import static com.tim.room.MainActivity.session;


/**
 * Created by Zeng on 2017/1/3.
 */

public class DiscoverFragment extends Fragment {

    List<Items> itemList;
    List<Items> varItemList;
    Context mContext;
    RecyclerView recyclerViewImages;
    public static GalleryAdapter mAdapter;
    private MaterialSearchView searchView;
    TabLayout tabLayout;
    RelativeLayout layoutKeyword;
    TextView tvKeyword;
    ImageView ivHideKeywordLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        itemList = new ArrayList<>();
        varItemList = new ArrayList<>();
        findView(rootView);
        setView();
        setListener();
        this.mContext = getContext();
        return rootView;
    }

    private void setListener() {
        ivHideKeywordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutKeyword.setVisibility(View.GONE);
                tvKeyword.setText("");
                if (tabLayout.getTabAt(0).isSelected()) {
                    varItemList.clear();
                    varItemList.addAll(itemList);
                    mAdapter.notifyDataSetChanged();
                } else {
                    tabLayout.getTabAt(0).select();
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                List<Items> tempItemList = new ArrayList<Items>();
                if (tvKeyword.getText().toString().trim().isEmpty()) {
                    tempItemList.addAll(itemList);
                } else {
                    for (Items item : itemList) {
                        if (item.getTitle().contains(tvKeyword.getText().toString()) || item.getTags().contains(tvKeyword.getText().toString())) {
                            tempItemList.add(item);
                        }
                    }
                }

                switch (tab.getText().toString()) {
                    case "Hot":
                        varItemList.clear();
                        varItemList.addAll(tempItemList);
                        break;
                    case "Men":
                        varItemList.clear();
                        for (Items item : tempItemList) {
                            if (item.getUser().getGender().equals("M")) {
                                varItemList.add(item);
                            }
                        }
                        break;
                    case "Women":
                        varItemList.clear();
                        for (Items item : tempItemList) {
                            if (item.getUser().getGender().equals("F")) {
                                varItemList.add(item);
                            }
                        }
                        break;
                    case "Tag":
                        varItemList.clear();
                        for (Items item : tempItemList) {
                            if (item.getTags().contains(tvKeyword.getText().toString())) {
                                varItemList.add(item);
                            }
                        }
                        break;
                    default:
                        break;
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setView() {
        tvKeyword.setText("");
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                layoutKeyword.setVisibility(View.VISIBLE);
                tvKeyword.setText(query);
                varItemList.clear();
                for (Items item : itemList) {
                    if (item.getTitle().contains(query) || item.getTags().contains(query)) {
                        varItemList.add(item);
                    }
                }
                mAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        dialog.show();
        RESTFulService findGlobalItemService = RESTFulServiceImp.createService(RESTFulService.class);
        findGlobalItemService.findAllGlobalItems().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Items>>() {
            @Override
            public void accept(List<Items> items) throws Exception {
                for (Items item : items) {
                    itemList.add(item);
                    varItemList.add(item);
                }
                mAdapter = new GalleryAdapter(mContext, varItemList);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                recyclerViewImages.setLayoutManager(staggeredGridLayoutManager);
                recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
                recyclerViewImages.setAdapter(mAdapter);

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                recyclerViewImages.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(mContext, recyclerViewImages, new GalleryAdapter.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(mContext, ItemFullScreenViewer.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("items", (Serializable) varItemList);
                        bundle.putSerializable("position", position);
                        bundle.putSerializable("current_user", session.getUser());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        Intent intent = new Intent(mContext, ItemSingleViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("items", varItemList.get(position));
                        bundle.putSerializable("current_user", session.getUser());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }));
            }
        });
    }

    private void findView(final View rootView) {
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.label_discover);

        recyclerViewImages = (RecyclerView) rootView.findViewById(R.id.recycler_view_images);
        searchView = (MaterialSearchView) rootView.findViewById(R.id.search_view);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        layoutKeyword = (RelativeLayout) rootView.findViewById(R.id.layout_keyword);
        tvKeyword = (TextView) rootView.findViewById(R.id.tv_keyword);
        ivHideKeywordLayout = (ImageView) rootView.findViewById(R.id.iv_hide_keyword_layout);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_global, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
