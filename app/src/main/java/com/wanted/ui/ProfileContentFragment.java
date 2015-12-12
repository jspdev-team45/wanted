package com.wanted.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Fragment to show user profile
 */
public class ProfileContentFragment extends Fragment {
    private Context context;
    private User user;
    private int role = -1;

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
    private ImageView imgBanner;
    private ImageView imgAvatar;

    GetProfileTask getProfileTask = null;

    public ProfileContentFragment() {
        // Required empty public constructor
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_content, container, false);
        getRole();
        findViews(view);
        initViews();
        return view;
    }

    private void getRole() {
        user = DataHolder.getInstance().getUser();
        if (user != null) {
            role = user.getRole();
        }
    }

    private void findViews(View view) {
        imgAvatar = (ImageView) ((Activity)context).findViewById(R.id.profile_backdrop);
        seekerBasicInfo = (CardView) view.findViewById(R.id.profile_basic_content_seeker);
        recruiterBasicInfo = (CardView) view.findViewById(R.id.profile_basic_content_recruiter);
        companyInfo = (CardView) view.findViewById(R.id.profile_company_content_card);
        otherInfo = (CardView) view.findViewById(R.id.profile_other_content_card);

        // if user is a seeker
        if (role == Role.SEEKER) {
            textName = (TextView) view.findViewById(R.id.profile_seeker_name);
            textCollege = (TextView) view.findViewById(R.id.profile_seeker_college);
            textMajor = (TextView) view.findViewById(R.id.profile_seeker_major);
        }
        // if user is a recruiter
        if (role == Role.RECRUITER) {
            textName = (TextView) view.findViewById(R.id.profile_recruiter_name);
            textDepart = (TextView) view.findViewById(R.id.profile_recruiter_department);
            textCompany = (TextView) view.findViewById(R.id.profile_company_name);
            textCompanyLoc = (TextView) view.findViewById(R.id.profile_company_location);
            textCompanyDesc = (TextView) view.findViewById(R.id.profile_company_desc);
            imgBanner = (ImageView) view.findViewById(R.id.profile_company_banner);
        }
        textEmail = (TextView) view.findViewById(R.id.profile_email);
        textPhone = (TextView) view.findViewById(R.id.profile_phone);
    }

    private void initViews() {
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
        }
        updateProfile();
    }

    public void updateProfile() {
        user = DataHolder.getInstance().getUser();
        Company company = DataHolder.getInstance().getCompany();

        if (user.getAvatar() != null) {
            String avatarAddr = new AddrUtil().getImageAddress(user.getAvatar());
            Glide.with(context).load(avatarAddr)
                    .placeholder(R.drawable.avatar_placeholder)
                    .centerCrop()
                    .dontAnimate()
                    .into(imgAvatar);
        }
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
            if (((Recruiter) user).getCompanyID() != -1) {
                getProfileTask = new GetProfileTask();
                getProfileTask.execute((Void) null);
            }
            else {
                company = new Company();
                setText(textCompany, company.getName());
                setText(textCompanyLoc, company.getLocation());
                setText(textCompanyDesc, company.getDescription());
            }
        }
    }

    private void setText(TextView targetView, String text) {
        if (text == null)
            targetView.setText("Not Set");
        else
            targetView.setText(text);
    }

    /**
     *
     */
    public class GetProfileTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        GetProfileTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Show the spinner and disable interaction
            pd = new DialogUtil().showProgress(context, "Fetching information...");
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
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            getProfileTask = null;

            // Did not get response from server
            if (response == null || response.getInfo() == Information.FAIL)
                new DialogUtil().showError(context, "Unable to fetch profile information.");
            else {
                DataHolder.getInstance().setCompany((Company) response.getContent());
                Company company = (Company) response.getContent();
                setText(textCompany, company.getName());
                setText(textCompanyLoc, company.getLocation());
                setText(textCompanyDesc, company.getDescription());
                if (company.getBanner() != null) {
                    String bannerAddr = new AddrUtil().getImageAddress(company.getBanner());
                    Glide.with(context).load(bannerAddr)
                            .placeholder(R.drawable.banner_placeholder)
                            .dontAnimate()
                            .into(imgBanner);
                }
            }
        }

        @Override
        protected void onCancelled() {
            getProfileTask = null;
        }
    }

    private Pack packData() {
        Integer companyID = ((Recruiter) user).getCompanyID();
        return new Pack(Information.GET_COMPANY, companyID);
    }
}
