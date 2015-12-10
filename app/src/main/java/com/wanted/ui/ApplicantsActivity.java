package com.wanted.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.squareup.picasso.RequestCreator;
import com.wanted.R;
import com.wanted.entities.Information;
import com.wanted.entities.Pack;
import com.wanted.entities.Seeker;
import com.wanted.util.AddrUtil;
import com.wanted.util.DialogUtil;
import com.wanted.util.ResizeUtil;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ApplicantsActivity extends AppCompatActivity {
    private MaterialListView applicantsListView;

    private ArrayList<Seeker> applicantsList;

    private ApplicantsTask applicantsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_applicants);

        findViews();
        addListeners();
        fetchData();
    }

    private void findViews() {
        applicantsListView = (MaterialListView) findViewById(R.id.applicants_listview);
    }

    private void addListeners() {
    }

    private void fetchData() {
        applicantsTask = new ApplicantsTask();
        applicantsTask.execute((Void) null);
    }

    private void addCards() {
        if (applicantsList == null) return;

        int len = applicantsList.size();
        for (int i = 0; i < len; ++i) {
            String addr = new AddrUtil().getImageAddress(applicantsList.get(i).getAvatar());
            Card card = new Card.Builder(ApplicantsActivity.this)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(applicantsList.get(i).getName())
                    .setTitleColor(Color.BLACK)
                    .setDescription(applicantsList.get(i).getEmail())
                    .setDrawable(addr)
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull final RequestCreator requestCreator) {
                            int[] size = new ResizeUtil(ApplicantsActivity.this).resizePeople();
                            requestCreator.resize(size[0], size[1]).centerCrop();
                        }
                    })
                    .addAction(R.id.left_text_button, new TextViewAction(ApplicantsActivity.this)
                            .setText("VIEW")
                            .setTextColor(Color.BLUE)
                            .setListener(viewListener))
                    .endConfig()
                    .build();
            applicantsListView.getAdapter().add(card);
        }
    }

    private OnActionClickListener viewListener = new OnActionClickListener() {
        @Override
        public void onActionClicked(View view, Card card) {
            Intent intent = new Intent(ApplicantsActivity.this, PeopleDetailActivity.class);
            intent.putExtra("user", applicantsList.get(applicantsListView.getAdapter().getPosition(card)));
            startActivity(intent);
        }
    };

    public class ApplicantsTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        ApplicantsTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Disable interaction
            pd = new DialogUtil().showProgress(ApplicantsActivity.this, "Loading applicants list...");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(new AddrUtil().getAddress("GetApply"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null) {
                return null;
            }

            int pid = getIntent().getIntExtra("pid", -1);
            if (pid == -1) return false;
            HttpClient client = new HttpClient(url);
            response = client.sendToServer(new Pack(Information.GET_APPLY, pid));
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            applicantsTask = null;

            // Get usr array
            if (response == null) {
                Toast.makeText(ApplicantsActivity.this, "Unable to fetch.", Toast.LENGTH_SHORT).show();
                return;
            }
            applicantsList = (ArrayList<Seeker>) response.getContent();

            // Update ui
            applicantsListView.getAdapter().clearAll();
            addCards();
            applicantsListView.scrollToPosition(0);

        }

        @Override
        protected void onCancelled() {
            applicantsTask = null;
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

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
