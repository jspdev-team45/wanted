package com.wanted.entities;

import java.io.Serializable;

/**
 * Created by xlin2 on 2015/11/13.
 */
public class Pack implements Serializable {
    private static final long serialVersionUID = 1L;
    private Information info;
    private Object content;

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
}
