package com.wanted.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.wanted.R;
import com.wanted.list.Follower;
import com.wanted.list.FollowerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/6
 */
public class FollowerActivity extends AppCompatActivity {
    private List<Follower> followerList = new ArrayList<Follower>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_follower);
        initFollower();
        FollowerAdapter adapter = new FollowerAdapter(FollowerActivity.this,
                R.layout.follower_item, followerList);
        ListView listView = (ListView) findViewById(R.id.list_follower);
        listView.setAdapter(adapter);
    }

    private void initFollower() {
        Follower rose = new Follower("Rose", R.drawable.people_rose);
        followerList.add(rose);
        Follower tom = new Follower("Tom", R.drawable.people_tom);
        followerList.add(tom);
        Follower keith = new Follower("Keith", R.drawable.people_keith);
        followerList.add(keith);
        Follower joan = new Follower("Joan", R.drawable.people_joan);
        followerList.add(joan);
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