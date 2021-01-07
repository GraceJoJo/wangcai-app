package com.jd.jrapp.other.pet.ui.dialog.bean;

import java.util.List;

/**
 * Author: chenghuan15
 * Date: 2020/12/31
 * Time: 3:50 PM
 */
public class CommitInfo {

    private List<CommitsBean> commits;

    public List<CommitsBean> getCommits() {
        return commits;
    }

    public void setCommits(List<CommitsBean> commits) {
        this.commits = commits;
    }

    public static class CommitsBean {
        public CommitsBean(boolean isCurrentCommit, String content) {
            this.isCurrentCommit = isCurrentCommit;
            this.content = content;
        }

        /**
         * isCurrentCommit : false
         * content : 今天有促销信息吗？
         */


        private boolean isCurrentCommit;
        private String content;

        public boolean isIsCurrentCommit() {
            return isCurrentCommit;
        }

        public void setIsCurrentCommit(boolean isCurrentCommit) {
            this.isCurrentCommit = isCurrentCommit;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
