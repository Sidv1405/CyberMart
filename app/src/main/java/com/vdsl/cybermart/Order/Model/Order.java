package com.vdsl.cybermart.Order.Model;

import com.vdsl.cybermart.Cart.Model.CartModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Order implements Serializable {
    private String seri, idStaff, address, status, paymentMethod,voucher;

    private CartModel cartModel;

    public Order() {
    }

    public String getSeri() {
        return seri;
    }

    public void setSeri(String seri) {
        this.seri = seri;
    }

    public String getIdStaff() {
        return idStaff;
    }

    public void setIdStaff(String idStaff) {
        this.idStaff = idStaff;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public CartModel getCartModel() {
        return cartModel;
    }

    public void setCartModel(CartModel cartModel) {
        this.cartModel = cartModel;
    }

    public Order(String seri, String address, String status, String paymentMethod, String voucher, CartModel cartModel) {
        this.seri = seri;
        this.address = address;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.voucher = voucher;
        this.cartModel = cartModel;
    }
}
