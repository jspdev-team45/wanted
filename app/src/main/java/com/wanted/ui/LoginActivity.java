package com.wanted.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wanted.R;
import com.wanted.exception.ErrType;
import com.wanted.exception.InputException;
import com.wanted.util.DialogUtil;
import com.wanted.util.ValidateUserInfo;

/**
 * Created by xlin2
 */
public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private TextView registerText;
    private EditText emailEdit;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_login);

        findViews();
        addListeners();
    }

    /**
     * Get ui objects
     */
    private void findViews() {
        loginBtn = (Button) findViewById(R.id.login_btn);
        registerText = (TextView) findViewById(R.id.txt_register);
        emailEdit = (EditText) findViewById(R.id.txt_email);
        passwordEdit = (EditText) findViewById(R.id.txt_password);
    }

    /**
     * Add listeners to ui objects
     */
    private void addListeners() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValid() == false) return;
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

    private boolean formValid() {
        boolean ret = false;
        ValidateUserInfo validate = new ValidateUserInfo();

        try {
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            if (email.equals("") || password.equals(""))
                throw new InputException(ErrType.EMPTY_INPUT);
            else if (!validate.isEmailValid(email))
                throw new InputException(ErrType.INVALID_EMAIL);
            else if (!validate.isPasswordValid(password))
                throw new InputException(ErrType.INVALID_PASSWORD);
            ret = true;
        } catch (InputException e) {
            e.handleEmpty();
            new DialogUtil().showError(LoginActivity.this, e.getErrMsg());
        }
        return ret;
    }
}
