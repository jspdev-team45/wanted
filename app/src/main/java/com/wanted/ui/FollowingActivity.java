package com.wanted.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.wanted.R;
import com.wanted.list.Constants;
import com.wanted.list.ListAdapter;
import com.wanted.list.SideBar;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/6
 */
public class FollowingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_follow);
        final ListAdapter adapter = new ListAdapter(this, new Constants().followingArray);
        final ListView listView = (ListView) findViewById(R.id.list_follow);
        listView.setAdapter(adapter);
        SideBar sideBar = (SideBar) findViewById(R.id.follow_sidebar);
        TextView dialog = (TextView) findViewById(R.id.follow_dialog);
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
