package com.wanted.ui;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wanted.R;
import com.wanted.entities.Company;
import com.wanted.entities.Post;
import com.wanted.entities.Recruiter;
import com.wanted.util.AddrUtil;

/**
 * Created by xlin2
 */
public class JobDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView imageView;

    private TextView textTitle;
    private TextView textDesc;
    private TextView textMajor;
    private TextView textCompany;
    private TextView textCompanyLoc;
    private TextView textName;
    private TextView textPhone;
    private TextView textEmail;

    Post post;
    Recruiter recruiter;
    Company company;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_job_detail);
        try {
            getPostData();
            findViews();
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPostData() {
        post = (Post) getIntent().getExtras().get("post");
        recruiter = post.getRecruiter();
        company = post.getCompany();
    }

    /**
     * Get ui objects
     */
    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.job_detail_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.job_detail_collapsing_toolbar);
        imageView = (ImageView) findViewById(R.id.job_detail_backdrop);

        textTitle = (TextView) findViewById(R.id.detail_post_title);
        textDesc = (TextView) findViewById(R.id.detail_post_desc);
        textMajor = (TextView) findViewById(R.id.detail_post_major);
        textCompany = (TextView) findViewById(R.id.detail_post_company);
        textCompanyLoc = (TextView) findViewById(R.id.detail_post_location);
        textName = (TextView) findViewById(R.id.detail_post_recruiter);
        textPhone = (TextView) findViewById(R.id.detail_post_phone);
        textEmail = (TextView) findViewById(R.id.detail_post_email);
    }

    /**
     * Do some initializtions
     */
    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar.setTitle("Job detail");

        loadBackdrop();

        setText(textTitle, post.getTitle());
        setText(textDesc, post.getDescription());
        setText(textMajor, post.getSuitMajor());
        setText(textCompany, company.getName());
        setText(textCompanyLoc, company.getLocation());
        setText(textName, recruiter.getName());
        setText(textPhone, recruiter.getPhone());
        setText(textEmail, recruiter.getEmail());
    }

    private void loadBackdrop() {
        if (company.getBanner() != null) {
            String bannerAddr = new AddrUtil().getImageAddress(company.getBanner());
            Glide.with(this).load(bannerAddr).into(imageView);
        }
    }

    private void setText(TextView targetView, String text) {
        if (text == null)
            targetView.setText("Not Set");
        else
            targetView.setText(text);
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
