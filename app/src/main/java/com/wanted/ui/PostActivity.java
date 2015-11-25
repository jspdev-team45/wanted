package com.wanted.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.wanted.R;
import com.wanted.entities.Recruiter;
import com.wanted.util.DataHolder;

public class PostActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton addPostFab;

    private Recruiter recruiter = (Recruiter)(DataHolder.getInstance().getUser());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_post);

        findViews();
        initViews();
        addListeners();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addPostFab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recruiter.setCompany(2);
    }

    private void addListeners() {
        addPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recruiter.getCompany() == null || recruiter.getCompany() == -1)
                    Snackbar.make(view, getString(R.string.no_profile_error), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else {
                    Intent intent = new Intent(PostActivity.this, AddPostActivity.class);
                    startActivity(intent);
                }
            }
        });
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
}
