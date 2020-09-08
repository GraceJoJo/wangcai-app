package com.jd.jrapp.other.pet.ui.dialog.bean;

import java.util.List;

/**
 * Author: chenghuan15
 * Date: 2020/9/1
 * Time: 10:28 AM
 */

public class MoneyManagementData {

    private List<RecordsBean> records;

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public static class RecordsBean {
        /**
         * typeStr : 平台热销｜京东小金库
         * rank : 今日热销榜No.1
         * interestRate : 4.75%
         * interestRateDate : 近7日年化收益
         * manageType : 灵活存取
         * manageTypeLimit : 全民消费 1元起购
         */

        private String typeStr;
        private String rank;
        private String interestRate;
        private String interestRateDate;
        private String manageType;
        private String manageTypeLimit;

        public String getTypeStr() {
            return typeStr;
        }

        public void setTypeStr(String typeStr) {
            this.typeStr = typeStr;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getInterestRate() {
            return interestRate;
        }

        public void setInterestRate(String interestRate) {
            this.interestRate = interestRate;
        }

        public String getInterestRateDate() {
            return interestRateDate;
        }

        public void setInterestRateDate(String interestRateDate) {
            this.interestRateDate = interestRateDate;
        }

        public String getManageType() {
            return manageType;
        }

        public void setManageType(String manageType) {
            this.manageType = manageType;
        }

        public String getManageTypeLimit() {
            return manageTypeLimit;
        }

        public void setManageTypeLimit(String manageTypeLimit) {
            this.manageTypeLimit = manageTypeLimit;
        }
    }
}
