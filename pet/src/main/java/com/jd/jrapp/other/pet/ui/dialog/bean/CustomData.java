package com.jd.jrapp.other.pet.ui.dialog.bean;

import java.util.List;

/**
 * Author: chenghuan15
 * Date: 2020/9/1
 * Time: 10:28 AM
 */

public class CustomData {

    /**
     * title : 浮窗显示形式
     * records : [{"icon":0,"iconStr":"收益模式"},{"icon":1,"iconStr":"爱宠模式"},{"icon":2,"iconStr":"隐身模式"}]
     */

    private String title;
    private List<RecordsBean> records;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public static class RecordsBean {
        /**
         * icon : 0
         * iconStr : 收益模式
         */

        private int icon;
        private String iconStr;
        private int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getIconStr() {
            return iconStr;
        }

        public void setIconStr(String iconStr) {
            this.iconStr = iconStr;
        }
    }
}
