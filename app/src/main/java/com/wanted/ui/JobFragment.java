package com.wanted.ui;

import android.app.Activity;
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
import com.dexafree.materialList.view.MaterialListView;
import com.wanted.R;


/**
 *
 */
public class JobFragment extends Fragment {
    MaterialListView jobListView;

    public JobFragment() {
        // Required empty public constructor
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
        return view;
    }

    private void findViews(View view) {
        jobListView = (MaterialListView) view.findViewById(R.id.job_listview);
    }

    private void addCards() {
        Card card = new Card.Builder(getActivity())
                            .setTag("Image Button Card")
                            .withProvider(new CardProvider())
                            .setLayout(R.layout.material_image_with_buttons_card)
                            .setTitle("DOG")
                            .setSubtitle("DOGDOG")
                            .setDescription("This is a dog")
                            .setDrawable(R.drawable.dog)
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
