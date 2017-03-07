package com.tim.room.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tim.room.R;
import com.tim.room.model.User;
import com.tim.room.model.UserResponse;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;

public class RegisterMoreActivity extends AppCompatActivity {
    private static final String TAG = RegisterMoreActivity.class.getSimpleName();

    Button btn_male, btn_female;
    EditText edt_male, edt_female;
    RESTFulService setGenderService;
    String name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_more);

        setGenderService = RESTFulServiceImp.createService(RESTFulService.class);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
//        Log.v(TAG, intent.getStringExtra("name"));
//        Log.v(TAG, intent.getStringExtra("password"));
        findView();
        setListener();
    }

    private void setListener() {
        btn_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edt_male.getText().toString().trim().isEmpty()) {
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setPassword(password);
                    newUser.setGender("M");
                    newUser.setRoomName(edt_male.getText().toString().trim());

                    addUser(newUser);
                }
            }
        });
        btn_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edt_female.getText().toString().trim().isEmpty()) {
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setPassword(password);
                    newUser.setGender("F");
                    newUser.setRoomName(edt_female.getText().toString().trim());

                    addUser(newUser);
                }
            }
        });
    }

    private void addUser(User newUser) {
        final RESTFulService regService = RESTFulServiceImp.createService(RESTFulService.class);
        regService.addUser(newUser)
                .subscribeOn(Schedulers.io())//在IO线程请求执行
                .observeOn(AndroidSchedulers.mainThread())//回到主线程去处理请求结果
                .doOnNext(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {

                    }
                })
                .observeOn(Schedulers.io())//回到IO线程去发起第二次请求
                .flatMap(new Function<User, ObservableSource<UserResponse>>() {
                    @Override
                    public ObservableSource<UserResponse> apply(User user) throws Exception {
                        return regService.login(user);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//回到主线程去处理第二次请求的结果
                .subscribe(new Consumer<UserResponse>() {
                    @Override
                    public void accept(UserResponse userResponse) throws Exception {
                        session.setLogin(true, null, userResponse.getUser());
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
    }

    private void findView() {
        btn_female = (Button) findViewById(R.id.btn_female);
        btn_male = (Button) findViewById(R.id.btn_male);
        edt_female = (EditText) findViewById(R.id.edt_female);
        edt_male = (EditText) findViewById(R.id.edt_male);
    }
}
