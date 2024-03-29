package com.vdsl.cybermart.Account.Model;

public class AddressModel {

    private boolean userAddress;
    private String fullName, address;

    public AddressModel() {
    }


    public AddressModel(String fullName, String address) {
        this.fullName = fullName;
        this.address = address;
    }

    public AddressModel(String address) {
        this.address = address;
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
