package com.vdsl.cybermart.Product.Model;

public class ProductModel {
    private String prodId;
    private String name;
    private String description;
    private Double price;
    private int quantity;
    private String image;
    private String categoryId;
    private boolean status;

    public ProductModel(String name, Double price, int quantity, String image) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public ProductModel(String name, String description, Double price, int quantity, String image, String categoryId, boolean status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.categoryId = categoryId;
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public String getProdId() {
        return prodId;
    }

    public void setProdId(String id) {
        this.prodId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public ProductModel() {
    }

}
