package com.wanted.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanted.R;

import java.util.List;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/6
 */
public class FollowingAdapter extends ArrayAdapter<Following> {
    private int resourceId;

    public FollowingAdapter(Context context, int textViewResourceId, List<Following> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Following following = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView fruitImage = (ImageView) view.findViewById(R.id.image_following);
        TextView fruitName = (TextView) view.findViewById(R.id.name_following);
        fruitImage.setImageResource(following.getImageId());
        fruitName.setText(following.getName());
        return view;
    }
}
