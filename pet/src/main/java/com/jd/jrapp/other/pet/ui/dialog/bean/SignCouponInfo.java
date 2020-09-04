package com.jd.jrapp.other.pet.ui.dialog.bean;
/**
 * Author: chenghuan15
 * Date: 2020/9/5
 * Time: 11:38 AM
 */
public class SignCouponInfo {

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getUsetype() {
        return usetype;
    }

    public void setUsetype(String usetype) {
        this.usetype = usetype;
    }

    private String money;
    private String couponType;
    private String usetype;

    public SignCouponInfo(String money, String couponType, String usetype) {
        this.money = money;
        this.couponType = couponType;
        this.usetype = usetype;
    }
}
