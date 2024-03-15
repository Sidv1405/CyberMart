package com.vdsl.cybermart.Home.Model;

public class CategoryModel {
    private String image;
    private String title;

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

    public CategoryModel(String image, String title) {
        this.image = image;
        this.title = title;
    }
}
