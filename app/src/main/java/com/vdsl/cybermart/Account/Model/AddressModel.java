package com.vdsl.cybermart.Account.Model;

public class AddressModel {

    private boolean userAddress;
    private String fullName, address;

    public AddressModel() {
    }

    public AddressModel(boolean check, String fullName, String address) {
        this.userAddress = check;
        this.fullName = fullName;
        this.address = address;
    }

    public AddressModel(String fullName, String address) {
        this.fullName = fullName;
        this.address = address;
    }

    public boolean isUserAddress() {
        return userAddress;
    }

    public void setUserAddress(boolean userAddress) {
        this.userAddress = userAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
