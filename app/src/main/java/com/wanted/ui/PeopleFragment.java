package com.wanted.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.wanted.entities.Pack;
import com.wanted.util.ResizeUtil;

/**
 * Created by xlin2
 */
public class PeopleFragment extends Fragment {
    private Context context;

    private RefreshTask refreshTask;
    private LoadTask loadTask;

    private MaterialListView peopleListView;
    private PullRefreshLayout peopleRefreshLayout;
    private LinearLayoutManager peopleLayoutManager;

    private int[] names = new int[]{R.string.people_john, R.string.people_tom, R.string.people_mike};
    private int[] schools = new int[]{R.string.school_john, R.string.school_tom, R.string.school_mike};
    private int[] drawables = new int[]{R.drawable.people_john,
                                           R.drawable.people_tom,
                                           R.drawable.people_mike};
    private boolean loading = true;
    private int previousTotal = 0;
    private int totalItemCount;
    private int lastVisibleItem;

    public PeopleFragment() {
        // Required empty public constructor
    }

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
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        findViews(view);
        initViews();
        addCards();
        addListeners();
        return view;
    }

    private void findViews(View view) {
        peopleListView = (MaterialListView) view.findViewById(R.id.people_listview);
        peopleRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.people_refresh_layout);
    }

    private void initViews() {
        peopleRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        peopleLayoutManager = new LinearLayoutManager(context);
        peopleListView.setLayoutManager(peopleLayoutManager);
        peopleListView.setDrawingCacheEnabled(true);
        firstRefresh();
    }

    private void firstRefresh() {
        peopleRefreshLayout.setRefreshing(true);
        refreshTask = new RefreshTask();
        refreshTask.execute((Void) null);
    }

    private void addListeners() {
        peopleListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(Card card, int position) {
                Intent intent = new Intent(getActivity(), PeopleDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Card card, int position) {
            }
        });

        peopleRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTask = new RefreshTask();
                refreshTask.execute((Void) null);
            }

        });

        peopleListView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = peopleLayoutManager.getItemCount();
                lastVisibleItem = peopleLayoutManager.findLastCompletelyVisibleItemPosition();

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
        for (int i = 0; i < 4; ++i) {
            int j = i % 3;
            Card card = new Card.Builder(getActivity())
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(names[j])
                    .setTitleColor(Color.BLACK)
                    .setDescription(schools[j])
                    .setDrawable(new ResizeUtil(getActivity()).resizePeople(drawables[j]))
                    .addAction(R.id.right_text_button, new TextViewAction(getActivity())
                            .setText("Chat")
                            .setTextColor(Color.BLUE)
                            .setListener(chatListener))
                    .addAction(R.id.left_text_button, new TextViewAction(getActivity())
                            .setText("Follow")
                            .setTextColor(Color.BLACK))
                    .endConfig()
                    .build();

            peopleListView.getAdapter().add(card);
        }
    }

    private OnActionClickListener chatListener = new OnActionClickListener() {
        @Override
        public void onActionClicked(View view, Card card) {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        }
    };

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
            peopleListView.getAdapter().clearAll();
            addCards();
            peopleListView.scrollToPosition(0);
            peopleRefreshLayout.setRefreshing(false);

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
            peopleListView.scrollToPosition(lastVisibleItem + 1);
        }

        @Override
        protected void onCancelled() {
            loadTask = null;
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }
}
