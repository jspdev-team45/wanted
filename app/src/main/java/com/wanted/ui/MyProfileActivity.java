package com.wanted.ui;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wanted.R;
import com.wanted.ws.local.ChangePhotoService;

/**
 * Created by xlin2
 */
public class MyProfileActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView avatarView;
    private ImageView targetView;
    private ImageView editBanner;
    private FloatingActionButton editFab;
    private FloatingActionButton saveFab;

    private ProfileEditFragment eFrag;
    private ProfileContentFragment cFrag;

    private ChangePhotoService changePhoto;

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

        // change photo service
        changePhoto = ChangePhotoService.getInstance();
        changePhoto.setContext(MyProfileActivity.this);
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
                boolean result = saveChange();
                if (result == false) return;
                switchToContent();
            }
        });
    }

    private void switchToEdit() {
        editFab.setVisibility(View.GONE);
        saveFab.setVisibility(View.VISIBLE);
        eFrag = new ProfileEditFragment();
        eFrag.setContext(MyProfileActivity.this);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoto.setTargetView(avatarView);
                changePhoto.openImageIntent();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.profile_container, eFrag).commit();

    }

    private boolean saveChange() {
        return eFrag.saveChange();
    }

    private void switchToContent() {
        editFab.setVisibility(View.VISIBLE);
        saveFab.setVisibility(View.GONE);
        cFrag = new ProfileContentFragment();
        avatarView.setOnClickListener(null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri = changePhoto.getImageFromResult(requestCode, resultCode, data);
        Glide.with(this).load(imageUri).centerCrop().into(changePhoto.getTargetView());
    }

}
