package com.vdsl.cybermart.Notify;

public class NotifyModel {
    private String title;
    private String body;

    private String userId;

    private int type;

    public NotifyModel() {
    }

    public NotifyModel(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public NotifyModel(String title, String body, String userId) {
        this.title = title;
        this.body = body;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

