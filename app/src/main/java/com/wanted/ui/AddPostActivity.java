package com.wanted.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.wanted.R;
import com.wanted.entities.Information;
import com.wanted.entities.Pack;
import com.wanted.entities.Post;
import com.wanted.entities.Role;
import com.wanted.util.AddrUtil;
import com.wanted.util.DataHolder;
import com.wanted.util.DialogUtil;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;

public class  AddPostActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editDesc;
    private Button btnClear;
    private Button btnSubmit;
    private Spinner spnMjr;

    private String title;
    private String desc;
    private String major;

    private AddPostTask addPostTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_add_post);

        findViews();
        addListeners();
    }

    private void findViews() {
        editTitle = (EditText) findViewById(R.id.add_post_edit_title);
        editDesc = (EditText) findViewById(R.id.add_post_edit_desc);
        btnClear = (Button) findViewById(R.id.add_post_btn_clear);
        btnSubmit = (Button) findViewById(R.id.add_post_btn_submit);
        spnMjr = (Spinner) findViewById(R.id.add_post_spinner_major);
    }

    private void addListeners() {
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTitle.setText("");
                editDesc.setText("");
                spnMjr.setSelection(0, false);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSendPost();
            }
        });
    }

    private void doSendPost() {
        getFormValue();
        boolean cancel = formValid();
        if (cancel)
            return;

        // submit recruitment
        addPostTask = new AddPostTask();
        addPostTask.execute((Void) null);
    }

    private void getFormValue() {
        title = editTitle.getText().toString();
        desc = editDesc.getText().toString();
        major = spnMjr.getSelectedItem().toString();
    }

    /**
     * Checks whether the form inputs are valid
     * @return
     */
    private boolean formValid() {
        boolean cancel = false;

        // Check for a valid email address.
        if (TextUtils.isEmpty(title)) {
            editTitle.requestFocus();
            editTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_sign, 0);
            Toast.makeText(AddPostActivity.this, "Title should not be empty", Toast.LENGTH_LONG).show();
            cancel = true;
        } else if (TextUtils.isEmpty(desc)) {
            editDesc.requestFocus();
            editTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            editDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_sign, 0);
            Toast.makeText(AddPostActivity.this, "Description should not be empty", Toast.LENGTH_LONG).show();
            cancel = true;
        }

        return cancel;
    }

    /**
     *
     */
    public class AddPostTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        AddPostTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Show the spinner and disable interaction
            pd = new DialogUtil().showProgress(AddPostActivity.this, "Processing...");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // If there's no account registered, register the new account here.
            //DefaultSocketClient client = new DefaultSocketClient("10.0.0.9", 8888);
            //response = client.sendToServer(packData());
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            URL url = null;
            try {
                url = new URL(new AddrUtil().getAddress("AddPost"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null)
                return false;
            HttpClient client = new HttpClient(url);
            response = client.sendToServer(packData());
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            addPostTask = null;

            if (response == null) {
                new DialogUtil().showError(AddPostActivity.this, "Unable to post.");
            } else {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            addPostTask = null;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Pack packData() {
        Post addPost = new Post(title, desc, major);
        addPost.setUid(DataHolder.getInstance().getUser().getId());
        Pack ret = new Pack(Information.ADD_POST, addPost);

        return ret;
    }
}
