package com.tim.room.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.tim.room.R;
import com.tim.room.adapter.ConstellationAdapter;
import com.tim.room.adapter.GalleryAdapter;
import com.tim.room.adapter.GirdDropDownAdapter;
import com.tim.room.adapter.ListDropDownAdapter;
import com.tim.room.helper.ColorPicker;
import com.tim.room.model.Categories;
import com.tim.room.model.ItemSeries;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.view.DropDownMenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

    RecyclerView recyclerViewImages;

    DropDownMenu mDropDownMenu;
    private String headers[] = {"Category", "年龄", "性别", "星座"};
    private List<View> popupViews = new ArrayList<>();

    private GirdDropDownAdapter categoryAdapter;
    private ListDropDownAdapter ageAdapter;
    private ListDropDownAdapter sexAdapter;
    private ConstellationAdapter constellationAdapter;

    List<String> categoryNameList;
    ArrayList<Categories> categoriesArrayList;
    //    private String categories[] = {"不限", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
    private String ages[] = {"不限", "18岁以下", "18-22岁", "23-26岁", "27-35岁", "35岁以上"};
    private String sexs[] = {"不限", "男", "女"};
    private String constellations[] = {"不限", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"};

    private int constellationPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);
//        overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
        this.mContext = getApplicationContext();

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        mDropDownMenu = (DropDownMenu) findViewById(R.id.dropDownMenu);
        initView();


    }

    private void initView() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        itemSeries = (ItemSeries) bundle.getSerializable("itemList");
        items = new ArrayList<Items>();
        for (Items item : itemSeries.getItems()) {
            items.add(item);
        }
        //init category menu
        final ListView categoryView = new ListView(this);
        RESTFulService itemCatesService = RESTFulServiceImp.createService(RESTFulService.class);
        itemCatesService.findSubCategoriesById(itemSeries.getCate_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Categories>>() {
                    @Override
                    public void accept(ArrayList<Categories> categories) throws Exception {
                        categoriesArrayList = categories;
                        categoryNameList = new ArrayList<String>();
                        for (Categories cate : categories) {
                            categoryNameList.add(cate.getCateName());
                        }
                        categoryNameList.add(0, "All");
                        categoryAdapter = new GirdDropDownAdapter(ItemViewerActivity.this, categoryNameList);
                        categoryView.setDividerHeight(0);
                        categoryView.setAdapter(categoryAdapter);
                    }
                });


        //init age menu
        final ListView ageView = new ListView(this);
        ageView.setDividerHeight(0);
        ageAdapter = new ListDropDownAdapter(this, Arrays.asList(ages));
        ageView.setAdapter(ageAdapter);

        //init sex menu
        final ListView sexView = new ListView(this);
        sexView.setDividerHeight(0);
        sexAdapter = new ListDropDownAdapter(this, Arrays.asList(sexs));
        sexView.setAdapter(sexAdapter);

        //init constellation
        final View constellationView = getLayoutInflater().inflate(R.layout.custom_layout, null);
        GridView constellation = (GridView) constellationView.findViewById(R.id.constellation);
        constellationAdapter = new ConstellationAdapter(this, Arrays.asList(constellations));
        constellation.setAdapter(constellationAdapter);
        TextView ok = (TextView) constellationView.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDropDownMenu.setTabText(constellationPosition == 0 ? headers[3] : constellations[constellationPosition]);
                mDropDownMenu.closeMenu();
            }
        });

        //init popupViews
        popupViews.add(categoryView);
        popupViews.add(ageView);
        popupViews.add(sexView);
        popupViews.add(constellationView);

        //add item click event
        categoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : categoryNameList.get(position));
                mDropDownMenu.closeMenu();

                items.clear();
                if (position == 0) { //Show All
                    items.addAll(itemSeries.getItems());
                } else {
                    for (Items item : itemSeries.getItems()) {
                        if (item.getCateId().equals(categoriesArrayList.get(position - 1).getId())) {
                            Log.v("CATEITEMS", "Filtered Items: " + item.getImageName());
                            items.add(item);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        ageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ageAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : ages[position]);
                mDropDownMenu.closeMenu();
            }
        });

        sexView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sexAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[2] : sexs[position]);
                mDropDownMenu.closeMenu();
            }
        });

        constellation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                constellationAdapter.setCheckItem(position);
                constellationPosition = position;
            }
        });

        //init context view

        mAdapter = new GalleryAdapter(getApplicationContext(), items);
        RecyclerView recyclerViewImages = new RecyclerView(this);
        recyclerViewImages.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerViewImages.setLayoutManager(mLayoutManager);
        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
        recyclerViewImages.setAdapter(mAdapter);
        recyclerViewImages.setHasFixedSize(true);
        recyclerViewImages.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerViewImages, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
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

        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, recyclerViewImages);
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
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
