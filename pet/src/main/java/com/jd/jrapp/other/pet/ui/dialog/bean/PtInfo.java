package com.jd.jrapp.other.pet.ui.dialog.bean;

/**
 * Author: chenghuan15
 * Date: 2020/9/12
 * Time: 11:38 AM
 */
public class PtInfo {
    private String ptName;
    private int icon;
    private boolean isSelected;

    public PtInfo(String ptName, int icon, boolean isSelected) {
        this.ptName = ptName;
        this.icon = icon;
        this.isSelected = isSelected;
    }

    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
