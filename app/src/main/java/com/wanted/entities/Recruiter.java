package com.wanted.entities;

/**
 * Author: Junjian Xie
 * Email: junjianx@andrew.cmu.edu
 * Date: 15/11/12
 */
public class Recruiter extends User {
    private String company;
    private String department;

    public Recruiter(String email, String passWord, String name, boolean role) {
        super(email, passWord, name, role);
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
