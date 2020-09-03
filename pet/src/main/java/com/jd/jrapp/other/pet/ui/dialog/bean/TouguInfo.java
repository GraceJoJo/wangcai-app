package com.jd.jrapp.other.pet.ui.dialog.bean;


public class TouguInfo {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    private String message;
    private int messageType;
    private String messageUrl;

    public TouguInfo(String message, int messageType, String messageUrl) {
        this.message = message;
        this.messageType = messageType;
        this.messageUrl = messageUrl;
    }
    public TouguInfo(String message, int messageType) {
        this.message = message;
        this.messageType = messageType;
    }
}
