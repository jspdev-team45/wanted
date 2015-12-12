package com.wanted.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListAdapter;
import com.dexafree.materialList.view.MaterialListView;
import com.squareup.picasso.RequestCreator;
import com.wanted.R;
import com.wanted.entities.Experience;
import com.wanted.entities.Information;
import com.wanted.entities.Pack;
import com.wanted.entities.Seeker;
import com.wanted.entities.User;
import com.wanted.util.AddrUtil;
import com.wanted.util.DataHolder;
import com.wanted.util.DialogUtil;
import com.wanted.util.ResizeUtil;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xlin2
 * Activity to show user resume
 */
public class ResumeActivity extends AppCompatActivity {

    private MaterialListView resume;
    private String[] cardTitle = { "Education", "Project", "Working" };
    private int cardNum = cardTitle.length;

    private ArrayList<TextView> inputStartDate = new ArrayList<TextView>();
    private ArrayList<TextView> inputEndDate = new ArrayList<TextView>();
    private ArrayList<TextView> inputDescription = new ArrayList<TextView>();
    private ArrayList<TextView> addBtn = new ArrayList<TextView>();
    private int startYear;
    private int startMonth;
    private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;
    private String startDate;
    private String endDate;
    private String description;
    private TextView targetStartView;
    private TextView targetEndView;
    private TextView targetDescView;
    private TableLayout targetTableLayout;
    private int exType;

    static final int START_DATE_DIALOG_ID = 0;
    static final int END_DATE_DIALOG_ID = 1;

    private AddExpTask addExpTask = null;
    private ShowExpTask showExpTask = null;

    private User user = DataHolder.getInstance().getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_resume);

        findViews();
        addCards();

    }

    private void findViews() {
        resume = (MaterialListView) findViewById(R.id.resume_basic_info);

    }

    /**
     * Add cards that include user resume information
     */
    private void addCards() {
        MaterialListAdapter adapter = resume.getAdapter();
        String name = user.getRealName() == null ? user.getName() : user.getRealName();
        String avatar = user.getAvatar() == null ? "default.jpg" : user.getAvatar();
        String avatarAddr = new AddrUtil().getImageAddress(avatar);
        String email = user.getEmail();
        String major = ((Seeker) user).getMajor() == null ? "Unknown major" : ((Seeker) user).getMajor();
        String college = ((Seeker) user).getCollege() == null ? "Unknown major" : ((Seeker) user).getCollege();
        String description = "COLLEGE: " + college + "\n" + "MAJOR: " + major + "\n" + "EMAIL: " + email;

        Card infoCard = new Card.Builder(this)
                .withProvider(new CardProvider())
                .setLayout(R.layout.my_image_with_buttons_card)
                .setTitle(name)
                .setTitleColor(Color.BLACK)
                .setDescription(description)
                .setDrawable(avatarAddr)
                .endConfig()
                .build();

        adapter.add(infoCard);

        for (int i=0; i<cardNum; ++i) {
            Card card = new Card.Builder(this)
                    .withProvider(new CardProvider<>())
                    .setLayout(R.layout.item_resume)
                    .setTitle(cardTitle[i])
                    .addAction(R.id.right_text_button, new TextViewAction(this)
                            .setText("Add")
                            .setTextColor(Color.BLUE)
                            .setListener(addListener))
                    .addAction(R.id.left_text_button, new TextViewAction(this)
                            .setText("Show")
                            .setTextColor(Color.BLUE)
                            .setListener(showListener))
                    .endConfig()
                    .build();

            adapter.add(card);

        }

    }

    private OnActionClickListener addListener = new OnActionClickListener() {
        @Override
        public void onActionClicked(View view, Card card) {
            bundleViews(view, card);
            doAddExperience();
        }
    };

    private OnActionClickListener showListener = new OnActionClickListener() {
        @Override
        public void onActionClicked(View view, Card card) {
            bundleViews(view, card);
            doShowExperience();
        }
    };

    public void bundleViews(View view, Card card) {
        exType = resume.getAdapter().getPosition(card) - 1;
        LinearLayout parentLayout = (LinearLayout) view.getParent().getParent();

        targetStartView = (TextView) parentLayout.findViewById(R.id.resume_start_date_textView);
        targetEndView = (TextView) parentLayout.findViewById(R.id.resume_end_date_textView);
        targetDescView = (TextView) parentLayout.findViewById(R.id.resume_description_textView);
        targetTableLayout = (TableLayout) parentLayout.findViewById(R.id.resume_table);
    }

    public void startListener(View v) {
        targetStartView = (TextView) v;
        final Calendar calendar = Calendar.getInstance();
        startYear = calendar.get(Calendar.YEAR);
        startMonth = calendar.get(Calendar.MONTH);
        startDay = calendar.get(Calendar.DAY_OF_MONTH);
        showDialog(START_DATE_DIALOG_ID);
    }

    public void endListener(View v) {
        targetEndView = (TextView) v;
        final Calendar calendar = Calendar.getInstance();
        endYear = calendar.get(Calendar.YEAR);
        endMonth = calendar.get(Calendar.MONTH);
        endDay = calendar.get(Calendar.DAY_OF_MONTH);
        showDialog(END_DATE_DIALOG_ID);
    }

    public void descListener(View v) {
        targetDescView = (TextView) v;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResumeActivity.this);
        alertDialog.setTitle("Description");
        alertDialog.setMessage("Enter Description");
        final EditText input = new EditText(ResumeActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(layoutParams);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                description = input.getText().toString();
                targetDescView.setText(description);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                description = null;
                targetDescView.setText("Input");
            }
        });

        alertDialog.show();
    }

    private void doAddExperience() {
        getFormValue();
        boolean cancel = formValid();
        if (cancel)
            return;

        // submit table
        addExpTask = new AddExpTask();
        addExpTask.execute((Void) null);
    }

    private void doShowExperience() {
        showExpTask = new ShowExpTask();
        showExpTask.execute((Void) null);
    }

    private void getFormValue() {
        startDate = targetStartView.getText().toString();
        endDate = targetEndView.getText().toString();
        description = targetDescView.getText().toString();
    }

    /**
     * Check whether user inputs are valid
     * @return
     */
    private boolean formValid() {
        boolean cancel = false;

        if (startDate.equals("Input date")) {
            Toast.makeText(ResumeActivity.this, "Start date should not be empty", Toast.LENGTH_LONG).show();
            cancel = true;
        } else if (endDate.equals("Input date")) {
            Toast.makeText(ResumeActivity.this, "End date should not be empty", Toast.LENGTH_LONG).show();
            cancel = true;
        } else if (startAfterEnd()) {
            Toast.makeText(ResumeActivity.this, "End date should be after start date", Toast.LENGTH_LONG).show();
            cancel = true;
        } else if (description.equals("Input")) {
            Toast.makeText(ResumeActivity.this, "Description should not be empty", Toast.LENGTH_LONG).show();
            cancel = true;
        }

        return cancel;
    }

    private boolean startAfterEnd() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date strStartDate = simpleDateFormat.parse(startDate);
            Date strEndDate = simpleDateFormat.parse(endDate);
            if (strStartDate.after(strEndDate)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("Date", "                 Illegal date!");
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this, startDatePickerListener,
                        startYear, startMonth, startDay);
            case END_DATE_DIALOG_ID:
                return new DatePickerDialog(this, endDatePickerListener,
                        endYear, endMonth, endDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startYear = year;
            startMonth = monthOfYear;
            startDay = dayOfMonth;

            targetStartView.setText(new StringBuilder()
                    .append(startYear).append("/")
                    .append(new DecimalFormat("00").format(startMonth + 1)).append("/")
                    .append(new DecimalFormat("00").format(startDay)));
        }


    };

    private DatePickerDialog.OnDateSetListener endDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            endYear = year;
            endMonth = monthOfYear;
            endDay = dayOfMonth;

            targetEndView.setText(new StringBuilder()
                    .append(endYear).append("/")
                    .append(new DecimalFormat("00").format(endMonth + 1)).append("/")
                    .append(new DecimalFormat("00").format(endDay)));
        }
    };

    private void createTableRow() {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
        targetTableLayout.setLayoutParams(layoutParams);

        TextView textViewSD = new TextView(this);
        textViewSD.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        textViewSD.setText(startDate);
        textViewSD.setGravity(Gravity.CENTER);
        TextView textViewED = new TextView(this);
        textViewED.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        textViewED.setText(endDate);
        textViewED.setGravity(Gravity.CENTER);
        TextView textViewDesc = new TextView(this);
        textViewDesc.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        textViewDesc.setText(description);
        textViewDesc.setGravity(Gravity.CENTER);

        tableRow.addView(textViewSD);
        tableRow.addView(textViewED);
        tableRow.addView(textViewDesc);

        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(2, 16, 2, 16);

        targetTableLayout.addView(tableRow, tableRowParams);
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

    private class AddExpTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        AddExpTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Show the spinner and disable interaction
            pd = new DialogUtil().showProgress(ResumeActivity.this, "Processing...");
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            URL url = null;
            try {
                url = new URL(new AddrUtil().getAddress("AddExperience"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null)
                return false;
            HttpClient client = new HttpClient(url);
            response = client.sendToServer(packData());
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            addExpTask = null;
            if (response == null) {
                new DialogUtil().showError(ResumeActivity.this, "Unable to post.");
            } else {
                createTableRow();
            }

            targetStartView.setText("Input date");
            targetEndView.setText("Input date");
            targetDescView.setText("Input");

            startDate = null;
            endDate = null;
            description = null;
        }

        @Override
        protected void onCancelled() {
            addExpTask = null;
        }

    }

    private class ShowExpTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        ShowExpTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Show the spinner and disable interaction
            pd = new DialogUtil().showProgress(ResumeActivity.this, "Processing...");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = null;
                try {
                    url = new URL(new AddrUtil().getAddress("GetExperience"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (url == null)
                    return false;
                HttpClient client = new HttpClient(url);
                response = client.sendToServer(new Pack(Information.RESUME, user.getId() + ":" + exType));
            } catch (Exception e ) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            addExpTask = null;

            try {
                if (response == null) {
                    new DialogUtil().showError(ResumeActivity.this, "Unable to fetch data.");
                } else {
                    targetTableLayout.removeAllViews();
                    ArrayList<Experience> exp = (ArrayList<Experience>) response.getContent();
                    int len = exp.size();
                    for (int i = 0; i < len; ++i) {
                        startDate = exp.get(i).getStartDate();
                        endDate = exp.get(i).getEndDate();
                        description = exp.get(i).getDescription();
                        createTableRow();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            addExpTask = null;
        }

    }

    private Pack packData() {
        Experience experience = new Experience();

        experience.setUserID(user.getId());
        experience.setExType((byte) exType);
        experience.setStartDate(startDate);
        experience.setEndDate(endDate);
        experience.setDescription(description);

        Pack ret = new Pack(Information.RESUME, experience);

        return ret;
    }
}
