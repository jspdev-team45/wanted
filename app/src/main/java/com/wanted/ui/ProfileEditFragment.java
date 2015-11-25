package com.wanted.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.wanted.R;
import com.wanted.entities.Pack;
import com.wanted.entities.Role;
import com.wanted.entities.User;
import com.wanted.util.DataHolder;
import com.wanted.util.DialogUtil;
import com.wanted.ws.local.ChangePhotoService;

/**
 *
 */
public class ProfileEditFragment extends Fragment {
    private Context context;

    private User user;
    private int role = -1;

    private CardView seekerBasicInfo;
    private CardView recruiterBasicInfo;
    private CardView companyInfo;
    private CardView otherInfo;

    private EditText editName;
    private Spinner editCollege;
    private Spinner editMajor;
    private Spinner editDepart;
    private EditText editPhone;
    private EditText editCompany;
    private EditText editCompanyLoc;
    private EditText editCompanyDesc;
    private ImageView editBanner;

    String name;
    String college;
    String major;
    String depart;
    String phone;
    String company;
    String companyLoc;
    String companyDesc;

    private ProfileTask profileTask = null;

    public ProfileEditFragment() {
        // Required empty public constructor
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        getRole();
        findViews(view);
        initViews();
        addListeners();
        return view;
    }

    private void getRole() {
        user = DataHolder.getInstance().getUser();
        if (user != null) {
            role = user.getRole();
        }
    }

    private void findViews(View view) {
        seekerBasicInfo = (CardView) view.findViewById(R.id.profile_basic_info_seeker);
        recruiterBasicInfo = (CardView) view.findViewById(R.id.profile_basic_info_recruiter);
        companyInfo = (CardView) view.findViewById(R.id.profile_company_info_card);
        otherInfo = (CardView) view.findViewById(R.id.profile_other_info_card);

        // if user is a seeker
        if (role == Role.SEEKER) {
            editName = (EditText) view.findViewById(R.id.profile_edit_seeker_name);
            editCollege = (Spinner) view.findViewById(R.id.profile_edit_seeker_college);
            editMajor = (Spinner) view.findViewById(R.id.profile_edit_seeker_major);
        }
        // if user is a recruiter
        if (role == Role.RECRUITER) {
            editName = (EditText) view.findViewById(R.id.profile_edit_recruiter_name);
            editDepart = (Spinner) view.findViewById(R.id.profile_edit_recruiter_department);
            editCompany = (EditText) view.findViewById(R.id.profile_edit_company_name);
            editCompanyLoc = (EditText) view.findViewById(R.id.profile_edit_company_location);
            editCompanyDesc = (EditText) view.findViewById(R.id.profile_edit_company_desc);
            editBanner = (ImageView) view.findViewById(R.id.profile_edit_company_banner);
        }
        editPhone = (EditText) view.findViewById(R.id.profile_edit_phone);
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

    private void addListeners() {
        if (role == Role.RECRUITER) {
            editBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangePhotoService changePhoto = ChangePhotoService.getInstance();
                    changePhoto.setContext(context);
                    changePhoto.setTargetView(editBanner);
                    changePhoto.openImageIntent();
                }
            });
        }
    }

    public boolean saveChange() {
        boolean cancel;
        // check form validation
        getFormValue();
        cancel= formValid();
        if (cancel)
            return false;

        // update user information
        profileTask = new ProfileTask();
        profileTask.execute((Void) null);
        return true;
    }

    /**
     *
     */
    private void getFormValue() {
        name = editName.getText().toString();
        phone = editPhone.getText().toString();
        if (role == Role.SEEKER) {
            college = editCollege.getSelectedItem().toString();
            major = editMajor.getSelectedItem().toString();
        }
        if (role == Role.RECRUITER) {
            depart = editDepart.getSelectedItem().toString();
            company = editCompany.getText().toString();
            companyLoc = editCompanyLoc.getText().toString();
            companyDesc = editCompanyDesc.getText().toString();
        }
    }

    /**
     * Checks whether the form inputs are valid
     * @return
     */
    private boolean formValid() {
        boolean cancel = false;

        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            editName.requestFocus();
            editName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_sign, 0);
            Toast.makeText(context, "Name should not be empty", Toast.LENGTH_LONG).show();
            cancel = true;
        } else if (TextUtils.isEmpty(phone)) {
            editPhone.requestFocus();
            editName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            editPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_sign, 0);
            Toast.makeText(context, "Phone should not be empty", Toast.LENGTH_LONG).show();
            cancel = true;
        } else if (role == Role.RECRUITER) {
            if (TextUtils.isEmpty(company)) {
                editPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                editCompany.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_sign, 0);
                Toast.makeText(context, "Company name should not be empty", Toast.LENGTH_LONG).show();
                cancel = true;
            } else if (TextUtils.isEmpty(companyLoc)) {
                editCompany.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                editCompanyLoc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_sign, 0);
                Toast.makeText(context, "Location should not be empty", Toast.LENGTH_LONG).show();
                cancel = true;
            } else if (TextUtils.isEmpty(companyDesc)) {
                editCompanyLoc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                editCompanyDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_sign, 0);
                Toast.makeText(context, "Description should not be empty", Toast.LENGTH_LONG).show();
                cancel = true;
            }
        }

        return cancel;
    }

    /**
     *
     */
    public class ProfileTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        ProfileTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Show the spinner and disable interaction
            pd = new DialogUtil().showProgress(context, "Processing...");
            ((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // If there's no account registered, register the new account here.
            //DefaultSocketClient client = new DefaultSocketClient("10.0.0.9", 8888);
            //response = client.sendToServer(packData());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            profileTask = null;

        }

        @Override
        protected void onCancelled() {
            profileTask = null;
        }

    }

}
