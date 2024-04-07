package com.vdsl.cybermart.Order.Model;

import com.vdsl.cybermart.Cart.Model.CartModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Order implements Serializable {
    private String seri, idStaff, address, status, paymentMethod,voucher, statusId;

    private CartModel cartModel;

    public Order() {
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
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

    public Order(String seri, String address, String status, String paymentMethod, String voucher, CartModel cartModel,String statusId) {
        this.seri = seri;
        this.address = address;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.voucher = voucher;
        this.cartModel = cartModel;
        this.statusId = statusId;
    }

    public Order(String seri, String idStaff, String address, String status, String paymentMethod, String voucher, String statusId, CartModel cartModel) {
        this.seri = seri;
        this.idStaff = idStaff;
        this.address = address;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.voucher = voucher;
        this.statusId = statusId;
        this.cartModel = cartModel;
    }
}
