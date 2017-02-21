package com.tim.room.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tim.room.R;
import com.tim.room.model.User;
import com.tim.room.model.UserResponse;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;


public class LoginAcitivity extends AppCompatActivity {
    private final static String TAG = LoginAcitivity.class.getSimpleName();
    Button btn_linkto_reg, btn_login;
    EditText edt_name, edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        findView();
        setListener();
    }

    private void setListener() {
        btn_linkto_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginAcitivity.this, RegisterActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User user = new User(edt_name.getText().toString(), edt_password.getText().toString());
                RESTFulService loginService = RESTFulServiceImp.createService(RESTFulService.class);
                loginService.login(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<UserResponse>() {
                            @Override
                            public void accept(UserResponse userResponse) throws Exception {
                                Log.v(TAG, "Status: " + userResponse.getStatus());
                                Log.v(TAG, "User ID: " + userResponse.getUser().getId().toString());
                                session.setLogin(true, null, userResponse.getUser());
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                        });

            }
        });
    }

    private void findView() {
        btn_linkto_reg = (Button) findViewById(R.id.btn_linkto_reg);
        btn_login = (Button) findViewById(R.id.btn_login);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_password = (EditText) findViewById(R.id.edt_password);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
