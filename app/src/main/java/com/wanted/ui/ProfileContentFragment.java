package com.wanted.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wanted.R;
import com.wanted.entities.Role;
import com.wanted.entities.User;
import com.wanted.util.DataHolder;

/**
 */
public class ProfileContentFragment extends Fragment {
    private Context context;
    private User user;
    private int role;

    private CardView seekerBasicInfo;
    private CardView recruiterBasicInfo;
    private CardView companyInfo;
    private CardView otherInfo;

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
        seekerBasicInfo = (CardView) view.findViewById(R.id.profile_basic_content_seeker);
        recruiterBasicInfo = (CardView) view.findViewById(R.id.profile_basic_content_recruiter);
        companyInfo = (CardView) view.findViewById(R.id.profile_company_content_card);
        otherInfo = (CardView) view.findViewById(R.id.profile_other_content_card);
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
    }
}
