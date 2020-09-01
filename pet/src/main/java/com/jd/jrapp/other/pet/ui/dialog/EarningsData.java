package com.jd.jrapp.other.pet.ui.dialog;

import java.util.List;

public class EarningsData {

    /**
     * sssy : +50.00
     * ssnh : 4.93%
     * hbjj : +30.00
     * gpjj : +10.00
     * zqjj : +10.00
     * records : [{"nickName":"一颗柠檬精","sssyl":"+235.22%","headerUrl":"https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLcibTq8rLzssIPFy6SanNUEJDxXxdUYfRCTTmPkq7Niaz4Wf36IlvMia13DM1UMKEhg1uUxRuxnpLhw/132"},{"nickName":"争当小锦鲤","sssyl":"+194.93%","headerUrl":"http://storage.360buyimg.com/wangfujing-weapp/header1.png"},{"nickName":"睡到自然醒","sssyl":"+94.12%","headerUrl":"http://storage.360buyimg.com/wangfujing-weapp/header1.png"}]
     */

    private String sssy;
    private String ssnh;
    private String hbjj;
    private String gpjj;
    private String zqjj;
    private List<RecordsBean> records;

    public String getSssy() {
        return sssy;
    }

    public void setSssy(String sssy) {
        this.sssy = sssy;
    }

    public String getSsnh() {
        return ssnh;
    }

    public void setSsnh(String ssnh) {
        this.ssnh = ssnh;
    }

    public String getHbjj() {
        return hbjj;
    }

    public void setHbjj(String hbjj) {
        this.hbjj = hbjj;
    }

    public String getGpjj() {
        return gpjj;
    }

    public void setGpjj(String gpjj) {
        this.gpjj = gpjj;
    }

    public String getZqjj() {
        return zqjj;
    }

    public void setZqjj(String zqjj) {
        this.zqjj = zqjj;
    }

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public static class RecordsBean {
        /**
         * nickName : 一颗柠檬精
         * sssyl : +235.22%
         * headerUrl : https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLcibTq8rLzssIPFy6SanNUEJDxXxdUYfRCTTmPkq7Niaz4Wf36IlvMia13DM1UMKEhg1uUxRuxnpLhw/132
         */

        private String nickName;
        private String sssyl;
        private String headerUrl;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getSssyl() {
            return sssyl;
        }

        public void setSssyl(String sssyl) {
            this.sssyl = sssyl;
        }

        public String getHeaderUrl() {
            return headerUrl;
        }

        public void setHeaderUrl(String headerUrl) {
            this.headerUrl = headerUrl;
        }
    }
}
