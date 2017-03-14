package com.tim.room.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.tim.room.utils.LocaleUtil;

public class AccountProfileSubFragment extends Fragment {
    TextView tv_room_name, tv_gender, tv_language;
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
        final String[] languageArray = {"English", "中文"};
        int selected = 0;
        if (tv_language.getText().equals(languageArray[1])) {
            selected = 1;
        }

        final int finalSelected = selected;
        tv_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Select language")
                        .setSingleChoiceItems(languageArray, finalSelected, null)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                if (selectedPosition == 0) {
                                    LocaleUtil.setLocale(mContext.getApplicationContext(), LocaleUtil.ENGLISH);
                                    tv_language.setText("English");
                                } else if (selectedPosition == 1) {
                                    LocaleUtil.setLocale(mContext.getApplicationContext(), LocaleUtil.SIMP_CHINESE);
                                    tv_language.setText("中文");
                                }
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
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
        if (LocaleUtil.getLocale(mContext.getApplicationContext()).equals(LocaleUtil.ENGLISH)) {
            tv_language.setText("English");
        } else if (LocaleUtil.getLocale(mContext.getApplicationContext()).equals(LocaleUtil.SIMP_CHINESE)) {
            tv_language.setText("中文");
        }
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
        tv_language = (TextView) view.findViewById(R.id.tv_language);

    }
}
