package com.wanted.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wanted.R;
import com.wanted.entities.Seeker;
import com.wanted.entities.User;
import com.wanted.util.AddrUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/6
 * List adapters for follower list
 */
public class ListAdapter extends BaseAdapter implements SectionIndexer {
    private List<User> data;
    private LayoutInflater inflater;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options;

    public ListAdapter(Context context, List<User> data) {
        this.data = data;
        Collections.sort(this.data);
        inflater = LayoutInflater.from(context);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
    }


    public void updateListView(List<User> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.follow_list_item, parent, false);
            holder = new ViewHolder();
            holder.catalog = (TextView) view.findViewById(R.id.catalog);
            holder.text = (TextView) view.findViewById(R.id.name_follow);
            holder.image = (ImageView) view.findViewById(R.id.image_follow);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            holder.catalog.setVisibility(View.VISIBLE);
            holder.catalog.setText(String.valueOf(data.get(position).getName().charAt(0)));
        }
        else {
            holder.catalog.setVisibility(View.GONE);
        }

        holder.text.setText(data.get(position).getName());
        String avatarAddr = new AddrUtil().getImageAddress(data.get(position).getAvatar());
        ImageLoader.getInstance().displayImage(avatarAddr, holder.image, options, animateFirstListener);

        return view;
    }


    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int l = getCount();
        for (int i = 0; i < l; ++i) {
            if (data.get(i).getName().charAt(0) == sectionIndex)
                return i;
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return data.get(position).getName().charAt(0);
    }

    static class ViewHolder {
        TextView catalog;
        TextView text;
        ImageView image;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
