package com.wanted.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.wanted.R;

/**
 * Created by xlin2
 */
public class JobFragment extends Fragment {
    private MaterialListView jobListView;
    private int[] companys = new int[]{R.string.company_google, R.string.company_facebook};
    private String[] descriptions = new String[]{"We are looking for a new software engineer",
                                                  "Looking for a front end engineer"};
    private int[] drawables = new int[]{R.drawable.google, R.drawable.facebook};

    public JobFragment() {
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
        addCards();
        addListeners();
        return view;
    }

    /**
     * Get ui objects
     * @param view
     */
    private void findViews(View view) {
        jobListView = (MaterialListView) view.findViewById(R.id.job_listview);
    }

    /**
     * Add cards to the card list
     */
    private void addCards() {
        for (int i = 0; i < 2; ++i) {
            Card card = new Card.Builder(getActivity())
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_image_with_buttons_card)
                    .setTitle(companys[i])
                    .setTitleColor(Color.WHITE)
                    .setDescription(descriptions[i])
                    .setDrawable(drawables[i])
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
    }

}
