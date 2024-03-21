package com.vdsl.cybermart.Voucher;

import java.util.HashMap;
import java.util.Map;

public class Voucher {
    String code,title,expiryDate;

    int discount;


    public Voucher(String code, String title, String expiryDate, int discount) {
        this.code = code;
        this.title = title;
        this.expiryDate = expiryDate;
        this.discount = discount;
    }

    public Voucher() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("code",code);
        map.put("title",title);
        map.put("discount",discount);
        map.put("expiryDate",expiryDate);

        return map;
    }
}
