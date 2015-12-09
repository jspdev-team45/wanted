package com.wanted.util;

import com.wanted.entities.Company;
import com.wanted.entities.User;

/**
 * Created by xlin2 on 2015/11/22.
 */
public class DataHolder {
    private static final DataHolder holder = new DataHolder();

    private User user;
    private Company company;
    private Boolean profileUpdated = false;

    private DataHolder() {}

    public static DataHolder getInstance() { return holder; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Boolean getProfileUpdated() {
        return profileUpdated;
    }

    public void setProfileUpdated(Boolean profileUpdated) {
        this.profileUpdated = profileUpdated;
    }
}
