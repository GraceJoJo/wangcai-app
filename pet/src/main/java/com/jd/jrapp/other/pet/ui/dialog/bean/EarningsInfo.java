package com.jd.jrapp.other.pet.ui.dialog.bean;
/**
 * Author: chenghuan15
 * Date: 2020/9/1
 * Time: 11:38 AM
 */
public class EarningsInfo {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSyl() {
        return syl;
    }

    public void setSyl(String syl) {
        this.syl = syl;
    }

    private String name;
    private String syl;

    public EarningsInfo(String name, String syl) {
        this.name = name;
        this.syl = syl;
    }
}
