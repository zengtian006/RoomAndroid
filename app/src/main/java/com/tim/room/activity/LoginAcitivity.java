package com.tim.room.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    private final static int INTENT_REQUEST_LOGIN = 0;

    Button btn_check_email, btn_reg, btn_login;
    EditText edt_name, edt_password;
    LinearLayout layout_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        findView();
        setListener();
    }

    private void setListener() {
        btn_check_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edt_name.getText().toString().trim().isEmpty()) {
                    layout_password.setVisibility(View.VISIBLE);
//                    btn_reg.setVisibility(View.VISIBLE);
                    btn_login.setVisibility(View.VISIBLE);
                    btn_check_email.setVisibility(View.GONE);
                }
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginAcitivity.this, RegisterMoreActivity.class);
                intent.putExtra("name", edt_name.getText().toString());
                intent.putExtra("password", edt_password.getText().toString());
                startActivityForResult(intent, INTENT_REQUEST_LOGIN);
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
                                if (userResponse.isSuccess()) {
                                    Log.v(TAG, "Status: " + userResponse.getStatus());
                                    Log.v(TAG, "User ID: " + userResponse.getUser().getId().toString());
                                    session.setLogin(true, null, userResponse.getUser());
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    Toast.makeText(LoginAcitivity.this, userResponse.getStatus(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        });
    }

    private void findView() {
        btn_check_email = (Button) findViewById(R.id.btn_check_email);
        btn_reg = (Button) findViewById(R.id.btn_reg);
        btn_login = (Button) findViewById(R.id.btn_login);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_password = (EditText) findViewById(R.id.edt_password);
        layout_password = (LinearLayout) findViewById(R.id.layout_password);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_REQUEST_LOGIN) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
