package com.lobomarket.wecart.Models;

public class Shop {
    String shopname;
    String typeofShop;
    String shopImage;
    String sellerName;
    String productType;

    public Shop() {
    }

    public Shop(String shopname, String typeofShop, String shopImage) {
        this.shopname = shopname;
        this.typeofShop = typeofShop;
        this.shopImage = shopImage;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getTypeofShop() {
        return typeofShop;
    }

    public void setTypeofShop(String typeofShop) {
        this.typeofShop = typeofShop;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
