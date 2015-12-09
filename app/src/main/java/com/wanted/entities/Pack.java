package com.wanted.entities;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by xlin2 on 2015/11/13.
 */
public class Pack implements Serializable {
    private static final long serialVersionUID = 1L;
    private Information info;
    private Object content;
    private byte[] image;

    public Pack(Information info, Object content) {
        this.info = info;
        this.content = content;
    }

    public Information getInfo() {
        return info;
    }

    public void setInfo(Information info) {
        this.info = info;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        image = stream.toByteArray();
    }
}
