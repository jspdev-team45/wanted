package com.wanted.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wanted.R;
import com.wanted.entities.Pack;
import com.wanted.entities.Recruiter;
import com.wanted.entities.Role;
import com.wanted.entities.User;
import com.wanted.util.DataHolder;
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
    private CheckBox rememberBox;

    private final String PREF_NAME = "loginPref";
    private SharedPreferences loginPreference;
    private SharedPreferences.Editor editor;

    private String email;
    private String password;
    private boolean remember;

    private LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_login);

        findViews();
        checkRemember();
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
        rememberBox = (CheckBox) findViewById(R.id.check_remem);
    }

    private void checkRemember() {
        loginPreference = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = loginPreference.edit();

        remember = loginPreference.getBoolean("remember", false);
        if (remember == true) {
            User user = new Recruiter("Hehe", email, password, Role.RECRUITER);
            DataHolder.getInstance().setUser(user);
            loginTask = new LoginTask();
            loginTask.execute((Void) null);
        }
    }

    /**
     * Add listeners to ui objects
     */
    private void addListeners() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValid() == false) return;
                User user = new Recruiter("Hehe", email, password, Role.RECRUITER);
                DataHolder.getInstance().setUser(user);
                loginTask = new LoginTask();
                loginTask.execute((Void) null);
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
        boolean valid = true;
        ValidateUserInfo validate = new ValidateUserInfo();

        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();

        if (email.equals("")) {
            emailEdit.requestFocus();
            emailEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!validate.isEmailValid(email)) {
            emailEdit.requestFocus();
            emailEdit.setError(getString(R.string.error_invalid_email));
            valid = false;
        } else if (password.equals("")) {
            passwordEdit.requestFocus();
            passwordEdit.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!validate.isPasswordValid(password)) {
            passwordEdit.requestFocus();
            passwordEdit.setError(getString(R.string.error_invalid_password));
            valid = false;
        }

        return valid;
    }

    public class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        LoginTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Show the spinner and disable interaction
            pd = new DialogUtil().showProgress(LoginActivity.this, "Login...");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loginTask = null;

            // set preference
            if (rememberBox.isChecked() && remember == false) {
                editor.putBoolean("remember", true);
                editor.putString("email", email);
                editor.putString("password", password);
                editor.commit();
            }

            // jump
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            // Update ui
//            if (response == null)
//                new DialogUtil().showError(LoginActivity.this, "Unable to login.");
//            else if (response.getInfo().equals(Information.FAIL))
//                new DialogUtil().showError(RegisterActivity.this, "Username or email exists.");
//            else {
//                user.setId(((User)(response.getContent())).getId());
//                DataHolder.getInstance().setUser(user);
//                jumpTo(MainActivity.class);
//            }

        }

        @Override
        protected void onCancelled() {
            loginTask = null;
        }

    }
}
