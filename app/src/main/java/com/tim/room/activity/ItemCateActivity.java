package com.tim.room.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tim.room.MainActivity;
import com.tim.room.R;
import com.tim.room.adapter.CateAdapter;
import com.tim.room.helper.ProgressDialog;
import com.tim.room.model.Categories;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ItemCateActivity extends AuthActivity {
    private final static String TAG = ItemCateActivity.class.getSimpleName();

    ListView listView;
    CateAdapter adapter;
    List<Categories> filterdCates;
    List<Categories> allCates;
    private int parent_id;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_cate);

        mContext = getApplicationContext();
        parent_id = -1;
        allCates = new ArrayList<Categories>();
        listView = (ListView) findViewById(R.id.lv_cate);

        RESTFulService CateService = RESTFulServiceImp.createService(RESTFulService.class);
        final ProgressDialog dialog = new ProgressDialog(ItemCateActivity.this);
        dialog.show();
        if (MainActivity.session.getUser().getGender().equals("F")) {
            CateService.findWomenCategories().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Categories>>() {
                @Override
                public void accept(List<Categories> categories) throws Exception {
                    allCates.addAll(categories);
                    filterdCates = filterCates(0);

                    adapter = new CateAdapter(mContext, filterdCates);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                }
            });
        } else {
            CateService.findManCategories().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Categories>>() {
                @Override
                public void accept(List<Categories> manCategories) throws Exception {
                    allCates.addAll(manCategories);
                    filterdCates = filterCates(0);

                    adapter = new CateAdapter(mContext, filterdCates);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                }
            });
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Categories item = (Categories) adapterView.getItemAtPosition(i);
                List<Categories> cateList = filterCates(item.getId());
                if (cateList.size() > 0) {
                    parent_id = item.getParentId();
                    filterdCates.clear();
                    filterdCates.addAll(cateList);
                    adapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("cate_name", item.getCateName());
                    intent.putExtra("cate_id", String.valueOf(item.getId()));
                    setResult(RESULT_OK, intent);
                    finish();
                }
//                Toast.makeText(ItemCateActivity.this, "Item text: " + item.getId(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private List<Categories> filterCates(int filer) {
        List<Categories> result = new ArrayList<>();

        for (Categories category : allCates) {
            if (category.getParentId().equals(filer)) {
                result.add(category);
            }
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        if (parent_id < 0) {
            super.onBackPressed();
        } else {

            List<Categories> cateList = filterCates(parent_id);
            filterdCates.clear();
            filterdCates.addAll(cateList);
            adapter.notifyDataSetChanged();
            if (parent_id == 0) {
                parent_id = -1;
            }
        }

    }
}
