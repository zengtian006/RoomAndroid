package com.tim.room.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.tim.room.MainActivity;
import com.tim.room.R;
import com.tim.room.adapter.ItemSeriesAdapter;
import com.tim.room.model.ItemLikes;
import com.tim.room.model.ItemSeries;
import com.tim.room.model.Items;
import com.tim.room.model.User;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.dialog;
import static com.tim.room.MainActivity.session;

public class HomeOthersActivity extends AppCompatActivity {
    private final static String TAG = HomeOthersActivity.class.getSimpleName();

    RecyclerView recyclerView;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_others);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User) bundle.getSerializable("user");
        Log.v(TAG, "OHERt:" + user.getName());

        findView();
        setView();
    }

    private void setView() {
        final RESTFulService findAllItemsService = RESTFulServiceImp.createService(RESTFulService.class);

        findAllItemsService.findAllItems(user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<ItemSeries>>() {
            @Override
            public void accept(ArrayList<ItemSeries> itemSeries) throws Exception {
                for (ItemSeries series : itemSeries) {
                    for (Items item : series.getItems()) {
                        item.setLiked(false);
                        for (ItemLikes itemLike : item.getItemLikes()) {
                            if (itemLike.getUserId().equals(MainActivity.session.getUser().getId())) {
                                item.setLiked(true);
                                break;
                            }
                        }
                    }
                }
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(HomeOthersActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(mLayoutManager);

                ItemSeriesAdapter adapter = new ItemSeriesAdapter(HomeOthersActivity.this, itemSeries);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void findView() {
        recyclerView = (RecyclerView) findViewById(R.id.other_recycler_view);
    }
}
