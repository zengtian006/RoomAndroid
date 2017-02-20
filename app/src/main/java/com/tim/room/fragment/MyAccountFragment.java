package com.tim.room.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.tim.room.utils.ImageUtils;

/**
 * Created by Zeng on 2017/1/3.
 */

public class MyAccountFragment extends Fragment implements ImageUtils.ImageAttachmentListener {

    private static final String TAG = MyAccountFragment.class.getSimpleName();
    Button btn_logout;
    TextView tv_name, tv_gender, tv_room_name;
    RelativeLayout relativeLayout;
    Context mContext;
    ImageView iv_user;
    ImageUtils imageutils;

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
//        imageutils = new ImageUtils((Activity) rootView.getContext());
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

        iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                imageutils.imagepicker(1);
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
        iv_user = (ImageView) rootView.findViewById(R.id.iv_user);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {

    }
}
