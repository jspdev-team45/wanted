package com.wanted.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

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
import com.wanted.util.ImageUtil;
import com.wanted.util.DataHolder;
import com.wanted.util.DialogUtil;
import com.wanted.ws.local.ChangePhotoService;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;

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
    private TextView editEmail;
    private EditText editPhone;
    private EditText editCompany;
    private EditText editCompanyLoc;
    private EditText editCompanyDesc;
    private ImageView editBanner;
    private ImageView editAvatar;

    String name;
    String college;
    String major;
    String depart;
    String phone;
    String companyName;
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
        editAvatar = (ImageView) ((Activity)context).findViewById(R.id.profile_backdrop);
        seekerBasicInfo = (CardView) view.findViewById(R.id.detail_basic_info_seeker);
        recruiterBasicInfo = (CardView) view.findViewById(R.id.detail_basic_info_recruiter);
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
        editEmail = (TextView) view.findViewById(R.id.profile_email);
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
        initViewText();
    }

    public void initViewText() {
        user = DataHolder.getInstance().getUser();
        Company company = DataHolder.getInstance().getCompany();

        setText(editName, user.getRealName());
        setText(editEmail, user.getEmail());
        setText(editPhone, user.getPhone());

        if (role == Role.RECRUITER) {
            if (company == null) company = new Company();
            setText(editCompany, company.getName());
            setText(editCompanyLoc, company.getLocation());
            setText(editCompanyDesc, company.getDescription());
            if (company.getBanner() != null) {
                String bannerAddr = new AddrUtil().getImageAddress(company.getBanner());
                Glide.with(context).load(bannerAddr)
                        .placeholder(R.drawable.banner_placeholder)
                        .dontAnimate()
                        .into(editBanner);
            }
        }
    }

    private void setText(View targetView, String text) {
        if (targetView.getClass().getSimpleName().equals("AppCompatTextView")) {
            if (text == null)
                ((TextView)targetView).setText("");
            else
                ((TextView)targetView).setText(text);
        }
        else if (targetView.getClass().getSimpleName().equals("AppCompatEditText")) {
            if (text == null)
                ((EditText)targetView).setText("");
            else
                ((EditText)targetView).setText(text);
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
            companyName = editCompany.getText().toString();
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
            if (TextUtils.isEmpty(companyName)) {
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
        private Boolean success;
        private ProgressDialog pd;

        ProfileTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Show the spinner and disable interaction
            try {
                pd = new DialogUtil().showProgress(context, "Updating profile...");
                ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (role == Role.RECRUITER) {
                    updateCompany();
                    if (success == false)
                        return false;
                }
                System.out.println("Update company finish.");
                updateUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            profileTask = null;

            try {
                //
                if (success == false || response == null || response.getInfo() == Information.FAIL) {
                    new DialogUtil().showError(context, "Unable to update.");
                } else {
                    DataHolder.getInstance().setUser((User) response.getContent());
                    DataHolder.getInstance().setProfileUpdated(true);
                    Fragment currentFragment = ((Activity) context).getFragmentManager().findFragmentById(R.id.profile_container);
                    ((ProfileContentFragment) currentFragment).updateProfile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            profileTask = null;
        }

        private void updateCompany() {
            URL url = null;
            try {
                url = new URL(new AddrUtil().getAddress("UpdateCompany"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null) {
                success = false;
                return;
            }
            HttpClient client = new HttpClient(url);
            response = client.sendToServer(packCompanyData());

            if (response == null || response.getInfo() == Information.FAIL) {
                success = false;
                return;
            }

            Company retCompany = (Company) response.getContent();
            DataHolder.getInstance().setCompany(retCompany);
            success = true;
        }

        private void updateUser() {
            URL url = null;
            try {
                url = new URL(new AddrUtil().getAddress("UpdateUser"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null) {
                success = false;
                return;
            }

            HttpClient client = new HttpClient(url);
            response = client.sendToServer(packUserData());
            success = true;
        }

    }

    private Pack packCompanyData() {
//        Bitmap banner = ((BitmapDrawable)editBanner.getDrawable()).getBitmap();
//        banner = Bitmap.createScaledBitmap(banner, editBanner.getMeasuredWidth(),
//                                           editBanner.getMeasuredHeight(), false);
        Drawable drawable = editBanner.getDrawable();
        Bitmap banner = new ImageUtil().convertToBitmap(drawable, editBanner.getMeasuredWidth(),
                editBanner.getMeasuredHeight());
        Company company = new Company();
        company.setName(companyName);
        company.setLocation(companyLoc);
        company.setDescription(companyDesc);

        Pack companyPack = new Pack(Information.UPDATE_COMPANY, company);
        companyPack.setImage(banner);

        return companyPack;
    }

    private Pack packUserData() {
        Drawable drawable = editAvatar.getDrawable();
        Bitmap avatar = new ImageUtil().convertToBitmap(drawable, editAvatar.getMeasuredWidth(),
                                                         editAvatar.getMeasuredHeight());
        Pack pack = null;
        if (role == Role.SEEKER) {
            Seeker seeker = new Seeker(user.getName(), user.getPassWord(), user.getEmail(), user.getRole());
            seeker.setId(user.getId());
            seeker.setRealName(name);
            seeker.setCollege(college);
            seeker.setMajor(major);
            seeker.setPhone(phone);
            pack = new Pack(Information.UPDATE_USER, seeker);
        }
        else if (role == Role.RECRUITER) {
            Recruiter recruiter = new Recruiter(user.getName(), user.getPassWord(), user.getEmail(), user.getRole());
            recruiter.setId(user.getId());
            recruiter.setRealName(name);
            recruiter.setCompanyID(DataHolder.getInstance().getCompany().getId());
            recruiter.setDepartment(depart);
            recruiter.setPhone(phone);
            pack = new Pack(Information.UPDATE_USER, recruiter);
        }
        pack.setImage(avatar);
        return pack;
    }

}
