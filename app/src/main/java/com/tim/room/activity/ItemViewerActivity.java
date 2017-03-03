package com.tim.room.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.FrameLayout;

import com.tim.room.R;
import com.tim.room.adapter.CateFilterAdapter;
import com.tim.room.adapter.GalleryAdapter;
import com.tim.room.helper.ColorPicker;
import com.tim.room.view.ProgressDialog;
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
    ItemSeries itemSeries;

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
        itemSeries = (ItemSeries) bundle.getSerializable("itemList");
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
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("items", (Serializable) items);
//                bundle.putInt("position", position);
//
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
//                newFragment.setArguments(bundle);
//                newFragment.show(ft, "slideshow");

                Intent intent = new Intent(ItemViewerActivity.this, ItemFullScreenViewer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("items", (Serializable) items);
                bundle.putSerializable("position", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            private static final float SELECTED_SCALE = .8f;
            private static final float UNSELECTED_SCALE = 1f;

            @Override
            public void onLongClick(View view, int position) {
                FrameLayout rl = (FrameLayout) view.findViewById(R.id.imageSquareLayout);
                final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
                if (cb.isChecked()) {
                    rl.animate()
                            .scaleY(UNSELECTED_SCALE)
                            .scaleX(UNSELECTED_SCALE)
                            .setDuration(200).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            cb.setChecked(false);
                        }
                    })
                            .start();
                } else {
                    rl.animate()
                            .scaleY(SELECTED_SCALE)
                            .scaleX(SELECTED_SCALE).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            cb.setChecked(true);
                        }
                    })
                            .setDuration(200)
                            .start();
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
                        Categories topCate = new Categories(0, null, null);
                        categories.add(0, topCate);
                        CateFilterAdapter cateFilterAdapter = new CateFilterAdapter(mContext, categories, itemSeries.getItems());
                        recycler_view_cate_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        recycler_view_cate_list.setAdapter(cateFilterAdapter);
                        recycler_view_cate_list.setHasFixedSize(true);
                        recycler_view_cate_list.setItemAnimator(new DefaultItemAnimator());
                        cateFilterAdapter.setOnItemClickListener(new CateFilterAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, Categories data) {
                                items.clear();
                                if (data.getId() == 0) { //Show All
                                    items.addAll(itemSeries.getItems());
                                } else {
                                    for (Items item : itemSeries.getItems()) {
                                        if (item.getCateId().equals(data.getId())) {
                                            Log.v("CATEITEMS", "Filtered Items: " + item.getImageName());
                                            items.add(item);
                                        }
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
            case R.id.action_public:
                items.clear();
                for (Items singleItem : itemSeries.getItems()) {
                    if (singleItem.getGlobal().equals("1")) {
                        Log.v("CATEITEMS", "Filtered Items: " + singleItem.getImageName());
                        items.add(singleItem);
                    }
                }
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_private:
                items.clear();
                for (Items singleItem : itemSeries.getItems()) {
                    if (singleItem.getGlobal().equals("0")) {
                        Log.v("CATEITEMS", "Filtered Items: " + singleItem.getImageName());
                        items.add(singleItem);
                    }
                }
                mAdapter.notifyDataSetChanged();
                return true;
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
