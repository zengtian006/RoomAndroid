package com.tim.room.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tim.room.MainActivity;

/**
 * Created by Zeng on 2/2/2017.
 */

public class AuthActivity extends AppCompatActivity {
    private final static int INTENT_REQUEST_LOGIN = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainActivity.session == null || !MainActivity.session.isLoggedIn()) {
            //just return to home
            //finish();
            //startActivity(new Intent(this, LoginActivity.class));
            startActivityForResult(new Intent(this, LoginAcitivity.class), INTENT_REQUEST_LOGIN);
        }
    }
}
