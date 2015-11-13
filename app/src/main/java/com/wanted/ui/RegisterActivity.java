package com.wanted.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanted.R;
import com.wanted.entities.Pack;
import com.wanted.entities.Seeker;
import com.wanted.util.ValidateUserInfo;
import com.wanted.ws.remote.DefaultSocketClient;
import com.wanted.entities.Information;

/**
 * Created by xlin2
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText emailEdit;
    private EditText nameEdit;
    private EditText passwordEdit;
    private Button registerBtn;
    private TextView loginText;
    private View focusView;
    private LinearLayout progressLayout;

    private RegisterTask registerTask = null;

    private String name;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();
        addListeners();
    }

    private void findViews() {
        emailEdit = (EditText) findViewById(R.id.edit_email);
        nameEdit = (EditText) findViewById(R.id.edit_name);
        passwordEdit = (EditText) findViewById(R.id.edit_password);
        registerBtn = (Button) findViewById(R.id.register_btn);
        loginText = (TextView) findViewById(R.id.txt_already_have);
        progressLayout = (LinearLayout) findViewById(R.id.layout_progress);
    }

    private void addListeners() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void doRegister() {
        boolean cancel = checkValid();

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user registration attempt.
            registerTask = new RegisterTask();
            registerTask.execute((Void) null);
        }
    }

    private boolean checkValid() {
        boolean cancel = false;
        ValidateUserInfo validate = new ValidateUserInfo();

        focusView = null;

        name = nameEdit.getText().toString();
        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            nameEdit.setError(getString(R.string.error_field_required));
            focusView = nameEdit;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            emailEdit.setError(getString(R.string.error_field_required));
            focusView = emailEdit;
            cancel = true;
        } else if (!validate.isEmailValid(email)) {
            emailEdit.setError(getString(R.string.error_invalid_email));
            focusView = emailEdit;
            cancel = true;
        } else {
            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty(password) || !validate.isPasswordValid(password)) {
                passwordEdit.setError(getString(R.string.error_invalid_password));
                focusView = passwordEdit;
                cancel = true;
            }
        }
        return cancel;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate the user.
     */
    public class RegisterTask extends AsyncTask<Void, Void, Boolean> {
        private final String mName;
        private final String mEmail;
        private final String mPassword;
        private Pack response;

        RegisterTask() {
            mName = name;
            mEmail = email;
            mPassword = password;
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            progressLayout.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                 WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: check if account already exists against a network service.
            checkExist();

            // TODO: if there's no account registered, register the new account here.
            DefaultSocketClient client = new DefaultSocketClient("10.0.0.9", 8888);
            response = client.sendToServer(packData());

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progressLayout.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            registerTask = null;

            if (response != null)
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        @Override
        protected void onCancelled() {
            registerTask = null;
        }

        private void checkExist() {
        }
    }

    private Pack packData() {
        Seeker seeker = new Seeker(email, password, name, 0);
        Pack ret = new Pack(Information.REGISTER, seeker);

        return ret;
    }
}
