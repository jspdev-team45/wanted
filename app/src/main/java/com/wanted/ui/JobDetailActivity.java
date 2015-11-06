package com.wanted.ui;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wanted.R;

public class JobDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        findViews();
        initViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.job_detail_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.job_detail_collapsing_toolbar);
        imageView = (ImageView) findViewById(R.id.job_detail_backdrop);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar.setTitle("Google");

        loadBackdrop();
    }

    private void loadBackdrop() {
        Glide.with(this).load(R.drawable.google).centerCrop().into(imageView);
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
