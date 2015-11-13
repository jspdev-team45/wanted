package com.wanted.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/13
 */
public class ResizeUtil {
    private final int LARGE = 1000;
    private final int XLARGE = 1500;

    /**
     * Resize an image
     * @param drawable
     * @return
     */
    public Drawable resizeAvatar(Context context, int drawable) {
        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(), drawable);

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int swidth = size.x;
        int width;
        int height;

        if (swidth > LARGE) {
            width = 225;
            height = 225;
        } else {
            width = 150;
            height = 150;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapOrg, width, height, false);

        Drawable ret = new BitmapDrawable(context.getResources(), resizedBitmap);
        return ret;
    }

    public Drawable resizePeople(Context context, int drawable) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int swidth = size.x;
        int width;
        int height;

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

        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(), drawable);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapOrg, width, height, false);

        Drawable ret = new BitmapDrawable(context.getResources(), resizedBitmap);
        return ret;
    }
}
