package com.wanted.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.wanted.R;
import com.wanted.entities.Company;
import com.wanted.entities.Information;
import com.wanted.entities.Pack;
import com.wanted.entities.Post;
import com.wanted.entities.Recruiter;
import com.wanted.entities.Role;
import com.wanted.entities.Seeker;
import com.wanted.entities.User;
import com.wanted.util.AddrUtil;
import com.wanted.util.DataHolder;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by xlin2
 */
public class JobFragment extends Fragment {
    private Context context;

    private RefreshTask refreshTask;
    private LoadTask loadTask;
    private ConnectTask connectTask;

    private MaterialListView jobListView;
    private PullRefreshLayout jobRefreshLayout;
    private LinearLayoutManager jobLayoutManager;

    private ArrayList<Post> postList;
    private int preLen = 0;
    private int cursor = -1;
    private int targetId;
    private String targetServlet;

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
        doTheRest();
        return view;
    }

    protected void doTheRest() {
        if (DataHolder.getInstance().getUser().getRole() == Role.SEEKER &&
                ((Seeker) DataHolder.getInstance().getUser()).getMajor() != null) {
            initViews();
            addListeners();
        }
        else {
            Toast.makeText(context, getString(R.string.no_profile_error), Toast.LENGTH_LONG).show();
        }
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
            }

            @Override
            public void onItemLongClick(Card card, int position) {
                Intent intent = new Intent(getActivity(), JobDetailActivity.class);
                intent.putExtra("post", postList.get(position));
                startActivity(intent);
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
        if (postList == null) return;

        int len = postList.size();
        for (int i = preLen; i < len; ++i) {
            String addr = new AddrUtil().getImageAddress(postList.get(i).getCompany().getBanner());
            Card card = new Card.Builder(getActivity())
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_image_with_buttons_card)
                    .setTitle(postList.get(i).getCompany().getName())
                    .setTitleColor(Color.WHITE)
                    .setDescription(postList.get(i).getDescription())
                    .setDrawable(addr)
                    .addAction(R.id.right_text_button, new TextViewAction(getActivity())
                            .setText("Apply")
                            .setTextColor(Color.BLUE)
                            .setListener(applyListener))
                    .addAction(R.id.left_text_button, new TextViewAction(getActivity())
                            .setText("Like")
                            .setTextColor(Color.BLACK)
                            .setListener(likeListener))
                    .endConfig()
                    .build();
            jobListView.getAdapter().add(card);
        }
    }

    private OnActionClickListener applyListener = new OnActionClickListener() {
        @Override
        public void onActionClicked(View view, Card card) {
            targetId = postList.get(jobListView.getAdapter().getPosition(card)).getPid();
            targetServlet = "AddApply";
            connectTask = new ConnectTask();
            connectTask.execute((Void) null);
        }
    };

    private OnActionClickListener likeListener = new OnActionClickListener() {
        @Override
        public void onActionClicked(View view, Card card) {
            targetId = postList.get(jobListView.getAdapter().getPosition(card)).getPid();
            targetServlet = "AddLike";
            connectTask = new ConnectTask();
            connectTask.execute((Void) null);
        }
    };

    public class RefreshTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;

        RefreshTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            cursor = -1;
            response = getResponse();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            refreshTask = null;

            // Get job array
            if (postList != null) postList.clear();
            postList = (ArrayList<Post>) response.getContent();
            preLen = 0;

            // Update ui
            jobListView.getAdapter().clearAll();
            addCards();
            jobListView.scrollToPosition(0);
            jobRefreshLayout.setRefreshing(false);

            // Initialize loading variables
            previousTotal = 0;
            loading = true;
            int size = postList.size();
            cursor = size == 0 ? 0 : postList.get(size - 1).getPid();
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
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            response = getResponse();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loadTask = null;

            ArrayList<Post> tempList = (ArrayList<Post>) response.getContent();
            preLen = postList.size();
            postList.addAll(tempList);

            // Update ui
            addCards();
            jobListView.scrollToPosition(lastVisibleItem + 1);
            if (tempList.size() <= 0)
                Toast.makeText(context, "All data has been fetched", Toast.LENGTH_LONG).show();

            //
            int size = postList.size();
            cursor = size == 0 ? 0 : postList.get(size - 1).getPid();
        }

        @Override
        protected void onCancelled() {
            loadTask = null;
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    public class ConnectTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;

        ConnectTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = null;
                try {
                    url = new URL(new AddrUtil().getAddress(targetServlet));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (url == null) {
                    return null;
                }

                int uid = DataHolder.getInstance().getUser().getId();
                HttpClient client = new HttpClient(url);
                response = client.sendToServer(new Pack(Information.ADD_CONNECT, uid + ":" + targetId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            connectTask = null;

            Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            connectTask = null;
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private Pack getResponse() {
        URL url = null;
        try {
            url = new URL(new AddrUtil().getAddress("GetPost"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }

        HttpClient client = new HttpClient(url);
        String major = ((Seeker) DataHolder.getInstance().getUser()).getMajor();
        Pack response = client.sendToServer(new Pack(Information.GET_POST, major + ":" + cursor));
        return response;
    }
}
