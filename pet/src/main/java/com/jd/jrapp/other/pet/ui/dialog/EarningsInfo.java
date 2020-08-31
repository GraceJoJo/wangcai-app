package com.jd.jrapp.other.pet.ui.dialog;

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
