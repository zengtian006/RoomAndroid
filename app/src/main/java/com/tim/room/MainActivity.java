package com.tim.room;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;
import com.tim.room.activity.AddItemActivity;
import com.tim.room.activity.LoginAcitivity;
import com.tim.room.app.SessionManager;
import com.tim.room.fragment.AddFragment;
import com.tim.room.fragment.CharityFragment;
import com.tim.room.fragment.DiscoverFragment;
import com.tim.room.fragment.HomeFragment;
import com.tim.room.fragment.MyAccountFragment;
import com.tim.room.utils.LocaleUtil;
import com.tim.room.view.ProgressDialog;

;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private final static int INTENT_REQUEST_LOGIN = 0;
    private final static int INTENT_REQUEST_ADD_ITEM = 1;

    public static ProgressDialog dialog;

    public static FragmentManager fragmentManager;
    public static BottomBar bottomBar;
    public static SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocaleUtil.initialize(getApplicationContext(), LocaleUtil.SIMP_CHINESE);
        Log.v(TAG, "Main LOCALE: " + LocaleUtil.getLocale(getApplicationContext()));

        //test
//        View decorView = getWindow().getDecorView();
//        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);

        session = new SessionManager(this);
        Log.v(TAG, "Login??: " + session.isLoggedIn());
        if (session.isLoggedIn()) {
            Log.v(TAG, "Gender??: " + session.getUser().getGender());

            Log.v(TAG, "MemberID??: " + session.getUser().getId());
        }

        dialog = new ProgressDialog(this);

//        jerseyService.addEmployee(em)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Exception {
//                        Log.v("wxl", "response1: " + responseBody.string());
//                    }
//                });

//        final RESTFulService jerseyService = RESTFulServiceImp.createCloudSightService(RESTFulService.class);
//        imageRequest request = new imageRequest();
//        request.setImage_request(new imageRequest.ImageRequestBean("en", "http://tim198789.oss-cn-shanghai.aliyuncs.com/0a00036a-5a26-1b79-815a-269d607e0000/15.jpg"));
//        jerseyService.sendImage(request)
//                .subscribeOn(Schedulers.io())//在IO线程请求执行
//                .observeOn(AndroidSchedulers.mainThread())//回到主线程去处理请求结果
//                .doOnNext(new Consumer<imageSendResponse>() {
//                    @Override
//                    public void accept(imageSendResponse responseBody) throws Exception {
//                        Log.v("wxl", "response1: " + responseBody.getToken());
//                    }
//                })
//                .observeOn(Schedulers.io())//回到IO线程去发起第二次请求
//                .flatMap(new Function<imageSendResponse, ObservableSource<imageResultResponse>>() {
//                    @Override
//                    public ObservableSource<imageResultResponse> apply(imageSendResponse imageSendResponse) throws Exception {
//                        return jerseyService.imageResponse(imageSendResponse.getToken());
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())//回到主线程去处理第二次请求的结果
//                .subscribe(new Consumer<imageResultResponse>() {
//                    @Override
//                    public void accept(imageResultResponse responseBody) throws Exception {
//                        Log.v("wxl", "response2: " + responseBody.getName());
//                    }
//                });

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.home_page) {
                    if (session.isLoggedIn()) {
                        displayView(0);
                    } else {
                        startActivityForResult(new Intent(MainActivity.this, LoginAcitivity.class), INTENT_REQUEST_LOGIN);
                    }
                } else if (tabId == R.id.shopping_cart) {
                    displayView(1);
                } else if (tabId == R.id.add) {
                    startActivityForResult(new Intent(MainActivity.this, AddItemActivity.class), INTENT_REQUEST_ADD_ITEM);
                } else if (tabId == R.id.search) {
                    displayView(3);
                } else if (tabId == R.id.my_account) {
                    if (session.isLoggedIn()) {
                        displayView(4);
//                        startActivityForResult(new Intent(MainActivity.this, RegisterActivity.class), INTENT_REQUEST_LOGIN);
                    } else {
                        startActivityForResult(new Intent(MainActivity.this, LoginAcitivity.class), INTENT_REQUEST_LOGIN);
                    }
                }

            }
        });

        BottomBarTab overdueItemCount = bottomBar.getTabWithId(R.id.my_account);
        overdueItemCount.setBadgeCount(5);

    }

    private void displayView(int position) {
        Fragment fragment = null;
//        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new DiscoverFragment();
                break;
            case 2:
                fragment = new AddFragment();
                break;
            case 3:
                fragment = new CharityFragment();
                break;
            case 4:
                fragment = new MyAccountFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            // set the toolbar title
//            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_REQUEST_ADD_ITEM) {
                bottomBar.selectTabWithId(R.id.home_page);
            }
            if (requestCode == INTENT_REQUEST_LOGIN) {
                bottomBar.selectTabWithId(R.id.home_page);
                displayView(0);
//                Toast.makeText(MainActivity.this, session.getUser().getId().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
