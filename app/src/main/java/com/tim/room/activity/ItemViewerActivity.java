package com.tim.room.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.tim.room.R;
import com.tim.room.adapter.CateFilterAdapter;
import com.tim.room.adapter.GalleryAdapter;
import com.tim.room.fragment.SlideshowDialogFragment;
import com.tim.room.helper.ColorPicker;
import com.tim.room.helper.ProgressDialog;
import com.tim.room.model.Categories;
import com.tim.room.model.ItemSeries;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ItemViewerActivity extends AppCompatActivity {
    private static final String TAG = ItemViewerActivity.class.getSimpleName();

    //    public static ArrayList<String> images;
    public static List<Items> items;
    public static GalleryAdapter mAdapter;
    Context mContext;

    RecyclerView recycler_view_cate_list, recyclerViewImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);
//        overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
        this.mContext = getApplicationContext();
        final ProgressDialog dialog = new ProgressDialog(ItemViewerActivity.this);
        dialog.show();
        recycler_view_cate_list = (RecyclerView) findViewById(R.id.recycler_view_cates);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final ItemSeries itemSeries = (ItemSeries) bundle.getSerializable("itemList");
        items = new ArrayList<Items>();
        for (Items item : itemSeries.getItems()) {
            items.add(item);
        }
        mAdapter = new GalleryAdapter(getApplicationContext(), items);
        recyclerViewImages = (RecyclerView) findViewById(R.id.recycler_view_images);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerViewImages.setLayoutManager(mLayoutManager);
        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
        recyclerViewImages.setAdapter(mAdapter);
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerViewImages, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("items", (Serializable) items);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
                if (cb.isChecked()) {
                    cb.setChecked(false);
                } else {
                    cb.setChecked(true);
                }
            }
        }));

        //Find Sub Category
        RESTFulService itemCatesService = RESTFulServiceImp.createService(RESTFulService.class);
        itemCatesService.findSubCategoriesById(itemSeries.getCate_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Categories>>() {
                    @Override
                    public void accept(ArrayList<Categories> categories) throws Exception {
                        CateFilterAdapter cateFilterAdapter = new CateFilterAdapter(mContext, categories, itemSeries.getItems());
                        recycler_view_cate_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        recycler_view_cate_list.setAdapter(cateFilterAdapter);
                        recycler_view_cate_list.setHasFixedSize(true);
                        recycler_view_cate_list.setItemAnimator(new DefaultItemAnimator());
                        cateFilterAdapter.setOnItemClickListener(new CateFilterAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, Categories data) {
                                items.clear();

                                for (Items item : itemSeries.getItems()) {
                                    if (item.getCateId().equals(data.getId())) {
                                        Log.v("CATEITEMS", "Filtered Items: " + item.getImageName());
                                        items.add(item);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                ColorPicker colorPicker = new ColorPicker(this);
                colorPicker.setRoundColorButton(true).show();
//                startActivity(new Intent(ItemViewerActivity.this, ItemFilterActivity.class));
                return true;
            case R.id.action_check_updates:
//                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                recyclerViewImages.setLayoutManager(mLayoutManager);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
    }
}
