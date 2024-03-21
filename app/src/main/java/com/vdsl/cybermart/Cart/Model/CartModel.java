package com.vdsl.cybermart.Cart.Model;

import com.vdsl.cybermart.Product.Model.ProductModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CartModel {
    private String cartId;
    private String accountId;
    private List<ProductModel> listProduct;
    private double totalPrice;
    private String creatAt;
    private String updateAt;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<ProductModel> getListProduct() {
        return listProduct;
    }

    public void setListProduct(List<ProductModel> listProduct) {
        this.listProduct = listProduct;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(String creatAt) {
        this.creatAt = creatAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public CartModel() {
    }

    public CartModel(String cartId, String accountId, List<ProductModel> listProduct, double totalPrice, String creatAt, String updateAt) {
        this.cartId = cartId;
        this.accountId = accountId;
        this.listProduct = listProduct;
        this.totalPrice = totalPrice;
        this.creatAt = creatAt;
        this.updateAt = updateAt;
    }
}
