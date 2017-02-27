package com.tim.room.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tim.room.MainActivity;
import com.tim.room.R;

public class AccountProfileSubFragment extends Fragment {
    TextView tv_room_name, tv_gender;
    Button btn_logout;
    Context mContext;

    public AccountProfileSubFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_profile_sub, container, false);
        this.mContext = getContext();
        findView(view);
        setView();
        setListener();
        return view;
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

    private void setView() {
        tv_room_name.setText(MainActivity.session.getUser().getRoomName());
        if (MainActivity.session.getUser().getGender().equals("F")) {
            tv_gender.setText("Female");
        } else {
            tv_gender.setText("Male");
        }
    }

    private void findView(View view) {
        tv_room_name = (TextView) view.findViewById(R.id.tv_room_name);
        tv_gender = (TextView) view.findViewById(R.id.tv_gender);
        btn_logout = (Button) view.findViewById(R.id.btn_logout);

    }
}
