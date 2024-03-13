package com.vdsl.cybermart.Category;

public class Category_Element {
    private int image;
    private String title;
    private boolean isSelected;

    public Category_Element(int image, String title) {
        this.image = image;
        this.title = title;
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
