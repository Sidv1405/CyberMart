package com.vdsl.cybermart.Favourite.Model;

import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.Product.Model.ProductModel;

import java.util.Map;

public class FavoriteModel {
    private String idFavorite;
    private String accountId;
    private Map<String, ProductModel> listFavorites;

    public String getIdFavorite() {
        return idFavorite;
    }

    public void setIdFavorite(String idFavorite) {
        this.idFavorite = idFavorite;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Map<String, ProductModel> getListFavorites() {
        return listFavorites;
    }

    public void setListFavorites(Map<String, ProductModel> listFavorites) {
        this.listFavorites = listFavorites;
    }

    public FavoriteModel() {
    }

    public FavoriteModel(String idFavorite, String accountId, Map<String, ProductModel> listFavorites) {
        this.idFavorite = idFavorite;
        this.accountId = accountId;
        this.listFavorites = listFavorites;
    }
}
