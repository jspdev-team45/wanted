package com.wanted.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wanted.R;

/**
 * Created by xlin2
 */
public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private TextView registerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        addListeners();
    }

    /**
     * Get ui objects
     */
    private void findViews() {
        loginBtn = (Button) findViewById(R.id.login_btn);
        registerText = (TextView) findViewById(R.id.txt_register);
    }

    /**
     * Add listeners to ui objects
     */
    private void addListeners() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
