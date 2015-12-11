package com.wanted.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.wanted.R;
import com.wanted.entities.Information;
import com.wanted.entities.Pack;
import com.wanted.entities.Post;
import com.wanted.util.AddrUtil;
import com.wanted.util.DataHolder;
import com.wanted.util.DialogUtil;
import com.wanted.ws.remote.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by xlin2
 */
public class FavoriteActivity extends AppCompatActivity {

    private MaterialListView favoriteJobListView;
    private ArrayList<Post> favoriteJobList;

    private GetLikeTask getLikeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_favorite);

        findViews();
        //addCards();
        addListeners();
        fetchData();
    }

    private void fetchData() {
        getLikeTask = new GetLikeTask();
        getLikeTask.execute((Void) null);
    }

    private void findViews() {
        favoriteJobListView = (MaterialListView) findViewById(R.id.favorite_job_listview);
    }

    /**
     * Add cards into the card list
     */
    private void addCards() {
        if (favoriteJobList == null) return;

        int len = favoriteJobList.size();
        for (int i = 0; i < len; ++i) {
            String addr = new AddrUtil().getImageAddress(favoriteJobList.get(i).getCompany().getBanner());
            Card card = new Card.Builder(FavoriteActivity.this)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_image_with_buttons_card)
                    .setTitle(favoriteJobList.get(i).getCompany().getName())
                    .setTitleColor(Color.WHITE)
                    .setDescription(favoriteJobList.get(i).getDescription())
                    .setDrawable(addr)
                    .endConfig()
                    .build();
            favoriteJobListView.getAdapter().add(card);
        }
    }

    private void addListeners() {
        favoriteJobListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(Card card, int position) {
                Intent intent = new Intent(FavoriteActivity.this, JobDetailActivity.class);
                intent.putExtra("post", favoriteJobList.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Card card, int position) {
            }
        });
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

    public class GetLikeTask extends AsyncTask<Void, Void, Boolean> {
        private Pack response;
        private ProgressDialog pd;

        GetLikeTask() {
            response = null;
        }

        @Override
        protected void onPreExecute() {
            // Disable interaction
            pd = new DialogUtil().showProgress(FavoriteActivity.this, "Loading favorite list...");
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(new AddrUtil().getAddress("GetLike"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null) {
                return null;
            }

            int uid = DataHolder.getInstance().getUser().getId();
            HttpClient client = new HttpClient(url);
            response = client.sendToServer(new Pack(Information.GET_LIKE, uid));
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Cancel the progress spinner and enable interaction
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            getLikeTask = null;

            favoriteJobList = (ArrayList<Post>) response.getContent();

            // Update ui
            addCards();
            favoriteJobListView.scrollToPosition(0);

        }

        @Override
        protected void onCancelled() {
            getLikeTask = null;
            pd.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

}
