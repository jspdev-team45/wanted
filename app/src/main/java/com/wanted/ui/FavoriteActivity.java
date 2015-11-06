package com.wanted.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.wanted.R;

public class FavoriteActivity extends AppCompatActivity {

    private MaterialListView jobListView;
    private int[] companys = new int[]{R.string.company_oracle, R.string.company_microsoft};
    private String[] descriptions = new String[]{"We are looking for a new database engineer",
            "If you love coding, please join us!"};
    private int[] drawables = new int[]{R.drawable.oracle, R.drawable.microsoft};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        findViews();
        addCards();
        addListeners();
    }

    private void findViews() {
        jobListView = (MaterialListView) findViewById(R.id.favorite_job_listview);
    }

    private void addCards() {
        for (int i = 0; i < 2; ++i) {
            Card card = new Card.Builder(this)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_image_with_buttons_card)
                    .setTitle(companys[i])
                    .setTitleColor(Color.WHITE)
                    .setDescription(descriptions[i])
                    .setDrawable(drawables[i])
                    .addAction(R.id.left_text_button, new TextViewAction(this)
                            .setText("Apply")
                            .setTextColor(Color.BLUE))
                    .endConfig()
                    .build();

            jobListView.getAdapter().add(card);
        }
    }

    private void addListeners() {
        jobListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(Card card, int position) {
                Intent intent = new Intent(getApplicationContext(), JobDetailActivity.class);
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

}
