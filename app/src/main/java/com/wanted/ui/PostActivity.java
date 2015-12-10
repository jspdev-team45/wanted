package com.wanted.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.wanted.R;
import com.wanted.entities.Company;
import com.wanted.entities.Information;
import com.wanted.entities.Pack;
import com.wanted.entities.Post;
import com.wanted.entities.Recruiter;
import com.wanted.entities.Role;
import com.wanted.entities.Seeker;
import com.wanted.util.AddrUtil;
import com.wanted.util.DataHolder;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton addPostFab;
    private RefreshTask refreshTask;
    private LoadTask loadTask;

    private MaterialListView postListView;
    private PullRefreshLayout postRefreshLayout;
    private LinearLayoutManager postLayoutManager;

    private ArrayList<Post> postList;
    private int preLen = 0;
    private int cursor = -1;

    private boolean loading = true;
    private int previousTotal = 0;
    private int totalItemCount;
    private int lastVisibleItem;

    private Recruiter recruiter = (Recruiter)(DataHolder.getInstance().getUser());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_post);

        try {
            findViews();
            initViews();
            addListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstRefresh();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addPostFab = (FloatingActionButton) findViewById(R.id.fab);
        postListView = (MaterialListView) findViewById(R.id.my_post_listview);
        postRefreshLayout = (PullRefreshLayout) findViewById(R.id.my_post_refresh_layout);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        postRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        postLayoutManager = new LinearLayoutManager(PostActivity.this);
        postListView.setLayoutManager(postLayoutManager);
        postListView.setDrawingCacheEnabled(true);
        firstRefresh();
    }

    /**
     * Add cards to the empty card list
     */
    private void firstRefresh() {
        postRefreshLayout.setRefreshing(true);
        refreshTask = new RefreshTask();
        refreshTask.execute((Void) null);
    }

    private void addListeners() {
        addPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recruiter.getCompanyID() == null || recruiter.getCompanyID() == -1)
                    Snackbar.make(view, getString(R.string.no_profile_error), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else {
                    Intent intent = new Intent(PostActivity.this, AddPostActivity.class);
                    startActivity(intent);
                }
            }
        });

        postRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTask = new RefreshTask();
                refreshTask.execute((Void) null);
            }

        });

        postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = postLayoutManager.getItemCount();
                lastVisibleItem = postLayoutManager.findLastCompletelyVisibleItemPosition();

                if (dy <= 0) return;

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && lastVisibleItem == totalItemCount - 1) {
                    // End has been reached, load more information
                    loadTask = new LoadTask();
                    loadTask.execute((Void) null);

                    loading = true;
                }
            }
        });
    }

    private void addCards() {
        if (postList == null) return;
        int len = postList.size();
        for (int i = preLen; i < len; ++i) {
            String addr = new AddrUtil().getImageAddress(postList.get(i).getCompany().getBanner());
            Card card = new Card.Builder(PostActivity.this)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_image_with_buttons_card)
                    .setTitle(postList.get(i).getTitle())
                    .setTitleColor(Color.WHITE)
                    .setDescription(postList.get(i).getDescription())
                    .setDrawable(addr)
                    .addAction(R.id.left_text_button, new TextViewAction(PostActivity.this)
                            .setText("SHOW APPLICANTS")
                            .setTextColor(Color.BLUE)
                            .setListener(applicantsListener))
                    .endConfig()
                    .build();
            postListView.getAdapter().add(card);
        }
    }

    private OnActionClickListener applicantsListener = new OnActionClickListener() {
        @Override
        public void onActionClicked(View view, Card card) {
            int pid = postList.get(postListView.getAdapter().getPosition(card)).getPid();
            Intent intent = new Intent(PostActivity.this, ApplicantsActivity.class);
            intent.putExtra("pid", pid);
            startActivity(intent);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class RefreshTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;

        RefreshTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Disable interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                cursor = -1;
                response = getResponse();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            refreshTask = null;

            try {
                // Get job array
                if (postList != null) postList.clear();
//            initPostList();
                postList = (ArrayList<Post>) response.getContent();
                preLen = 0;

                // Update ui
                postListView.getAdapter().clearAll();
                addCards();
                postListView.scrollToPosition(0);
                postRefreshLayout.setRefreshing(false);

                // Initialize loading variables
                previousTotal = 0;
                loading = true;
                int size = postList.size();
                cursor = size == 0 ? 0 : postList.get(size - 1).getPid();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            refreshTask = null;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    public class LoadTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;

        LoadTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Disable interaction
            Toast.makeText(PostActivity.this, "Loading more...", Toast.LENGTH_LONG).show();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            response = getResponse();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loadTask = null;

            ArrayList<Post> tempList = (ArrayList<Post>) response.getContent();
            preLen = postList.size();
            postList.addAll(tempList);

            // Update ui
            addCards();
            postListView.scrollToPosition(lastVisibleItem + 1);
            if (tempList.size() <= 0)
                Toast.makeText(PostActivity.this, "All data has been fetched", Toast.LENGTH_LONG).show();

            //
            int size = postList.size();
            cursor = size == 0 ? 0 : postList.get(size - 1).getPid();
        }

        @Override
        protected void onCancelled() {
            loadTask = null;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    private Pack getResponse() {
        URL url = null;
        try {
            url = new URL(new AddrUtil().getAddress("GetMyPost"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }

        HttpClient client = new HttpClient(url);
        Pack response = client.sendToServer(new Pack(Information.GET_POST, recruiter.getId() + ":" + cursor));
        return response;
    }
}
