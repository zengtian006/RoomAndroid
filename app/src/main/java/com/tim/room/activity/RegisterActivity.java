package com.tim.room.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tim.room.R;

public class RegisterActivity extends AppCompatActivity {
    Button btn_reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findView();
        setListener();
    }

    private void setListener() {
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, RegisterMoreActivity.class));
            }
        });
    }

    private void findView() {
        btn_reg = (Button) findViewById(R.id.btn_reg);
    }
}
