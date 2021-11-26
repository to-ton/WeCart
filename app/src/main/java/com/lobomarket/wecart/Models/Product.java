package com.lobomarket.wecart.Models;

import java.io.Serializable;

public class Product {
    String productName;
    String description;
    int price;
    String stock;
    String productPhoto;
    String productPrice;
    String username;
    String product_type;

    public Product() {
    }

    public Product(String productName, String description, int price, String stock, String productPhoto) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.productPhoto = productPhoto;

    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public void setProductPhoto(String productPhoto) {
        this.productPhoto = productPhoto;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }
}
