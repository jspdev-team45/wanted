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
import android.support.annotation.NonNull;
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
import com.squareup.picasso.RequestCreator;
import com.wanted.R;
import com.wanted.entities.Information;
import com.wanted.entities.Pack;
import com.wanted.entities.Recruiter;
import com.wanted.entities.Role;
import com.wanted.entities.Seeker;
import com.wanted.entities.User;
import com.wanted.util.AddrUtil;
import com.wanted.util.DataHolder;
import com.wanted.util.ResizeUtil;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by xlin2
 */
public class PeopleFragment extends Fragment {
    private Context context;

    private RefreshTask refreshTask;
    private LoadTask loadTask;
    private FollowTask followTask;

    private MaterialListView peopleListView;
    private PullRefreshLayout peopleRefreshLayout;
    private LinearLayoutManager peopleLayoutManager;

    private ArrayList<User> peopleList;
    private int preLen = 0;
    private int cursor = -1;
    private int otherId;
    private Card targetCard;
    private int targetPos;
    private final int MAX_FETCH = 4;

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
        View view = null;
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_people, container, false);
            findViews(view);
            initViews();
//        addCards();
            addListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        //initPeopleList();
        firstRefresh();
    }

    /**
     * For debug use
     */
    private void initPeopleList() {
        peopleList = new ArrayList<User>();
        Seeker people1 = new Seeker("Alice", null, "alice@aa.com", Role.SEEKER);
        people1.setId(8);
        people1.setAvatar("people_ann.jpg");
        people1.setPhone("111111");
        people1.setCollege("Some college");
        people1.setMajor("Some major");
        peopleList.add(people1);

        Seeker people2 = new Seeker("John", null, "john@aa.com", Role.SEEKER);
        people2.setId(7);
        people2.setAvatar("people_john.jpg");
        people2.setPhone("22222222");
        people2.setCollege("Some college");
        people2.setMajor("Some major");
        peopleList.add(people2);

        Recruiter people3 = new Recruiter("Tom", null, "tom@aa.com", Role.RECRUITER);
        people3.setId(6);
        people3.setCompanyID(-1);
        people3.setAvatar("people_tom.jpg");
        people3.setPhone("333333333");
        peopleList.add(people3);

        Recruiter people4 = new Recruiter("Mike", null, "mike@aa.com", Role.RECRUITER);
        people4.setId(5);
        people4.setCompanyID(-1);
        people4.setAvatar("people_mike.jpg");
        people4.setPhone("44444444");
        peopleList.add(people4);
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
            }

            @Override
            public void onItemLongClick(Card card, int position) {
                Intent intent = new Intent(getActivity(), PeopleDetailActivity.class);
                intent.putExtra("user", peopleList.get(position));
                startActivity(intent);
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
        int len = peopleList.size();
        for (int i = preLen; i < len; ++i) {
            String addr = new AddrUtil().getImageAddress(peopleList.get(i).getAvatar());
            Card card = new Card.Builder(getActivity())
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(peopleList.get(i).getName())
                    .setTitleColor(Color.BLACK)
                    .setDescription(peopleList.get(i).getEmail())
                    .setDrawable(addr)
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull final RequestCreator requestCreator) {
                            int[] size = new ResizeUtil(context).resizePeople();
                            requestCreator.resize(size[0], size[1]).centerCrop();
                        }
                    })
                    .addAction(R.id.right_text_button, new TextViewAction(getActivity())
                            .setText("Chat")
                            .setTextColor(Color.BLUE)
                            .setListener(chatListener))
                    .addAction(R.id.left_text_button, new TextViewAction(getActivity())
                            .setText("Follow")
                            .setTextColor(Color.BLACK)
                            .setListener(followListener))
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

    private OnActionClickListener followListener = new OnActionClickListener() {
        @Override
        public void onActionClicked(View view, Card card) {
            otherId = peopleList.get(peopleListView.getAdapter().getPosition(card)).getId();
            targetCard = card;
            followTask = new FollowTask();
            followTask.execute((Void) null);
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

            cursor = -1;
            response = getResponse();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            refreshTask = null;

            // Get usr array
            peopleList = (ArrayList<User>) response.getContent();
            preLen = 0;

            // Update ui
            peopleListView.getAdapter().clearAll();
            addCards();
            peopleListView.scrollToPosition(0);
            peopleRefreshLayout.setRefreshing(false);

            // Initialize loading variables
            previousTotal = 0;
            loading = true;
            int size = peopleList.size();
            cursor = size == 0 ? 0 : peopleList.get(size - 1).getId();
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
            response = getResponse();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loadTask = null;

            ArrayList<User> tempList = (ArrayList<User>) response.getContent();
            preLen = peopleList.size();
            peopleList.addAll(tempList);

            // Update ui
            addCards();
            peopleListView.scrollToPosition(lastVisibleItem + 1);
            if (tempList.size() <= 0)
                Toast.makeText(context, "All data has been fetched", Toast.LENGTH_LONG).show();

            // Update cursor
            int size = peopleList.size();
            cursor = size == 0 ? 0 : peopleList.get(size - 1).getId();
        }

        @Override
        protected void onCancelled() {
            loadTask = null;
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public class FollowTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;

        FollowTask() {
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
                URL url = null;
                try {
                    url = new URL(new AddrUtil().getAddress("AddFollow"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (url == null) {
                    return null;
                }

                int uid = DataHolder.getInstance().getUser().getId();
                HttpClient client = new HttpClient(url);
                response = client.sendToServer(new Pack(Information.GET_PEOPLE, uid + ":" + otherId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            followTask = null;

            Toast.makeText(context, "Follow success!", Toast.LENGTH_SHORT).show();
            targetCard.setDismissible(true);
            targetCard.dismiss();
        }

        @Override
        protected void onCancelled() {
            followTask = null;
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private Pack getResponse() {
        URL url = null;
        try {
            url = new URL(new AddrUtil().getAddress("GetPeople"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }

        int uid = DataHolder.getInstance().getUser().getId();
        HttpClient client = new HttpClient(url);
        Pack response = client.sendToServer(new Pack(Information.GET_PEOPLE, uid + ":" + cursor));
        return response;
    }
}
