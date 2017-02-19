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

import com.tim.room.helper.ProgressDialog;
import com.tim.room.R;
import com.tim.room.adapter.CateFilterAdapter;
import com.tim.room.adapter.GalleryAdapter;
import com.tim.room.fragment.SlideshowDialogFragment;
import com.tim.room.helper.ColorPicker;
import com.tim.room.model.Categories;
import com.tim.room.model.ItemSeries;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;
import static com.tim.room.app.AppConfig.IMG_BASE_URL;

public class ItemViewerActivity extends AppCompatActivity {
    private static final String TAG = ItemViewerActivity.class.getSimpleName();

    public static ArrayList<String> images;
    public static GalleryAdapter mAdapter;
    Context mContext;

    RecyclerView recycler_view_cate_list, recyclerViewImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);
        this.mContext = getApplicationContext();
        recycler_view_cate_list = (RecyclerView) findViewById(R.id.recycler_view_cates);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final ItemSeries itemSeries = (ItemSeries) bundle.getSerializable("itemList");

        images = new ArrayList<>();
        for (Items item : itemSeries.getItems()) {
            images.add(IMG_BASE_URL + session.getUser().getId().toString() + "/" + item.getImageName());
        }
        mAdapter = new GalleryAdapter(getApplicationContext(), images);
        recyclerViewImages = (RecyclerView) findViewById(R.id.recycler_view_images);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerViewImages.setLayoutManager(mLayoutManager);
        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
        recyclerViewImages.setAdapter(mAdapter);
        recyclerViewImages.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerViewImages, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                Log.v(TAG, images.toString());
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //Find Sub Category
        RESTFulService itemCatesService = RESTFulServiceImp.createService(RESTFulService.class);
        final ProgressDialog dialog = new ProgressDialog(ItemViewerActivity.this);
        dialog.show();
        itemCatesService.findSubCategoriesById(itemSeries.getCate_id()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Categories>>() {
                    @Override
                    public void accept(ArrayList<Categories> categories) throws Exception {
                        CateFilterAdapter cateFilterAdapter = new CateFilterAdapter(mContext, categories, itemSeries.getItems());
                        recycler_view_cate_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        recycler_view_cate_list.setAdapter(cateFilterAdapter);
                        recycler_view_cate_list.setItemAnimator(new DefaultItemAnimator());
                        dialog.dismiss();
                    }
                });
//        RESTFulService itemService = RESTFulServiceImp.createService(RESTFulService.class);
//        itemService.findAllItemsTest(session.getUser().getId().toString()).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<Items>>() {
//                    @Override
//                    public void accept(List<Items> items) throws Exception {
//                        images = new ArrayList<>();
//                        for (Items item : items) {
//                            String image_path = IMG_BASE_URL + session.getUser().getId().toString() + "/" + item.getImageName();
//                            images.add(image_path);
//                            Log.v(TAG, image_path);
//                            images.add("http://www.w3schools.com/css/trolltunga.jpg");
//                            images.add("http://www.w3schools.com/howto/img_fjords.jpg");
//                            images.add("http://www.nikon.com.cn/tmp/CN/4016499630/3760176746/3015334490/1054978028/4291728192/3007905606.jpg");
//                        }
//
//                    }
//                });

//        ImageButton btn_change = (ImageButton) findViewById(R.id.imageButton1);
//        btn_change.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                images.clear();
//                images.add("http://www.w3schools.com/css/trolltunga.jpg");
//                images.add("http://www.w3schools.com/howto/img_fjords.jpg");
//                mAdapter.notifyDataSetChanged();
//            }
//        });

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
