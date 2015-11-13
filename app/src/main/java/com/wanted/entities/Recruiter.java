package com.wanted.entities;

import java.io.Serializable;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/12
 */
public class Recruiter extends User implements Serializable {
    private String company;
    private String department;

    public Recruiter(String email, String passWord, String name, Integer role) {
        super(email, passWord, name, role);
    }

    public String getEmail() {
        return super.getEmail();
    }

    public void setEmail(String email) {
        super.setEmail(email);
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public String getPassWord() {
        return super.getPassWord();
    }

    public void setPassWord(String passWord) {
        super.setPassWord(passWord);
    }

    public String getAvatar() {
        return super.getAvatar();
    }

    public void setAvatar(String avatar) {
        super.setAvatar(avatar);
    }

    public Integer getRole() {
        return super.getRole();
    }

    public void setRole(Integer role) {
        super.setRole(role);
    }

    public String getRealName() {
        return super.getRealName();
    }

    public void setRealName(String realName) {
        super.setRealName(realName);
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
