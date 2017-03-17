package com.tim.room.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tim.room.MainActivity;
import com.tim.room.R;

import static com.tim.room.MainActivity.fragmentManager;
import static com.tim.room.MainActivity.session;
import static com.tim.room.utils.CommonUtil.containerHeight;

/**
 * Created by Zeng on 2017/1/3.
 */

public class MyAccountFragment extends Fragment {

    private static final String TAG = MyAccountFragment.class.getSimpleName();
    TextView tv_name, tv_gender, tv_room_name;
    RelativeLayout relativeLayout;
    Context mContext;
    ImageView iv_user, iv_zoom;
    TabLayout userProfileTabs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_myaccount, container, false);
        this.mContext = getContext();
        findView(rootView);
        setView();
        setListener();

        init();
        return rootView;
    }

    private void init() {
        displayView(3);
    }

    private void setListener() {
        iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                imageutils.imagepicker(1);
            }
        });
        userProfileTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                Toast.makeText(mContext, tab.getTag().toString(), Toast.LENGTH_SHORT).show();
                displayView(Integer.valueOf(tab.getTag().toString()));
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
        if (MainActivity.session.isLoggedIn()) {
            tv_name.setText(MainActivity.session.getUser().getName());
        }

        ViewGroup.LayoutParams params = iv_zoom.getLayoutParams();
        params.height = containerHeight(getActivity(), 2);
        iv_zoom.setLayoutParams(params);
        if (session.getUser().getGender().equals("M")) {
            iv_zoom.setImageResource(R.drawable.bg_man_2);
        } else {
            iv_zoom.setImageResource(R.drawable.bg_women_1);
        }
        View view4 = getActivity().getLayoutInflater().inflate(R.layout.custom_tab, null);
        view4.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_account_like_24dp);
        userProfileTabs.addTab(userProfileTabs.newTab().setCustomView(view4).setTag("3"));
        View view2 = getActivity().getLayoutInflater().inflate(R.layout.custom_tab, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_account_alarm_24dp);
        userProfileTabs.addTab(userProfileTabs.newTab().setCustomView(view2).setTag("1"));
        View view3 = getActivity().getLayoutInflater().inflate(R.layout.custom_tab, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_account_trash_24dp);
        userProfileTabs.addTab(userProfileTabs.newTab().setCustomView(view3).setTag("2"));
        View view1 = getActivity().getLayoutInflater().inflate(R.layout.custom_tab, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_account_profile_24dp);
        userProfileTabs.addTab(userProfileTabs.newTab().setCustomView(view1).setTag("0"));



//        userProfileTabs.addTab(userProfileTabs.newTab().setIcon(R.drawable.ic_account_profile_24dp).setTag("0"));
//        userProfileTabs.addTab(userProfileTabs.newTab().setIcon(R.drawable.ic_account_alarm_24dp).setTag("1"));
//        userProfileTabs.addTab(userProfileTabs.newTab().setIcon(R.drawable.ic_account_trash_24dp).setTag("2"));
//        userProfileTabs.addTab(userProfileTabs.newTab().setIcon(R.drawable.ic_account_like_24dp).setTag("3"));
    }

    private void findView(View rootView) {
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.RelativeLayout1);
        iv_user = (ImageView) rootView.findViewById(R.id.iv_user);
        userProfileTabs = (TabLayout) rootView.findViewById(R.id.userProfileTabs);
        iv_zoom = (ImageView) rootView.findViewById(R.id.iv_zoom);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new AccountProfileSubFragment();
                break;
            case 1:
                fragment = AccountOverdueSubFragment.newInstance(1);//almost overdue items
                break;
            case 2:
                fragment = AccountOverdueSubFragment.newInstance(0); //overdue items
                break;
            case 3:
                fragment = AccountOverdueSubFragment.newInstance(99); //liked items
                break;
            default:
                break;
        }

        if (fragment != null) {
            fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.me_container_body, fragment);
            fragmentTransaction.commit();
        }
    }
}
