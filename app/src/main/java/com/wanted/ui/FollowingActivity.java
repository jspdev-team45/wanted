package com.wanted.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.wanted.R;
import com.wanted.entities.Information;
import com.wanted.entities.Pack;
import com.wanted.entities.User;
import com.wanted.list.Constants;
import com.wanted.list.ListAdapter;
import com.wanted.list.SideBar;
import com.wanted.util.AddrUtil;
import com.wanted.util.DataHolder;
import com.wanted.util.DialogUtil;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/6
 */
public class FollowingActivity extends AppCompatActivity {

    private ListAdapter adapter;
    private ListView listView;
    private SideBar sideBar;
    private TextView dialog;

    private FetchTask fetchTask;

    private User user = DataHolder.getInstance().getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_follow);

        findViews();
        initViews();
        fetchData();
    }

    private void findViews() {
        listView = (ListView) findViewById(R.id.list_follow);
        sideBar = (SideBar) findViewById(R.id.follow_sidebar);
        dialog = (TextView) findViewById(R.id.follow_dialog);
    }

    private void initViews() {
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1)
                    listView.setSelection(position);
            }
        });
    }

    private void fetchData() {
        fetchTask = new FetchTask();
        fetchTask.execute((Void) null);
    }

    public class FetchTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        FetchTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Show the spinner and disable interaction
            pd = new DialogUtil().showProgress(FollowingActivity.this, "Fetch list...");
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = null;
                try {
                    url = new URL(new AddrUtil().getAddress("GetFollowing"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (url == null)
                    return false;
                HttpClient client = new HttpClient(url);
                response = client.sendToServer(new Pack(Information.GET_FOLLOWING, user.getId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            fetchTask = null;

            try {
                // Update ui
                List<User> users = (List<User>) response.getContent();
                adapter = new ListAdapter(FollowingActivity.this, users);
                listView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            fetchTask = null;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
