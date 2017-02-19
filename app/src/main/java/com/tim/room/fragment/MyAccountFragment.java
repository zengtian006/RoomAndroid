package com.tim.room.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tim.room.MainActivity;
import com.tim.room.R;

/**
 * Created by Zeng on 2017/1/3.
 */

public class MyAccountFragment extends Fragment {

    private static final String TAG = MyAccountFragment.class.getSimpleName();
    Button btn_logout;
    TextView tv_name, tv_gender, tv_room_name;
    RelativeLayout relativeLayout;
    Context mContext;
    ImageView iv2;

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
        serView();
        setListener();
        return rootView;
    }

    private void setListener() {
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.session.setLogin(false, null, null);
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void serView() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
        params.height = dm.heightPixels;
        relativeLayout.setLayoutParams(params);

        if (MainActivity.session.isLoggedIn()) {
            tv_name.setText(MainActivity.session.getUser().getName());
            tv_room_name.setText(MainActivity.session.getUser().getRoomName());
            if (MainActivity.session.getUser().getGender().equals("F")) {
                tv_gender.setText("Female");
            } else {
                tv_gender.setText("Male");
            }
        }
    }

    private void findView(View rootView) {
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_room_name = (TextView) rootView.findViewById(R.id.tv_room_name);
        tv_gender = (TextView) rootView.findViewById(R.id.tv_gender);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.RelativeLayout1);
        btn_logout = (Button) rootView.findViewById(R.id.btn_logout);
    }
}
