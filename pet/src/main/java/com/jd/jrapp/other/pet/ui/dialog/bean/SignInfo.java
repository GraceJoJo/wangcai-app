package com.jd.jrapp.other.pet.ui.dialog.bean;

/**
 * Author: chenghuan15
 * Date: 2020/9/5
 * Time: 11:38 AM
 */
public class SignInfo {
    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getSigntype() {
        return signtype;
    }

    public void setSigntype(int signtype) {
        this.signtype = signtype;
    }

    private String week;
    private int signtype;

    public SignInfo(String week, int signtype) {
        this.week = week;
        this.signtype = signtype;
    }
}
