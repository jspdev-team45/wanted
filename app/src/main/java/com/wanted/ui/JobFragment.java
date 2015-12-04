package com.wanted.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.wanted.R;
import com.wanted.entities.Pack;

/**
 * Created by xlin2
 */
public class JobFragment extends Fragment {
    private Context context;

    private RefreshTask refreshTask;
    private LoadTask loadTask;

    private MaterialListView jobListView;
    private PullRefreshLayout jobRefreshLayout;
    private LinearLayoutManager jobLayoutManager;

    private int[] companys = new int[]{R.string.company_google, R.string.company_facebook};
    private String[] descriptions = new String[]{"We are looking for a new software engineer",
                                                  "Looking for a front end engineer"};
    private int[] drawables = new int[]{R.drawable.google, R.drawable.facebook};
    private boolean loading = true;
    private int previousTotal = 0;
    private int totalItemCount;
    private int lastVisibleItem;

    public JobFragment() {}

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);
        findViews(view);
        initViews();
        addListeners();
        return view;
    }

    /**
     * Get ui objects
     * @param view
     */
    private void findViews(View view) {
        jobListView = (MaterialListView) view.findViewById(R.id.job_listview);
        jobRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.job_refresh_layout);
    }

    private void initViews() {
        jobRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        jobLayoutManager = new LinearLayoutManager(context);
        jobListView.setLayoutManager(jobLayoutManager);
        jobListView.setDrawingCacheEnabled(true);
        firstRefresh();
    }

    /**
     * Add cards to the empty card list
     */
    private void firstRefresh() {
        jobRefreshLayout.setRefreshing(true);
        refreshTask = new RefreshTask();
        refreshTask.execute((Void) null);
    }

    /**
     * Add listeners to ui objects
     */
    private void addListeners() {
        jobListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(Card card, int position) {
                Intent intent = new Intent(getActivity(), JobDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Card card, int position) {
            }
        });

        jobRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTask = new RefreshTask();
                refreshTask.execute((Void) null);
            }

        });

        jobListView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = jobLayoutManager.getItemCount();
                lastVisibleItem = jobLayoutManager.findLastCompletelyVisibleItemPosition();

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
        for (int i = 0; i < 3; ++i) {
            int j = i % 2;
            Card card = new Card.Builder(getActivity())
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_image_with_buttons_card)
                    .setTitle(companys[j])
                    .setTitleColor(Color.WHITE)
                    .setDescription(descriptions[j])
                    .setDrawable(drawables[j])
                    .addAction(R.id.right_text_button, new TextViewAction(getActivity())
                            .setText("Apply")
                            .setTextColor(Color.BLUE))
                    .addAction(R.id.left_text_button, new TextViewAction(getActivity())
                            .setText("Like")
                            .setTextColor(Color.BLACK))
                    .endConfig()
                    .build();

            jobListView.getAdapter().add(card);
        }
    }

    public class RefreshTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;

        RefreshTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Disable interaction
            ((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            refreshTask = null;

            // Update ui
            jobListView.getAdapter().clearAll();
            addCards();
            jobListView.scrollToPosition(0);
            jobRefreshLayout.setRefreshing(false);

            // Initialize loading variables
            previousTotal = 0;
            loading = true;
        }

        @Override
        protected void onCancelled() {
            refreshTask = null;
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
            Toast.makeText(context, "Loading more...", Toast.LENGTH_LONG).show();
            ((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loadTask = null;

            // Update ui
            addCards();
            jobListView.scrollToPosition(lastVisibleItem + 1);
        }

        @Override
        protected void onCancelled() {
            loadTask = null;
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

}
