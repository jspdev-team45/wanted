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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wanted.R;
import com.wanted.entities.Pack;
import com.wanted.entities.Recruiter;
import com.wanted.entities.Role;
import com.wanted.entities.Seeker;
import com.wanted.entities.User;
import com.wanted.util.DialogUtil;
import com.wanted.util.ValidateUserInfo;
import com.wanted.ws.remote.CheckNetwork;
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
    private RadioGroup roleSelect;

    private RegisterTask registerTask = null;

    private String name;
    private String email;
    private String password;
    private int role;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_register);

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
        roleSelect = (RadioGroup) findViewById(R.id.role_select);
    }

    private void addListeners() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
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
        boolean cancel = formValid();

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user registration attempt.
            registerTask = new RegisterTask();
            registerTask.execute((Void) null);
        }
    }

    private boolean formValid() {
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
            // Show the spinner and disable interaction
            progressLayout.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                 WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (new CheckNetwork().isConnected(RegisterActivity.this)) {
                // If there's no account registered, register the new account here.
                DefaultSocketClient client = new DefaultSocketClient("10.0.0.9", 8888);
                response = client.sendToServer(packData());
                return true;
            }
            else {
                new DialogUtil().showError(RegisterActivity.this, "Network not connected.");
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            progressLayout.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            registerTask = null;

            // Update ui
            if (response == null)
                new DialogUtil().showError(RegisterActivity.this, "Unable to register.");
            else if (response.getInfo().equals(Information.FAIL))
                new DialogUtil().showError(RegisterActivity.this, "Username or email exists.");
            else {
                user.setId(((User)(response.getContent())).getId());
                jumpTo(MainActivity.class);
            }

        }

        @Override
        protected void onCancelled() {
            registerTask = null;
        }

    }

    private Pack packData() {
        role = roleSelect.getCheckedRadioButtonId();
        if (role == R.id.role_seeker) {
            role = Role.SEEKER;
            user = new Seeker(email, password, name, role);
        }
        else {
            role = Role.RECRUITER;
            user = new Recruiter(email, password, name, role);
        }
        Pack ret = new Pack(Information.REGISTER, user);

        return ret;
    }

    private void jumpTo(Class<?> target) {
        Intent intent = new Intent(getApplicationContext(), target);
        intent.putExtra("User", user);
        startActivity(intent);
    }
}
