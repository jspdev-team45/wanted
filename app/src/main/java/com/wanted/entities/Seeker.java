package com.wanted.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/12
 */
public class Seeker extends User {
    private String college;
    private String major;
    private String skill;
    private List<Experience> experiences;

    public Seeker(String email, String passWord, String name, boolean role) {
        super(email, passWord, name, role);
        this.experiences = new ArrayList<Experience>();
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
