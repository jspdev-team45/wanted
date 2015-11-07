package com.wanted.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.wanted.R;
import com.wanted.list.Following;
import com.wanted.list.FollowingAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/6
 */
public class FollowingActivity extends AppCompatActivity {
    private List<Following> followingList = new ArrayList<Following>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        initFollowing();
        FollowingAdapter adapter = new FollowingAdapter(FollowingActivity.this,
                R.layout.following_item, followingList);
        ListView listView = (ListView) findViewById(R.id.list_following);
        listView.setAdapter(adapter);
    }

    private void initFollowing() {
        Following mike = new Following("Mike", R.drawable.people_mike);
        followingList.add(mike);
        Following john = new Following("John", R.drawable.people_john);
        followingList.add(john);
        Following ann = new Following("Ann", R.drawable.people_ann);
        followingList.add(ann);
        Following mary = new Following("Mary", R.drawable.people_mary);
        followingList.add(mary);
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
