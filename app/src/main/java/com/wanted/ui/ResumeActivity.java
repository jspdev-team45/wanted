package com.wanted.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.wanted.R;

public class ResumeActivity extends AppCompatActivity {

    private MaterialListView resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        findViews();
        addCards();
    }

    private void findViews() {
        resume = (MaterialListView) findViewById(R.id.resume_listview);
    }

    private void addCards() {
        Card card = new Card.Builder(this)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_image_with_buttons_card)
                .setTitle("Android Studio")
                .setTitleColor(Color.WHITE)
                .setDescription("Carnegie Mellon University")
                .addAction(R.id.left_text_button, new TextViewAction(this)
                        .setText("Modify")
                        .setTextColor(Color.BLUE))
                .setDrawable(R.drawable.avatar)
                .endConfig()
                .build();

        resume.getAdapter().add(card);

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
