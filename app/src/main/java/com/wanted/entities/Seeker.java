package com.wanted.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/12
 */
public class Seeker extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String college;
    private String major;
    private String skill;
    private List<Experience> experiences;

    public Seeker(String email, String passWord, String name, Integer role) {
        super(email, passWord, name, role);
        this.experiences = new ArrayList<Experience>();
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

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }
}
