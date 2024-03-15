package com.vdsl.cybermart.Home.Model;

public class CategoryModel {
    private String id;
    private String title;
    private String image;
    private boolean status;

    public boolean isStatus() {
        return status;
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

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CategoryModel() {
    }

}
