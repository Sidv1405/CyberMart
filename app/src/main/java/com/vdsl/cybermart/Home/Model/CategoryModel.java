package com.vdsl.cybermart.Home.Model;

public class CategoryModel {
    private String id;
    private final String title;
    private final String image;
    private boolean status;

    public String getId() {
        return id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CategoryModel(String id, String title, String image, boolean status) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.status = status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}
