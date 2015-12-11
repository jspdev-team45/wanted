package com.wanted.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wanted.R;
import com.wanted.entities.Company;
import com.wanted.entities.Information;
import com.wanted.entities.Pack;
import com.wanted.entities.Recruiter;
import com.wanted.entities.Role;
import com.wanted.entities.Seeker;
import com.wanted.entities.User;
import com.wanted.util.AddrUtil;
import com.wanted.util.DataHolder;
import com.wanted.util.DialogUtil;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xlin2
 */
public class PeopleDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView imageAvatar;

    private CardView seekerBasicInfo;
    private CardView recruiterBasicInfo;
    private CardView companyInfo;
    private CardView otherInfo;

    private TextView textName;
    private TextView textCollege;
    private TextView textMajor;
    private TextView textDepart;
    private TextView textEmail;
    private TextView textPhone;
    private TextView textCompany;
    private TextView textCompanyLoc;
    private TextView textCompanyDesc;
    private ImageView imageBanner;

    private User user;
    private Company company;
    private int role;
    private GetCompanyTask getCompanyTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_people_detail);

        getUserData();
        findViews();
        initViews();
    }

    private void getUserData() {
        user = (User) getIntent().getExtras().get("user");
        role = user.getRole();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.people_detail_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.people_detail_collapsing_toolbar);
        imageAvatar = (ImageView) findViewById(R.id.people_detail_backdrop);
        seekerBasicInfo = (CardView) findViewById(R.id.detail_basic_info_seeker);
        recruiterBasicInfo = (CardView) findViewById(R.id.detail_basic_info_recruiter);
        companyInfo = (CardView) findViewById(R.id.detail_company_content_card);
        otherInfo = (CardView) findViewById(R.id.detail_other_info_card);

        // if user is a seeker
        if (role == Role.SEEKER) {
            textName = (TextView) findViewById(R.id.detail_seeker_name);
            textCollege = (TextView) findViewById(R.id.detail_seeker_college);
            textMajor = (TextView) findViewById(R.id.detail_seeker_major);
        }
        // if user is a recruiter
        if (role == Role.RECRUITER) {
            textName = (TextView) findViewById(R.id.detail_recruiter_name);
            textDepart = (TextView) findViewById(R.id.detail_recruiter_department);
            textCompany = (TextView) findViewById(R.id.detail_company_name);
            textCompanyLoc = (TextView) findViewById(R.id.detail_company_location);
            textCompanyDesc = (TextView) findViewById(R.id.detail_company_desc);
            imageBanner = (ImageView) findViewById(R.id.detail_company_banner);
        }
        textEmail = (TextView) findViewById(R.id.detail_email);
        textPhone = (TextView) findViewById(R.id.detail_phone);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar.setTitle(user.getName());

        loadBackdrop();

        if (role == Role.SEEKER) {
            seekerBasicInfo.setVisibility(View.VISIBLE);
            recruiterBasicInfo.setVisibility(View.GONE);
            otherInfo.setVisibility(View.VISIBLE);
            companyInfo.setVisibility(View.GONE);
        }
        else {
            seekerBasicInfo.setVisibility(View.GONE);
            recruiterBasicInfo.setVisibility(View.VISIBLE);
            otherInfo.setVisibility(View.GONE);
            companyInfo.setVisibility(View.VISIBLE);
            if (((Recruiter) user).getCompanyID() != -1) {
                getCompanyTask = new GetCompanyTask();
                getCompanyTask.execute((Void) null);
            }
        }

        showInformation();
    }

    private void loadBackdrop() {
        if (user.getAvatar() != null) {
            String avatarAddr = new AddrUtil().getImageAddress(user.getAvatar());
            Glide.with(PeopleDetailActivity.this).load(avatarAddr).into(imageAvatar);
        }
        //Glide.with(this).load(R.drawable.people_john).centerCrop().into(imageAvatar);
    }

    private void showInformation() {
        setText(textName, user.getRealName());
        setText(textEmail, user.getEmail());
        setText(textPhone, user.getPhone());

        if (role == Role.SEEKER) {
            Seeker seeker = (Seeker) user;
            setText(textCollege, seeker.getCollege());
            setText(textMajor, seeker.getMajor());
        }
        else if (role == Role.RECRUITER) {
            Recruiter recruiter = (Recruiter) user;
            setText(textDepart, recruiter.getDepartment());
            if (company == null) company = new Company();
            setCompanyInfo();
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

    /**
     *
     */
    public class GetCompanyTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        GetCompanyTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Show the spinner and disable interaction
            pd = new DialogUtil().showProgress(PeopleDetailActivity.this, "Fetching information...");
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                                 WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(new AddrUtil().getAddress("GetCompany"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null) {
                return false;
            }

            HttpClient client = new HttpClient(url);
            response = client.sendToServer(packData());
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            getCompanyTask = null;

            //
            if (response == null || response.getInfo() == Information.FAIL)
                new DialogUtil().showError(PeopleDetailActivity.this, "Unable to fetch company information.");
            else {
                company = (Company) response.getContent();
                setCompanyInfo();
            }
        }

        @Override
        protected void onCancelled() {
            getCompanyTask = null;
        }
    }

    private void setCompanyInfo() {
        setText(textCompany, company.getName());
        setText(textCompanyLoc, company.getLocation());
        setText(textCompanyDesc, company.getDescription());
        if (company.getBanner() != null) {
            String bannerAddr = new AddrUtil().getImageAddress(company.getBanner());
            Glide.with(PeopleDetailActivity.this).load(bannerAddr).into(imageBanner);
        }
    }

    private Pack packData() {
        Integer companyID = ((Recruiter) user).getCompanyID();
        return new Pack(Information.GET_COMPANY, companyID);
    }

}
