package com.wanted.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.wanted.R;

/**
 *
 */
public class PeopleFragment extends Fragment {
    private MaterialListView peopleListView;
    private final int rHeight = 300;
    private final int rWidth = 300;
    private int[] names = new int[]{R.string.people_john, R.string.people_tom, R.string.people_mike};
    private int[] schools = new int[]{R.string.school_john, R.string.school_tom, R.string.school_mike};
    private int[] drawables = new int[]{R.drawable.people_john,
                                           R.drawable.people_tom,
                                           R.drawable.people_mike};

    public PeopleFragment() {
        // Required empty public constructor
    }

    public static PeopleFragment newInstance(String param1, String param2) {
        PeopleFragment fragment = new PeopleFragment();
        return fragment;
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
        addCards();
        addListeners();
        return view;
    }

    private void findViews(View view) {
        peopleListView = (MaterialListView) view.findViewById(R.id.people_listview);
    }

    private void addCards() {
        for (int i = 0; i < 3; ++i) {
            Card card = new Card.Builder(getActivity())
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(names[i])
                    .setTitleColor(Color.BLACK)
                    .setDescription(schools[i])
                    .setDrawable(resize(drawables[i]))
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

    private Drawable resize(int drawable) {
        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), drawable);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapOrg, rWidth, rHeight, false);

        Drawable ret = new BitmapDrawable(getResources(), resizedBitmap);
        return ret;
    }

    private OnActionClickListener chatListener = new OnActionClickListener() {
        @Override
        public void onActionClicked(View view, Card card) {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        }
    };

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
    }


}
