package com.wanted.util;

import com.wanted.entities.User;

/**
 * Created by xlin2 on 2015/11/22.
 */
public class DataHolder {
    private static final DataHolder holder = new DataHolder();

    private User user;

    private DataHolder() {}

    public static DataHolder getInstance() { return holder; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
