package com.wanted.ui;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wanted.R;

/**
 * Created by xlin2
 */
public class MyProfileActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView avatarView;
    private FloatingActionButton editFab;
    private FloatingActionButton saveFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_my_profile);

        findViews();
        initViews();
        addListeners();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.profile_collapsing_toolbar);
        avatarView = (ImageView) findViewById(R.id.profile_backdrop);
        editFab = (FloatingActionButton) findViewById(R.id.profile_fab_edit);
        saveFab = (FloatingActionButton) findViewById(R.id.profile_fab_save);
    }

    private void initViews() {
        // set action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // collapsing tool bar
        collapsingToolbar.setTitle("My Profile");

        // load backdrop image
        loadBackdrop();

        // set fragment
        ProfileContentFragment cFrag = new ProfileContentFragment();
        getFragmentManager().beginTransaction().add(R.id.profile_container, cFrag).commit();
    }

    private void loadBackdrop() {
        Glide.with(this).load(R.drawable.avatar).centerCrop().into(avatarView);
    }

    private void addListeners() {
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEdit();
            }
        });

        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToContent();
            }
        });
    }

    private void switchToEdit() {
        editFab.setVisibility(View.GONE);
        saveFab.setVisibility(View.VISIBLE);
        ProfileEditFragment eFrag = new ProfileEditFragment();
        getFragmentManager().beginTransaction().replace(R.id.profile_container, eFrag).commit();
    }

    private void switchToContent() {
        editFab.setVisibility(View.VISIBLE);
        saveFab.setVisibility(View.GONE);
        ProfileContentFragment cFrag = new ProfileContentFragment();
        getFragmentManager().beginTransaction().replace(R.id.profile_container, cFrag).commit();
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
