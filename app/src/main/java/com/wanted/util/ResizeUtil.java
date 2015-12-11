package com.wanted.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wanted.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.Inflater;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/13
 */
public class ResizeUtil {
    private final int LARGE = 1000;
    private final int XLARGE = 1500;

    private int swidth;
    private Context context;

    public ResizeUtil(Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.swidth = size.x;

        this.context = context;
    }

    /**
     * Resize an avatar_default image
     * @param drawable
     * @return
     */
    public Drawable resizeAvatar(int drawable) {
        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(), drawable);
        bitmapOrg = new ImageUtil().centerCrop(bitmapOrg);

        // New width and height
        int width;
        int height;

        // Changed size
        if (swidth > LARGE) {
            width = 225;
            height = 225;
        } else {
            width = 150;
            height = 150;
        }

        // Resize
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapOrg, width, height, false);

        Drawable ret = new BitmapDrawable(context.getResources(), resizedBitmap);
        return ret;
    }

    public int[] resizeAvatar() {
        // New width and height
        int width;
        int height;

        // Changed size
        if (swidth > LARGE) {
            width = 225;
            height = 225;
        } else {
            width = 150;
            height = 150;
        }

        int[] ret = new int[] { width, height };
        return ret;
    }

    /**
     * Resize people image
     * @param drawable
     * @return
     */
    public Drawable resizePeople(int drawable) {
        int width;
        int height;

        // Changed size
        if (swidth > XLARGE) {
            width = 600;
            height = 600;
        } else if (swidth > LARGE) {
            width = 400;
            height = 400;
        } else {
            width = 300;
            height = 300;
        }

        // Resize
        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(), drawable);
        bitmapOrg = new ImageUtil().centerCrop(bitmapOrg);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapOrg, width, height, false);

        Drawable ret = new BitmapDrawable(context.getResources(), resizedBitmap);
        return ret;
    }

    public int[] resizePeople() {
        int width;
        int height;

        // Changed size
        if (swidth > XLARGE) {
            width = 600;
            height = 600;
        } else if (swidth > LARGE) {
            width = 400;
            height = 400;
        } else {
            width = 300;
            height = 300;
        }

        int[] ret = new int[] { width, height };
        return ret;
    }

    /**
     *
     * @return
     */
    public int sideBarSize() {
        if (swidth > XLARGE) {
            return 50;
        } else if (swidth > LARGE) {
            return 35;
        } else {
            return 20;
        }
    }

}
