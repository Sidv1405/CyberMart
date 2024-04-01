package com.vdsl.cybermart.Cart.Model;

import com.vdsl.cybermart.Product.Model.ProductModel;

import java.util.Map;

public class CartModel {
    private String cartId;
    private String accountId;
    private Map<String, ProductModel> cartDetail;
    private double totalPrice;
    private String date;

    public CartModel() {
    }

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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, ProductModel> getCartDetail() {
        return cartDetail;
    }

    public void setCartDetail(Map<String, ProductModel> cartDetail) {
        this.cartDetail = cartDetail;
    }

    public CartModel(String cartId, String accountId, Map<String, ProductModel> cartDetail, double totalPrice, String date) {
        this.cartId = cartId;
        this.accountId = accountId;
        this.cartDetail = cartDetail;
        this.totalPrice = totalPrice;
        this.date = date;
    }
}
