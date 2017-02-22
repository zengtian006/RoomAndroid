package com.tim.room.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.R;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.app.AppConfig.IMG_BASE_URL;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private List<Items> items;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private ImageButton btn_global;
    private int selectedPosition = 0;
    RESTFulService updateItemService;

    public static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);
        btn_global = (ImageButton) v.findViewById(R.id.btn_global);

        items = (List<Items>) getArguments().getSerializable("items");
        selectedPosition = getArguments().getInt("position");

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        this.updateItemService = RESTFulServiceImp.createService(RESTFulService.class);
        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(final int position) {
        lblCount.setText((position + 1) + " of " + items.size());
        Items item = items.get(position);
        String image = IMG_BASE_URL + item.getUser().getId().toString() + "/" + item.getImageName();
        lblTitle.setText(item.getUser().getName());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            lblDate.setText(formatter.format(formatter.parse(item.getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (item.getGlobal().equals("1")) {
            btn_global.setImageResource(R.drawable.ic_global_checked);
        } else {
            btn_global.setImageResource(R.drawable.ic_global);
        }
        btn_global.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              if (items.get(position).getGlobal().equals("1")) {
                                                  items.get(position).setGlobal("0");
                                                  updateItemService.updateItem(items.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                                                      @Override
                                                      public void accept(Boolean aBoolean) throws Exception {
                                                          if (aBoolean) {
                                                              btn_global.setImageResource(R.drawable.ic_global);
                                                          }
                                                      }
                                                  });
                                              } else {
                                                  items.get(position).setGlobal("1");
                                                  updateItemService.updateItem(items.get(position)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                                                      @Override
                                                      public void accept(Boolean aBoolean) throws Exception {
                                                          Log.v(TAG, "result: " + aBoolean);
                                                          if (aBoolean) {
                                                              btn_global.setImageResource(R.drawable.ic_global_checked);
                                                          }
                                                      }
                                                  });
                                              }
                                              myViewPagerAdapter.notifyDataSetChanged();
                                          }
                                      }
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            Items item = items.get(position);
            String image = IMG_BASE_URL + item.getUser().getId().toString() + "/" + item.getImageName();

            Glide.with(getActivity()).load(image)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
