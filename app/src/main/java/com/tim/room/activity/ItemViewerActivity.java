package com.tim.room.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.tim.room.adapter.GalleryAdapter;
import com.tim.room.adapter.GirdDropDownAdapter;
import com.tim.room.adapter.ListDropDownAdapter;
import com.tim.room.adapter.TagsAdapter;
import com.tim.room.helper.ColorPicker;
import com.tim.room.model.Categories;
import com.tim.room.model.ItemSeries;
import com.tim.room.model.Items;
import com.tim.room.model.TagEntry;
import com.tim.room.model.User;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.view.DropDownMenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ItemViewerActivity extends AppCompatActivity {
    private static final String TAG = ItemViewerActivity.class.getSimpleName();

    //    public static ArrayList<String> images;
    public static List<Items> items;
    private User currentUser;
    public static GalleryAdapter mAdapter;
    Context mContext;
    ItemSeries itemSeries;

    RecyclerView recyclerViewImages;

    DropDownMenu mDropDownMenu;
    private String[] headers;
    private List<View> popupViews = new ArrayList<>();

    private ListDropDownAdapter categoryAdapter;
    private ListDropDownAdapter seasonAdapter;
    private GirdDropDownAdapter sortAdapter;
    private TagsAdapter tagAdapter;

    private List<String> categoryNameList;
    private List<String> tagNameList;
    ArrayList<Categories> categoriesArrayList;
    private String[] season;
    private String[] sort;
    private int tagPosition = 0;

    //Filter combination
    private int filterCategory;
    private String filterSeason;
    private String filterTag;
    private String filterSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);
//        overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
        this.mContext = getApplicationContext();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        itemSeries = (ItemSeries) bundle.getSerializable("itemList");
        currentUser = (User) bundle.getSerializable("current_user");

        headers = getResources().getStringArray(R.array.filter_header);
        season = getResources().getStringArray(R.array.filter_season);
        sort = getResources().getStringArray(R.array.filter_sort);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(itemSeries.getTitle());
        mDropDownMenu = (DropDownMenu) findViewById(R.id.dropDownMenu);
        initView();
    }

    private void initView() {
        filterCategory = 0;
        filterSeason = "0";
        filterTag = "0";
        filterSort = "0";

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
                        categoryAdapter = new ListDropDownAdapter(ItemViewerActivity.this, categoryNameList);
                        categoryView.setDividerHeight(0);
                        categoryView.setAdapter(categoryAdapter);
                    }
                });


        //init season menu
        final ListView seasonView = new ListView(this);
        seasonView.setDividerHeight(0);
        seasonAdapter = new ListDropDownAdapter(this, Arrays.asList(season));
        seasonView.setAdapter(seasonAdapter);


        //init tag
        final View tagView = getLayoutInflater().inflate(R.layout.custom_layout, null);
        GridView tagGridView = (GridView) tagView.findViewById(R.id.tags);
        tagNameList = new ArrayList<>();
        if (!itemSeries.getAllTagsMap().isEmpty()) {
            List<TagEntry> tagEntryList = itemSeries.getAllTagsMap();
            for (TagEntry tagEntry : tagEntryList) {
//                tagNameList.add(tagEntry.getKey() + "(" + tagEntry.getValue() + ")");
                tagNameList.add(tagEntry.getKey());
            }
        }
        tagNameList.add(0, "All");
        tagAdapter = new TagsAdapter(this, tagNameList);
        tagGridView.setAdapter(tagAdapter);
        TextView ok = (TextView) tagView.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDropDownMenu.setTabText(tagPosition == 0 ? headers[2] : tagNameList.get(tagPosition));
                mDropDownMenu.closeMenu();

                if (tagPosition == 0) {
                    filterTag = "0";
                } else {
                    filterTag = tagNameList.get(tagPosition);
                }
                dateSetChanged(filterCategory, filterSeason, filterTag, filterSort);
//                items.clear();
//                if (tagPosition == 0) { //Show All
//                    items.addAll(itemSeries.getItems());
//                } else {
////                    String tagName = tagNameList.get(tagPosition).substring(0, tagNameList.get(tagPosition).indexOf("("));
//                    String tagName = tagNameList.get(tagPosition);
//                    Log.v(TAG, "TAGNAME: " + tagName);
//                    for (Items item : itemSeries.getItems()) {
//                        if (item.getTags().contains(tagName)) {
//                            Log.v("CATEITEMS", "Filtered Items: " + item.getImageName());
//                            items.add(item);
//                        }
//                    }
//                }
//                mAdapter.notifyDataSetChanged();
            }
        });
        //init sort menu
        final ListView sortView = new ListView(this);
        sortView.setDividerHeight(0);
        sortAdapter = new GirdDropDownAdapter(this, Arrays.asList(sort));
        sortView.setAdapter(sortAdapter);

        //init popupViews
        popupViews.add(categoryView);
        popupViews.add(seasonView);
        popupViews.add(tagView);
        popupViews.add(sortView);

        //add item click event
        categoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : categoryNameList.get(position));
                mDropDownMenu.closeMenu();

                if (position == 0) {
                    filterCategory = 0;
                } else {
                    filterCategory = categoriesArrayList.get(position - 1).getId();
                }
                dateSetChanged(filterCategory, filterSeason, filterTag, filterSort);
//                items.clear();
//                if (position == 0) { //Show All
//                    items.addAll(itemSeries.getItems());
//                } else {
//                    for (Items item : itemSeries.getItems()) {
//                        if (item.getCateId().equals(categoriesArrayList.get(position - 1).getId())) {
//                            Log.v("CATEITEMS", "Filtered Items: " + item.getImageName());
//                            items.add(item);
//                        }
//                    }
//                }
//                mAdapter.notifyDataSetChanged();
            }
        });

        seasonView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seasonAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : season[position]);
                mDropDownMenu.closeMenu();
                if (position == 0) { //Show All
                    filterSeason = "0";
                } else {
                    filterSeason = season[position];
                }
                dateSetChanged(filterCategory, filterSeason, filterTag, filterSort);

//                items.clear();
//                if (position == 0) { //Show All
//                    items.addAll(itemSeries.getItems());
//                } else {
//                    for (Items item : itemSeries.getItems()) {
//                        if (item.getSeasons().contains(season[position])) {
//                            Log.v("CATEITEMS", "Filtered Items: " + item.getImageName());
//                            items.add(item);
//                        }
//                    }
//                }
//                mAdapter.notifyDataSetChanged();
            }
        });

        tagGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tagAdapter.setCheckItem(position);
                tagPosition = position;
            }
        });

        sortView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sortAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[3] : sort[position]);
                mDropDownMenu.closeMenu();

                if (position == 0) {
                    filterSort = "0";
                } else {
                    filterSort = "1";
                }
                dateSetChanged(filterCategory, filterSeason, filterTag, filterSort);

//                if (position == 0) {
//                    items.clear();
//                    items.addAll(itemSeries.getItems());
//                } else {
//                    Collections.reverse(items);
//                }
//                mAdapter.notifyDataSetChanged();
            }
        });

        //init context view
        mAdapter = new GalleryAdapter(getApplicationContext(), items);
        recyclerViewImages = new RecyclerView(this);
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
                bundle.putSerializable("current_user", currentUser);

                intent.putExtras(bundle);
                startActivity(intent);
            }

            private static final float SELECTED_SCALE = .8f;
            private static final float UNSELECTED_SCALE = 1f;

            @Override
            public void onLongClick(View view, int position) {
                Intent intent = new Intent(ItemViewerActivity.this, ItemSingleViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("items", items.get(position));
                bundle.putSerializable("current_user", currentUser);
                intent.putExtras(bundle);
                startActivity(intent);
//                FrameLayout rl = (FrameLayout) view.findViewById(R.id.imageSquareLayout);
//                final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
//                if (cb.isChecked()) {
//                    rl.animate()
//                            .scaleY(UNSELECTED_SCALE)
//                            .scaleX(UNSELECTED_SCALE)
//                            .setDuration(200).withEndAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            cb.setChecked(false);
//                        }
//                    })
//                            .start();
//                } else {
//                    rl.animate()
//                            .scaleY(SELECTED_SCALE)
//                            .scaleX(SELECTED_SCALE).withEndAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            cb.setChecked(true);
//                        }
//                    })
//                            .setDuration(200)
//                            .start();
//                }
            }
        }));

        //init dropdownview
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, recyclerViewImages);
    }

    private void dateSetChanged(int filterCategory, String filterSeason, String filterTag, String filterSort) {
        Log.v(TAG, "FILTER: " + filterCategory);
        Log.v(TAG, "FILTER: " + filterSeason);
        Log.v(TAG, "FILTER: " + filterTag);
        Log.v(TAG, "FILTER: " + filterSort);
        items.clear();
//        List<Items> tempItems = new ArrayList<>();
        items.addAll(itemSeries.getItems());
        for (int i = items.size() - 1; i > -1; i--) {
            Items item = items.get(i);
            if (filterCategory != 0) {
                if (!item.getCateId().equals(filterCategory)) {
                    items.remove(item);
                }
            }
            if (!filterSeason.equals("0")) {
                if (!item.getSeasons().contains(filterSeason)) {
                    items.remove(item);
                }
            }
            if (!filterTag.equals("0")) {
                if (!item.getTags().contains(filterTag)) {
                    items.remove(item);
                }
            }
        }
        if (!filterSort.equals("0")) {
            Collections.reverse(items);
        }
//        items = tempItems;
        Log.v(TAG, "FILTERSIZE: " + items.size());
        mAdapter.notifyDataSetChanged();
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
