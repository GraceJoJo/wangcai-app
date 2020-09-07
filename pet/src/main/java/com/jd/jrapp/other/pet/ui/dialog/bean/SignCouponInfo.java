package com.jd.jrapp.other.pet.ui.dialog.bean;
/**
 * Author: chenghuan15
 * Date: 2020/9/5
 * Time: 11:38 AM
 */
public class SignCouponInfo {

    private int money;
    private String couponType;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public SignCouponInfo(int money, String couponType) {
        this.money = money;
        this.couponType = couponType;
    }
}
